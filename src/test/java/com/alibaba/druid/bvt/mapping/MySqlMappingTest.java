package com.alibaba.druid.bvt.mapping;

import junit.framework.TestCase;

import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.mapping.Property;


public class MySqlMappingTest extends TestCase {
    MappingEngine engine = new MappingEngine();

    protected void setUp() throws Exception {
        Entity entity = new Entity();
        entity.setName("用户");
        entity.setTableName("user");

        entity.addProperty(new Property("名称", "", "uid"));
        entity.addProperty(new Property("昵称", "", "name"));

        engine.addEntity(entity);
    }
    
    public void test_0 () throws Exception {
        String oql = "select * from 用户 u where u.名称 = 'a'";
        
        String sql = engine.explainToSelectSQL(oql);
        
        System.out.println(sql);
    }
}
