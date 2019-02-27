package top.pfjia.protocol.request;

import lombok.Data;
import lombok.ToString;
import top.pfjia.protocol.enums.RequestOperationType;
import top.pfjia.protocol.response.SessionSetAutocommitResponse;

/**
 * @author pfjia
 * @since 2019/2/2 18:50
 */
@Data
@ToString(callSuper = true)
public class SessionSetAutocommitRequest extends CommandRequest<SessionSetAutocommitResponse> {
    {
        operationType= RequestOperationType.SESSION_SET_AUTOCOMMIT;
    }

    private boolean autoCommit;

}
