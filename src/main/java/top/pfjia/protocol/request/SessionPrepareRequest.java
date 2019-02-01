package top.pfjia.protocol.request;

import top.pfjia.protocol.enums.RequestCommandType;

/**
 * @author pfjia
 * @since 2019/1/29 11:12
 */
public class SessionPrepareRequest extends CommandRequest {
    {
        commandType = RequestCommandType.SESSION_PREPARE;
    }

    private int id;
    private String sql;
}
