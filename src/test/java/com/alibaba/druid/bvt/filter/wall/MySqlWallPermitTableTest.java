package com.alibaba.druid.bvt.filter.wall;

import org.junit.Assert;

import com.alibaba.druid.filter.wall.WallUtils;

import junit.framework.TestCase;

/**
 * 这个场景测试访问MySql系统表
 * 
 * @author admin
 */
public class MySqlWallPermitTableTest extends TestCase {

    public void test_permitTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("SELECT User,Password FROM mysql.user"));
        Assert.assertFalse(WallUtils.isValidateMySql("SELECT User,Password FROM `mysql`.`user`"));
        Assert.assertFalse(WallUtils.isValidateMySql("SELECT User,Password FROM \"mysql\".\"user\""));
        Assert.assertFalse(WallUtils.isValidateMySql("SELECT User,Password FROM MYSQL.USER"));
    }
    
    public void test_permitTable_subquery() throws Exception {
        Assert.assertFalse(WallUtils.isValidateMySql("select * from(SELECT User,Password FROM mysql.user) a"));
        Assert.assertFalse(WallUtils.isValidateMySql("select * from(SELECT User,Password FROM `mysql`.`user`) a"));
        Assert.assertFalse(WallUtils.isValidateMySql("select * from(SELECT User,Password FROM \"mysql\".\"user\") a"));
        Assert.assertFalse(WallUtils.isValidateMySql("select * from(SELECT User,Password FROM MYSQL.USER) a"));
    }
}
