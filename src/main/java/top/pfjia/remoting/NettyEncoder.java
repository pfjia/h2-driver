package top.pfjia.remoting;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import top.pfjia.protocol.request.H2Request;

/**
 * @author pfjia
 * @since 2019/1/28 22:05
 */
@Slf4j
public class NettyEncoder extends MessageToByteEncoder<H2Request> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, H2Request h2Request, ByteBuf byteBuf) throws Exception {
        ByteBuf header = h2Request.toByteBuf();
        byteBuf.writeBytes(header);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("", cause);
        System.out.println(cause);
        super.exceptionCaught(ctx, cause);
    }
}
