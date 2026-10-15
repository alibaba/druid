package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.jupiter.api.Assertions.*;

public class TimeBetweenLogStatsMillisTest3 {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        System.setProperty("druid.timeBetweenLogStatsMillis", "10");

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
        System.clearProperty("druid.timeBetweenLogStatsMillis");
    }

    @Test
    public void test_0() throws Exception {
        dataSource.init();
        for (int i = 0; i < 10; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select ?");
            stmt.setString(1, "aaa");
            ResultSet rs = stmt.executeQuery();
            rs.close();
            stmt.close();
            conn.close();

            Thread.sleep(10);
        }
        assertEquals(10, dataSource.getTimeBetweenLogStatsMillis());
    }
}
