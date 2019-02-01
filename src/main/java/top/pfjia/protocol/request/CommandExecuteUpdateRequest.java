package top.pfjia.protocol.request;

import top.pfjia.protocol.enums.RequestCommandType;

/**
 * @author pfjia
 * @since 2019/1/29 11:13
 */
public class CommandExecuteUpdateRequest extends CommandRequest {
    {
        commandType = RequestCommandType.COMMAND_EXECUTE_UPDATE;
    }

    /**
     * 对应prepare阶段生成的id
     */
    private int id;
    private int sqlParameterSize;

}
