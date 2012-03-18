package com.alibaba.druid.bvt.filter.wall;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

import junit.framework.TestCase;

/**
 * 这个场景测试访问MySql系统函数
 * 
 * @author admin
 */
public class MySqlWallLoadDataInfileTest extends TestCase {

    public void test_permit_stmt() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("load data infile 'c:/boot.ini' into table foo"));
    }
    
}
