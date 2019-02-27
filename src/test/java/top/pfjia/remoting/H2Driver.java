package top.pfjia.remoting;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.*;
import java.util.Arrays;

/**
 * @author pfjia
 * @since 2019/2/1 18:42
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class H2Driver {
    private static final String JDBC_DRIVER = "top.pfjia.Driver";
    private static final String DB_URL = "jdbc:h2:tcp://localhost/~/test";
    private static final String USER = "sa";
    private static final String PASSWORD = "";


    @BeforeAll
    void beforeAll() throws Exception {
        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

        String createTableSql = "drop table if exists user;\n" +
                "create table user\n" +
                "(\n" +
                "id int(11) not null auto_increment primary key,\n" +
                "name varchar(255),\n" +
                "age int(11)\n" +
                ");";

        Statement statement = connection.createStatement();
        int num = statement.executeUpdate(createTableSql);


        String insertSql = "INSERT INTO user (name,age) values(?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
        int times = 1000_000;
        for (int i = 0; i < times; i++) {
            preparedStatement.setString(1, "name " + i);
            preparedStatement.setInt(2, i);
            preparedStatement.addBatch();
        }
        int[] result = preparedStatement.executeBatch();
        Assertions.assertAll(Arrays.stream(result)
                .mapToObj(value -> () -> Assertions.assertEquals(1, value)));
    }

    @Test
    void statementDemo() throws Exception {

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

        Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);

        String sql = "SELECT * FROM user WHERE id = ? ";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, 1);

        ResultSet resultSet = statement.executeQuery();

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
