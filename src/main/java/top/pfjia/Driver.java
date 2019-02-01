package top.pfjia;

import org.h2.engine.ConnectionInfo;
import org.h2.message.DbException;
import top.pfjia.jdbc.JdbcConnection;
import top.pfjia.remoting.NettyClient;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author pfjia
 * @since 2019/1/28 11:54
 */
public class Driver implements java.sql.Driver {

    private static volatile boolean registered;
    private static final Driver INSTANCE = new Driver();

    private final NettyClient nettyClient;

    public Driver() {
        nettyClient = new NettyClient();
        nettyClient.start();
    }

    static {
        load();
    }


    /**
     * INTERNAL
     */
    public static synchronized Driver load() {
        try {
            if (!registered) {
                registered = true;
                DriverManager.registerDriver(INSTANCE);
            }
        } catch (SQLException e) {
            DbException.traceThrowable(e);
        }
        return INSTANCE;
    }

    /**
     * INTERNAL
     */
    public static synchronized void unload() {
        try {
            if (registered) {
                registered = false;
                DriverManager.deregisterDriver(INSTANCE);
            }
        } catch (SQLException e) {
            DbException.traceThrowable(e);
        }
    }

    /**
     * @param url
     * @param info 数据库连接信息，包括user和password
     * @return
     * @throws SQLException
     */
    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        ConnectionInfo connectionInfo = new ConnectionInfo(url, info);
        JdbcConnection jdbcConnection = new JdbcConnection(nettyClient, connectionInfo);
        try {
            jdbcConnection.handshake();
        } catch (IOException e) {
            throw new SQLException("连接失败", e);
        }
        jdbcConnection.setSessionId();
        return jdbcConnection;
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return false;
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        return new DriverPropertyInfo[0];
    }

    @Override
    public int getMajorVersion() {
        return 0;
    }

    @Override
    public int getMinorVersion() {
        return 0;
    }

    @Override
    public boolean jdbcCompliant() {
        return false;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return null;
    }

    public boolean closeNetty() {
        nettyClient.close();
        return true;
    }
}
