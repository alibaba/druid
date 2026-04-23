package com.alibaba.druid.pool;

import com.alibaba.druid.util.JdbcUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.ResultSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DruidPooledStatementTest {

    private static DruidDataSource dataSource;

    @BeforeClass
    public static void init() throws Exception {

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:h2:mem:test");

        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        JdbcUtils.execute(dataSource,
                "CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(100), age INT);");
        JdbcUtils.execute(dataSource, "INSERT INTO users(id, name, age) VALUES (1, 'test1', 10);");
        JdbcUtils.execute(dataSource, "INSERT INTO users(id, name, age) VALUES (2, 'test2', 20);");
    }

    @AfterClass
    public static void destroy() throws Exception {
        JdbcUtils.execute(dataSource, "DROP TABLE users;");
        dataSource.close();
    }

    @Test
    public void test_resultSetTrace() throws Exception {
        DruidPooledConnection connection = dataSource.getConnection();
        DruidPooledStatement statement = (DruidPooledStatement) connection.createStatement();
        ResultSet lastResultSet = null;
        ResultSet nowResultSet = statement.executeQuery("SELECT * FROM users;");
        for (int i = 0; i < 100; i++) {
            lastResultSet = nowResultSet;
            nowResultSet = statement.executeQuery("SELECT * FROM users;");
        }
        assertTrue(lastResultSet.isClosed());
        assertFalse(nowResultSet.isClosed());
        assertEquals(1, statement.resultSetTrace.size());
    }

}
