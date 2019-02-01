package top.pfjia.remoting;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.Attribute;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import top.pfjia.Const;
import top.pfjia.protocol.response.H2Response;


/**
 * @author pfjia
 * @since 2019/1/29 8:03
 */
@Slf4j
public class NettyClientInboundHandler extends SimpleChannelInboundHandler<H2Response> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, H2Response msg) throws Exception {
        Channel channel = ctx.channel();
        Attribute<Promise<H2Response>> promiseAttribute = channel.attr(Const.RESPONSE_FUTURE_ATTRIBUTE_KEY);
        if (promiseAttribute.get() == null) {
            throw new RuntimeException("无responsePromise");
        }
        Promise<H2Response> h2ResponsePromise = promiseAttribute.get();
        h2ResponsePromise.setSuccess(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("", cause);
    }
}
