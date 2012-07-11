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
package com.alibaba.druid.pool.ha;

import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.ConcurrentIdentityHashMap;

public class MultiDataSourceStatManager implements MultiDataSourceStatManagerMBean {

    private final static Log                                                    LOG         = LogFactory.getLog(MultiDataSourceStatManager.class);

    private final static MultiDataSourceStatManager                             instance    = new MultiDataSourceStatManager();

    private final AtomicLong                                                    resetCount  = new AtomicLong();

    // global instances
    private static final ConcurrentIdentityHashMap<MultiDataSource, ObjectName> dataSources = new ConcurrentIdentityHashMap<MultiDataSource, ObjectName>();

    private final static String                                                 MBEAN_NAME  = "com.alibaba.druid.ha:type=HADruidDataSourceStat";

    public static MultiDataSourceStatManager getInstance() {
        return instance;
    }

    public synchronized static void add(MultiDataSource dataSource) {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        if (dataSources.size() == 0) {
            try {
                mbeanServer.registerMBean(instance, new ObjectName(MBEAN_NAME));
            } catch (JMException ex) {
                LOG.error("register mbean error", ex);
            }
        }

        ObjectName objectName = null;
        if (dataSource.getNameInternal() != null) {
            try {
                objectName = new ObjectName("com.alibaba.druid.ha:type=DruidDataSource,id=" + dataSource.getNameInternal());
                mbeanServer.registerMBean(dataSource, objectName);
            } catch (JMException ex) {
                LOG.error("register mbean error", ex);
                objectName = null;
            }
        }

        if (objectName == null) {
            try {
                int id = System.identityHashCode(dataSource);
                objectName = new ObjectName("com.alibaba.druid.ha:type=DruidDataSource,id=" + id);
                mbeanServer.registerMBean(dataSource, objectName);
            } catch (JMException ex) {
                LOG.error("register mbean error", ex);
                objectName = null;
            }
        }

        dataSources.put(dataSource, objectName);
        dataSource.setObjectName(objectName);
    }

    public synchronized static void remove(MultiDataSource dataSource) {
        ObjectName objectName = dataSources.remove(dataSource);
        
        if (objectName == null) {
        	objectName = dataSource.getObjectName();
        }

        if (objectName == null) {
            LOG.error("unregister mbean failed. ");
            return;
        }

        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

        if (objectName != null) {
            try {
                mbeanServer.unregisterMBean(objectName);
            } catch (JMException ex) {
                LOG.error("unregister mbean error", ex);
            }
        }

        if (dataSources.size() == 0) {
            try {
                mbeanServer.unregisterMBean(new ObjectName(MBEAN_NAME));
            } catch (JMException ex) {
                LOG.error("unregister mbean error", ex);
            }
        }
    }

    public static Set<MultiDataSource> getDruidDataSourceInstances() {
        return dataSources.keySet();
    }

    public void reset() {
        final Set<MultiDataSource> dataSources = getDruidDataSourceInstances();
        for (MultiDataSource dataSource : dataSources) {
            dataSource.resetStat();
        }

        resetCount.incrementAndGet();
    }

    public long getResetCount() {
        return resetCount.get();
    }


}
