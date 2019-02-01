package top.pfjia.protocol;

/**
 * 连接状态，用于处理response
 *
 * @author pfjia
 * @since 2019/1/29 10:39
 */
public enum ChannelState {

    /**
     * 未握手
     */
    BEFORE_HANDSHAKE,

    /**
     * 已握手但是未设置sessionId
     */
    AFTER_HANDSHAKE,

    /**
     * 已设置sessionId
     */
    AFTER_SET_SESSION_ID
}
