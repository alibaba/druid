package com.alibaba.druid.bvt.support.hibernate;

import com.alibaba.druid.support.hibernate.DruidConnectionProvider;
import junit.framework.TestCase;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

public class DruidConnectionProviderTest extends TestCase {
    private DruidConnectionProvider provider;

    protected void setUp() throws Exception {
        provider = new DruidConnectionProvider();

        Map properties = new HashMap<String, Object>();
        properties.put("url", "jdbc:mock:xxx");

        provider.configure(properties);
    }

    protected void tearDown() throws Exception {
        provider.stop();
    }

    public void test_hibernate() throws Exception {
        Connection conn = provider.getConnection();
        assertFalse(conn.isClosed());

        provider.closeConnection(conn);
        assertTrue(conn.isClosed());
    }
}
