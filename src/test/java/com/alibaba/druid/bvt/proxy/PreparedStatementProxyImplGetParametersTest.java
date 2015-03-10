package com.alibaba.druid.bvt.proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.util.JdbcUtils;

public class PreparedStatementProxyImplGetParametersTest extends TestCase {

    private String     url = "jdbc:wrap-jdbc:filters=default:name=driverTest:jdbc:mock:xxx";
    private Connection conn;

    protected void setUp() throws Exception {
        conn = DriverManager.getConnection(url);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(conn);
    }

    public void test_get_parameters() throws Exception {
        final PreparedStatementProxy stmt = (PreparedStatementProxy) conn.prepareStatement("select 1");

        {
            Map<Integer, JdbcParameter> paramMap = stmt.getParameters();
            Assert.assertNotNull(paramMap);
            Assert.assertEquals(paramMap.size(), 0);
        }
        stmt.setInt(1, 1);
        {
            Map<Integer, JdbcParameter> paramMap1 = stmt.getParameters();
            Assert.assertNotNull(paramMap1);
   
            Map<Integer, JdbcParameter> paramMap2 = stmt.getParameters();
            Assert.assertNotNull(paramMap2);
            
            Assert.assertSame(paramMap1, paramMap2);
            Assert.assertEquals(paramMap1.size(), 1);
        }
        stmt.close();
    }
}
