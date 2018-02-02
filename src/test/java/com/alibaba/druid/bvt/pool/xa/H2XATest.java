/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.pool.xa;

import javax.sql.XAConnection;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import com.alibaba.druid.pool.xa.DruidXADataSource;
import com.alibaba.druid.util.JdbcUtils;

public class H2XATest extends PoolTestCase {

    private DruidXADataSource dataSource;

    protected void setUp() throws Exception {
        super.setUp();

        dataSource = new DruidXADataSource();
        dataSource.setMinIdle(1);
        dataSource.setUrl("jdbc:h2:mem:test;");
        dataSource.setTestOnBorrow(false);

        JdbcUtils.execute(dataSource, "CREATE TABLE user (id INT, name VARCHAR(40))");

    }

    protected void tearDown() throws Exception {
        JdbcUtils.execute(dataSource, "DROP TABLE user");
        JdbcUtils.close(dataSource);

        super.tearDown();
    }

    public void test_0() throws Exception {
        XAConnection conn = dataSource.getXAConnection();
        conn.close();
    }
}
