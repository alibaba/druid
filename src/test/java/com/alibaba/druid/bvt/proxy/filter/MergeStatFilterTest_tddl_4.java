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
import java.sql.PreparedStatement;

import javax.management.openmbean.TabularData;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.JdbcUtils;

public class MergeStatFilterTest_tddl_4 extends TestCase {

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
        dataSource.setFilters("mergeStat");
        dataSource.setDbType("mysql");
        dataSource.setConnectionProperties("druid.useGloalDataSourceStat");
        dataSource.setPoolPreparedStatements(true);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);

        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    public void test_merge() throws Exception {
        for (int i = 1000; i < 2000; ++i) {
            String tableName = "t_" + i;
            
            Connection conn = dataSource.getConnection();

            String sql = "update " + tableName + " SET a = ? WHERE b = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "aaa");
            stmt.setInt(1, 2);
            stmt.execute();
            stmt.close();

            conn.close();
        }

        Assert.assertEquals(1, dataSource.getDataSourceStat().getSqlStatMap().size());

    }

}
