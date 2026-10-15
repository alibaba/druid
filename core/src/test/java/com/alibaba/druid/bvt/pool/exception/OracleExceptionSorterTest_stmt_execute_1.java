package com.alibaba.druid.bvt.pool.exception;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.test.util.OracleMockDriver;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class OracleExceptionSorterTest_stmt_execute_1 {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        JdbcStatManager.getInstance().reset();
        dataSource = new DruidDataSource();

        dataSource.setExceptionSorter(new OracleExceptionSorter());

        dataSource.setDriver(new OracleMockDriver());
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);
    }

        @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
        DruidDataSourceStatManager.clear();
    }

    @Test
    public void test_connect() throws Exception {
        String sql = "SELECT 1";
        {
            DruidPooledConnection conn = dataSource.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.execute();
            pstmt.close();
            conn.close();
        }

        DruidPooledConnection conn = dataSource.getConnection();
        MockConnection mockConn = conn.unwrap(MockConnection.class);
        assertNotNull(mockConn);

        Statement stmt = conn.createStatement();

        SQLException exception = new SQLException("xx", "xxx", 28);
        mockConn.setError(exception);

        SQLException execErrror = null;
        try {
            stmt.execute(sql, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException ex) {
            execErrror = ex;
        }
        assertNotNull(execErrror);
        assertSame(exception, execErrror);

        SQLException commitError = null;
        try {
            conn.commit();
        } catch (SQLException ex) {
            commitError = ex;
        }

        assertNotNull(commitError);
        assertSame(exception, commitError.getCause());

        conn.close();
    }
}
