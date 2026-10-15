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

import com.alibaba.druid.wall.WallUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQLServerWallPermitObjectTest
 *
 * @author RaymondXiu
 * @version 1.0, 2012-3-18
 * @see
 */
public class SQLServerWallPermitObjectTest {
    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @BeforeEach
    protected void setUp() throws Exception {
    }

    /*
     * (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    @AfterEach
    protected void tearDown() throws Exception {
    }

    @Test
    public void test_user() throws Exception {
        assertTrue(WallUtils.isValidateSqlServer("SELECT user;"));
    }

    @Test
    public void test_user2() throws Exception {
        assertFalse(WallUtils.isValidateSqlServer("SELECT id from T where 1=1 and 1!=1 union select user;"));
    }

    @Test
    public void test_system_user() throws Exception {
        assertTrue(WallUtils.isValidateSqlServer("SELECT system_user;"));
    }

    @Test
    public void test_system_user2() throws Exception {
        assertFalse(WallUtils.isValidateSqlServer("SELECT id from T where 1=1 and 1!=1 union select system_user;"));
    }
}
