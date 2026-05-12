package com.alibaba.druid.bvt.filter.log;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LogFilterTest3 {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        System.setProperty("druid.log.stmt.executableSql", "true");
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:derby:classpath:petstore-db");
        dataSource.setFilters("log4j,slf4j");
    }

    @Test
    public void test_select() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ITEM WHERE LISTPRICE > ?");
        stmt.setInt(1, 10);

        for (int i = 0; i < 10; ++i) {
            ResultSet rs = stmt.executeQuery();
            rs.close();
        }

        stmt.close();

        conn.close();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);

        System.clearProperty("druid.log.stmt.executableSql");
    }
}
