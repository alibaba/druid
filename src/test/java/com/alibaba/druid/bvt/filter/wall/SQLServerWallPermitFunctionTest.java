/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.wall.WallUtils;

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
    public SQLServerWallPermitFunctionTest(String name){
        super(name);
    }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void test01() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT user_name() FROM X;"));
    }

    public final void test_sp_addsrvrolemenber() throws Exception {
        Assert.assertTrue(WallUtils.isValidateSqlServer("EXEC master.dbo.sp_addsrvrolemember ‘user’, ‘sysadmin"));
    }

    public final void test_sp_helpdb() throws Exception {
        Assert.assertTrue(WallUtils.isValidateSqlServer("EXEC sp_helpdb master"));
        Assert.assertTrue(WallUtils.isValidateSqlServer("EXEC sp_helpdb pubs"));
    }

    public final void test_sp_droplogin() throws Exception {
        Assert.assertTrue(WallUtils.isValidateSqlServer("EXEC sp_droplogin ‘user’;"));
    }

    public final void test_sp_addlogin() throws Exception {
        Assert.assertTrue(WallUtils.isValidateSqlServer("EXEC sp_addlogin ‘user’, ‘pass’; "));
    }

    public final void test_db_name() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT DB_NAME() FROM X"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT DB_NAME(0) FROM X; "));
    }

    public final void test_host_name() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT HOST_NAME() FROM X"));
    }

    /**
     * 测试权限结构
     * 
     * @throws Exception
     */
    public void test_is_srvrolemember() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT is_srvrolemember(‘sysadmin’) FROM X;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT is_srvrolemember(‘dbcreator’) FROM X;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT is_srvrolemember(‘bulkadmin’) FROM X;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT is_srvrolemember(‘diskadmin’) FROM X;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT is_srvrolemember(‘processadmin’) FROM X;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT is_srvrolemember(‘serveradmin’) FROM X;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT is_srvrolemember(‘setupadmin’) FROM X;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT is_srvrolemember(‘securityadmin’) FROM X;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT is_srvrolemember(‘sysadmin’) FROM X; "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("select * from t where fid = 1 UNION SELECT is_srvrolemember(‘sysadmin’, ‘sa’) FROM X; "));
    }

}
