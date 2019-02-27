package top.pfjia.protocol.request;

import top.pfjia.protocol.enums.RequestOperationType;

/**
 * @author pfjia
 * @since 2019/2/2 18:57
 */
public class LobReadRequest extends CommandRequest {
    {
        operationType= RequestOperationType.LOB_READ;
    }

    // TODO: 2019/2/2
}
