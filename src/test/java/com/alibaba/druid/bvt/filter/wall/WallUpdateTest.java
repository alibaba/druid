package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.wall.WallConfig;
import com.alibaba.druid.filter.wall.WallUtils;

/**
 * 这个场景，检测
 * @author wenshao
 *
 */
public class WallUpdateTest extends TestCase {
    private String sql = "UPDATE T_USER SET FNAME = ? WHERE FID = ?";
    
    private WallConfig config = new WallConfig();
    
    protected void setUp() throws Exception {
        config.setDeleteAllow(false);
    }

    public void testMySql() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config));
    }
    
    public void testORACLE() throws Exception {
        
        Assert.assertTrue(WallUtils.isValidateOracle(sql, config));
    }
}
