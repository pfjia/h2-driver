package top.pfjia.protocol.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.ToString;
import top.pfjia.kit.TransferKit;
import top.pfjia.protocol.enums.RequestOperationType;
import top.pfjia.protocol.response.CommandGetMetadataResponse;

/**
 * @author pfjia
 * @since 2019/2/2 17:45
 */
@Data
@ToString(callSuper = true)
public class CommandGetMetadataRequest extends CommandRequest<CommandGetMetadataResponse> {
    {
        operationType = RequestOperationType.COMMAND_GET_META_DATA;
    }

    /**
     * 对应prepare阶段生成的id
     */
    private int statementId;
    /**
     * 跟id类似，实际上就是一个递增计数器，在server端缓存查询结果集时，这个objectId就是结果集的缓存key
     */
    private int resultSetId;

    @Override
    public CommandGetMetadataResponse parseResponse(Channel channel, ByteBuf in) {
        CommandGetMetadataResponse response = super.parseResponse(channel, in);
        response.setColumnCount(TransferKit.readInt(in))
                .setRowCount(TransferKit.readInt(in))
                .setResultColumnList(TransferKit.resultColumnList(in));
        return response;
    }
}
