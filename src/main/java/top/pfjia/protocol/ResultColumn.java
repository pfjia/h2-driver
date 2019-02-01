package top.pfjia.protocol;

/*
 * Copyright 2004-2018 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */

import lombok.Data;
import org.h2.result.ResultInterface;
import org.h2.value.Transfer;

import java.io.IOException;

/**
 * A result set column of a remote result.
 */
@Data
public class ResultColumn {

    private String alias;
    private String schemaName;
    private String tableName;
    private String columnName;
    private int columnType;
    private long columnPrecision;
    private int columnScale;
    private int displaySize;
    private boolean isAutoIncrement;
    private int nullable;

    /**
     * The column alias.
     */

    /**
     * The schema name or null.
     */

    /**
     * The table name or null.
     */

    /**
     * The column name or null.
     */

    /**
     * The value type of this column.
     */

    /**
     * The precision.
     */

    /**
     * The scale.
     */

    /**
     * The expected display size.
     */

    /**
     * True if this is an autoincrement column.
     */

    /**
     * True if this column is nullable.
     */


    public ResultColumn() {
    }

    /**
     * Read an object from the given transfer object.
     *
     * @param in the object from where to read the data
     */

    /**
     * Write a result column to the given output.
     *
     * @param out    the object to where to write the data
     * @param result the result
     * @param i      the column index
     */
    public static void writeColumn(Transfer out, ResultInterface result, int i)
            throws IOException {
        out.writeString(result.getAlias(i));
        out.writeString(result.getSchemaName(i));
        out.writeString(result.getTableName(i));
        out.writeString(result.getColumnName(i));
        out.writeInt(result.getColumnType(i));
        out.writeLong(result.getColumnPrecision(i));
        out.writeInt(result.getColumnScale(i));
        out.writeInt(result.getDisplaySize(i));
        out.writeBoolean(result.isAutoIncrement(i));
        out.writeInt(result.getNullable(i));
    }

}
