/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.filter.wall.sqlserver;

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
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT * FROM T UNION select * from sysObjects"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT * FROM T UNION select * from sysdatabases"));
    }

    public void test03() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT * FROM T UNION SELECT sysobjects.name as tablename, syscolumns.name as columnname FROM sysobjects JOIN syscolumns ON sysobjects.id = syscolumns.id WHERE sysobjects.xtype = ‘U’ AND syscolumns.name LIKE ‘%PASSWORD%’ "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT * FROM T UNION SELECT name FROM syscolumns WHERE id = (SELECT id FROM sysobjects WHERE name = ‘mytable’);"));
    }

}
