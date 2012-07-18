package com.alibaba.druid.support.spring;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;

import org.springframework.beans.factory.FactoryBean;

public class MBeanServerFactoryBean implements FactoryBean<MBeanServer> {

    public MBeanServer getObject() throws Exception {
        return ManagementFactory.getPlatformMBeanServer();
    }

    public Class<?> getObjectType() {
        return MBeanServer.class;
    }

    public boolean isSingleton() {
        return true;
    }

}
