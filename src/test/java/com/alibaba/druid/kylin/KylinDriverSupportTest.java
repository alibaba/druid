package com.alibaba.druid.kylin;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author yinheli
 */
public class KylinDriverSupportTest {

    // test config
    private static final String URL = "jdbc:kylin://172.168.1.111:7070/jlkBigData";
    private static final String USER_NAME = "ADMIN";
    private static final String PASSWORD = "KYLIN";
    private static final String VALIDATION_QUERY = "select 1";

    @Test
    public void testDriverClassName() throws SQLException {
        String driverClass = JdbcUtils.getDriverClassName(URL);
        Assert.assertThat("check get driverClassName", driverClass, Is.is(JdbcConstants.KYLIN_DRIVER));
    }

    @Test
    public void testQuery() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        try {
            dataSource = new DruidDataSource();
            dataSource.setUrl(URL);
            dataSource.setUsername(USER_NAME);
            dataSource.setPassword(PASSWORD);
            dataSource.setValidationQuery(VALIDATION_QUERY);

            Connection conn = dataSource.getConnection();
            PreparedStatement state = conn.prepareStatement(VALIDATION_QUERY);
            ResultSet resultSet = state.executeQuery();
            Assert.assertThat("check result", resultSet, IsNull.notNullValue());
        } finally {
            dataSource.close();
        }

    }

}
