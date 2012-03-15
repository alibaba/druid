package com.alibaba.druid.bvt.filter.wall;

import org.junit.Assert;

import com.alibaba.druid.filter.wall.WallUtils;

import junit.framework.TestCase;

public class OracleWallPermitTableTest extends TestCase {

    public void test_permitTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select * from TAB"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from tab"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from SYS.TAB"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from SYS.\"TAB\""));
    }
}
