package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockStatementBase;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ExceptionSorter;

/**
 * 这个场景测试defaultAutoCommit
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_exceptionSorter extends TestCase {

    private DruidDataSource dataSource;

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

    protected void tearDown() throws Exception {
        dataSource.close();
    }

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
        assertEquals(createCount - 1, dataSource.getDiscardCount());
        assertEquals(1, dataSource.getPoolingCount());
    }
}
