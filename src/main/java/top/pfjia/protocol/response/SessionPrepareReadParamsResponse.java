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


    private int cmdType;

    private List<ParameterMetadata> parameterMetadataList;
}
