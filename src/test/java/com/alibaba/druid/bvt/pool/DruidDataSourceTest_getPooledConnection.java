package com.alibaba.druid.bvt.pool;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockStatementBase;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

/**
 * 这个场景测试defaultAutoCommit
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_getPooledConnection extends TestCase {

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

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_conn() throws Exception {
        PooledConnection conn = dataSource.getPooledConnection();
        conn.close();
    }

    public void test_conn_1() throws Exception {
        Exception error = null;
        try {
            dataSource.getPooledConnection(null, null);
        } catch (UnsupportedOperationException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }

    public void test_event_error() throws Exception {
        DruidPooledConnection conn = (DruidPooledConnection) dataSource.getPooledConnection();

        final AtomicInteger errorCount = new AtomicInteger();
        conn.addConnectionEventListener(new ConnectionEventListener() {

            @Override
            public void connectionErrorOccurred(ConnectionEvent event) {
                errorCount.incrementAndGet();
            }

            @Override
            public void connectionClosed(ConnectionEvent event) {

            }
        });

        PreparedStatement stmt = conn.prepareStatement("select ?");
        try {
            stmt.executeQuery();
        } catch (SQLException e) {

        }
        
        Assert.assertEquals(1, errorCount.get());
        conn.close();
    }
}
