package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.osjava.sj.SimpleContext;

import java.sql.Connection;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceConverterTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        String osName = System.getProperty("os.name");

        String root = DruidDataSourceConverterTest.class
                .getResource("/com/alibaba/druid/pool/simplejndi/").toString();
        if (root.startsWith("file:/")) {
            root = root.substring("file://".length() - 1);
        }

        if (osName.toLowerCase().indexOf("win") == -1) {
            root = "/" + root;
        }
        Properties props = new Properties();
        props.put("org.osjava.sj.root", root);
        props.put("java.naming.factory.initial",
                "org.osjava.sj.SimpleContextFactory");
        props.put("org.osjava.sj.delimiter", "/");
        javax.naming.Context ctx = new SimpleContext(props);
        dataSource = (DruidDataSource) ctx.lookup("jdbc/druidTest");
        dataSource.init();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_conn() throws Exception {
        assertEquals(true, dataSource.isInited());
        Connection conn = dataSource.getConnection();
        assertEquals(1, dataSource.getActiveCount());
        conn.close();
        assertEquals(0, dataSource.getActiveCount());
    }
}
