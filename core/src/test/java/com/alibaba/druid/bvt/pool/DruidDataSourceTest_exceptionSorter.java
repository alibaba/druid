package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockStatementBase;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ExceptionSorter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 这个场景测试defaultAutoCommit
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_exceptionSorter {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setRemoveAbandoned(true);
        dataSource.setDriver(new MockDriver() {
            public ResultSet executeQuery(MockStatementBase stmt, String sql) throws SQLException {
                throw new SQLException();
            }
        });
        dataSource.setExceptionSorter(new ExceptionSorter() {
            @Override
            public boolean isExceptionFatal(SQLException e) {
                return true;
            }

            @Override
            public void configFromProperties(Properties properties) {
            }
        });
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_event_error() throws Exception {
        {
            Connection conn = dataSource.getConnection();

            PreparedStatement stmt = conn.prepareStatement("select ?");
            try {
                stmt.executeQuery();
            } catch (SQLException e) {
            }

            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        long createCount = dataSource.getCreateCount();
        assertTrue(createCount == 2 || createCount == 3);
//        long discardCount = dataSource.getDiscardCount();
//        assertTrue(createCount == discardCount || createCount - 1 == discardCount, "createCount : " + createCount + ", discardCount" + discardCount
//);
//        assertEquals(1, dataSource.getPoolingCount());
    }
}
