package top.pfjia.protocol.response;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author pfjia
 * @since 2019/1/31 9:19
 */
@Data
@ToString(callSuper = true)
public class HandshakeResponse extends H2Response {
    private int clientVersion;




}
