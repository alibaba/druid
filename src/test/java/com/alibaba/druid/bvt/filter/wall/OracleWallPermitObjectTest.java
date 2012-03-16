package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.wall.WallUtils;

/**
 * 这个场景测试访问Oracle系统对象
 * 
 * @author admin
 */
public class OracleWallPermitObjectTest extends TestCase {

    public void test_permitTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select utl_inaddr.get_host_address from DUAL"));
        Assert.assertFalse(WallUtils.isValidateOracle("select TO_CHAR(utl_inaddr.get_host_address) from DUAL"));
    }
    
}
