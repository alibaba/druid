package com.alibaba.druid.bvt.filter.wall;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

import junit.framework.TestCase;

/**
 * SQLServerWallTest
 *
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class SQLServerWallTest extends TestCase {

    /**
     * @param name
     */
    public SQLServerWallTest(String name) {
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
    
    public void test_stuff() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT @@version"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT 1 — comment"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT /*comment*/1"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT user;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT loginame FROM master..sysprocesses WHERE spid = @@SPID"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name, password FROM master..sysxlogins"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name, master.dbo.fn_varbintohexstr(password) FROM master..sysxlogins"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name, password_hash FROM master.sys.sql_logins"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name + ‘-’ + master.sys.fn_varbintohexstr(password_hash) from master.sys.sql_logins"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT permission_name FROM master..fn_my_permissions(null, ‘DATABASE’); "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT permission_name FROM master..fn_my_permissions(null, ‘SERVER’); "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT permission_name FROM master..fn_my_permissions(‘master..syslogins’, ‘OBJECT’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT permission_name FROM master..fn_my_permissions(‘sa’, ‘USER’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘sysadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘dbcreator’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘bulkadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘diskadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘processadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘serveradmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘setupadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘securityadmin’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE denylogin = 0;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE hasaccess = 1;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE isntname = 0;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE isntgroup = 0;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE sysadmin = 1;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE securityadmin = 1;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE serveradmin = 1;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE setupadmin = 1;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE processadmin = 1;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE diskadmin = 1;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE dbcreator = 1;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE bulkadmin = 1;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘sysadmin’); "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‘sysadmin’, ‘sa’); "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE sysadmin = ’1′ "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..sysdatabases;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM syscolumns WHERE id = (SELECT id FROM sysobjects WHERE name = ‘mytable’);"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT master..syscolumns.name, TYPE_NAME(master..syscolumns.xtype) FROM master..syscolumns, master..sysobjects WHERE master..syscolumns.id=master..sysobjects.id AND master..sysobjects.name=’sometable’; "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..sysobjects WHERE xtype = ‘U’; — use xtype = ‘V’ for views SELECT name FROM someotherdb..sysobjects WHERE xtype = ‘U’; "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT sysobjects.name as tablename, syscolumns.name as columnname FROM sysobjects JOIN syscolumns ON sysobjects.id = syscolumns.id WHERE sysobjects.xtype = ‘U’ AND syscolumns.name LIKE ‘%PASSWORD%’ "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT TOP 1 name FROM (SELECT TOP 9 name FROM master..syslogins ORDER BY name ASC) sq ORDER BY name DESC "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("WAITFOR DELAY ’0:0:5′ "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("BULK INSERT mydata FROM ‘c:boot.ini’;"));                  
    }
}