package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ValidConnectionChecker;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 给对象 {@link DruidDataSource} 已经设置了自定义的 validConnectionChecker的情况下,
 * 使用MySQL等已知的Driver的时候, 会用内置的checker覆盖自定义的checker.
 * 由于测试用例{@link DruidDataSourceTest5}使用的是mock driver, 导致并不能测试自定义checker被修改的问题.
 * 此处单独列出Test Case. 由于需要用到真实的MySQL Driver, 以main函数方式提供测试用例.
 * 如需运行此用例, 请先修改连接信息.
 */
public class DruidDataSourceTestValidConnectionChecker {

    public static void main(String[] args) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();

        ValidConnectionChecker checker = new ValidConnectionChecker() {
            @Override
            public boolean isValidConnection(Connection c, String query, int validationQueryTimeout) throws Exception {
                return true;
            }

            @Override
            public void configFromProperties(Properties properties) {
            }
        };

        // 运行测试用例之前请先修改连接信息.
        String jdbcUrl = "jdbc:mysql://localhost:3306/test";
        String user = "test";
        String password = "test";

        Assert.assertTrue("运行此用例之前请先修改上面的连接信息, 并注释此行", false);

        String driverClass = "com.mysql.jdbc.Driver";

        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClass);

        dataSource.setValidConnectionChecker(checker);
        Assert.assertEquals(checker, dataSource.getValidConnectionChecker());

        dataSource.init();

        // 已经设置了自定义的validConnectionChecker的情况下, 即使加载了MySQL Driver之后checker对象也不应该发生变化.
        Assert.assertEquals(checker, dataSource.getValidConnectionChecker());
    }
}
