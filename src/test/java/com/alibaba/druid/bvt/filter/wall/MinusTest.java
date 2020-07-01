package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class MinusTest extends TestCase {

    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setIntersectAllow(false);
        Assert.assertFalse(WallUtils.isValidateOracle(//
        "SELECT * FROM A Intersect SELECT * FROM B", config)); //
    }

    public void test_true() throws Exception {

        Assert.assertTrue(WallUtils.isValidateOracle(//
        "SELECT * FROM A Intersect SELECT * FROM B")); //
    }
}
