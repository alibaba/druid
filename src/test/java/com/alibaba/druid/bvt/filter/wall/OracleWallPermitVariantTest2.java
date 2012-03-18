package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景测试访问Oracle系统对象
 * 
 * @author admin
 */
public class OracleWallPermitVariantTest2 extends TestCase {

    public void test_permitTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select UID from dual"));
        Assert.assertFalse(WallUtils.isValidateOracle("select USER from dual"));
    }
    
}
