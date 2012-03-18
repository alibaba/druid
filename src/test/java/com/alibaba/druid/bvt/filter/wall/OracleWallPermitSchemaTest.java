package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景测试访问Oracle系统对象
 * 
 * @author admin
 */
public class OracleWallPermitSchemaTest extends TestCase {

    public void test_permitTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select banner from sys.v_$version where rownum=1"));
        Assert.assertFalse(WallUtils.isValidateOracle("select banner from sys.v where rownum=1"));
    }
    
}
