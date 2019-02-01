package top.pfjia.protocol.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.ToString;
import top.pfjia.Const;
import top.pfjia.kit.TransferKit;
import top.pfjia.jdbc.JdbcConnection;
import top.pfjia.protocol.enums.RequestCommandType;
import top.pfjia.protocol.response.SessionSetIdResponse;

/**
 * client自己生成一个长度为64个字符的sessionId，然后传给server，
 *
 * @author pfjia
 * @since 2019/1/29 11:08
 */
@Data
@ToString(callSuper = true)
public class SessionSetIdRequest extends CommandRequest<SessionSetIdResponse> {
    {
        commandType = RequestCommandType.SESSION_SET_ID;
    }

    private String sessionId;

    @Override
    public ByteBuf toByteBuf() {
        ByteBuf byteBuf = super.toByteBuf();
        byteBuf.writeBytes(TransferKit.writeString(sessionId));
        return byteBuf;
    }

    @Override
    public SessionSetIdResponse parseResponse(Channel channel, ByteBuf in) {
        SessionSetIdResponse sessionSetIdResponse = super.parseResponse(channel, in);
        JdbcConnection connection = channel.attr(Const.CONNECTION_ATTRIBUTE_KEY).get();
        int clientVersion = connection.getClientVersion();
        if (clientVersion >= 15) {
            boolean autoCommit = TransferKit.readBoolean(in);
            sessionSetIdResponse.setAutoCommit(autoCommit);
        }
        return sessionSetIdResponse;
    }
}
