package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class IntersectTest extends TestCase {

    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setMinusAllow(false);
        Assert.assertFalse(WallUtils.isValidateOracle(//
        "SELECT * FROM A MINUS SELECT * FROM B", config)); //
    }

    public void test_true() throws Exception {
        Assert.assertTrue(WallUtils.isValidateOracle(//
        "SELECT * FROM A MINUS SELECT * FROM B")); //
    }
}
