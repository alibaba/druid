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
public class WallSelectIntoTest1 extends TestCase {
    private String sql = "SELECT F1, F2 INTO T2 FROM T1";
    
    private WallConfig config = new WallConfig();
    
    protected void setUp() throws Exception {
        config.setSelectIntoAllow(false);
    }

    public void testMySql() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql(sql, config));
    }
    
    public void testORACLE() throws Exception {
        
        Assert.assertFalse(WallUtils.isValidateOracle(sql, config));
    }
}
