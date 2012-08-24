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
