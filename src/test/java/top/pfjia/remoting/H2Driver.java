package top.pfjia.remoting;

import org.junit.jupiter.api.Test;

import java.sql.*;

/**
 * @author pfjia
 * @since 2019/2/1 18:42
 */
public class H2Driver {
    private static final String JDBC_DRIVER = "top.pfjia.Driver";
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    private static final String USER = "sa";
    private static final String PASSWORD = "";


    @Test
    void statementDemo() throws Exception {
        System.setProperty("h2.check", "false");

        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

        String sql = "SELECT * FROM user WHERE id = 1 ";
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int age = resultSet.getInt("age");
            System.out.println(id + " " + name + " " + age);
        }
        resultSet.close();
        statement.close();
        connection.close();
    }

    @Test
    void prepareStatementDemo() throws Exception {
        System.setProperty("h2.check", "false");

        Class.forName(JDBC_DRIVER);
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

        String sql = "SELECT * FROM user WHERE id = ? ";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, 1);

        ResultSet resultSet = statement.executeQuery(sql);

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            int age = resultSet.getInt("age");
            System.out.println(id + " " + name + " " + age);
        }
        resultSet.close();
        statement.close();
        connection.close();
    }
}
