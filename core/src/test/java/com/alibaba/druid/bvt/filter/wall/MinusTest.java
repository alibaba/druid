package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import static org.junit.*;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class MinusTest extends TestCase {
    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setIntersectAllow(false);
        assertFalse(WallUtils.isValidateOracle(//
                "SELECT * FROM A Intersect SELECT * FROM B", config)); //
    }

    public void test_true() throws Exception {
        assertTrue(WallUtils.isValidateOracle(//
                "SELECT * FROM A Intersect SELECT * FROM B")); //
    }
}
