package top.pfjia.protocol.response;

import lombok.Data;
import lombok.ToString;
import org.h2.value.Value;

import java.util.List;

/**
 * @author pfjia
 * @since 2019/2/2 17:59
 */
@Data
@ToString(callSuper = true)
public class ResultFetchRowsResponse extends H2Response {
    private List<Value[]> valueList;
}
