package top.pfjia.protocol.request;

import lombok.Data;
import lombok.ToString;
import org.h2.expression.Parameter;
import top.pfjia.protocol.enums.RequestOperationType;

import java.util.List;

/**
 * @author pfjia
 * @since 2019/1/29 11:13
 */
@Data
@ToString(callSuper = true)
public class CommandExecuteUpdateRequest extends CommandRequest {
    {
        operationType = RequestOperationType.COMMAND_EXECUTE_UPDATE;
    }

    /**
     * 对应prepare阶段生成的id
     */
    private int statementId;
    private int sqlParameterSize;

    // TODO: 2019/2/2
}
