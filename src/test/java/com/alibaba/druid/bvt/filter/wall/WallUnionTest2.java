package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景，被攻击者用于测试当前SQL拥有多少字段
 * @author wenshao
 *
 */
public class WallUnionTest2 extends TestCase {

    public void testMySql() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("select f1, f2 from t union select 1, 2 where 1 = 1"));
    }

    public void testOracle() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select f1, f2 from t union select 1, 2 where 1 = 1"));
    }
}
