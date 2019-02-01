package top.pfjia.protocol;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import top.pfjia.protocol.enums.RemotingCommandType;

/**
 * request和response的父类
 *
 * @author pfjia
 * @since 2019/1/28 17:17
 */
@Data
@Slf4j
public abstract class RemotingCommand {
    protected RemotingCommandType remotingCommandType;


    public abstract ByteBuf toByteBuf();
}
