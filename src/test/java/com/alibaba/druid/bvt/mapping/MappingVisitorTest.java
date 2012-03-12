package com.alibaba.druid.bvt.mapping;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mapping.Entity;
import com.alibaba.druid.mapping.MappingEngine;
import com.alibaba.druid.mapping.Property;

public class MappingVisitorTest extends TestCase {

    public void test_0() throws Exception {
        MappingEngine engine = new MappingEngine();

        {
            Entity entity = new Entity();
            entity.setName("设备");
            entity.setDescription("Device");
            entity.setTableName("device");

            entity.addProperty(new Property("编号", "", "serviceTag"));
            entity.addProperty(new Property("IP地址", "", "ip"));

            engine.addEntity(entity);
        }

        Assert.assertEquals("SELECT serviceTag AS \"编号\", ip AS \"IP地址\"\nFROM device", engine.explain("select *"));
        Assert.assertEquals("SELECT serviceTag AS \"编号\", ip AS \"IP地址\"\nFROM device\nWHERE ip = '127.0.0.1'",
                            engine.explain("select 编号, IP地址 WHERE IP地址 = '127.0.0.1'"));

    }
}
