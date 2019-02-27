package top.pfjia.protocol.request;

import lombok.Data;
import lombok.ToString;
import top.pfjia.protocol.enums.RequestOperationType;
import top.pfjia.protocol.response.NoneResponse;

/**
 * @author pfjia
 * @since 2019/2/2 18:28
 */
@Data
@ToString(callSuper = true)
public class ChangeIdRequest extends CommandRequest<NoneResponse> {
    {
        operationType = RequestOperationType.CHANGE_ID;
    }

    private int oldId;
    private int newId;


}
