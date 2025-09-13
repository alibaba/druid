package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class DruidDataSourceTest10 {
    DruidDataSource ds;

    @Before
    public void setup() {
        ds = new DruidDataSource();
    }

    @After
    public void tearDown() {
        JdbcUtils.close(ds);
        ds = null;
    }

    @Test
    public void test() throws Exception {
        ds.setSocketTimeout(10);
        ds.setConnectTimeout(20);

        DruidDataSource ds1 = (DruidDataSource) ds.clone();
        assertEquals(ds.getConnectTimeout(), ds1.getConnectTimeout());
        assertEquals(ds.getSocketTimeout(), ds1.getSocketTimeout());
    }

    @Test
    public void test1() throws Exception {
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/xxx?connectTimeout=3000&socketTimeout=6000");
        ds.init();
        assertEquals(3000, ds.getConnectTimeout());
        assertEquals(6000, ds.getSocketTimeout());
    }
    @Test
    public void test_timeout_is_zero() throws Exception {
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/xxx?connectTimeout=0&socketTimeout=0");
        ds.init();
        assertEquals(0, ds.getConnectTimeout());
        assertEquals(0, ds.getSocketTimeout());
    }

    @Test
    public void test_timeout_is_zero_default() throws Exception {
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/xxx");
        ds.init();
        assertEquals(0, ds.getConnectTimeout());
        assertEquals(0, ds.getSocketTimeout());
    }
    @Test
    public void test_timeout_is_zero2() throws Exception {
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/xxx");
        ds.setConnectTimeout(-1);
        ds.setSocketTimeout(-1);
        ds.init();
        assertEquals(-1, ds.getConnectTimeout());
        assertEquals(-1, ds.getSocketTimeout());
    }

    /**
     * @throws Exception
     * @see <a href="https://github.com/alibaba/druid/issues/5396">...</a>
     */
    @Test
    public void test_timeout_in_loadbalance() throws Exception {
        ds.setUrl(
            "jdbc:mysql:loadbalance://localhost:3306,localhost:3310/test?connectTimeout=98&socketTimeout=99&loadBalanceConnectionGroup=first&ha.enableJMX=true");
        ds.init();
        assertEquals(98, ds.getConnectTimeout());
        assertEquals(99, ds.getSocketTimeout());
    }
    @Test
    public void test_timeout_is_zero_in_loadbalance() throws Exception {
        ds.setUrl(
            "jdbc:mysql:loadbalance://localhost:3306,localhost:3310/test?connectTimeout=0&socketTimeout=0&loadBalanceConnectionGroup=first&ha.enableJMX=true");
        ds.init();
        assertEquals(0, ds.getConnectTimeout());
        assertEquals(0, ds.getSocketTimeout());
    }
    @Test
    public void test_timeout_in_replication() throws Exception {
        ds.setUrl(
            "jdbc:mysql:replication://localhost:3306,localhost:3310/test?connectTimeout=0&socketTimeout=0&loadBalanceConnectionGroup=first&ha.enableJMX=true");
        ds.init();
        assertEquals(0, ds.getConnectTimeout());
        assertEquals(0, ds.getSocketTimeout());
    }
    @Test
    public void test_timeout_in_mariadb() throws Exception {
        ds.setUrl(
            "jdbc:mariadb://localhost:3306/test?connectTimeout=0&socketTimeout=0&loadBalanceConnectionGroup=first&ha.enableJMX=true");
        ds.init();
        assertEquals(0, ds.getConnectTimeout());
        assertEquals(0, ds.getSocketTimeout());
    }
    @Test
    public void test_timeout_in_mariadb_loadbalance() throws Exception {
        ds.setUrl(
            "jdbc:mariadb:loadbalance://localhost:3306,localhost:3310/test?connectTimeout=0&socketTimeout=0&loadBalanceConnectionGroup=first&ha.enableJMX=true");
        ds.init();
        assertEquals(0, ds.getConnectTimeout());
        assertEquals(0, ds.getSocketTimeout());
    }

    @Test
    public void test_timeout_in_mariadb_replication() throws Exception {
        ds.setUrl(
            "jdbc:mariadb:replication://localhost:3306,localhost:3310/test?connectTimeout=0&socketTimeout=0&loadBalanceConnectionGroup=first&ha.enableJMX=true");
        ds.init();
        assertEquals(0, ds.getConnectTimeout());
        assertEquals(0, ds.getSocketTimeout());
    }
    @Test
    public void test_timeout_in_mariadb2() throws Exception {
        ds.setUrl(
            "jdbc:mariadb://localhost:3306/test?connectTimeout=1&socketTimeout=2&loadBalanceConnectionGroup=first&ha.enableJMX=true");
        ds.init();
        assertEquals(1, ds.getConnectTimeout());
        assertEquals(2, ds.getSocketTimeout());
    }
    @Test
    public void test_timeout_in_mariadb_loadbalance2() throws Exception {
        ds.setUrl(
            "jdbc:mariadb:loadbalance://localhost:3306,localhost:3310/test?connectTimeout=3&socketTimeout=4&loadBalanceConnectionGroup=first&ha.enableJMX=true");
        ds.init();
        assertEquals(3, ds.getConnectTimeout());
        assertEquals(4, ds.getSocketTimeout());
    }

    @Test
    public void test_timeout_in_mariadb_replication2() throws Exception {
        ds.setUrl(
            "jdbc:mariadb:replication://localhost:3306,localhost:3310/test?connectTimeout=5&socketTimeout=6&loadBalanceConnectionGroup=first&ha.enableJMX=true");
        ds.init();
        assertEquals(5, ds.getConnectTimeout());
        assertEquals(6, ds.getSocketTimeout());
    }

    @Test
    public void test2() throws Exception {
        Properties properties = new Properties();
        properties.put("connectTimeout", "3000");
        properties.put("socketTimeout", "6000");
        ds.setConnectProperties(properties);
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/xxx");
        ds.init();
        assertEquals(3000, ds.getConnectTimeout());
        assertEquals(6000, ds.getSocketTimeout());
    }

    @Test
    public void test3() throws Exception {
        ds.setConnectionProperties("connectTimeout=3000;socketTimeout=6000");
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/xxx");
        ds.init();
        assertEquals(3000, ds.getConnectTimeout());
        assertEquals(6000, ds.getSocketTimeout());
    }

    @Test
    public void test4() throws Exception {
        ds.setConnectionProperties("connectTimeout=3000;socketTimeout=6000");
        ds.setDriver(MockDriver.instance);
        ds.init();
        assertEquals(3000, ds.getConnectTimeout());
        assertEquals(6000, ds.getSocketTimeout());
    }

    @Test
    public void test5() throws Exception {
        ds.setDriver(MockDriver.instance);
        ds.setUrl("jdbc:mock:xxx?connectTimeout=3000&socketTimeout=6000");
        ds.init();
        assertEquals(3000, ds.getConnectTimeout());
        assertEquals(6000, ds.getSocketTimeout());
    }

    @Test
    public void test6() throws Exception {
        ds.setConnectionProperties("socketTimeout=6000");
        ds.setDriver(MockDriver.instance);
        ds.init();
        assertEquals(6000, ds.getSocketTimeout());
    }

    @Test
    public void test7() throws Exception {
        ds.setDriver(MockDriver.instance);
        ds.setUrl("jdbc:mock:xxx?socketTimeout=6000");
        ds.init();
        assertEquals(6000, ds.getSocketTimeout());
    }
}
