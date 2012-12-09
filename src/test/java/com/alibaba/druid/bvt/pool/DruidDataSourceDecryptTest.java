package com.alibaba.druid.bvt.pool;

import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author Jonas Yang
 */
public class DruidDataSourceDecryptTest {

    @Test
    public void testDecrypt() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@");
        dataSource.setUsername("xiaoyu");
        dataSource.setPassword("OJfUm6WCHi7EuXqE6aEc+Po2xFrAGBeSNy8O2jWhV2FTG8/5kbRRr2rjNKhptlevm/03Y0048P7h88gdUOXAYg==");
        dataSource.setFilters("config");
        dataSource.setConnectionProperties("config.decrypt=true");

        try {
            dataSource.init();
            Assert.assertEquals("The password is " + dataSource.getPassword(), "xiaoyu", dataSource.getPassword());
        } finally {
            JdbcUtils.close(dataSource);
        }
    }
}
