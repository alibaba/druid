package com.alibaba.boot.druid.demo;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

/**
 * druid auto configuration test
 *
 * @author leijuan
 */
@RunWith(SpringRunner.class)
@ImportAutoConfiguration(DruidDemoApplication.class)
@TestPropertySource("classpath:application.properties")
public class DruidAutoConfigurationTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseOperations() {
        Assert.assertNotNull(dataSource);
    }

}
