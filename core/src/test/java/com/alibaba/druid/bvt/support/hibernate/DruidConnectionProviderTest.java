package com.alibaba.druid.bvt.support.hibernate;

import com.alibaba.druid.support.hibernate.DruidConnectionProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DruidConnectionProviderTest {
    private DruidConnectionProvider provider;

    @BeforeEach
    protected void setUp() throws Exception {
        provider = new DruidConnectionProvider();

        Map properties = new HashMap<String, Object>();
        properties.put("url", "jdbc:mock:xxx");

        provider.configure(properties);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        provider.stop();
    }

    @Test
    public void test_hibernate() throws Exception {
        Connection conn = provider.getConnection();
        assertFalse(conn.isClosed());

        provider.closeConnection(conn);
        assertTrue(conn.isClosed());
    }
}
