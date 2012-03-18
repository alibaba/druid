package com.alibaba.druid.bvt.filter.wall;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

import junit.framework.TestCase;

/**
 * SQLServerWallPermitFunctionTest
 *
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class SQLServerWallPermitFunctionTest extends TestCase {

    /**
     * @param name
     */
    public SQLServerWallPermitFunctionTest(String name) {
        super(name);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void test01() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT user_name();"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT DB_NAME()"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT DB_NAME(0); "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT HOST_NAME()"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("EXEC sp_addlogin ‘user’, ‘pass’; "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("EXEC sp_droplogin ‘user’;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("EXEC master.dbo.sp_addsrvrolemember ‘user’, ‘sysadmin"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("EXEC sp_helpdb master"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("EXEC sp_helpdb pubs"));
    }
    
    /**
     * 测试权限结构
     * @throws Exception
     */
    public void test02() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘sysadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘dbcreator’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘bulkadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘diskadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘processadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘serveradmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘setupadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘securityadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘sysadmin’); "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘sysadmin’, ‘sa’); "));
    }

}
