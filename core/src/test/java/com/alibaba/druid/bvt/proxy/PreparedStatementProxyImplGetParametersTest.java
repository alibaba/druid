package com.alibaba.druid.bvt.proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;


import junit.framework.TestCase;

import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.util.JdbcUtils;

public class PreparedStatementProxyImplGetParametersTest extends TestCase {
    private String url = "jdbc:wrap-jdbc:filters=default:name=driverTest:jdbc:mock:xxx";
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
            assertNotNull(paramMap);
            assertEquals(paramMap.size(), 0);
        }
        stmt.setInt(1, 1);
        {
            Map<Integer, JdbcParameter> paramMap1 = stmt.getParameters();
            assertNotNull(paramMap1);

            Map<Integer, JdbcParameter> paramMap2 = stmt.getParameters();
            assertNotNull(paramMap2);

            assertSame(paramMap1, paramMap2);
            assertEquals(paramMap1.size(), 1);
        }
        stmt.close();
    }
}
