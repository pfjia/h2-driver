package top.pfjia.protocol.response;

import lombok.Data;
import lombok.ToString;
import top.pfjia.protocol.ParameterMetadata;

import java.util.List;

/**
 * @author pfjia
 * @since 2019/1/31 13:52
 */
@Data
@ToString(callSuper = true)
public class SessionPrepareReadParamsResponse extends H2Response {
    /**
     * 请求处理的命令是否是query
     */
    private boolean isQuery;

    private boolean readonly;


    /**
     * {@link top.pfjia.protocol.request.SessionPrepareReadParams2Request}返回此参数
     */
    private int cmdType;

    /**
     * {@link top.pfjia.protocol.request.SessionPrepareReadParams2Request}和{@link top.pfjia.protocol.request.SessionPrepareReadParamsRequest}返此参数
     */
    private List<ParameterMetadata> parameterMetadataList;
}
