package top.pfjia.protocol.request;

import lombok.Data;
import lombok.ToString;
import top.pfjia.protocol.enums.RequestOperationType;

/**
 * @author pfjia
 * @since 2019/2/2 14:29
 */
@Data
@ToString(callSuper = true)
public class SessionPrepareRequest extends BaseSessionPrepareRequest {
    {
        operationType = RequestOperationType.SESSION_PREPARE;
    }
}
