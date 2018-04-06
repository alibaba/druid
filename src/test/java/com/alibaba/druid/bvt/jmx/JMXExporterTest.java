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
package com.alibaba.druid.bvt.jmx;

import java.lang.management.ManagementFactory;

import javax.management.ObjectName;

import org.junit.Assert;
import junit.framework.TestCase;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JMXExporterTest extends TestCase {

    public void test_export() throws Exception {
        String file = "com/alibaba/druid/jmx/spring_stat_export.xml";

        ObjectName objectName = new ObjectName("com.alibaba.druid:type=JdbcStatManager");

        Assert.assertFalse(ManagementFactory.getPlatformMBeanServer().isRegistered(objectName)); // before jmx register

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(file);

        Assert.assertTrue(ManagementFactory.getPlatformMBeanServer().isRegistered(objectName)); // after jmx register

        context.close();

        Assert.assertFalse(ManagementFactory.getPlatformMBeanServer().isRegistered(objectName));
    }
}
