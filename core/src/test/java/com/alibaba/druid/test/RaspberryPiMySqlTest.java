package com.alibaba.druid.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by wenshao on 10/12/2016.
 */
public class RaspberryPiMySqlTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://raspberrypi_mysql:3306/druid_test_db?allowMultiQueries=true");
        dataSource.setUsername("druid_test");
        dataSource.setPassword("druid_test");
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setInitVariants(true);
        dataSource.setInitGlobalVariants(true);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_mysql() throws Exception {
        DruidPooledConnection connection = dataSource.getConnection();

        System.out.println("variables : " + connection.getVariables());
        System.out.println("gloabl variables : " + connection.getGloablVariables());

        connection.close();
    }
}
