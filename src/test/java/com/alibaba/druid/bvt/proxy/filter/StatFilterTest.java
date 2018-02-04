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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.support.json.JSONUtils;

public class StatFilterTest extends TestCase {

    public void setUp() throws Exception {
        JdbcStatManager.getInstance().reset();
    }

    public void tearDown() throws Exception {
        JdbcStatManager.getInstance().reset();
        
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    public void test_stat() throws Exception {
        String url = "jdbc:wrap-jdbc:filters=default:jdbc:mock:xx";
        Connection conn = DriverManager.getConnection(url);

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1");
        while (rs.next()) {
            rs.getInt(1);
        }
        rs.close();
        stmt.close();

        conn.close();

        TabularData sqlList = JdbcStatManager.getInstance().getSqlList();
        Assert.assertEquals(true, sqlList.size() > 0);

        int count = 0;
        for (Object item : sqlList.values()) {
            CompositeData row = (CompositeData) item;
            if (url.equals((String) row.get("URL"))) {
                count++;
            }
            long[] histogram = (long[]) row.get("Histogram");
            Assert.assertEquals(0L, histogram[histogram.length - 1]);
        }
        Assert.assertEquals(true, count > 0);

        System.out.println(JSONUtils.toJSONString(sqlList));
    }
}
