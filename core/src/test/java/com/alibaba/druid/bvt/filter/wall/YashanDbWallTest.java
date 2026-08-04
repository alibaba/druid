package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.WallFilter;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class YashanDbWallTest {
    private DruidDataSource dataSource;

    @AfterEach
    protected void tearDown() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Test
    public void test_wall_check_select() throws Exception {
        OracleWallProvider provider = new OracleWallProvider();

        String sql = "SELECT * FROM employees WHERE id = 1";
        WallCheckResult result = provider.check(sql);
        List<Violation> violations = result.getViolations();
        assertTrue(violations.isEmpty(), "SELECT should be allowed");
    }

    @Test
    public void test_wall_check_insert() throws Exception {
        OracleWallProvider provider = new OracleWallProvider();

        String sql = "INSERT INTO t (id, name) VALUES (1, 'test')";
        WallCheckResult result = provider.check(sql);
        assertTrue(result.getViolations().isEmpty(), "INSERT should be allowed");
    }

    @Test
    public void test_wall_check_update() throws Exception {
        OracleWallProvider provider = new OracleWallProvider();

        String sql = "UPDATE employees SET salary = 10000 WHERE id = 1";
        WallCheckResult result = provider.check(sql);
        assertTrue(result.getViolations().isEmpty(), "UPDATE should be allowed");
    }

    @Test
    public void test_wall_check_delete() throws Exception {
        OracleWallProvider provider = new OracleWallProvider();

        String sql = "DELETE FROM employees WHERE id = 1";
        WallCheckResult result = provider.check(sql);
        assertTrue(result.getViolations().isEmpty(), "DELETE should be allowed");
    }

    @Test
    public void test_wall_block_truncate_when_disabled() throws Exception {
        OracleWallProvider provider = new OracleWallProvider();
        provider.getConfig().setTruncateAllow(false);

        String sql = "TRUNCATE TABLE t";
        WallCheckResult result = provider.check(sql);
        assertFalse(result.getViolations().isEmpty(), "TRUNCATE should be blocked when truncateAllow=false");
    }

    @Test
    public void test_wall_oracle_style_rownum() throws Exception {
        OracleWallProvider provider = new OracleWallProvider();

        String sql = "SELECT * FROM t WHERE ROWNUM <= 10";
        WallCheckResult result = provider.check(sql);
        assertTrue(result.getViolations().isEmpty(), "Oracle-style ROWNUM should pass Oracle wall provider");
    }

    /**
     * Verify that WallFilter.init() routes DbType.yashandb to OracleWallProvider.
     * If "case yashandb:" is removed from WallFilter.initWallProviderInternal(),
     * this test will fail because the provider will not be an OracleWallProvider.
     */
    @Test
    public void test_wallFilter_routes_yashandb_to_oracle_provider() throws Exception {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setDbType(JdbcConstants.YASHANDB);

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:h2:mem:yashandb_wall_routing_test");
        wallFilter.init(dataSource);

        assertTrue(wallFilter.getProvider() instanceof OracleWallProvider,
                "WallFilter with DbType.yashandb should route to OracleWallProvider");
    }

    @Test
    public void test_wallFilter_check_select() throws Exception {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setDbType(JdbcConstants.YASHANDB);

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:h2:mem:yashandb_wall_select_test");
        wallFilter.init(dataSource);

        String sql = "SELECT * FROM employees WHERE id = 1";
        WallCheckResult result = wallFilter.getProvider().check(sql);
        assertTrue(result.getViolations().isEmpty(), "SELECT should be allowed through WallFilter");
    }

    @Test
    public void test_wallFilter_block_truncate_when_disabled() throws Exception {
        WallFilter wallFilter = new WallFilter();
        wallFilter.setDbType(JdbcConstants.YASHANDB);

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:h2:mem:yashandb_wall_truncate_test");
        wallFilter.init(dataSource);

        wallFilter.getProvider().getConfig().setTruncateAllow(false);
        String sql = "TRUNCATE TABLE t";
        WallCheckResult result = wallFilter.getProvider().check(sql);
        assertFalse(result.getViolations().isEmpty(),
                "TRUNCATE should be blocked through WallFilter when truncateAllow=false");
    }
}
