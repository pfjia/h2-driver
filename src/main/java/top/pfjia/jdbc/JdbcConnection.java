package top.pfjia.jdbc;

import io.netty.channel.Channel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.h2.api.ErrorCode;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Constants;
import org.h2.jdbc.JdbcSQLException;
import org.h2.message.DbException;
import org.h2.message.TraceObject;
import top.pfjia.Const;
import top.pfjia.kit.IdGeneratorKit;
import top.pfjia.protocol.enums.IdType;
import top.pfjia.protocol.enums.ResponseStatus;
import top.pfjia.protocol.request.H2Request;
import top.pfjia.protocol.request.HandshakeRequest;
import top.pfjia.protocol.request.SessionCloseRequest;
import top.pfjia.protocol.request.SessionSetIdRequest;
import top.pfjia.protocol.response.H2Response;
import top.pfjia.protocol.response.HandshakeResponse;
import top.pfjia.protocol.response.SessionCloseResponse;
import top.pfjia.protocol.response.SessionSetIdResponse;
import top.pfjia.remoting.NettyClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.Executor;


/**
 * @author pfjia
 * @since 2019/1/28 13:16
 */
@Slf4j
public class JdbcConnection extends TraceObject implements Connection {
    @Getter
    private NettyClient nettyClient;
    private ConnectionInfo connectionInfo;
    @Getter
    private SocketAddress socketAddress;
    private String databaseName;
    @Getter
    private int clientVersion;
    private String sessionId = UUID.randomUUID().toString();

    private boolean autoCommit;

    public JdbcConnection(NettyClient nettyClient, ConnectionInfo ci) {
        this.nettyClient = nettyClient;
        this.connectionInfo = ci;
        parse();
    }

    public <T extends H2Response> T invokeSync(SocketAddress socketAddress, H2Request<T> h2Request) {
        return nettyClient.invokeSync(socketAddress, h2Request);
    }

    public void handshake() throws IOException {
        Channel channel = nettyClient.getOrCreateChannel(socketAddress);
        channel.attr(Const.CONNECTION_ATTRIBUTE_KEY).set(this);
        HandshakeRequest handshakeRequest = new HandshakeRequest();
        handshakeRequest.setMinClientVersion(Constants.TCP_PROTOCOL_VERSION_MIN_SUPPORTED)
                .setMaxClientVersion(Constants.TCP_PROTOCOL_VERSION_MAX_SUPPORTED)
                .setDb(databaseName)
                .setOriginalUrl(connectionInfo.getOriginalURL())
                .setUsername(connectionInfo.getUserName())
                .setPassword(connectionInfo.getProperty("password", ""))
                .setFilePassword(null)
        ;
        HandshakeResponse h2Response = nettyClient.invokeSync(socketAddress, handshakeRequest);
        ResponseStatus responseStatus = h2Response.getEnumResponseStatus();
        switch (responseStatus) {
            case STATUS_OK:
                this.clientVersion = h2Response.getClientVersion();
                break;
            case STATUS_ERROR:
                String sqlstate = h2Response.getSqlState();
                String message = h2Response.getMessage();
                String sql = h2Response.getSql();
                int errorCode = h2Response.getErrorCode();
                String stackTrace = h2Response.getStackTrace();
                JdbcSQLException s = new JdbcSQLException(message, sql, sqlstate,
                        errorCode, null, stackTrace);
                if (errorCode == ErrorCode.CONNECTION_BROKEN_1) {
                    // allow re-connect
                    throw new IOException(s.toString(), s);
                }
                throw DbException.convert(s);
            default:
                log.error("default");
        }
    }

    private void parse() {
        String name = connectionInfo.getName();
        if (name.startsWith("//")) {
            name = name.substring("//".length());
        }
        int idx = name.indexOf('/');
        if (idx < 0) {
            throw new RuntimeException("index error");
        }
        databaseName = name.substring(idx + 1);
        String server = name.substring(0, idx);
        socketAddress = new InetSocketAddress(server, Constants.DEFAULT_TCP_PORT);
    }

    public void setSessionId() {
        SessionSetIdRequest sessionSetIdRequest = new SessionSetIdRequest();
        sessionSetIdRequest.setSessionId(sessionId);

        SessionSetIdResponse sessionSetIdResponse = nettyClient.invokeSync(socketAddress, sessionSetIdRequest);
        ResponseStatus responseStatus = sessionSetIdResponse.getEnumResponseStatus();
        switch (responseStatus) {
            case STATUS_OK:
                autoCommit = sessionSetIdResponse.isAutoCommit();
                break;
            default:
                log.error("default");
        }
    }


    public void setConnectionInfo(ConnectionInfo connectionInfo) {
        this.connectionInfo = connectionInfo;
    }

    public void setNettyClient(NettyClient nettyClient) {
        this.nettyClient = nettyClient;
    }

    @Override
    public Statement createStatement() throws SQLException {
        int id = IdGeneratorKit.getNextId(IdType.STATEMENT);
        return new JdbcStatement(id, this, ResultSet.TYPE_FORWARD_ONLY, 1007, false);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        int id = IdGeneratorKit.getNextId(IdType.PREPARED_STATEMENT);
        return new JdbcPreparedStatement(id, this, ResultSet.TYPE_FORWARD_ONLY, 1007, false);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        return null;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {

    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return false;
    }

    @Override
    public void commit() throws SQLException {

    }

    @Override
    public void rollback() throws SQLException {

    }

    @Override
    public void close() throws SQLException {
        SessionCloseRequest sessionCloseRequest = new SessionCloseRequest();
        SessionCloseResponse sessionCloseResponse = invokeSync(sessionCloseRequest);
        ResponseStatus responseStatus = sessionCloseResponse.getEnumResponseStatus();
        switch (responseStatus) {
            case STATUS_OK:
                nettyClient.disConnect(socketAddress);
                break;
            case STATUS_ERROR:
                break;
            default:
                break;
        }
    }

    private <T extends H2Response> T invokeSync(H2Request<T> request) {
        return nettyClient.invokeSync(socketAddress, request);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {

    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {

    }

    @Override
    public String getCatalog() throws SQLException {
        return null;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {

    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return 0;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    @Override
    public void setHoldability(int holdability) throws SQLException {

    }

    @Override
    public int getHoldability() throws SQLException {
        return 0;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {

    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {

    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        return null;
    }

    @Override
    public Clob createClob() throws SQLException {
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return null;
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {

    }

    @Override
    public String getSchema() throws SQLException {
        return null;
    }

    @Override
    public void abort(Executor executor) throws SQLException {

    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {

    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }
}
