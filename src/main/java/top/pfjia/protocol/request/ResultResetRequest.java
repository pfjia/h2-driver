package top.pfjia.protocol.request;

import lombok.Data;
import lombok.ToString;
import top.pfjia.protocol.enums.RequestOperationType;

/**
 * @author pfjia
 * @since 2019/2/2 18:26
 */
@Data
@ToString(callSuper = true)
public class ResultResetRequest extends CommandRequest {
    {
        operationType = RequestOperationType.RESULT_RESET;
    }

    private int resultSetId;


}
