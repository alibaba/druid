package com.alibaba.druid.bvt.filter.wall;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import junit.framework.TestCase;


import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class IntersectTest extends TestCase {
    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setMinusAllow(false);
        assertFalse(WallUtils.isValidateOracle(//
                "SELECT * FROM A MINUS SELECT * FROM B", config)); //
    }

    public void test_true() throws Exception {
        assertTrue(WallUtils.isValidateOracle(//
                "SELECT * FROM A MINUS SELECT * FROM B")); //
    }
}
