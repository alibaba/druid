package com.alibaba.druid.bvt.filter.wall;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BigSqlTest {
    @Test
    public void test_true() throws Exception {
        String sql = "SELECT c from sbtest where id=0";

        for (int i = 0; i < 10000; i++) {
            sql += " or id=0";
        }
        WallConfig config = new WallConfig();
        assertTrue(WallUtils.isValidateMySql(sql, config));
    }

    @Test
    public void test_true2() throws Exception {
        String sql = "SELECT c from sbtest where id=0";

        for (int i = 0; i < 10000; i++) {
            sql += " and id=0";
        }
        WallConfig config = new WallConfig();
        assertTrue(WallUtils.isValidateMySql(sql, config));
    }
}
