package top.pfjia.protocol.enums;

import lombok.Getter;

/**
 * @author pfjia
 * @since 2019/2/1 20:20
 */
public enum IdType {
    CALLABLE_STATEMENT(0),
    CONNECTION(1),
    DATABASE_META_DATA(2),
    PREPARED_STATEMENT(3),
    RESULT_SET(4),
    RESULT_SET_META_DATA(5),
    SAVEPOINT(6),
    STATEMENT(8),
    BLOB(9),
    CLOB(10),
    PARAMETER_META_DATA(11),
    DATA_SOURCE(12),
    XA_DATA_SOURCE(13),
    XID(15),
    ARRAY(16);
    @Getter
    private final int id;

    IdType(int id) {
        this.id = id;
    }
}
