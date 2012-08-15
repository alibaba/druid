package com.alibaba.druid.bvt.mapping;

import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class MySqlMappingEngineTest extends TestCase {

    private DruidDataSource dataSource;
    private MappingEngine   mapping = new MappingEngine();

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setMinIdle(1);
        dataSource.setUrl("jdbc:h2:mem:test;");
        dataSource.setTestOnBorrow(false);

        JdbcUtils.execute(dataSource, "CREATE TABLE user (id INT, name VARCHAR(40))");

        mapping.setDataSource(dataSource);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.execute(dataSource, "DROP TABLE user");
        JdbcUtils.close(dataSource);
    }

    public void test_curd() throws Exception {
        {
            List<Map<String, Object>> list = mapping.select("select * from user");
            Assert.assertEquals(0, list.size());
        }

        mapping.insert("insert into user (id, name) values (?, ?)", 123, "wenshao");
        
        {
            List<Map<String, Object>> list = mapping.select("select * from user");
            Assert.assertEquals(1, list.size());
            
            Map<String, Object> data = list.get(0);
            Assert.assertEquals(123, data.get("ID"));
            Assert.assertEquals("wenshao", data.get("NAME"));
        }
        
        {
            List<Map<String, Object>> list = mapping.select("select id \"id\", name \"name\" from user");
            Assert.assertEquals(1, list.size());
            
            Map<String, Object> data = list.get(0);
            Assert.assertEquals(123, data.get("id"));
            Assert.assertEquals("wenshao", data.get("name"));
        }
        
        mapping.delete("delete from user");
        
        {
            List<Map<String, Object>> list = mapping.select("select * from user");
            Assert.assertEquals(0, list.size());
        }
    }
}
