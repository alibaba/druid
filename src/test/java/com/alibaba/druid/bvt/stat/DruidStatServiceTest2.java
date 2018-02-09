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
package com.alibaba.druid.bvt.stat;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Multiple data source test case.
 */
public class DruidStatServiceTest2 extends TestCase {

    private DruidDataSource dataSource;
    private DruidDataSource dataSource2;

    // every test, two data source initialized.
    public void setUp() throws Exception {
        // DruidStatService is singleton, reset all for other testcase.
        DruidStatService.getInstance().service("/reset-all.json");
        // mock datasource1
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);
        dataSource.init();
        // mock datasource2
        dataSource2 = new DruidDataSource();
        dataSource2.setUrl("jdbc:mock:xxx2");
        dataSource2.setFilters("stat");
        dataSource2.setTestOnBorrow(false);
        dataSource2.init();
    }

    public void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
        JdbcUtils.close(dataSource2);
    }

    public void test_statService_getSqlList() throws Exception {
        String sql = "select 1";

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        Thread.sleep(1);
        rs.close();
        stmt.close();
        conn.close();

        // second data source
        String sql2 = "select 1,1";
        conn = dataSource2.getConnection();
        stmt = conn.prepareStatement(sql2);
        rs = stmt.executeQuery();
        rs.next();
        Thread.sleep(1);
        rs.close();
        stmt.close();
        conn.close();

        String result = DruidStatService.getInstance().service("/sql.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);

        List<Map<String, Object>> sqlList = (List<Map<String, Object>>) resultMap.get("Content");

        assertThat(sqlList.size(), equalTo(2));
        for (Map<String, Object> sqlStat : sqlList) {
            assertThat((Integer) sqlStat.get("RunningCount"), equalTo(0));
            assertThat((Integer) sqlStat.get("ExecuteCount"), equalTo(1));
            assertThat((Integer) sqlStat.get("FetchRowCount"), equalTo(1));
            assertThat((Integer) sqlStat.get("EffectedRowCount"), equalTo(0));
        }
    }

    public void test_statService_getSqlById() throws Exception {
        String sql = "select 1";

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();
        stmt.close();
        conn.close();

        // second data source
        String sql2 = "select 2";
        conn = dataSource2.getConnection();
        stmt = conn.prepareStatement(sql2);
        rs = stmt.executeQuery();
        rs.next();
        rs.close();
        stmt.close();
        conn.close();

        long id = dataSource.getSqlStatMap().values().iterator().next().getId();
        String result = DruidStatService.getInstance().service("/sql-" + id + ".json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        Map<String, Object> sqlStat = (Map<String, Object>) resultMap.get("Content");

        assertThat((Integer) sqlStat.get("RunningCount"), equalTo(0));
        assertThat((Integer) sqlStat.get("ExecuteCount"), equalTo(1));
        assertThat((Integer) sqlStat.get("FetchRowCount"), equalTo(1));
        assertThat((Integer) sqlStat.get("EffectedRowCount"), equalTo(0));
        assertThat((String) sqlStat.get("SQL"), equalTo(sql));

        id = dataSource2.getSqlStatMap().values().iterator().next().getId();
        result = DruidStatService.getInstance().service("/sql-" + id + ".json");
        resultMap = (Map<String, Object>) JSONUtils.parse(result);
        sqlStat = (Map<String, Object>) resultMap.get("Content");

        assertThat((Integer) sqlStat.get("RunningCount"), equalTo(0));
        assertThat((Integer) sqlStat.get("ExecuteCount"), equalTo(1));
        assertThat((Integer) sqlStat.get("FetchRowCount"), equalTo(1));
        assertThat((Integer) sqlStat.get("EffectedRowCount"), equalTo(0));
        assertThat((String) sqlStat.get("SQL"), equalTo(sql2));

        String result2 = DruidStatService.getInstance().service("/sql-" + Integer.MAX_VALUE + ".json");
        resultMap = (Map<String, Object>) JSONUtils.parse(result2);
        assertThat(resultMap.get("Content"), is(nullValue()));
    }

    public void test_statService_getDataSourceList() throws Exception {
        DruidStatService.getInstance().service("/reset-all.json");
        String result = DruidStatService.getInstance().service("/datasource.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        List<Map<String, Object>> dataSourceList = (List<Map<String, Object>>) resultMap.get("Content");

        //assertThat(dataSourceList.size(), equalTo(2));

        Map<String, Object> dataSourceStat = dataSourceList.get(0);
        //assertThat((Integer) dataSourceStat.get("PoolingCount"), equalTo(0));
        //assertThat((Integer) dataSourceStat.get("ActiveCount"), equalTo(0));
    }
    
    public void test_getWallStatMap() throws Exception {
        DruidStatService.getInstance().getWallStatMap(Collections.<String, String>emptyMap());
    }

}
