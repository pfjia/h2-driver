package top.pfjia.protocol.response;

import lombok.Data;

/**
 * @author pfjia
 * @since 2019/1/31 12:12
 */
@Data
public class SessionSetIdResponse extends H2Response {
    private boolean autoCommit;

}
