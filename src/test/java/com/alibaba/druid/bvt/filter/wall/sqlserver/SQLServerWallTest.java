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
    public SQLServerWallTest(String name){
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

    public void test_stuff() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT * from table where version = @@version"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT 1 — comment"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT * from t where a=1 /* and b=1*/"));
        Assert.assertFalse(WallUtils.isValidateSqlServer("WAITFOR DELAY ’0:0:5′ "));
        Assert.assertFalse(WallUtils.isValidateSqlServer("BULK INSERT mydata FROM ‘c:boot.ini’;"));
    }
}
