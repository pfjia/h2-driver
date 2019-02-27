package top.pfjia.protocol.request;

import lombok.Data;
import top.pfjia.protocol.enums.RequestOperationType;
import top.pfjia.protocol.response.SessionCloseResponse;

/**
 * @author pfjia
 * @since 2019/1/31 17:07
 */
@Data
public class SessionCloseRequest extends CommandRequest<SessionCloseResponse> {
    {
        operationType = RequestOperationType.SESSION_CLOSE;
    }

}
