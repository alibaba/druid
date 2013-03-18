package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

public class ConstantArithmeticCheckTest extends TestCase {

    public void test_true() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql(//
        "SELECT * from t where id = 3 - 1")); // 
    }
}
