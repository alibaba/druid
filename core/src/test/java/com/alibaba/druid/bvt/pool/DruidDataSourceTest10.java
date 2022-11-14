package com.alibaba.druid.bvt.pool;

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
}
