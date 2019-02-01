package top.pfjia.protocol.enums;

import lombok.Getter;

/**
 * 请求命令类型
 *
 * @author pfjia
 * @since 2019/1/29 10:57
 */
public enum RequestCommandType {
    // 握手阶段(此阶段并没有对应的commandId)

    // sessionId设置
    SESSION_SET_ID(12),
    // prepare 阶段
    SESSION_PREPARE_READ_PARAMS(11),
    SESSION_PREPARE(0),

    //update
    COMMAND_EXECUTE_UPDATE(3),


    //query
    COMMAND_EXECUTE_QUERY(2),


    //其他命令
    COMMAND_GET_META_DATA(10),
    COMMAND_CLOSE(4, false),
    RESULT_FETCH_ROWS(5),
    RESULT_CLOSE(7, false),
    RESULT_RESET(6, false),
    COMMAND_COMMIT(8),
    CHANGE_ID(9, false),
    SESSION_SET_AUTOCOMMIT(15),
    SESSION_UNDO_LOG_POS(16),

    SESSION_CLOSE(1),
    SESSION_CANCEL_STATEMENT(13),
    SESSION_CHECK_KEY(14),
    LOB_READ(17),
    SESSION_PREPARE_READ_PARAMS2(18);


    @Getter
    private final int id;
    @Getter
    private boolean needResponse = true;

    RequestCommandType(int id, boolean needResponse) {
        this.id = id;
        this.needResponse = needResponse;
    }

    RequestCommandType(int id) {
        this.id = id;
    }
}
