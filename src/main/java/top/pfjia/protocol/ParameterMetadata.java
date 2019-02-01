package top.pfjia.protocol;

import lombok.Data;

/**
 * @author pfjia
 * @since 2019/1/31 13:59
 */
@Data
public class ParameterMetadata {
    private int dataType;
    private long precision;
    private int scale;
    /**
     * {@link java.sql.ResultSetMetaData#columnNoNulls}
     */
    private int nullable;
}
