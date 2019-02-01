package top.pfjia.remoting;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.Attribute;
import lombok.extern.slf4j.Slf4j;
import top.pfjia.Const;
import top.pfjia.protocol.request.H2Request;
import top.pfjia.protocol.response.H2Response;

import java.util.List;

/**
 * @author pfjia
 * @since 2019/1/29 7:56
 */
@Slf4j
public class NettyDecoder extends ReplayingDecoder<Void> {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        Channel channel = channelHandlerContext.channel();
        Attribute<H2Request> h2RequestAttribute = channel.attr(Const.REQUEST_TO_BE_RESPONDED_ATTRIBUTE_KEY);
        if (h2RequestAttribute.get() == null) {
            log.error("无待响应的request.");
            return;
        }
        H2Request h2Request = h2RequestAttribute.get();
        H2Response h2Response;
        h2Response = h2Request.parseResponse(channel, byteBuf);
        list.add(h2Response);
    }
}
