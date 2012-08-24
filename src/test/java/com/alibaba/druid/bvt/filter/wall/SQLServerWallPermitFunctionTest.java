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
    }
    
    public final void test_sp_addsrvrolemenber() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("EXEC master.dbo.sp_addsrvrolemember ‚Äòuser‚Ä? ‚Äòsysadmin"));
    }
    
    public final void test_sp_helpdb() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("EXEC sp_helpdb master"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("EXEC sp_helpdb pubs"));
    }
    
    public final void test_sp_droplogin() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("EXEC sp_droplogin ‚Äòuser‚Ä?"));
    }
    public final void test_sp_addlogin() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("EXEC sp_addlogin ‚Äòuser‚Ä? ‚Äòpass‚Ä? "));
    }
    
    public final void test_db_name() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT DB_NAME()"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT DB_NAME(0); "));
    }
    
    public final void test_host_name() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT HOST_NAME()"));
    }
    
    /**
     * ÊµãËØïÊùÉÈôêÁªìÊûÑ
     * @throws Exception
     */
    public void test_is_srvrolemember() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‚Äòsysadmin‚Ä?;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‚Äòdbcreator‚Ä?;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‚Äòbulkadmin‚Ä?;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‚Äòdiskadmin‚Ä?;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‚Äòprocessadmin‚Ä?;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‚Äòserveradmin‚Ä?;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‚Äòsetupadmin‚Ä?;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‚Äòsecurityadmin‚Ä?;"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‚Äòsysadmin‚Ä?; "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT is_srvrolemember(‚Äòsysadmin‚Ä? ‚Äòsa‚Ä?; "));
    }


}
