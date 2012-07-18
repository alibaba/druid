package com.alibaba.druid.bvt.jmx;

import java.lang.management.ManagementFactory;

import javax.management.ObjectName;

import junit.framework.Assert;
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
