package top.pfjia.protocol.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.ToString;
import top.pfjia.kit.TransferKit;
import top.pfjia.protocol.ParameterMetadata;
import top.pfjia.protocol.enums.RequestOperationType;
import top.pfjia.protocol.enums.SqlCmdType;
import top.pfjia.protocol.response.SessionPrepareReadParamsResponse;

import java.util.List;

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
public class SessionPrepareReadParamsRequest extends BaseSessionPrepareRequest {
    {
        operationType = RequestOperationType.SESSION_PREPARE_READ_PARAMS;
    }


    @Override
    public SessionPrepareReadParamsResponse parseResponse(Channel channel, ByteBuf in) {
        SessionPrepareReadParamsResponse response = super.parseResponse(channel, in);
        List<ParameterMetadata> parameterMetadataList = TransferKit.readParameterList(in);
        response.setCmdType(SqlCmdType.UNKNOWN.getId())
                .setParameterMetadataList(parameterMetadataList);
        return response;
    }
}
