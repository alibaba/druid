package com.alibaba.druid.bvt.filter.wall;

import org.junit.Assert;

import com.alibaba.druid.filter.wall.WallUtils;

import junit.framework.TestCase;

/**
 * 这个场景测试访问Oracle系统表
 * 
 * @author admin
 */
public class OracleWallPermitTableTest extends TestCase {

    public void test_permitTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select * from TAB"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from tab"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from SYS.TAB"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from SYS.\"TAB\""));
    }
    
    public void test_permitTable_subquery() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select * from(select * from TAB) a"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from(select * from tab) a"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from(select * from SYS.TAB) a"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from(select * from SYS.\"TAB\") a"));
    }
    
    public void test_permitTable_join() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select * from t1, TAB"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from t1, tab"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from t1, SYS.TAB"));
        Assert.assertFalse(WallUtils.isValidateOracle("select * from t1, SYS.\"TAB\""));
    }
}
