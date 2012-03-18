package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

/**
 * 测试禁止多条语句执行的场景
 * @author admin
 *
 */
public class WallMultiStatementTest extends TestCase {
    private String sql = "SELECT email FROM members WHERE email = 'x'; UPDATE members SET email = 'steve@unixwiz.net' WHERE email = 'bob@example.com';";
    
    public void testOracle() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle(sql));
    }
    
    public void testMySql() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql(sql));
    }
}
