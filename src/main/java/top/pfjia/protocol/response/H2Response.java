package top.pfjia.protocol.response;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.ToString;
import top.pfjia.protocol.RemotingCommand;
import top.pfjia.protocol.enums.RemotingCommandType;
import top.pfjia.protocol.enums.ResponseStatus;

/**
 * @author pfjia
 * @since 2019/1/30 16:33
 */
@Data
@ToString(callSuper = true)
public class H2Response extends RemotingCommand {
    {
        remotingCommandType = RemotingCommandType.RESPONSE;
    }

    private int status;

    // status_ok
    //status_error

    private String sqlState;
    private String message;
    private String sql;
    private int errorCode;
    private String stackTrace;

    @Override
    public ByteBuf toByteBuf() {
        throw new UnsupportedOperationException();
    }

    public ResponseStatus getEnumResponseStatus() {
        return ResponseStatus.fromInt(status);
    }


}
