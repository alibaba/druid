package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestIdle2 extends TestCase {

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.cear();
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_idle2() throws Exception {
        MockDriver driver = new MockDriver();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(14);
        dataSource.setMaxActive(14);
        dataSource.setMaxIdle(14);
        dataSource.setMinIdle(14);
        dataSource.setMinEvictableIdleTimeMillis(50 * 1);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");

        {
            Assert.assertEquals(0, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());

            Connection conn = dataSource.getConnection();

            Assert.assertEquals(dataSource.getInitialSize(), dataSource.getCreateCount());
            Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            Assert.assertEquals(1, dataSource.getActiveCount());

            conn.close();
            Assert.assertEquals(0, dataSource.getDestroyCount());
            Assert.assertEquals(true, dataSource.getPoolingCount() == driver.getConnections().size());
            Assert.assertEquals(0, dataSource.getActiveCount());
        }

        String text = dataSource.toString();
        System.out.println(text);

        {
            int count = 14;
            Connection[] connections = new Connection[count];
            for (int i = 0; i < count; ++i) {
                connections[i] = dataSource.getConnection();
                Assert.assertEquals(i + 1, dataSource.getActiveCount());
            }
            Assert.assertEquals(dataSource.getMaxActive(), dataSource.getCreateCount());
            Assert.assertEquals(count, driver.getConnections().size());
            for (int i = 0; i < count; ++i) {
                connections[i].close();
                Assert.assertEquals(count - i - 1, dataSource.getActiveCount());
            }
            Assert.assertEquals(dataSource.getMaxActive(), dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(14, driver.getConnections().size());
        }

        for (int i = 0; i < 100; ++i) {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, dataSource.getActiveCount());

            Thread.sleep(1);
            conn.close();
        }
        Assert.assertEquals(true, dataSource.getPoolingCount() == dataSource.getMaxActive());

        dataSource.close();
    }
}
