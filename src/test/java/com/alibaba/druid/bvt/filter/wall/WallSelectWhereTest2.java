package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景，检测可疑的Having条件
 * @author wenshao
 *
 */
public class WallSelectWhereTest2 extends TestCase {
    private String sql = "SELECT F1, F2 WHERE 1 = 1 AND FID = ?";

    public void testMySql() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql(sql));
    }
    
    public void testORACLE() throws Exception {
        Assert.assertTrue(WallUtils.isValidateOracle(sql));
    }
}
