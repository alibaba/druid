package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景，被攻击者用于测试当前SQL拥有多少字段
 * @author wenshao
 *
 */
public class WallDropTest extends TestCase {

    public void testMySql() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("DROP TABLE T1"));
    }
    
    public void testOracle() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("DROP TABLE T1"));
    }
}
