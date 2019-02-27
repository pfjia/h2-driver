package top.pfjia.protocol.request;

import lombok.Data;
import top.pfjia.protocol.enums.RequestOperationType;
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
        operationType = RequestOperationType.RESULT_CLOSE;
    }

    private int id;
}
