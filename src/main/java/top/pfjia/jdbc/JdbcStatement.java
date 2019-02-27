package top.pfjia.jdbc;

import org.h2.api.ErrorCode;
import org.h2.jdbc.JdbcSQLException;
import org.h2.message.DbException;
import org.h2.value.Value;
import top.pfjia.kit.IdGeneratorKit;
import top.pfjia.protocol.enums.IdType;
import top.pfjia.protocol.enums.ResponseStatus;
import top.pfjia.protocol.request.*;
import top.pfjia.protocol.response.CommandExecuteQueryResponse;
import top.pfjia.protocol.response.H2Response;
import top.pfjia.protocol.response.SessionPrepareReadParamsResponse;
import top.pfjia.remoting.NettyClient;

import java.net.SocketAddress;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author pfjia
 * @since 2019/1/31 21:39
 */
public class JdbcStatement implements Statement {
    private JdbcConnection jdbcConnection;
    private int resultSetType;
    private int resultSetConcurrency;
    private boolean closedByResultSet;
    private int fetchSize = 100;
    private int id;

    public NettyClient nettyClient() {
        return jdbcConnection.getNettyClient();
    }

    public SocketAddress socketAddress() {
        return jdbcConnection.getSocketAddress();
    }


    public JdbcStatement(int id, JdbcConnection jdbcConnection, int resultSetType, int resultSetConcurrency, boolean closedByResultSet) {
        this.id = id;
        this.jdbcConnection = jdbcConnection;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.closedByResultSet = closedByResultSet;
    }


    public <T extends H2Response> T invokeSync(H2Request<T> h2Request) {
        return nettyClient().invokeSync(socketAddress(), h2Request);
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        prepareParam(sql);
//        response


        int resultSetId = IdGeneratorKit.getNextId(IdType.RESULT_SET);

        CommandExecuteQueryRequest r2 = new CommandExecuteQueryRequest();
        List<Value> valueList = new ArrayList<>();
        r2.setStatementId(id)
                .setFetchSize(Integer.MAX_VALUE)
                .setMaxRows(Integer.MAX_VALUE)
                .setResultSetId(resultSetId)
                .setValueList(valueList);
        CommandExecuteQueryResponse response1 = nettyClient().invokeSync(socketAddress(), r2);

        checkResponse(response1);
        return new JdbcResultSet(resultSetId, response1, this);
    }

    private void prepareParam(String sql) {
        boolean v16 = jdbcConnection.getClientVersion() >= 16;
        BaseSessionPrepareRequest request;
        if (v16) {
            request = new SessionPrepareReadParams2Request();
        } else {
            request = new SessionPrepareReadParamsRequest();
        }
        request.setSqlId(id)
                .setSql(sql);
        SessionPrepareReadParamsResponse response = invokeSync(request);
        checkResponse(response);
    }

    private void checkResponse(H2Response response) {
        ResponseStatus responseStatus = response.getEnumResponseStatus();
        if (responseStatus == ResponseStatus.STATUS_ERROR) {
            int errorCode = response.getErrorCode();
            JdbcSQLException s = new JdbcSQLException(response.getMessage(), response.getSql(), response.getSqlState(),
                    errorCode, null, response.getStackTrace());
            if (errorCode == ErrorCode.CONNECTION_BROKEN_1) {
                // allow re-connect
//                throw new IOException(s.toString(), s);
            }
            throw DbException.convert(s);
        }

    }


    @Override
    public int executeUpdate(String sql) throws SQLException {
        prepareParam(sql);
//        response
        // TODO: 2019/2/3  


        CommandExecuteUpdateRequest request = new CommandExecuteUpdateRequest();

        int resultSetId = IdGeneratorKit.getNextId(IdType.RESULT_SET);

        CommandExecuteQueryRequest r2 = new CommandExecuteQueryRequest();
        List<Value> valueList = new ArrayList<>();
        r2.setStatementId(id)
                .setFetchSize(Integer.MAX_VALUE)
                .setMaxRows(Integer.MAX_VALUE)
                .setResultSetId(resultSetId)
                .setValueList(valueList);
        CommandExecuteQueryResponse response1 = nettyClient().invokeSync(socketAddress(), r2);

        checkResponse(response1);
        return 0;
    }

    @Override
    public void close() throws SQLException {
        CommandCloseRequest commandCloseRequest = new CommandCloseRequest();
        commandCloseRequest.setStatementId(id);
        invokeSync(commandCloseRequest);
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {

    }

    @Override
    public int getMaxRows() throws SQLException {
        return 0;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {

    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {

    }

    @Override
    public int getQueryTimeout() throws SQLException {
        return 0;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {

    }

    @Override
    public void cancel() throws SQLException {

    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {

    }

    @Override
    public void setCursorName(String name) throws SQLException {

    }

    @Override
    public boolean execute(String sql) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        return null;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {

    }

    @Override
    public int getFetchDirection() throws SQLException {
        return 0;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {

    }

    @Override
    public int getFetchSize() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        return 0;
    }

    @Override
    public void addBatch(String sql) throws SQLException {

    }

    @Override
    public void clearBatch() throws SQLException {

    }

    @Override
    public int[] executeBatch() throws SQLException {
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        return null;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {

    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
    }

    @Override
    public void closeOnCompletion() throws SQLException {

    }

    @Override
    public boolean isCloseOnCompletion() throws SQLException {
        return false;
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
