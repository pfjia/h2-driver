package top.pfjia.protocol.request;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Data;
import lombok.ToString;
import top.pfjia.protocol.enums.RequestOperationType;
import top.pfjia.protocol.response.H2Response;

/**
 * @author pfjia
 * @since 2019/1/29 10:56
 */
@Data
@ToString(callSuper = true)
public abstract class CommandRequest<R extends H2Response> extends H2Request<R> {
    protected RequestOperationType operationType;

    @Override
    public ByteBuf toByteBuf() {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeInt(operationType.getId());
        return byteBuf;
    }
}
