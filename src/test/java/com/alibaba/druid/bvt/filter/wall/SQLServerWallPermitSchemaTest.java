/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.filter.wall;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

import junit.framework.TestCase;

/**
 * SQLServerWallPermitSchemaTest
 *
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class SQLServerWallPermitSchemaTest extends TestCase {

    /**
     * @param name
     */
    public SQLServerWallPermitSchemaTest(String name) {
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


    public void test_master() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name, password FROM master..sysxlogins"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT permission_name FROM master..fn_my_permissions(null, â€˜DATABASEâ€?; "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT permission_name FROM master..fn_my_permissions(null, â€˜SERVERâ€?; "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT permission_name FROM master..fn_my_permissions(â€˜master..sysloginsâ€? â€˜OBJECTâ€?;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT permission_name FROM master..fn_my_permissions(â€˜saâ€? â€˜USERâ€?;"));
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
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name, master.dbo.fn_varbintohexstr(password) FROM master..sysxlogins"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT master..syscolumns.name, TYPE_NAME(master..syscolumns.xtype) FROM master..syscolumns, master..sysobjects WHERE master..syscolumns.id=master..sysobjects.id AND master..sysobjects.name=â€™sometableâ€? "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..sysobjects WHERE xtype = â€˜Uâ€? â€?use xtype = â€˜Vâ€?for views SELECT name FROM someotherdb..sysobjects WHERE xtype = â€˜Uâ€? "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins WHERE sysadmin = â€?â€?"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..sysdatabases;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT TOP 1 name FROM (SELECT TOP 9 name FROM master..syslogins ORDER BY name ASC) sq ORDER BY name DESC "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name + â€?â€?+ master.sys.fn_varbintohexstr(password_hash) from master.sys.sql_logins"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT loginame FROM master..sysprocesses WHERE spid = @@SPID"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name FROM master..syslogins"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT name, password_hash FROM master.sys.sql_logins"));
    }
}
