package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import static org.junit.*;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class ConstantArithmeticCheckTest extends TestCase {
    public void test_true() throws Exception {
        assertTrue(WallUtils.isValidateMySql(//
                "SELECT * from t where 3 - 1")); //
    }

    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setConstArithmeticAllow(false);
        assertFalse(WallUtils.isValidateMySql(//
                "SELECT * from t where  3 - 1", config)); //
    }
}
