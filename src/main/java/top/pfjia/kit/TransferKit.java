package top.pfjia.kit;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.h2.api.ErrorCode;
import org.h2.engine.Constants;
import org.h2.engine.Session;
import org.h2.message.DbException;
import org.h2.tools.SimpleResultSet;
import org.h2.util.DateTimeUtils;
import org.h2.util.JdbcUtils;
import org.h2.value.*;
import top.pfjia.protocol.ParameterMetadata;
import top.pfjia.protocol.ResultColumn;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author pfjia
 * @since 2019/1/31 9:23
 */
public class TransferKit {
    private static final int BUFFER_SIZE = 64 * 1024;
    private static final int LOB_MAGIC = 0x1234;
    private static final int LOB_MAC_SALT_LENGTH = 16;


    public static int readInt(ByteBuf in) {
        return in.readInt();
    }

    public static long readLong(ByteBuf in) {
        return in.readLong();
    }

    public static double readDouble(ByteBuf in) {
        return in.readDouble();
    }


    public static float readFloat(ByteBuf in) {
        return in.readFloat();
    }

    public static byte readByte(ByteBuf in) {
        return in.readByte();
    }

    public static List<ResultColumn> resultColumnList(ByteBuf in) {
        return null;
    }

    public static ResultColumn readResultColumn(ByteBuf in) {
        ResultColumn resultColumn = new ResultColumn();
        String alias = readString(in);
        String schemaName = readString(in);
        String tableName = readString(in);
        String columnName = readString(in);
        int columnType = in.readInt();
        long precision = in.readLong();
        int scale = in.readInt();
        int displaySize = in.readInt();
        boolean autoIncrement = in.readBoolean();
        int nullable = in.readInt();
        resultColumn.setAlias(alias)
                .setSchemaName(schemaName)
                .setTableName(tableName)
                .setColumnName(columnName)
                .setColumnType(columnType)
                .setColumnPrecision(precision)
                .setColumnScale(scale)
                .setDisplaySize(displaySize)
                .setAutoIncrement(autoIncrement)
                .setNullable(nullable);
        return resultColumn;

    }

