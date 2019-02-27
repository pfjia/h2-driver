package top.pfjia.protocol.request;

import lombok.Data;
import top.pfjia.protocol.enums.RequestOperationType;
import top.pfjia.protocol.response.NoneResponse;

/**
 * 无响应
 * @author pfjia
 * @since 2019/1/31 17:02
 */
@Data
public class CommandCloseRequest extends CommandRequest<NoneResponse> {
    {
        operationType = RequestOperationType.COMMAND_CLOSE;
    }

    private int statementId;
}
