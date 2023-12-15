package com.alibaba.druid.bvt.pool.exception;

import java.lang.Thread.State;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;

import org.junit.Assert;
import org.springframework.test.util.ReflectionTestUtils;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSource.CreateConnectionThread;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.test.util.OracleMockDriverConnectFailed;

public class OracleExceptionSorterTest_closeConnectFailedDatasource extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());

        dataSource = new DruidDataSource();

        dataSource.setExceptionSorter(new OracleExceptionSorter());

        dataSource.setDriver(new OracleMockDriverConnectFailed());
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setMaxActive(1);
        dataSource.setMaxWait(1000);
        
        OracleMockDriverConnectFailed.CONNECT_BARIER.reset();
    }

    @Override
    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_connect() throws Exception {
        Thread connectFailedThread = new Thread() {
            public void run() {
                try {
                    dataSource.getConnection();
                } catch (SQLException e) {
                     e.printStackTrace();
                }
            }
        };
        connectFailedThread.start();

        OracleMockDriverConnectFailed.CONNECT_BARIER.await(100, TimeUnit.MILLISECONDS);
        dataSource.close();
        
        // waiting for createConnectionThread terminated.
        CreateConnectionThread thread = (CreateConnectionThread) ReflectionTestUtils.getField(dataSource, "createConnectionThread");
        for (int i = 0; i < 10 && State.TERMINATED != thread.getState(); i++) {
            Thread.sleep(1000);
        }
        Assert.assertEquals(State.TERMINATED, thread.getState());
    }

}
