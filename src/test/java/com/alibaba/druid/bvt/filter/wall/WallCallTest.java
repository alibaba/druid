package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景，被攻击者用于测试当前SQL拥有多少字段
 * @author wenshao
 *
 */
public class WallCallTest extends TestCase {

    public void testMySql() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql("{ call INTERFACE_DATA_EXTRACTION.INVOICE_INFO(?,?,?)}"));
    }
    
    public void testOracle() throws Exception {
        Assert.assertTrue(WallUtils.isValidateOracle("{ call INTERFACE_DATA_EXTRACTION.INVOICE_INFO(?,?,?)}"));
    }
    
    public void testSqlServer() throws Exception {
        Assert.assertTrue(WallUtils.isValidateSqlServer("{ call INTERFACE_DATA_EXTRACTION.INVOICE_INFO(?,?,?)}"));
    }
}
