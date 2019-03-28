package core;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * @author pfjia
 * @since 2019/1/27 22:14
 */
public class Main {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    private static final String USER = "";
    private static final String PASSWORD = "";

    public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException, RemotingException, MQClientException, MQBrokerException {
        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
        connection.setAutoCommit(false);

        Statement statement = connection.createStatement();
        String sql = "SELECT * FROM test_data";
        ResultSet resultSet = statement.executeQuery(sql);
        System.out.println("原有数据");

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String user = resultSet.getString("user");
            String  data = resultSet.getString("data");
            System.out.println(id + " " + user + " " + data);
        }
        String insert="insert into test_data values("+1+",'"+ LocalDateTime.now() +"','data')";
        System.out.println(insert);
        statement.execute(insert);

        connection.commit();

        resultSet.close();
        statement.close();
        connection.close();
    }
}
