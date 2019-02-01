package top.pfjia.remoting;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.Attribute;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;
import top.pfjia.Const;
import top.pfjia.protocol.request.H2Request;
import top.pfjia.protocol.request.HandshakeRequest;
import top.pfjia.protocol.response.H2Response;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 1. 一个{@link java.sql.Connection}关联一个NettyClient
 *
 * @author pfjia
 * @since 2019/1/28 11:56
 */
@Slf4j
public class NettyClient {

    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    private EventExecutor promiseEventExecutor = new DefaultEventExecutor();

    private Bootstrap bootstrap = new Bootstrap();
    /**
     * key:服务器的地址
     * value:与服务器的长连接
     */
    private Map<SocketAddress, ChannelFuture> channelMap = new ConcurrentHashMap<>();
    private Lock channelMapLock = new ReentrantLock();

    public void start() {
        bootstrap.group(workerGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyEncoder())
                                .addLast(new NettyDecoder())
                                .addLast(new NettyClientInboundHandler());
                    }
                });
    }

    public void close() {
        workerGroup.shutdownGracefully();
        promiseEventExecutor.shutdownGracefully();

    }


    public void disConnect(SocketAddress socketAddress) {
        ChannelFuture channelFuture = channelMap.get(socketAddress);
        if (channelFuture != null) {
            channelFuture.channel().close()
                    .addListener(new GenericFutureListener<Future<? super Void>>() {
                        @Override
                        public void operationComplete(Future<? super Void> future) throws Exception {
                            channelMap.remove(socketAddress);
                            log.info("channel closed.socketAddress:{}", socketAddress);
                        }
                    });
        }
    }

    public Channel getOrCreateChannel(SocketAddress socketAddress) {
        ChannelFuture cf = channelMap.get(socketAddress);
        channelMapLock.lock();
        try {
            if (cf == null || cf.channel() == null || !cf.channel().isActive()) {
                cf = bootstrap.connect(socketAddress);
                channelMap.putIfAbsent(socketAddress, cf);
            }
        } finally {
            channelMapLock.unlock();
        }
        return cf
                //必须同步,否则channel未注册成功,ChannelInitializer.initChannel不会执行
                .syncUninterruptibly()
                .channel();
    }


    public <T extends H2Response> T invokeSync(SocketAddress socketAddress, H2Request<T> h2Request) {
        //log
        log.debug("h2Request:{}", h2Request);
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        JSONArray stackTraceElementList = Arrays.stream(stackTraceElements)
                .limit(10)
                .map(new Function<StackTraceElement, JSONObject>() {
                    @Override
                    public JSONObject apply(StackTraceElement stackTraceElement) {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.fluentPut("className", stackTraceElement.getClassName())
                                .fluentPut("methodName", stackTraceElement.getMethodName());
                        return jsonObject;
                    }
                })
                .collect(Collectors.toCollection(JSONArray::new));
        log.debug("stackTraceElements:{}", JSON.toJSONString(stackTraceElementList, true));


        Channel channel = getOrCreateChannel(socketAddress);
        if (h2Request.needResponse()) {
            //设置待响应的request
            Attribute<H2Request> h2ResponseAttribute = channel.attr(Const.REQUEST_TO_BE_RESPONDED_ATTRIBUTE_KEY);
            if (h2ResponseAttribute.get() != null) {
                throw new RuntimeException("request未清除");
            }
            h2ResponseAttribute.set(h2Request);
            //设置responsePromise
            Promise<H2Response> h2ResponsePromise = new DefaultPromise<>(promiseEventExecutor);
            Attribute<Promise<H2Response>> promiseAttribute = channel.attr(Const.RESPONSE_FUTURE_ATTRIBUTE_KEY);
            if (promiseAttribute.get() != null) {
                throw new RuntimeException("promise未清除");
            }
            promiseAttribute.set(h2ResponsePromise);
            //写数据
            channel.writeAndFlush(h2Request);

            H2Response h2Response = h2ResponsePromise.syncUninterruptibly().getNow();

            //清除responsePromise
            boolean clear = channel.attr(Const.RESPONSE_FUTURE_ATTRIBUTE_KEY).compareAndSet(h2ResponsePromise, null);
            if (!clear) {
                throw new RuntimeException();
            }
            clear = channel.attr(Const.REQUEST_TO_BE_RESPONDED_ATTRIBUTE_KEY).compareAndSet(h2Request, null);
            if (!clear) {
                throw new RuntimeException();
            }
            @SuppressWarnings("unchecked")
            T cast = (T) h2Response;
            return cast;
        } else {
            channel.writeAndFlush(h2Request);
            return null;
        }

    }

    public static void main(String[] args) throws InterruptedException {
        NettyClient nettyClient = new NettyClient();
        nettyClient.start();
        H2Response h2Response = nettyClient.invokeSync(new InetSocketAddress("localhost", 9092), new HandshakeRequest());
        System.out.println(h2Response);
    }

}
