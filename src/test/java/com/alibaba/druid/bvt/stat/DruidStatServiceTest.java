/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidStatService;
import com.alibaba.druid.support.json.JSONUtils;
import com.alibaba.druid.util.JdbcUtils;

public class DruidStatServiceTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_statService() throws Exception {
        String sql = "select 1";
        Connection conn = dataSource.getConnection();
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();
        stmt.close();
        
        conn.close();
        
        String result = DruidStatService.getInstance().service("/sql.json");
        Map<String, Object> resultMap = (Map<String, Object>) JSONUtils.parse(result);
        
        List<Map<String, Object>> sqlList = (List<Map<String, Object>>) resultMap.get("Content");
        
        Assert.assertEquals(1, sqlList.size());
    }
}
