package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.wall.WallUtils;

/**
 * 这个场景，被攻击者用于测试当前SQL拥有多少字段
 * @author wenshao
 *
 */
public class WallUnionTest4 extends TestCase {

    public void testMySql() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("SELECT id, product FROM T1 t UNION (SELECT * FROM (SELECT 1,'x') X)"));
    }
    
    public void testORACLE() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("SELECT id, product FROM T1 t UNION (SELECT * FROM (SELECT 1,'x') X)"));
    }
}
