package top.pfjia.protocol.response;

import lombok.Data;
import lombok.ToString;

/**
 * @author pfjia
 * @since 2019/2/2 18:55
 */
@Data
@ToString(callSuper = true)
public class SessionHasPendingTransactionResponse extends H2Response {
    private boolean hasTransaction;
}
