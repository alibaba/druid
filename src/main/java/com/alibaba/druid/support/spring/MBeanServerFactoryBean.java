package com.alibaba.druid.support.spring;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import org.springframework.beans.factory.FactoryBean;

public class MBeanServerFactoryBean implements FactoryBean {

    public MBeanServer getObject() throws Exception {
        return ManagementFactory.getPlatformMBeanServer();
    }

    public Class<?> getObjectType() {
        return ManagementFactory.getPlatformMBeanServer().getClass();
    }

    public boolean isSingleton() {
        return true;
    }

}
