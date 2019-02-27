package top.pfjia.protocol.request;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.util.internal.TypeParameterMatcher;
import lombok.extern.slf4j.Slf4j;
import top.pfjia.kit.TransferKit;
import top.pfjia.protocol.RemotingCommand;
import top.pfjia.protocol.enums.RemotingCommandType;
import top.pfjia.protocol.enums.ResponseStatus;
import top.pfjia.protocol.response.H2Response;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author pfjia
 * @since 2019/1/31 10:33
 */
@Slf4j
public abstract class H2Request<R extends H2Response> extends RemotingCommand {
    private static final String TYPE_PARAM_NAME = "R";

    {
        remotingCommandType = RemotingCommandType.REQUEST;
    }


    public boolean needResponse() {
        if (this instanceof CommandRequest){
            return ((CommandRequest<R>) this).operationType.isNeedResponse();
        }
        throw new RuntimeException();
    }

    /**
     * @return 子类的泛型参数类
     */
    private Class<? extends H2Response> getParameterizedClass() {
        TypeParameterMatcher matcher = TypeParameterMatcher.find(this, H2Request.class, TYPE_PARAM_NAME);
        try {
            Field field = matcher.getClass().getDeclaredField("type");
            field.setAccessible(true);
            Class<?> aClass = (Class<?>) field.get(matcher);
            @SuppressWarnings("unchecked")
            Class<? extends H2Response> cast = (Class<? extends H2Response>) aClass;
            return cast;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析response status,若为status_error,解析error信息
     *
     * @param channel channel
     * @param in      in
     * @return request泛型对应的类的实例
     */
    public R parseResponse(Channel channel, ByteBuf in) {
        Class<? extends H2Response> aClass = getParameterizedClass();
        try {
            @SuppressWarnings("unchecked")
            R h2Response = (R) aClass.getDeclaredConstructor().newInstance();
            int status = in.readInt();
            h2Response.setStatus(status);
            ResponseStatus responseStatus = h2Response.getEnumResponseStatus();
            switch (responseStatus) {
                case STATUS_OK:
                    //do nothing
                    break;
                case STATUS_ERROR:
                    String sqlstate = TransferKit.readString(in);
                    String message = TransferKit.readString(in);
                    String sql = TransferKit.readString(in);
                    int errorCode = TransferKit.readInt(in);
                    String stackTrace = TransferKit.readString(in);
                    h2Response.setSqlState(sqlstate)
                            .setMessage(message)
                            .setSql(sql)
                            .setErrorCode(errorCode)
                            .setStackTrace(stackTrace);
                    log.error("STATUS_ERROR");
                    break;
                case STATUS_OK_STATE_CHANGED:
                    log.error("STATUS_OK_STATE_CHANGED");
                    break;
                case STATUS_CLOSED:
                    log.error("STATUS_CLOSED");
                    break;
                default:
                    log.error("无法解析的responseStatus");
            }
            return h2Response;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            log.error("", e);
        }
        throw new RuntimeException("parseResponse失败");
    }
}
