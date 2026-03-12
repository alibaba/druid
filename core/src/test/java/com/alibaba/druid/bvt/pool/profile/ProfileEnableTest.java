package com.alibaba.druid.bvt.pool.profile;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProfileEnableTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxWait(1000);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void testDefault() throws Exception {
        for (int i = 0; i < 10; ++i) {
            DruidPooledConnection conn = dataSource.getConnection();
            System.out.println("physicalConnectNanoSpan : " + conn.getPhysicalConnectNanoSpan());
            System.out.println("physicalConnectionUsedCount : " + conn.getPhysicalConnectionUsedCount());
            System.out.println("connectNotEmptyWaitNanos : " + conn.getConnectNotEmptyWaitNanos());
            conn.close();
        }
    }
}
