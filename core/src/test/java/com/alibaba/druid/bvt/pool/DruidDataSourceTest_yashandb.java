package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.pool.vendor.OracleValidConnectionChecker;
import com.alibaba.druid.pool.vendor.YashanDbValidConnectionChecker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DruidDataSourceTest_yashandb {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:yasdb://127.0.0.1:1688/REGRESS?productName=Oracle");
        dataSource.setTestOnBorrow(false);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_yashandb_oracle_compatible_init() throws Exception {
        dataSource.init();

        assertEquals("yashandb", dataSource.getDbType());
        assertFalse(dataSource.isOracle());
        assertTrue(dataSource.getValidConnectionChecker() instanceof YashanDbValidConnectionChecker);
        assertTrue(dataSource.getExceptionSorter() instanceof OracleExceptionSorter);
    }
}
