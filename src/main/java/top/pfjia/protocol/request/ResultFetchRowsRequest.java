package top.pfjia.protocol.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.ToString;
import top.pfjia.kit.TransferKit;
import top.pfjia.protocol.enums.RequestOperationType;
import top.pfjia.protocol.response.ResultFetchRowsResponse;

/**
 * @author pfjia
 * @since 2019/2/2 17:57
 */
@Data
@ToString(callSuper = true)
public class ResultFetchRowsRequest extends CommandRequest<ResultFetchRowsResponse> {
    {
        operationType = RequestOperationType.RESULT_FETCH_ROWS;
    }

    private int statementId;
    private int fetchSize;

    @Override
    public ResultFetchRowsResponse parseResponse(Channel channel, ByteBuf in) {
        ResultFetchRowsResponse response = super.parseResponse(channel, in);

        // TODO: 2019/2/2
        response.setValueList(TransferKit.readValuesList(in,0,0));
        return response;
    }
}
