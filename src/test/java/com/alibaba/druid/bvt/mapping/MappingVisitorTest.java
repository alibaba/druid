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

import org.junit.Assert;

import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.mapping.Property;

public class MappingVisitorTest extends TestCase {

    MappingEngine engine = new MappingEngine();

    protected void setUp() throws Exception {
        Entity entity = new Entity();
        entity.setName("设备");
        entity.setDescription("Device");
        entity.setTableName("device");

        entity.addProperty(new Property("编号", "", "serviceTag"));
        entity.addProperty(new Property("IP地址", "", "ip"));

        engine.addEntity(entity);
    }

    public void test_0() throws Exception {

        Assert.assertEquals("SELECT *\nFROM device",
                            engine.explainToSelectSQL("select *"));
        Assert.assertEquals("SELECT serviceTag, ip\nFROM device\nWHERE ip = '127.0.0.1'",
                            engine.explainToSelectSQL("select 编号, IP地址 WHERE IP地址 = '127.0.0.1'"));
        Assert.assertEquals("SELECT *\nFROM device\nWHERE ip = '127.0.0.1'",
                            engine.explainToSelectSQL("WHERE IP地址 = '127.0.0.1'"));

    }
    
    public void test_delete() throws Exception {
        Assert.assertEquals("DELETE FROM device\nWHERE ip = '127.0.0.1'",
                            engine.explainToDeleteSQL("WHERE IP地址 = '127.0.0.1'"));
    }
    
    public void test_update() throws Exception {
        Assert.assertEquals("UPDATE device\nSET serviceTag = ?\nWHERE ip = '127.0.0.1'",
                            engine.explainToUpdateSQL("SET 编号 = ? WHERE IP地址 = '127.0.0.1'"));
    }
    
    public void test_insert() throws Exception {
        Assert.assertEquals("INSERT INTO device (serviceTag, ip)\nVALUES (?, ?)",
                            engine.explainToInsertSQL("(编号, IP地址) VALUES (?, ?)"));
    }

    public void test_1() throws Exception {
        engine.setMaxLimit(10);

        Assert.assertEquals("SELECT *\nFROM device\nLIMIT 10",
                            engine.explainToSelectSQL("select *"));
        Assert.assertEquals("SELECT serviceTag, ip\nFROM device\nWHERE ip = '127.0.0.1'\nLIMIT 10",
                            engine.explainToSelectSQL("select 编号, IP地址 WHERE IP地址 = '127.0.0.1'"));
        Assert.assertEquals("SELECT serviceTag, ip\nFROM device\nWHERE ip = '127.0.0.1'\nLIMIT 3",
                            engine.explainToSelectSQL("select 编号, IP地址 WHERE IP地址 = '127.0.0.1' limit 3"));
        Assert.assertEquals(0, engine.exportParameters(engine.explainToSelectSQLObject("select 1")).size());
        Assert.assertEquals(0,
                            engine.exportParameters(engine.explainToSelectSQLObject("select 编号, IP地址 where 1 = 0")).size());
        Assert.assertEquals(1,
                            engine.exportParameters(engine.explainToSelectSQLObject("select 编号, IP地址 where 1 = 0 and IP地址 = '127.0.0.1'")).size());
        Assert.assertEquals("127.0.0.1",
                            engine.exportParameters(engine.explainToSelectSQLObject("select 编号, IP地址 where 1 = 0 and IP地址 = '127.0.0.1'")).get(0));

        engine.setMaxLimit(null);

    }
}
