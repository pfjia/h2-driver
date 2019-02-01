package top.pfjia.protocol.response;

/**
 * @author pfjia
 * @since 2019/2/1 21:56
 */
public class NoneResponse extends H2Response {
    public static final NoneResponse INSTANCE = new NoneResponse();

    private NoneResponse() {
    }
}
