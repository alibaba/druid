package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.pool.vendor.YashanDbValidConnectionChecker;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    public void test_yashandb_dbType_enum() throws Exception {
        dataSource.init();
        assertEquals(DbType.yashandb, DbType.of(dataSource.getDbType()));
    }

    @Test
    public void test_yashandb_not_oracle_implicit_cache() throws Exception {
        dataSource.init();
        // YashanDB should NOT have Oracle implicit cache setting
        assertFalse(dataSource.isOracle());
        // The Oracle implicit cache property should not be set for YashanDB
        assertNull(dataSource.getConnectProperties().getProperty("oracle.jdbc.FreeMemoryOnEnterImplicitCache"));
    }

    @Test
    public void test_yashandb_custom_validation_query() throws Exception {
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        dataSource.init();

        assertEquals("yashandb", dataSource.getDbType());
        assertEquals("SELECT 1 FROM DUAL", dataSource.getValidationQuery());
        assertTrue(dataSource.getValidConnectionChecker() instanceof YashanDbValidConnectionChecker);
    }

    @Test
    public void test_yashandb_driver_version_check_skipped() throws Exception {
        // The mock YashanDB driver returns majorVersion=1 which would fail
        // Oracle's version check (requires >= 10). YashanDB should skip this check.
        dataSource.init();
        assertTrue(dataSource.isInited());
    }

    @Test
    public void test_yashandb_exception_sorter_is_oracle() throws Exception {
        dataSource.init();
        // YashanDB uses OracleExceptionSorter because it's Oracle-compatible
        assertTrue(dataSource.getExceptionSorter() instanceof OracleExceptionSorter);
    }
}
