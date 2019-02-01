package core;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {

    private Bootstrap b = new Bootstrap();

    public void start() {
        EventLoopGroup group = new NioEventLoopGroup();//创建客户端处理I/O读写的线程组
        b.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) throws Exception {
                        //创建NioSocketChannel成功之后,在进行初始化时,将它的ChannelHandler设置为ChannelPipeline中,用于处理网络I/O事件
                        ch.pipeline().addLast(new TimeClientHandler());
                    }
                });
    }

    public ChannelFuture connect(int port, String host) throws Exception {
        //发起异步连接操作
        return b.connect(host, port).sync();//发起异步连接
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        TimeClient timeClient = new TimeClient();
        timeClient.start();
        ChannelFuture f = timeClient.connect(port, "127.0.0.1");
        int times = 1000_000;
        for (int i = 0; i < times; i++) {
            byte[] req = "QUERY TIME ORDER".getBytes();
            ByteBuf firstMessage = Unpooled.buffer(req.length);
            firstMessage.writeBytes(req);
            f.channel().write(firstMessage);
        }
        f.channel().flush();
    }
}
