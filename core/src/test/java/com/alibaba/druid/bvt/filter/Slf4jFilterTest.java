package com.alibaba.druid.bvt.filter;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class Slf4jFilterTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("slf4j");
        dataSource.setDbType("mysql");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    @Test
    public void test_slf4j() throws Exception {
        dataSource.init();

        Slf4jLogFilter filter = dataSource.unwrap(Slf4jLogFilter.class);
        assertNotNull(filter);

        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
