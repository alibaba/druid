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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.ConcurrentIdentityHashMap;

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

    public synchronized static void add(DruidDataSource dataSource) {
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
                objectName = new ObjectName("com.alibaba.druid:type=DruidDataSource,id=" + dataSource.getNameInternal());
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
    }

    public synchronized static void remove(DruidDataSource dataSource) {
        ObjectName objectName = dataSources.remove(dataSource);
        
        if (objectName == null) {
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

        map.put("Name", dataSource.getName());
        map.put("URL", dataSource.getUrl());
        map.put("CreateCount", dataSource.getCreateCount());
        map.put("DestroyCount", dataSource.getDestroyCount());
        map.put("ConnectCount", dataSource.getConnectCount());

        map.put("CloseCount", dataSource.getCloseCount());
        map.put("ActiveCount", dataSource.getActiveCount());
        map.put("PoolingCount", dataSource.getPoolingCount());
        map.put("LockQueueLength", dataSource.getLockQueueLength());
        map.put("WaitThreadCount", dataSource.getWaitThreadCount());

        map.put("InitialSize", dataSource.getInitialSize());
        map.put("MaxActive", dataSource.getMaxActive());
        map.put("MinIdle", dataSource.getMinIdle());
        map.put("PoolPreparedStatements", dataSource.isPoolPreparedStatements());
        map.put("TestOnBorrow", dataSource.isTestOnBorrow());

        map.put("TestOnReturn", dataSource.isTestOnReturn());
        map.put("MinEvictableIdleTimeMillis", dataSource.getMinEvictableIdleTimeMillis());
        map.put("ConnectErrorCount", dataSource.getConnectErrorCount());
        map.put("CreateTimespanMillis", dataSource.getCreateTimespanMillis());
        map.put("DbType", dataSource.getDbType());

        map.put("ValidationQuery", dataSource.getValidationQuery());
        map.put("ValidationQueryTimeout", dataSource.getValidationQueryTimeout());
        map.put("DriverClassName", dataSource.getDriverClassName());
        map.put("Username", dataSource.getUsername());
        map.put("RemoveAbandonedCount", dataSource.getRemoveAbandonedCount());

        map.put("NotEmptyWaitCount", dataSource.getNotEmptyWaitCount());
        map.put("NotEmptyWaitNanos", dataSource.getNotEmptyWaitNanos());
        map.put("ErrorCount", dataSource.getErrorCount());
        map.put("ReusePreparedStatementCount", dataSource.getReusePreparedStatementCount());
        map.put("StartTransactionCount", dataSource.getStartTransactionCount());
        
        map.put("CommitCount", dataSource.getCommitCount());
        map.put("RollbackCount", dataSource.getRollbackCount());

        return new CompositeDataSupport(rowType, map);
    }

    private static CompositeType COMPOSITE_TYPE = null;

    public static CompositeType getDruidDataSourceCompositeType() throws JMException {

        if (COMPOSITE_TYPE != null) {
            return COMPOSITE_TYPE;
        }

        OpenType<?>[] indexTypes = new OpenType<?>[] {
                //
                SimpleType.STRING, SimpleType.STRING, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, //
                SimpleType.LONG, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER, //
                SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.BOOLEAN, SimpleType.BOOLEAN, //
                SimpleType.BOOLEAN, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.STRING, //
                SimpleType.STRING, SimpleType.INTEGER, SimpleType.STRING, SimpleType.STRING, SimpleType.LONG, //
                SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG//
                , SimpleType.LONG, SimpleType.LONG
        //
        };

        String[] indexNames = {
                //
                "Name", "URL", "CreateCount", "DestroyCount", "ConnectCount", //
                "CloseCount", "ActiveCount", "PoolingCount", "LockQueueLength", "WaitThreadCount", //
                "InitialSize", "MaxActive", "MinIdle", "PoolPreparedStatements", "TestOnBorrow", //
                "TestOnReturn", "MinEvictableIdleTimeMillis", "ConnectErrorCount", "CreateTimespanMillis", "DbType", //
                "ValidationQuery", "ValidationQueryTimeout", "DriverClassName", "Username", "RemoveAbandonedCount", //
                "NotEmptyWaitCount", "NotEmptyWaitNanos", "ErrorCount", "ReusePreparedStatementCount", "StartTransactionCount", //
                "CommitCount", "RollbackCount"
        //
        };

        String[] indexDescriptions = indexNames;
        COMPOSITE_TYPE = new CompositeType("DataSourceStatistic", "DataSource Statistic", indexNames,
                                           indexDescriptions, indexTypes);

        return COMPOSITE_TYPE;
    }
}
