package top.pfjia.protocol.request;

import lombok.Data;
import lombok.ToString;
import top.pfjia.protocol.enums.RequestOperationType;
import top.pfjia.protocol.response.CommandCommitResponse;

/**
 * @author pfjia
 * @since 2019/2/2 17:41
 */
@Data
@ToString(callSuper = true)
public class CommandCommitRequest extends CommandRequest<CommandCommitResponse> {
    {
        operationType = RequestOperationType.COMMAND_COMMIT;
    }
}
