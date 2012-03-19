/*
 * Copyright 2011 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.druid.spring;

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
