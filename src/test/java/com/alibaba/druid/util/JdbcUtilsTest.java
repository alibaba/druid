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
package com.alibaba.druid.util;

import junit.framework.TestCase;

/**
 * test for support cobar driver
 * 
 * @author zhoujh
 */
public class JdbcUtilsTest extends TestCase {

    /**
     * Test method for {@link com.alibaba.druid.util.JdbcUtils#getDbType(java.lang.String, java.lang.String)}.
     */
    public void testGetDbType() {
        String jdbcUrl = "jdbc:cobar://localhost:8066/test";
        String dbType = JdbcUtils.getDbType(jdbcUrl, null);
        assertEquals("not support cobar driver, url like jdbc:cobar:...", JdbcConstants.MYSQL, dbType);
    }
    
    public void test_log4jdbc_mysql() {
        String jdbcUrl = "jdbc:log4jdbc:mysql://localhost:8066/test";
        String dbType = JdbcUtils.getDbType(jdbcUrl, null);
        assertEquals("not support log4jdbc mysql, url like jdbc:log4jdbc:mysql:...", JdbcConstants.MYSQL, dbType);
    }

}
