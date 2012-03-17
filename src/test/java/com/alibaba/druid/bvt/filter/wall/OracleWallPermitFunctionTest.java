package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景测试访问Oracle系统对象
 * 
 * @author admin
 */
public class OracleWallPermitFunctionTest extends TestCase {

    public void test_permitTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select SYS_CONTEXT ('USERENV', 'CURRENT_USER') from dual"));
    }
    
    public void test_permitTable_allow() throws Exception {
        WallConfig config = new WallConfig();
        config.setFunctionCheck(false);
        Assert.assertTrue(WallUtils.isValidateOracle("select SYS_CONTEXT ('USERENV', 'CURRENT_USER') from dual", config));
    }
    
}
