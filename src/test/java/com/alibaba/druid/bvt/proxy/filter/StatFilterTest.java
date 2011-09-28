package com.alibaba.druid.bvt.proxy.filter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.fastjson.JSON;

public class StatFilterTest extends TestCase {

    public void setUp() throws Exception {
        JdbcStatManager.getInstance().reset();
    }

    public void tearDown() throws Exception {
        JdbcStatManager.getInstance().reset();
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

        System.out.println(JSON.toJSONString(sqlList));
    }
}
