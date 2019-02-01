package top.pfjia;

import org.h2.engine.ConnectionInfo;
import org.h2.engine.Constants;
import org.h2.engine.SessionRemote;
import org.h2.security.SHA256;
import org.h2.value.Transfer;
import top.pfjia.remoting.NettyClient;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * @author pfjia
 * @since 2019/1/29 15:08
 */
public class SocketDemo {
    public static void main(String[] args) throws Exception {
        NettyClient nettyClient = new NettyClient();
        nettyClient.start();
        SocketAddress socketAddress = new InetSocketAddress("localhost", 9092);
//        nettyClient.invokeSync(socketAddress,);

        ConnectionInfo ci = new ConnectionInfo("tcp://localhost/~/test");
        SessionRemote sessionRemote = new SessionRemote(ci);
        Socket socket = new Socket("localhost", 9092);
        Transfer trans = new Transfer(sessionRemote, socket);

        String db = "~/test";
        String originalUrl="jdbc:h2:tcp://localhost/~/test";
        String username="sa".toUpperCase();
        String password="";
        byte[]userPasswordHash= SHA256.getKeyPasswordHash(username,password.toCharArray());
        trans.init();
        //发送握手请求
        trans.writeInt(Constants.TCP_PROTOCOL_VERSION_MIN_SUPPORTED);
        trans.writeInt(Constants.TCP_PROTOCOL_VERSION_MAX_SUPPORTED);
        trans.writeString(db);
        trans.writeString(originalUrl);
        trans.writeString(username);
        trans.writeBytes(userPasswordHash);
        trans.writeBytes(null);
        trans.writeInt(0);
        trans.flush();
        //发送setSessionId请求
        trans.writeInt(SessionRemote.SESSION_SET_ID).writeString("sessionId");
        trans.flush();



        int status = trans.readInt();
        System.out.println(status);
        int clientVersion=trans.readInt();
        System.out.println(clientVersion);
        int status2=trans.readInt();
        System.out.println(status2);
        boolean autoCommit = trans.readBoolean();
        System.out.println(autoCommit);
    }
}
