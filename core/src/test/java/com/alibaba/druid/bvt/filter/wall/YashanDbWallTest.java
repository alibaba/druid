package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.Violation;
import com.alibaba.druid.wall.WallCheckResult;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class YashanDbWallTest {

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
}
