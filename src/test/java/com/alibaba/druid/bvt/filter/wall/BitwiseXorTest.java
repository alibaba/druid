package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class BitwiseXorTest extends TestCase {

    public void test_true() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql(//
        "SELECT * from t where (id = 1) ^ (1=1)")); //
    }

    public void test_false() throws Exception {
        WallConfig config = new WallConfig();
        config.setConditionOpBitwseAllow(false);
        Assert.assertFalse(WallUtils.isValidateMySql(//
        "SELECT * from t where (id = 1) ^ (1=1)", config)); //
    }
}
