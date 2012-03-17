package com.alibaba.druid.bvt.filter.wall;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

import junit.framework.TestCase;

/**
 * 这个场景测试访问MySql系统函数
 * 
 * @author admin
 */
public class MySqlWallLimitTest extends TestCase {

    public void test_permitTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("SELECT * FROM T LIMIT 0"));
        Assert.assertFalse(WallUtils.isValidateMySql("SELECT * FROM T LIMIT 10, 0"));
    }
    
}
