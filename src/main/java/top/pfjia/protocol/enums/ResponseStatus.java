package top.pfjia.protocol.enums;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pfjia
 * @since 2019/1/28 23:20
 */
public enum ResponseStatus {
    STATUS_ERROR(0),
    STATUS_OK(1),
    STATUS_CLOSED(2),
    STATUS_OK_STATE_CHANGED(3);
    @Getter
    private final int status;

    private static final Map<Integer, ResponseStatus> STATUS_MAP = new HashMap<>();

    static {
        for (ResponseStatus value : values()) {
            STATUS_MAP.put(value.getStatus(), value);
        }
    }

    public static ResponseStatus fromInt(int i) {
        return STATUS_MAP.get(i);
    }

    ResponseStatus(int status) {
        this.status = status;
    }
}
