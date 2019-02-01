package top.pfjia.protocol.request;

import lombok.Data;
import lombok.ToString;
import top.pfjia.protocol.enums.RequestCommandType;

/**
 * @author pfjia
 * @since 2019/1/29 11:29
 */
@Data
@ToString(callSuper = true)
public class SessionPrepareReadParams2Request extends BaseSessionPrepareReadParamsRequest {
    {
        commandType = RequestCommandType.SESSION_PREPARE_READ_PARAMS2;
    }

}
