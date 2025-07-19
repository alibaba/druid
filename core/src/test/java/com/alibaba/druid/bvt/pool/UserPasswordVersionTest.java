package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import junit.framework.TestCase;
import org.junit.Assert;

import java.sql.Connection;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这个场景测试initialSize > maxActive
 *
 * @author wenshao [szujobs@hotmail.com]
 */
public class UserPasswordVersionTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(1);
        dataSource.setMaxWait(30);
        dataSource.setInitialSize(1);
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_maxWait() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();
        assertEquals(0, conn.getConnectionHolder().getUserPasswordVersion());

        Properties properties = new Properties();
        properties.put("druid.username", "u1");
        properties.put("druid.password", "p1");

        dataSource.configFromProperties(properties);

        conn.close();


        DruidPooledConnection conn1 = dataSource.getConnection();
        assertEquals(1, conn1.getConnectionHolder().getUserPasswordVersion());
    }
}
