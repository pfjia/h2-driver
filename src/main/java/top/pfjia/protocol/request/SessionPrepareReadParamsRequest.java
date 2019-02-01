package top.pfjia.protocol.request;

import lombok.Data;
import lombok.ToString;
import top.pfjia.protocol.enums.RequestCommandType;

/**
 * 执行SQL之前，需要先进行prepare，
 * client给server发送的是一个SESSION_PREPARE_READ_PARAMS或SESSION_PREPARE数据包，两者格式几本上一样，
 * SESSION_PREPARE_READ_PARAMS需要server响应的数据包中包含SQL语句中的参数元数据，SESSION_PREPARE则不需要，
 * SESSION_PREPARE用在第二次对同一个SQL进行prepare时。
 * <p>
 * 版本<16使用该请求;版本>16使用{@link SessionPrepareReadParams2Request}
 *
 * @author pfjia
 * @since 2019/1/29 11:10
 */
@Data
@ToString(callSuper = true)
public class SessionPrepareReadParamsRequest extends BaseSessionPrepareReadParamsRequest {
    {
        commandType = RequestCommandType.SESSION_PREPARE_READ_PARAMS;
    }

}
