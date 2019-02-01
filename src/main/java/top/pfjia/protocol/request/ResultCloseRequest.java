package top.pfjia.protocol.request;

import lombok.Data;
import top.pfjia.protocol.enums.RequestCommandType;
import top.pfjia.protocol.response.NoneResponse;

/**
 * 无响应
 *
 * @author pfjia
 * @since 2019/1/31 16:52
 */
@Data
public class ResultCloseRequest extends CommandRequest<NoneResponse> {
    {
        commandType = RequestCommandType.RESULT_CLOSE;
    }

    private int id;
}
