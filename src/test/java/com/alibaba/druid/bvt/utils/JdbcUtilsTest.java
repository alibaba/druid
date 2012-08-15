package com.alibaba.druid.bvt.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class JdbcUtilsTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setMinIdle(1);
        dataSource.setUrl("jdbc:h2:mem:test;");
        dataSource.setTestOnBorrow(false);

        JdbcUtils.execute(dataSource, "CREATE TABLE user (id INT, name VARCHAR(40))");

    }

    protected void tearDown() throws Exception {
        JdbcUtils.execute(dataSource, "DROP TABLE user");
        JdbcUtils.close(dataSource);
    }

    public void test_curd() throws Exception {
        {
            List<Map<String, Object>> list = JdbcUtils.executeQuery(dataSource, "select * from user");
            Assert.assertEquals(0, list.size());
        }
        {
            Map<String, Object> data = new HashMap<String, Object>();
            data.put("id", 123);
            data.put("name", "高傲的羊");
            JdbcUtils.insertToTable(dataSource, "user", data);
        }
        {
            List<Map<String, Object>> list = JdbcUtils.executeQuery(dataSource, "select * from user");
            Assert.assertEquals(1, list.size());
            Map<String, Object> data = list.get(0);
            
            Assert.assertEquals(123, data.get("ID"));
            Assert.assertEquals("高傲的羊", data.get("NAME"));
        }
        {
            List<Map<String, Object>> list = JdbcUtils.executeQuery(dataSource, "select id \"id\", name \"name\" from user");
            Assert.assertEquals(1, list.size());
            Map<String, Object> data = list.get(0);
            
            Assert.assertEquals(123, data.get("id"));
            Assert.assertEquals("高傲的羊", data.get("name"));
        }
        {
            JdbcUtils.executeUpdate(dataSource, "delete from user");
        }
        {
            List<Map<String, Object>> list = JdbcUtils.executeQuery(dataSource, "select * from user");
            Assert.assertEquals(0, list.size());
        }
    }
}
