package top.pfjia.remoting;

import org.h2.engine.Constants;
import top.pfjia.protocol.request.HandshakeRequest;
import top.pfjia.protocol.response.H2Response;

import java.net.InetSocketAddress;

class NettyClientTest {

    @org.junit.jupiter.api.Test
    void invokeSync() throws InterruptedException {
        String db = "~/test";
        String originalUrl = "jdbc:h2:tcp://localhost/~/test";
        String username = "sa".toUpperCase();
        String password = "";
        //发送setSessionId请求

        NettyClient nettyClient = new NettyClient();
        nettyClient.start();
        HandshakeRequest handshakeRequest = new HandshakeRequest();
        handshakeRequest.setMinClientVersion(Constants.TCP_PROTOCOL_VERSION_MIN_SUPPORTED)
                .setMaxClientVersion(Constants.TCP_PROTOCOL_VERSION_MAX_SUPPORTED)
                .setDb(db)
                .setOriginalUrl(originalUrl)
                .setUsername(username)
                .setPassword("")
                .setFilePassword(null);

        H2Response h2Response = nettyClient.invokeSync(new InetSocketAddress("localhost", 9092), handshakeRequest);
        System.out.println(h2Response);
    }
}