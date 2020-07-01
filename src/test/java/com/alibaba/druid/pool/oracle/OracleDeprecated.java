package com.alibaba.druid.pool.oracle;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import junit.framework.TestCase;

public class OracleDeprecated extends TestCase {
    public void test_deprecated() throws Exception {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(JdbcConstants.ORACLE_DRIVER2);
    }
}
