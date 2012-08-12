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
package com.alibaba.druid.stat;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.ConcurrentIdentityHashMap;
import com.alibaba.druid.util.JMXUtils;

public class DruidDataSourceStatManager implements DruidDataSourceStatManagerMBean {

    private final static Log                                                    LOG         = LogFactory.getLog(DruidDataSourceStatManager.class);

    private final static DruidDataSourceStatManager                             instance    = new DruidDataSourceStatManager();

    private final AtomicLong                                                    resetCount  = new AtomicLong();

    // global instances
    private static final ConcurrentIdentityHashMap<DruidDataSource, ObjectName> dataSources = new ConcurrentIdentityHashMap<DruidDataSource, ObjectName>();

    private final static String                                                 MBEAN_NAME  = "com.alibaba.druid:type=DruidDataSourceStat";

    public static DruidDataSourceStatManager getInstance() {
        return instance;
    }
    
    public static void cear() {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        for (Map.Entry<DruidDataSource, ObjectName> entry : dataSources.entrySet()) {
            ObjectName objectName = entry.getValue();
            if (objectName == null) {
                continue;
            }
            try {
                mbeanServer.unregisterMBean(objectName);
            } catch (JMException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        dataSources.clear();
    }

    public synchronized static ObjectName add(DruidDataSource dataSource, String name) {
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        if (dataSources.size() == 0) {
            try {

                ObjectName objectName = new ObjectName(MBEAN_NAME);
                if (!mbeanServer.isRegistered(objectName)) {
                    mbeanServer.registerMBean(instance, objectName);
                }
            } catch (JMException ex) {
                LOG.error("register mbean error", ex);
            }
        }

        ObjectName objectName = null;
        if (name != null) {
            try {
                objectName = new ObjectName("com.alibaba.druid:type=DruidDataSource,id=" + name);
                mbeanServer.registerMBean(dataSource, objectName);
            } catch (JMException ex) {
                LOG.error("register mbean error", ex);
                objectName = null;
            }
        }

        if (objectName == null) {
            try {
                int id = System.identityHashCode(dataSource);
                objectName = new ObjectName("com.alibaba.druid:type=DruidDataSource,id=" + id);
                mbeanServer.registerMBean(dataSource, objectName);
            } catch (JMException ex) {
                LOG.error("register mbean error", ex);
                objectName = null;
            }
        }

        dataSources.put(dataSource, objectName);
        return objectName;
    }

    public synchronized static void remove(DruidDataSource dataSource) {
        ObjectName objectName = dataSources.remove(dataSource);

        if (objectName == null) {
            objectName = dataSource.getObjectName();
        }

        if (objectName == null) {
            LOG.error("unregister mbean failed. url " + dataSource.getUrl());
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

    public static Set<DruidDataSource> getDruidDataSourceInstances() {
        return dataSources.keySet();
    }

    public void reset() {
        final Set<DruidDataSource> dataSources = getDruidDataSourceInstances();
        for (DruidDataSource dataSource : dataSources) {
            dataSource.resetStat();
        }

        resetCount.incrementAndGet();
    }

    public long getResetCount() {
        return resetCount.get();
    }

    public TabularData getDataSourceList() throws JMException {
        CompositeType rowType = getDruidDataSourceCompositeType();
        String[] indexNames = rowType.keySet().toArray(new String[rowType.keySet().size()]);

        TabularType tabularType = new TabularType("DruidDataSourceStat", "DruidDataSourceStat", rowType, indexNames);
        TabularData data = new TabularDataSupport(tabularType);

        final Set<DruidDataSource> dataSources = getDruidDataSourceInstances();
        for (DruidDataSource dataSource : dataSources) {
            data.put(getCompositeData(dataSource));
        }

        return data;
    }

    public CompositeDataSupport getCompositeData(DruidDataSource dataSource) throws JMException {
        CompositeType rowType = getDruidDataSourceCompositeType();

        Map<String, Object> map = new HashMap<String, Object>();

        // 0 - 4
        map.put("Name", dataSource.getName());
        map.put("URL", dataSource.getUrl());
        map.put("CreateCount", dataSource.getCreateCount());
        map.put("DestroyCount", dataSource.getDestroyCount());
        map.put("ConnectCount", dataSource.getConnectCount());

        // 5 - 9
        map.put("CloseCount", dataSource.getCloseCount());
        map.put("ActiveCount", dataSource.getActivePeak());
        map.put("PoolingCount", dataSource.getPoolingCount());
        map.put("LockQueueLength", dataSource.getLockQueueLength());
        map.put("WaitThreadCount", dataSource.getNotEmptyWaitThreadPeak());

        // 10 - 14
        map.put("InitialSize", dataSource.getInitialSize());
        map.put("MaxActive", dataSource.getMaxActive());
        map.put("MinIdle", dataSource.getMinIdle());
        map.put("PoolPreparedStatements", dataSource.isPoolPreparedStatements());
        map.put("TestOnBorrow", dataSource.isTestOnBorrow());

        // 15 - 19
        map.put("TestOnReturn", dataSource.isTestOnReturn());
        map.put("MinEvictableIdleTimeMillis", dataSource.getMinEvictableIdleTimeMillis());
        map.put("ConnectErrorCount", dataSource.getConnectErrorCount());
        map.put("CreateTimespanMillis", dataSource.getCreateTimespanMillis());
        map.put("DbType", dataSource.getDbType());

        // 20 - 24
        map.put("ValidationQuery", dataSource.getValidationQuery());
        map.put("ValidationQueryTimeout", dataSource.getValidationQueryTimeout());
        map.put("DriverClassName", dataSource.getDriverClassName());
        map.put("Username", dataSource.getUsername());
        map.put("RemoveAbandonedCount", dataSource.getRemoveAbandonedCount());

        // 25 - 29
        map.put("NotEmptyWaitCount", dataSource.getNotEmptyWaitCount());
        map.put("NotEmptyWaitNanos", dataSource.getNotEmptyWaitNanos());
        map.put("ErrorCount", dataSource.getErrorCount());
        map.put("ReusePreparedStatementCount", dataSource.getCachedPreparedStatementHitCount());
        map.put("StartTransactionCount", dataSource.getStartTransactionCount());

        // 30 - 34
        map.put("CommitCount", dataSource.getCommitCount());
        map.put("RollbackCount", dataSource.getRollbackCount());
        map.put("LastError", JMXUtils.getErrorCompositeData(dataSource.getLastError()));
        map.put("LastCreateError", JMXUtils.getErrorCompositeData(dataSource.getLastCreateError()));
        map.put("PreparedStatementCacheDeleteCount", dataSource.getCachedPreparedStatementDeleteCount());

        // 35 - 39
        map.put("PreparedStatementCacheAccessCount", dataSource.getCachedPreparedStatementAccessCount());
        map.put("PreparedStatementCacheMissCount", dataSource.getCachedPreparedStatementMissCount());
        map.put("PreparedStatementCacheHitCount", dataSource.getCachedPreparedStatementHitCount());
        map.put("PreparedStatementCacheCurrentCount", dataSource.getCachedPreparedStatementCount());
        map.put("Version", dataSource.getVersion());

        // 40 -
        map.put("LastErrorTime", dataSource.getLastErrorTime());
        map.put("LastCreateErrorTime", dataSource.getLastCreateErrorTime());
        map.put("CreateErrorCount", dataSource.getCreateErrorCount());
        map.put("DiscardCount", dataSource.getDiscardCount());

        return new CompositeDataSupport(rowType, map);
    }

    private static CompositeType COMPOSITE_TYPE = null;

    public static CompositeType getDruidDataSourceCompositeType() throws JMException {

        if (COMPOSITE_TYPE != null) {
            return COMPOSITE_TYPE;
        }

        OpenType<?>[] indexTypes = new OpenType<?>[] {
                // 0 - 4
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //

                // 5 - 9
                SimpleType.LONG, //
                SimpleType.INTEGER, //
                SimpleType.INTEGER, //
                SimpleType.INTEGER, //
                SimpleType.INTEGER, //

                // 10 - 14
                SimpleType.INTEGER, //
                SimpleType.INTEGER, //
                SimpleType.INTEGER, //
                SimpleType.BOOLEAN, //
                SimpleType.BOOLEAN, //

                // 15 - 19
                SimpleType.BOOLEAN, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.STRING, //

                // 20 - 24
                SimpleType.STRING, //
                SimpleType.INTEGER, //
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.LONG, //

                // 25 - 29
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //

                // 30 - 34
                SimpleType.LONG, //
                SimpleType.LONG, //
                JMXUtils.getThrowableCompositeType(), //
                JMXUtils.getThrowableCompositeType(), //
                SimpleType.LONG, //

                // 35 - 39
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.STRING, //

                // 40 -
                SimpleType.DATE, //
                SimpleType.DATE, //
                SimpleType.LONG, //
                SimpleType.LONG //
        //
        };

        String[] indexNames = {
                // 0 - 4
                "Name", //
                "URL", //
                "CreateCount", //
                "DestroyCount", //
                "ConnectCount", //

                // 5 - 9
                "CloseCount", //
                "ActiveCount", //
                "PoolingCount", //
                "LockQueueLength", //
                "WaitThreadCount", //

                // 10 - 14
                "InitialSize", //
                "MaxActive", //
                "MinIdle", //
                "PoolPreparedStatements", //
                "TestOnBorrow", //

                // 15 - 19
                "TestOnReturn", //
                "MinEvictableIdleTimeMillis", //
                "ConnectErrorCount", //
                "CreateTimespanMillis", //
                "DbType", //

                // 20 - 24
                "ValidationQuery", //
                "ValidationQueryTimeout", //
                "DriverClassName", //
                "Username", //
                "RemoveAbandonedCount", //

                // 25 - 29
                "NotEmptyWaitCount", //
                "NotEmptyWaitNanos", //
                "ErrorCount", //
                "ReusePreparedStatementCount", //
                "StartTransactionCount", //

                // 30 - 34
                "CommitCount", //
                "RollbackCount", //
                "LastError", //
                "LastCreateError", //
                "PreparedStatementCacheDeleteCount", //

                // 35 - 39
                "PreparedStatementCacheAccessCount", //
                "PreparedStatementCacheMissCount", //
                "PreparedStatementCacheHitCount", //
                "PreparedStatementCacheCurrentCount", //
                "Version" //

                // 40 -
                , "LastErrorTime", //
                "LastCreateErrorTime", //
                "CreateErrorCount", //
                "DiscardCount", //
        //
        };

        String[] indexDescriptions = indexNames;
        COMPOSITE_TYPE = new CompositeType("DataSourceStatistic", "DataSource Statistic", indexNames,
                                           indexDescriptions, indexTypes);

        return COMPOSITE_TYPE;
    }
}
