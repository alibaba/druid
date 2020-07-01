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
 * SQLServerWallPermitObjectTest
 * 
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class SQLServerWallPermitObjectTest extends TestCase {

    /**
     * @param name
     */
    public SQLServerWallPermitObjectTest(String name){
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

    public void test_user() throws Exception {
        Assert.assertTrue(WallUtils.isValidateSqlServer("SELECT user;"));
    }

    public void test_user2() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT id from T where 1=1 and 1!=1 union select user;"));
    }

    public void test_system_user() throws Exception {
        Assert.assertTrue(WallUtils.isValidateSqlServer("SELECT system_user;"));
    }

    public void test_system_user2() throws Exception {
        Assert.assertFalse(WallUtils.isValidateSqlServer("SELECT id from T where 1=1 and 1!=1 union select system_user;"));
    }
}
