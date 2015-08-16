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
package com.alibaba.druid;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcTraceManager;
import com.alibaba.druid.util.JMXUtils;

@SuppressWarnings("deprecation")
public class TestTraceFilter extends TestCase {

    public void test_loop() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setFilters("stat,trace");
        dataSource.setUrl("jdbc:mock:");

        JMXUtils.register("com.alibaba.dragoon:type=JdbcTraceManager", JdbcTraceManager.getInstance());

        for (int i = 0; i < 1000; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            rs.next();
            rs.close();
            stmt.close();
            conn.close();

            Thread.sleep(1000);
        }
        dataSource.close();
    }
}
