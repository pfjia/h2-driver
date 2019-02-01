package top.pfjia.protocol.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.ToString;
import top.pfjia.Const;
import top.pfjia.kit.TransferKit;
import top.pfjia.jdbc.JdbcConnection;
import top.pfjia.protocol.enums.SqlCmdType;
import top.pfjia.protocol.ParameterMetadata;
import top.pfjia.protocol.response.SessionPrepareReadParamsResponse;

import java.util.List;

/**
 * @author pfjia
 * @since 2019/1/31 22:28
 */
@Data
@ToString(callSuper = true)
public class BaseSessionPrepareReadParamsRequest extends CommandRequest<SessionPrepareReadParamsResponse> {
    protected int sqlId;
    protected String sql;

    @Override
    public ByteBuf toByteBuf() {
        ByteBuf byteBuf = super.toByteBuf();
        byteBuf.writeBytes(TransferKit.writeInt(sqlId));
        byteBuf.writeBytes(TransferKit.writeString(sql));
        return byteBuf;
    }

    @Override
    public SessionPrepareReadParamsResponse parseResponse(Channel channel, ByteBuf in) {
        SessionPrepareReadParamsResponse response = super.parseResponse(channel, in);
        boolean isQuery = TransferKit.readBoolean(in);
        boolean readonly = TransferKit.readBoolean(in);

        JdbcConnection jdbcConnection = channel.attr(Const.CONNECTION_ATTRIBUTE_KEY).get();
        boolean v16 = jdbcConnection.getClientVersion() >= 16;
        if (v16) {
            int cmdType = TransferKit.readInt(in);
            response.setCmdType(cmdType);
        } else {
            response.setCmdType(SqlCmdType.UNKNOWN.getId());
        }

        List<ParameterMetadata> parameterMetadataList = TransferKit.readParameterList(in);

        response.setQuery(isQuery)
                .setReadonly(readonly)
                .setParameterMetadataList(parameterMetadataList);
        return response;
    }
}
