package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class BigSqlTest extends TestCase {

    public void test_true() throws Exception {
        String sql = "SELECT c from sbtest where id=0";

        for (int i = 0; i < 10000; i++) {
            sql += " or id=0";
        }
        WallConfig config = new WallConfig();
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config));
    }

    public void test_true2() throws Exception {
        String sql = "SELECT c from sbtest where id=0";

        for (int i = 0; i < 10000; i++) {
            sql += " and id=0";
        }
        WallConfig config = new WallConfig();
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config));
    }
}
