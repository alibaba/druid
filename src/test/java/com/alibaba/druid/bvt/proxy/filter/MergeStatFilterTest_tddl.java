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
package com.alibaba.druid.bvt.proxy.filter;

import java.sql.Connection;
import java.sql.Statement;

import javax.management.openmbean.TabularData;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.JdbcUtils;

public class MergeStatFilterTest_tddl extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        TabularData sqlList = JdbcStatManager.getInstance().getSqlList();
        if (sqlList.size() > 0) {
            for (Object item : JdbcStatManager.getInstance().getSqlList().values()) {
                String text = JSONUtils.toJSONString(item);
                System.out.println(text);
            }
        }
        
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setDbType("mysql");

//        {
//            Connection conn = dataSource.getConnection();
//            {
//                Statement stmt = conn.createStatement();
//                stmt.execute("select 1");
//            }
//            conn.close();
//            System.out.println(conn.getClass().getName());
//        }
        dataSource.setFilters("mergeStat");

        Connection conn = dataSource.getConnection();
        {
            Statement stmt = conn.createStatement();
            stmt.execute("select 1");
            System.out.println(stmt.getClass().getName());
        }
        conn.close();
        System.out.println(conn.getClass().getName());
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);

        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    public void test_merge() throws Exception {
        for (int i = 0; i < 100; ++i) {
            String tableName = "t_" + i;
            String sql = "select * from " + tableName + " where " + tableName + ".id = " + i;
            Connection conn = dataSource.getConnection();

            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            stmt.close();

            conn.close();
        }

        Assert.assertEquals(2, dataSource.getDataSourceStat().getSqlStatMap().size());

    }

}