    public static String readString(ByteBuf in) {
        int len = in.readInt();
        if (len == -1) {
            return null;
        }
        StringBuilder buff = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            buff.append(in.readChar());
        }
        return buff.toString();
    }


    public static byte[] readBytes(ByteBuf in) {
        int len = in.readInt();
        if (len == -1) {
            return null;
        }
        byte[] b = new byte[len];
        in.readBytes(b);
        return b;
    }


    public static List<ParameterMetadata> readParameterList(ByteBuf byteBuf) {
        List<ParameterMetadata> parameterMetadataList = new ArrayList<>();
        int parameterCount = byteBuf.readInt();
        for (int i = 0; i < parameterCount; i++) {
            ParameterMetadata parameterMetadata = readParameter(byteBuf);
            parameterMetadataList.add(parameterMetadata);
        }
        return parameterMetadataList;
    }

    public static ParameterMetadata readParameter(ByteBuf transfer) {
        ParameterMetadata p = new ParameterMetadata();
        int dataType = transfer.readInt();
        long precision = transfer.readLong();
        int scale = transfer.readInt();
        int nullable = transfer.readInt();
        p.setDataType(dataType)
                .setPrecision(precision)
                .setScale(scale)
                .setNullable(nullable);
        return p;
    }

    public static boolean readBoolean(ByteBuf in) {
        return in.readByte() == 1;
    }

    public static ByteBuf writeBoolean(boolean x) {
        ByteBuf out = Unpooled.buffer();
        out.writeByte((byte) (x ? 1 : 0));
        return out;
    }

    public static ByteBuf writeValueList(List<Value> valueList) {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(valueList.size());
        for (Value value : valueList) {
            byteBuf.writeBytes(writeValue(value));
        }
        return byteBuf;
    }

    public static List<Value[]> readValuesList(ByteBuf in, int rowCount, int columnCount) {
        List<Value[]> result = new ArrayList<>();
        for (int i = 0; i < rowCount; i++) {
            boolean row = TransferKit.readBoolean(in);
            if (!row) {
                break;
            }
            Value[] values = new Value[columnCount];
            for (int j = 0; j < columnCount; j++) {
                Value v = TransferKit.readValue(in);
                values[j] = v;
            }
            result.add(values);
        }
        return result;
    }

    public static Value readValue(ByteBuf in) {
        Session session = null;
        int version = 18;
        int type = readInt(in);
        switch (type) {
            case Value.NULL:
                return ValueNull.INSTANCE;
            case Value.BYTES:
                return ValueBytes.getNoCopy(readBytes(in));
            case Value.UUID:
                return ValueUuid.get(readLong(in), readLong(in));
            case Value.JAVA_OBJECT:
                return ValueJavaObject.getNoCopy(null, readBytes(in), session.getDataHandler());
            case Value.BOOLEAN:
                return ValueBoolean.get(readBoolean(in));
            case Value.BYTE:
                return ValueByte.get(readByte(in));
            case Value.DATE:
                if (version >= Constants.TCP_PROTOCOL_VERSION_9) {
                    return ValueDate.fromDateValue(readLong(in));
                } else {
                    return ValueDate.fromMillis(DateTimeUtils.getTimeUTCWithoutDst(readLong(in)));
                }
            case Value.TIME:
                if (version >= Constants.TCP_PROTOCOL_VERSION_9) {
                    return ValueTime.fromNanos(readLong(in));
                } else {
                    return ValueTime.fromMillis(DateTimeUtils.getTimeUTCWithoutDst(readLong(in)));
                }
            case Value.TIMESTAMP: {
                if (version >= Constants.TCP_PROTOCOL_VERSION_9) {
                    return ValueTimestamp.fromDateValueAndNanos(
                            readLong(in), readLong(in));
                } else {
                    return ValueTimestamp.fromMillisNanos(
                            DateTimeUtils.getTimeUTCWithoutDst(readLong(in)),
                            readInt(in) % 1_000_000);
                }
            }
            case Value.TIMESTAMP_TZ: {
                return ValueTimestampTimeZone.fromDateValueAndNanos(readLong(in),
                        readLong(in), (short) readInt(in));
            }
            case Value.DECIMAL:
                return ValueDecimal.get(new BigDecimal(readString(in)));
            case Value.DOUBLE:
                return ValueDouble.get(readDouble(in));
            case Value.FLOAT:
                return ValueFloat.get(readFloat(in));
            case Value.ENUM: {
                final int ordinal = readInt(in);
                final String label = readString(in);
                return ValueEnumBase.get(label, ordinal);
            }
            case Value.INT:
                return ValueInt.get(readInt(in));
            case Value.LONG:
                return ValueLong.get(readLong(in));
            case Value.SHORT:
                return ValueShort.get((short) readInt(in));
            case Value.STRING:
                return ValueString.get(readString(in));
            case Value.STRING_IGNORECASE:
                return ValueStringIgnoreCase.get(readString(in));
            case Value.STRING_FIXED:
                return ValueStringFixed.get(readString(in), ValueStringFixed.PRECISION_DO_NOT_TRIM, null);
            case Value.BLOB: {
                long length = readLong(in);
                if (version >= Constants.TCP_PROTOCOL_VERSION_11) {
                    if (length == -1) {
                        int tableId = readInt(in);
                        long id = readLong(in);
                        byte[] hmac;
                        if (version >= Constants.TCP_PROTOCOL_VERSION_12) {
                            hmac = readBytes(in);
                        } else {
                            hmac = null;
                        }
                        long precision = readLong(in);
                        return ValueLobDb.create(
                                Value.BLOB, session.getDataHandler(), tableId, id, hmac, precision);
                    }
                }
//                Value v = session.getDataHandler().getLobStorage().createBlob(in, length);
                int magic = readInt(in);
                if (magic != LOB_MAGIC) {
                    throw DbException.get(
                            ErrorCode.CONNECTION_BROKEN_1, "magic=" + magic);
                }
                return null;
//                return v;
            }
            case Value.CLOB: {
                long length = readLong(in);
                if (version >= Constants.TCP_PROTOCOL_VERSION_11) {
                    if (length == -1) {
                        int tableId = readInt(in);
                        long id = readLong(in);
                        byte[] hmac;
                        if (version >= Constants.TCP_PROTOCOL_VERSION_12) {
                            hmac = readBytes(in);
                        } else {
                            hmac = null;
                        }
                        long precision = readLong(in);
                        return ValueLobDb.create(
                                Value.CLOB, session.getDataHandler(), tableId, id, hmac, precision);
                    }
                    if (length < 0) {
                        throw DbException.get(
                                ErrorCode.CONNECTION_BROKEN_1, "length=" + length);
                    }
                }
//                Value v = session.getDataHandler().getLobStorage().
//                        createClob(new DataReader(in), length);
                int magic = readInt(in);
                if (magic != LOB_MAGIC) {
                    throw DbException.get(
                            ErrorCode.CONNECTION_BROKEN_1, "magic=" + magic);
                }
                return null;
//                return v;
            }
            case Value.ARRAY: {
                int len = readInt(in);
                Class<?> componentType = Object.class;
                if (len < 0) {
                    len = -(len + 1);
                    componentType = JdbcUtils.loadUserClass(readString(in));
                }
                Value[] list = new Value[len];
                for (int i = 0; i < len; i++) {
                    list[i] = readValue(in);
                }
                return ValueArray.get(componentType, list);
            }
            case Value.RESULT_SET: {
                SimpleResultSet rs = new SimpleResultSet();
                rs.setAutoClose(false);
                int columns = readInt(in);
                for (int i = 0; i < columns; i++) {
                    rs.addColumn(readString(in), readInt(in), readInt(in), readInt(in));
                }
                while (readBoolean(in)) {
                    Object[] o = new Object[columns];
                    for (int i = 0; i < columns; i++) {
                        o[i] = readValue(in).getObject();
                    }
                    rs.addRow(o);
                }
                return ValueResultSet.get(rs);
            }
            case Value.GEOMETRY:
                if (version >= Constants.TCP_PROTOCOL_VERSION_14) {
                    return ValueGeometry.get(readBytes(in));
                }
                return ValueGeometry.get(readString(in));
            default:
                if (JdbcUtils.customDataTypesHandler != null) {
                    return JdbcUtils.customDataTypesHandler.convert(
                            ValueBytes.getNoCopy(readBytes(in)), type);
                }
                throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "type=" + type);
        }
    }

    public static ByteBuf writeValue(Value v) {
        ByteBuf byteBuf = Unpooled.buffer();
        int version = 17;
        int type = v.getType();
        writeInt(type);
        switch (type) {
            case Value.NULL:
                break;
            case Value.BYTES:
            case Value.JAVA_OBJECT:
                writeBytes(v.getBytesNoCopy());
                break;
            case Value.UUID: {
                ValueUuid uuid = (ValueUuid) v;
                byteBuf.writeLong(uuid.getHigh());
                byteBuf.writeLong(uuid.getLow());
                break;
            }
            case Value.BOOLEAN:
                writeBoolean(v.getBoolean());
                break;
            case Value.BYTE:
                byteBuf.writeByte(v.getByte());
                break;
            case Value.TIME:
                if (version >= Constants.TCP_PROTOCOL_VERSION_9) {
                    byteBuf.writeLong(((ValueTime) v).getNanos());
                } else {
                    byteBuf.writeLong(DateTimeUtils.getTimeLocalWithoutDst(v.getTime()));
                }
                break;
            case Value.DATE:
                if (version >= Constants.TCP_PROTOCOL_VERSION_9) {
                    byteBuf.writeLong(((ValueDate) v).getDateValue());
                } else {
                    byteBuf.writeLong(DateTimeUtils.getTimeLocalWithoutDst(v.getDate()));
                }
                break;
            case Value.TIMESTAMP: {
                if (version >= Constants.TCP_PROTOCOL_VERSION_9) {
                    ValueTimestamp ts = (ValueTimestamp) v;
                    byteBuf.writeLong(ts.getDateValue());
                    byteBuf.writeLong(ts.getTimeNanos());
                } else {
                    Timestamp ts = v.getTimestamp();
                    byteBuf.writeLong(DateTimeUtils.getTimeLocalWithoutDst(ts));
                    writeInt(ts.getNanos() % 1_000_000);
                }
                break;
            }
            case Value.TIMESTAMP_TZ: {
                ValueTimestampTimeZone ts = (ValueTimestampTimeZone) v;
                byteBuf.writeLong(ts.getDateValue());
                byteBuf.writeLong(ts.getTimeNanos());
                writeInt(ts.getTimeZoneOffsetMins());
                break;
            }
            case Value.DECIMAL:
                writeString(v.getString());
                break;
            case Value.DOUBLE:
                byteBuf.writeDouble(v.getDouble());
                break;
            case Value.FLOAT:
                byteBuf.writeFloat(v.getFloat());
                break;
            case Value.INT:
                writeInt(v.getInt());
                break;
            case Value.LONG:
                byteBuf.writeLong(v.getLong());
                break;
            case Value.SHORT:
                writeInt(v.getShort());
                break;
            case Value.STRING:
            case Value.STRING_IGNORECASE:
            case Value.STRING_FIXED:
                writeString(v.getString());
                break;
            case Value.BLOB: {
                if (version >= Constants.TCP_PROTOCOL_VERSION_11) {
                    if (v instanceof ValueLobDb) {
                        ValueLobDb lob = (ValueLobDb) v;
                        if (lob.isStored()) {
                            byteBuf.writeLong(-1);
                            writeInt(lob.getTableId());
                            byteBuf.writeLong(lob.getLobId());
                            if (version >= Constants.TCP_PROTOCOL_VERSION_12) {
//                                writeBytes(calculateLobMac(lob.getLobId()));
                            }
                            byteBuf.writeLong(lob.getPrecision());
                            break;
                        }
                    }
                }
                long length = v.getPrecision();
                if (length < 0) {
                    throw DbException.get(
                            ErrorCode.CONNECTION_BROKEN_1, "length=" + length);
                }
                byteBuf.writeLong(length);
//                long written = IOUtils.copyAndCloseInput(v.getInputStream(), out);
//                if (written != length) {
//                    throw DbException.get(
//                            ErrorCode.CONNECTION_BROKEN_1, "length:" + length + " written:" + written);
//                }
                byteBuf.writeInt(LOB_MAGIC);
                break;
            }
            case Value.CLOB: {
                if (version >= Constants.TCP_PROTOCOL_VERSION_11) {
                    if (v instanceof ValueLobDb) {
                        ValueLobDb lob = (ValueLobDb) v;
                        if (lob.isStored()) {
                            byteBuf.writeLong(-1);
                            writeInt(lob.getTableId());
                            byteBuf.writeLong(lob.getLobId());
                            if (version >= Constants.TCP_PROTOCOL_VERSION_12) {
//                                writeBytes(calculateLobMac(lob.getLobId()));
                            }
                            byteBuf.writeLong(lob.getPrecision());
                            break;
                        }
                    }
                }
                long length = v.getPrecision();
                if (length < 0) {
                    throw DbException.get(
                            ErrorCode.CONNECTION_BROKEN_1, "length=" + length);
                }
                byteBuf.writeLong(length);
                Reader reader = v.getReader();
//                Data.copyString(reader, out);
                writeInt(LOB_MAGIC);
                break;
            }
            case Value.ARRAY: {
                ValueArray va = (ValueArray) v;
                Value[] list = va.getList();
                int len = list.length;
                Class<?> componentType = va.getComponentType();
                if (componentType == Object.class) {
                    writeInt(len);
                } else {
                    writeInt(-(len + 1));
                    writeString(componentType.getName());
                }
                for (Value value : list) {
                    writeValue(value);
                }
                break;
            }
            case Value.ENUM: {
                writeInt(v.getInt());
                writeString(v.getString());
                break;
            }
            case Value.RESULT_SET: {
                try {
                    ResultSet rs = ((ValueResultSet) v).getResultSet();
                    rs.beforeFirst();
                    ResultSetMetaData meta = rs.getMetaData();
                    int columnCount = meta.getColumnCount();
                    writeInt(columnCount);
                    for (int i = 0; i < columnCount; i++) {
                        writeString(meta.getColumnName(i + 1));
                        writeInt(meta.getColumnType(i + 1));
                        writeInt(meta.getPrecision(i + 1));
                        writeInt(meta.getScale(i + 1));
                    }
                    while (rs.next()) {
                        writeBoolean(true);
                        for (int i = 0; i < columnCount; i++) {
                            int t = DataType.getValueTypeFromResultSet(meta, i + 1);
//                            Value val = DataType.readValue(session, rs, i + 1, t);
//                            writeValue(val);
                        }
                    }
                    writeBoolean(false);
                    rs.beforeFirst();
                } catch (SQLException e) {
//                    throw DbException.convertToIOException(e);
                }
                break;
            }
            case Value.GEOMETRY:
                if (version >= Constants.TCP_PROTOCOL_VERSION_14) {
                    writeBytes(v.getBytesNoCopy());
                } else {
                    writeString(v.getString());
                }
                break;
            default:
                if (JdbcUtils.customDataTypesHandler != null) {
                    writeBytes(v.getBytesNoCopy());
                    break;
                }
                throw DbException.get(ErrorCode.CONNECTION_BROKEN_1, "type=" + type);
        }
        return byteBuf;
    }

    public static ByteBuf writeInt(int i) {
        return Unpooled.buffer().writeInt(i);
    }

    public static ByteBuf writeMap(Map<String, String> map) {
        ByteBuf byteBuffer = Unpooled.buffer();
        if (map == null) {
            byteBuffer.writeInt(0);
        } else {
            byteBuffer.writeInt(map.size());
            for (Map.Entry<String, String> entry : map.entrySet()) {
                byteBuffer.writeBytes(TransferKit.writeString(entry.getKey()));
                byteBuffer.writeBytes(TransferKit.writeString(entry.getValue()));
            }
        }
        return byteBuffer;
    }

    public static ByteBuf writeString(String s) {
        ByteBuf out = Unpooled.buffer();
        if (s == null) {
            out.writeInt(-1);
        } else {
            int len = s.length();
            out.writeInt(len);
            for (int i = 0; i < len; i++) {
                out.writeChar(s.charAt(i));
            }
        }
        return out;
    }

    public static ByteBuf writeBytes(byte[] bytes) {
        ByteBuf result = Unpooled.buffer();
        if (bytes == null) {
            result.writeInt(-1);
        } else {
            result.writeInt(bytes.length);
            result.writeBytes(bytes);
        }
        return result;
    }
}
