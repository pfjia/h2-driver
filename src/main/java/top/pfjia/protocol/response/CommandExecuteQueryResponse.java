package top.pfjia.protocol.response;

import lombok.Data;
import lombok.ToString;
import org.h2.value.Value;
import top.pfjia.protocol.ResultColumn;

import java.util.List;

/**
 * @author pfjia
 * @since 2019/1/31 14:36
 */
@Data
@ToString(callSuper = true)
public class CommandExecuteQueryResponse extends H2Response {
    private int columnCount;
    private int rowCount;

    private List<ResultColumn> resultColumnList;

    private List<Value[]> result;
}
