package com.alibaba.druid.proxy.demo;

import java.sql.Connection;
import java.sql.DriverManager;

import com.alibaba.druid.stat.JdbcStatManager;

import junit.framework.Assert;
import junit.framework.TestCase;

public class Demo1 extends TestCase {

    public void test_0() throws Exception {
        JdbcStatManager.getInstance().reset(); // 重置计数器

        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getCloseCount());

        String url = "jdbc:wrap-jdbc:filters=default:name=preCallTest:jdbc:derby:memory:Demo1;create=true";
        Connection conn = DriverManager.getConnection(url);

        Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
        Assert.assertEquals(0, JdbcStatManager.getInstance().getConnectionstat().getCloseCount());

        conn.close();

        Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionstat().getConnectCount());
        Assert.assertEquals(1, JdbcStatManager.getInstance().getConnectionstat().getCloseCount());
    }
}
