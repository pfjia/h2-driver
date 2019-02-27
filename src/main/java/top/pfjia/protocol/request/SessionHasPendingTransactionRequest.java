package top.pfjia.protocol.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.ToString;
import top.pfjia.kit.TransferKit;
import top.pfjia.protocol.response.SessionHasPendingTransactionResponse;

/**
 * @author pfjia
 * @since 2019/2/2 18:54
 */
@Data
@ToString(callSuper = true)
public class SessionHasPendingTransactionRequest extends CommandRequest<SessionHasPendingTransactionResponse> {

    @Override
    public SessionHasPendingTransactionResponse parseResponse(Channel channel, ByteBuf in) {
        SessionHasPendingTransactionResponse response = super.parseResponse(channel, in);
        int i = TransferKit.readInt(in);
        response.setHasTransaction(i == 1);
        return response;
    }
}
