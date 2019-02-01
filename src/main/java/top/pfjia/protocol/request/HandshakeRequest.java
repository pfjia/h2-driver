package top.pfjia.protocol.request;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.h2.security.SHA256;
import top.pfjia.kit.TransferKit;
import top.pfjia.protocol.enums.ResponseStatus;
import top.pfjia.protocol.response.HandshakeResponse;

import java.util.Map;

/**
 * @author pfjia
 * @since 2019/1/28 22:02
 */
@Slf4j
@Data
public class HandshakeRequest extends H2Request<HandshakeResponse> {
    /**
     * minClientVersion和maxClientVersion用来告诉server端当前client能支持的最小和最大协议版本是多少，
     * 根据这两个参数，server端会选择一个合适的协议版本与client通信，
     */
    private int minClientVersion;
    private int maxClientVersion;
    private String db;
    private String originalUrl;
    private String username;
    private String password;
    private String filePassword;
    private byte[] userPasswordHash;
    private byte[] filePasswordHash;
    private Map<String, String> map;


    @Override
    public boolean needResponse() {
        return true;
    }

    public HandshakeRequest setPassword(String password) {
        if (password == null) {
            this.password = null;
            this.userPasswordHash = null;
        } else {
            this.password = password;
            this.userPasswordHash = SHA256.getKeyPasswordHash(username.toUpperCase(), password.toCharArray());
        }
        return this;
    }

    public HandshakeRequest setFilePassword(String filePassword) {
        if (filePassword == null) {
            this.filePassword = null;
            this.filePasswordHash = null;
        } else {
            this.filePassword = filePassword;
            this.filePasswordHash = SHA256.getKeyPasswordHash("file".toUpperCase(), filePassword.toCharArray());
        }
        return this;
    }

    @Override
    public HandshakeResponse parseResponse(Channel channel, ByteBuf in) {
        HandshakeResponse handshakeResponse = super.parseResponse(channel, in);
        ResponseStatus responseStatus = handshakeResponse.getEnumResponseStatus();
        if (responseStatus == null) {
            throw new RuntimeException("无法解析的response");
        }
        switch (responseStatus) {
            case STATUS_OK:
                int clientVersion = TransferKit.readInt(in);
                handshakeResponse.setClientVersion(clientVersion);
                break;
            case STATUS_ERROR:
                String sqlStateValue = TransferKit.readString(in);
                String message = TransferKit.readString(in);
                String sql = TransferKit.readString(in);
                int errorCode = TransferKit.readInt(in);
                String trace = TransferKit.readString(in);

                handshakeResponse.setSqlState(sqlStateValue)
                        .setMessage(message)
                        .setSql(sql)
                        .setErrorCode(errorCode)
                        .setStackTrace(trace);
                break;
            case STATUS_CLOSED:
                log.error("不支持的status");
                break;
            case STATUS_OK_STATE_CHANGED:
                log.error("不支持的status");
                break;
            default:
                break;
        }
        return handshakeResponse;
    }

    @Override
    public ByteBuf toByteBuf() {
        ByteBuf byteBuffer = Unpooled.buffer();
        byteBuffer.writeInt(minClientVersion);
        byteBuffer.writeInt(maxClientVersion);
        byteBuffer.writeBytes(TransferKit.writeString(db));
        byteBuffer.writeBytes(TransferKit.writeString(originalUrl));
        byteBuffer.writeBytes(TransferKit.writeString(username));
        byteBuffer.writeBytes(TransferKit.writeBytes(userPasswordHash));
        byteBuffer.writeBytes(TransferKit.writeBytes(filePasswordHash));
        byteBuffer.writeBytes(TransferKit.writeMap(map));
        return byteBuffer;
    }
}
