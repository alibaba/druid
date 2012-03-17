package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.wall.WallConfig;
import com.alibaba.druid.filter.wall.WallUtils;

/**
 * 这个场景测试访问Oracle系统对象
 * 
 * @author admin
 */
public class MySqlWallPermitVariantTest extends TestCase {

    public void test_allow() throws Exception {
        WallConfig config = new WallConfig();
        config.setVariantCheck(false);
        
        Assert.assertTrue(WallUtils.isValidateMySql("select @@version_compile_os", config));
    }
    
    public void test_not_allow() throws Exception {
        WallConfig config = new WallConfig();
        config.setVariantCheck(true);
        
        Assert.assertFalse(WallUtils.isValidateMySql("select @@version_compile_os", config));
    }
}
