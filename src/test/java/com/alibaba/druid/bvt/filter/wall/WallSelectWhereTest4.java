package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景，检测可疑的Where条件
 * @author wenshao
 *
 */
public class WallSelectWhereTest4 extends TestCase {
    private String sql = "select * from t WHERE FID = 256 OR CHR(67)||CHR(65)||CHR(84) = 'CAT'";

    public void testMySql() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql(sql));
    }
    
    public void testORACLE() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle(sql));
    }
}
