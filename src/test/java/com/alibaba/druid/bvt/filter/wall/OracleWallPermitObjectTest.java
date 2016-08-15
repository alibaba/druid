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

import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallUtils;

/**
 * 这个场景测试访问Oracle系统对象
 * 
 * @author admin
 */
public class OracleWallPermitObjectTest extends TestCase {

    public void test_permitTable() throws Exception {
        Assert.assertFalse(WallUtils.isValidateOracle("select  sys.LinxReadFile('c:/boot.ini') from dual"));
        Assert.assertFalse(WallUtils.isValidateOracle("select  sys.LinxRunCMD('cmd /c net user linx /add') from dual"));
        Assert.assertFalse(WallUtils.isValidateOracle("select utl_inaddr.get_host_address from DUAL"));
        Assert.assertFalse(WallUtils.isValidateOracle("select TO_CHAR(utl_inaddr.get_host_address) from DUAL"));
        Assert.assertFalse(WallUtils.isValidateOracle("SELECT SYS.DBMS_EXPORT_EXTENSION.GET_DOMAIN_INDEX_TABLES('FOO','BAR','DBMS_OUTPUT'.PUT(:P1));"));
        Assert.assertFalse(WallUtils.isValidateOracle("select SYS.DBMS_EXPORT_EXTENSION.GET_DOMAIN_INDEX_TABLES()"));
    }
    
    public void test_permitTable_allow() throws Exception {
        WallConfig config = new WallConfig();
        config.setObjectCheck(false);
        Assert.assertTrue(WallUtils.isValidateOracle("select  sys.LinxReadFile('c:/boot.ini') from dual", config));
        Assert.assertTrue(WallUtils.isValidateOracle("select  sys.LinxRunCMD('cmd /c net user linx /add') from dual", config));
        Assert.assertTrue(WallUtils.isValidateOracle("select utl_inaddr.get_host_address from DUAL", config));
        Assert.assertTrue(WallUtils.isValidateOracle("select TO_CHAR(utl_inaddr.get_host_address) from DUAL", config));
        Assert.assertTrue(WallUtils.isValidateOracle("SELECT SYS.DBMS_EXPORT_EXTENSION.GET_DOMAIN_INDEX_TABLES('FOO','BAR','DBMS_OUTPUT'.PUT(:P1));", config));
        Assert.assertTrue(WallUtils.isValidateOracle("select SYS.DBMS_EXPORT_EXTENSION.GET_DOMAIN_INDEX_TABLES()", config));
    }
}
