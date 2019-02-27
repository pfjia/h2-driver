package top.pfjia.protocol.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.ToString;
import top.pfjia.kit.TransferKit;
import top.pfjia.protocol.ParameterMetadata;
import top.pfjia.protocol.enums.RequestOperationType;
import top.pfjia.protocol.response.SessionPrepareReadParamsResponse;

import java.util.List;

/**
 * server多返回一个cmdType
 *
 * @author pfjia
 * @since 2019/1/29 11:29
 */
@Data
@ToString(callSuper = true)
public class SessionPrepareReadParams2Request extends BaseSessionPrepareRequest {
    {
        operationType = RequestOperationType.SESSION_PREPARE_READ_PARAMS2;
    }


    @Override
    public SessionPrepareReadParamsResponse parseResponse(Channel channel, ByteBuf in) {
        SessionPrepareReadParamsResponse response = super.parseResponse(channel, in);
        int cmdType = TransferKit.readInt(in);
        List<ParameterMetadata> parameterMetadataList = TransferKit.readParameterList(in);

        response.setCmdType(cmdType)
                .setParameterMetadataList(parameterMetadataList);
        return response;
    }
}
