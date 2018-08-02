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
package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class Case1 extends PoolTestCase {

    public void test_f() throws Exception {
        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setTimeBetweenConnectErrorMillis(100);

        final long startTime = System.currentTimeMillis();
        final long okTime = startTime + 1000 * 1;

        dataSource.setDriver(new MockDriver() {

            @Override
            public Connection connect(String url, Properties info) throws SQLException {
                if (System.currentTimeMillis() < okTime) {
                    throw new SQLException();
                }

                return super.connect(url, info);
            }
        });
        dataSource.setUrl("jdbc:mock:");

        dataSource.setMinIdle(0);
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);

        Connection conn = dataSource.getConnection();
        conn.close();

        dataSource.close();
    }
}
