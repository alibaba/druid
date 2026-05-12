package com.alibaba.druid.pool.oracle;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import org.junit.jupiter.api.Test;

public class OracleDeprecated {
    @Test
    public void test_deprecated() throws Exception {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(JdbcConstants.ORACLE_DRIVER2);
    }
}
