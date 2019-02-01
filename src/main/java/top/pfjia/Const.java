package top.pfjia;

import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Promise;
import top.pfjia.jdbc.JdbcConnection;
import top.pfjia.protocol.ChannelState;
import top.pfjia.protocol.request.H2Request;
import top.pfjia.protocol.response.H2Response;

import java.sql.Connection;

/**
 * @author pfjia
 * @since 2019/1/29 15:41
 */
public class Const {
    /**
     * channel所处状态,如未握手,已握手等
     */
    public static final AttributeKey<ChannelState> CHANNEL_STATE_ATTRIBUTE_KEY = AttributeKey.valueOf("CHANNEL_STATE");

    /**
     * channel最新已发送的request
     */
    public static final AttributeKey<H2Request> REQUEST_TO_BE_RESPONDED_ATTRIBUTE_KEY = AttributeKey.valueOf("REQUEST_TO_BE_RESPONDED");

    public static final AttributeKey<Promise<H2Response>> RESPONSE_FUTURE_ATTRIBUTE_KEY = AttributeKey.valueOf("RESPONSE_FUTURE");

    public static final AttributeKey<JdbcConnection> CONNECTION_ATTRIBUTE_KEY = AttributeKey.valueOf("CONNECTION");

}
