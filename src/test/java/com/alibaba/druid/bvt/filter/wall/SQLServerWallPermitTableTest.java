package com.alibaba.druid.bvt.filter.wall;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景测试访问SQLServer系统表
 *
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class SQLServerWallPermitTableTest extends TestCase {
    public void test01() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from sysObjects"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from sysdatabases"));
    }
    
    public void test02() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT system_user;"));
    }
    
    public void test03() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT sysobjects.name as tablename, syscolumns.name as columnname FROM sysobjects JOIN syscolumns ON sysobjects.id = syscolumns.id WHERE sysobjects.xtype = ‘U’ AND syscolumns.name LIKE ‘%PASSWORD%’ "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM syscolumns WHERE id = (SELECT id FROM sysobjects WHERE name = ‘mytable’);"));
    }
    

}
