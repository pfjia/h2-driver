package top.pfjia.protocol.response;

import lombok.Data;
import lombok.ToString;
import top.pfjia.protocol.ResultColumn;

import java.util.List;

/**
 * @author pfjia
 * @since 2019/2/2 17:47
 */
@Data
@ToString(callSuper = true)
public class CommandGetMetadataResponse extends H2Response {
    private int columnCount;
    private int rowCount;
    private List<ResultColumn> resultColumnList;

}
