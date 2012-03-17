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
public class WallInsertTest2 extends TestCase {
    private String sql = "INSERT INTO T (F1, F2) VALUES (1, 2)";
    
    private WallConfig config = new WallConfig();
    
    protected void setUp() throws Exception {
        config.setInsertAllow(true);
    }

    public void testMySql() throws Exception {
        Assert.assertTrue(WallUtils.isValidateMySql(sql, config));
    }
    
    public void testORACLE() throws Exception {
        
        Assert.assertTrue(WallUtils.isValidateOracle(sql, config));
    }
}
