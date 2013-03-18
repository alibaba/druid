package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

public class StrictSyntaxCheckTest extends TestCase {

    public void test_syntax() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql(//
        "SELECT SELECT")); // 部分永真
    }

    public void test_syntax_1() throws Exception {
        WallConfig config = new WallConfig();
        config.setStrictSyntaxCheck(false);
        Assert.assertTrue(WallUtils.isValidateMySql(//
        "SELECT SELECT", config)); // 部分永真
    }
}
