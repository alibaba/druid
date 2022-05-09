package com.alibaba.druid.sql.dialect.h2.visitor;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import org.junit.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class H2OutputVisitorTest {

    @BeforeClass
    public static void loadDriver() {
        org.h2.Driver.load();
    }

    @AfterClass
    public static void unloadDriver() {
        org.h2.Driver.unload();
    }

    private Connection connection;

    @Before
    public void initConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:test;MODE=MySQL");
    }

    @After
    public void closeConnection() throws SQLException {
        connection.close();
    }

    private void executeUpdate(String sql) throws SQLException {
        try (Statement sqlStat = connection.createStatement()) {
            sqlStat.executeUpdate(sql);
        }
    }

    @Test
    public void testConvertCreateIndex() throws SQLException {
        String mysqlSql = "CREATE SCHEMA hinex;CREATE TABLE hinex.employees (jobTitle VARCHAR2(50));CREATE FULLTEXT INDEX hinex.jobTitle USING BTREE ON hinex.employees(jobTitle);";

        List<SQLStatement> sqlStatements = SQLUtils.parseStatements(mysqlSql, DbType.mysql);

        String h2Sql = SQLUtils.toSQLString(sqlStatements, DbType.h2);

        System.out.println(h2Sql);
        executeUpdate(h2Sql);
    }

}
