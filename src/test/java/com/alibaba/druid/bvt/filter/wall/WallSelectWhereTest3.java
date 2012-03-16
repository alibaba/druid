package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.wall.WallUtils;

/**
 * 这个场景，检测可疑的Having条件
 * @author wenshao
 *
 */
public class WallSelectWhereTest3 extends TestCase {
    private String sql = "select * from t WHERE FID = 256 AND CHR(67)||CHR(65)||CHR(84) = 'CAT'";

    public void testMySql() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql(sql));
    }
    
    public void testORACLE() throws Exception {
        Assert.assertTrue(WallUtils.isValidateOracle(sql));
    }
}
