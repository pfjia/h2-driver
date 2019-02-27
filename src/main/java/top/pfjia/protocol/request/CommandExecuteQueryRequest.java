package top.pfjia.protocol.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.ToString;
import org.h2.value.Value;
import top.pfjia.kit.TransferKit;
import top.pfjia.protocol.enums.ResponseStatus;
import top.pfjia.protocol.ResultColumn;
import top.pfjia.protocol.enums.RequestOperationType;
import top.pfjia.protocol.response.CommandExecuteQueryResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pfjia
 * @since 2019/1/29 11:14
 */
@Data
@ToString(callSuper = true)
public class CommandExecuteQueryRequest extends CommandRequest<CommandExecuteQueryResponse> {
    {
        operationType = RequestOperationType.COMMAND_EXECUTE_QUERY;
    }

    /**
     * 对应prepare阶段生成的id
     */
    private int statementId;
    /**
     * 跟id类似，实际上就是一个递增计数器，在server端缓存查询结果集时，这个objectId就是结果集的缓存key
     */
    private int resultSetId;

    private int maxRows;
    private int fetchSize;

    private List<Value> valueList;


    @Override
    public CommandExecuteQueryResponse parseResponse(Channel channel, ByteBuf in) {
        CommandExecuteQueryResponse response = super.parseResponse(channel, in);
        ResponseStatus responseStatus = response.getEnumResponseStatus();
        if (responseStatus == ResponseStatus.STATUS_OK) {
            int columnCount = TransferKit.readInt(in);
            int rowCount = TransferKit.readInt(in);

            List<ResultColumn> resultColumnList = new ArrayList<>(columnCount);
            for (int i = 0; i < columnCount; i++) {
                ResultColumn resultColumn = TransferKit.readResultColumn(in);
                resultColumnList.add(resultColumn);
            }

            response.setResult(TransferKit.readValuesList(in,rowCount,columnCount));

            response.setColumnCount(columnCount)
                    .setRowCount(rowCount)
                    .setResultColumnList(resultColumnList);
        }
        return response;
    }

    @Override
    public ByteBuf toByteBuf() {
        ByteBuf byteBuf = super.toByteBuf();
        byteBuf.writeBytes(TransferKit.writeInt(statementId));
        byteBuf.writeBytes(TransferKit.writeInt(resultSetId));
        byteBuf.writeBytes(TransferKit.writeInt(maxRows));
        byteBuf.writeBytes(TransferKit.writeInt(fetchSize));
        byteBuf.writeBytes(TransferKit.writeValueList(valueList));
        return byteBuf;
    }
}
