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
package com.alibaba.druid.benckmark.pool.druid;

import java.sql.Connection;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidCase0 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxActive(8);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(1000 * 60 * 5);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_benchmark() throws Exception {
        for (int i = 0; i < 10; ++i) {
            long startMillis = System.currentTimeMillis();
            benchmark();
            long millis = System.currentTimeMillis() - startMillis;
            
            System.out.println("millis : " + millis);
        }
    }

    public void benchmark() throws Exception {
        for (int i = 0; i < 1000 * 1000 * 10; ++i) {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
    }

}
