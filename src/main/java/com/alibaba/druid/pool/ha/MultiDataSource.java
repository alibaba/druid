package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.management.ObjectName;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.DataSourceAdapter;
import com.alibaba.druid.pool.ha.balance.Balancer;
import com.alibaba.druid.pool.ha.balance.WeightBalancer;
import com.alibaba.druid.pool.ha.config.ConfigLoader;
import com.alibaba.druid.pool.ha.valid.DataSourceFailureDetecter;
import com.alibaba.druid.pool.ha.valid.DefaultDataSourceFailureDetecter;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.util.JdbcUtils;

public class MultiDataSource extends DataSourceAdapter implements MultiDataSourceMBean, DataSourceProxy {

    private final static Log                        LOG                       = LogFactory.getLog(MultiDataSource.class);

    private Properties                              properties                = new Properties();

    private final AtomicLong                        connectionIdSeed          = new AtomicLong();
    private final AtomicLong                        statementIdSeed           = new AtomicLong();
    private final AtomicLong                        resultSetIdSeed           = new AtomicLong();
    private final AtomicLong                        transactionIdSeed         = new AtomicLong();

    private final AtomicLong                        configLoadCount           = new AtomicLong();
    private final AtomicLong                        failureDetectCount        = new AtomicLong();

    private final AtomicLong                        busySkipCount             = new AtomicLong();
    private final AtomicLong                        retryGetConnectionCount   = new AtomicLong();

    protected DataSourceFailureDetecter             failureDetector           = new DefaultDataSourceFailureDetecter();
    private long                                    failureDetectPeriodMillis = 3000;
    private long                                    configLoadPeriodMillis    = 1000 * 60;

    private int                                     schedulerThreadCount      = 3;
    private ScheduledExecutorService                scheduler;

    private ScheduledFuture<?>                      failureDetectFuture;
    private ScheduledFuture<?>                      configLoadFuture;

    private boolean                                 inited                    = false;
    protected final Lock                            lock                      = new ReentrantLock();
    protected final Condition                       notFull                   = lock.newCondition();
    protected final Condition                       notFail                   = lock.newCondition();

    private ConcurrentMap<String, DataSourceHolder> dataSources               = new ConcurrentHashMap<String, DataSourceHolder>();

    private ObjectName                              objectName;

    private List<Filter>                            filters                   = new ArrayList<Filter>();

    private boolean                                 enable;

    private String                                  name;

    private ConfigLoader                            configLoader;

    private int                                     maxPoolSize               = 50;

    private long                                    activeCount               = 0;
    private long                                    maxWaitMillis             = 0;

    private Balancer                                balancer                  = new WeightBalancer();

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public void setMaxWait(long seconds) {
        this.setMaxWaitMillis(1000 * seconds);
    }

    public long getMaxWait() {
        return this.getMaxWaitMillis() / 1000;
    }

    public int getMaxPoolSize() {
        return maxPoolSize;
    }

    public void setMaxPoolSize(int maxPoolSize) throws SQLException {
        if (this.isIntited()) {
            throw new SQLException("dataSource inited");
        }

        this.maxPoolSize = maxPoolSize;
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public void setConfigLoader(ConfigLoader configLoader) {
        this.configLoader = configLoader;
    }

    public String getName() {
        if (name == null) {
            return "HADataSource-" + System.identityHashCode(this);
        }
        return name;
    }

    public String getNameInternal() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIntited() {
        return this.inited;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    public void setObjectName(ObjectName objectName) {
        this.objectName = objectName;
    }

    public MultiDataSource(){

    }

    public void init() throws SQLException {
        if (inited) {
            return;
        }

        lock.lock();
        try {
            if (inited) {
                return;
            }

            this.balancer.init(this);
            
            initInternal();

            scheduler = Executors.newScheduledThreadPool(schedulerThreadCount);
            startFailureDetectScheduleTask();
            startConfigLoadScheduleTask();
            inited = true;

            MultiDataSourceStatManager.add(this);
        } finally {
            lock.unlock();
        }
    }

    public boolean startConfigLoadScheduleTask() {
        if (configLoadFuture == null) {
            configLoadFuture = scheduler.scheduleAtFixedRate(new ConfigLoadTask(), configLoadPeriodMillis,
                                                             configLoadPeriodMillis, TimeUnit.MILLISECONDS);
            return true;
        }

        return false;
    }

    public boolean stopConfigLoadScheduleTask() {
        if (configLoadFuture != null) {
            configLoadFuture.cancel(true);
            configLoadFuture = null;
            return true;
        }

        return false;
    }

    public boolean startFailureDetectScheduleTask() {
        if (failureDetectFuture == null) {
            failureDetectFuture = scheduler.scheduleAtFixedRate(new FailureDetectTask(), failureDetectPeriodMillis,
                                                                failureDetectPeriodMillis, TimeUnit.MILLISECONDS);
            return true;
        }

        return false;
    }

    public boolean stopFailureDetectScheduleTask() {
        if (failureDetectFuture != null) {
            failureDetectFuture.cancel(true);
            failureDetectFuture = null;
            return true;
        }

        return false;
    }

    protected void initInternal() throws SQLException {

    }

    public void resetStat() {

    }

    protected void close() {
        scheduler.shutdownNow();

        Object[] items = this.getDataSources().values().toArray();
        for (Object item : items) {
            JdbcUtils.close((DataSourceHolder) item);
        }

        MultiDataSourceStatManager.remove(this);
    }

    public void failureDetect() {
        int changeCount = 0;
        for (DataSourceHolder dataSourceHolder : getDataSources().values()) {
            boolean isFail = !failureDetector.isValid(dataSourceHolder.getDataSource());

            if (isFail != dataSourceHolder.isFail()) {
                dataSourceHolder.setFail(isFail);
                changeCount++;
            }
        }
        if (changeCount != 0) {
            afterDataSourceChanged(null);
        }

        failureDetectCount.incrementAndGet();
    }

    protected ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public void setFailureDetectPeriodMillis(long failureDetectPeriodMillis) {
        this.failureDetectPeriodMillis = failureDetectPeriodMillis;
    }

    public void setConfigLoadPeriodMillis(long configLoadPeriodMillis) {
        this.configLoadPeriodMillis = configLoadPeriodMillis;
    }

    public DataSourceFailureDetecter getFailureDetector() {
        return failureDetector;
    }

    public void setFailureDetector(DataSourceFailureDetecter failureDetector) {
        this.failureDetector = failureDetector;
    }

    public long createConnectionId() {
        return connectionIdSeed.getAndIncrement();
    }

    public long createStatementId() {
        return statementIdSeed.incrementAndGet();
    }

    public Map<String, DataSourceHolder> getDataSources() {
        return dataSources;
    }

    public DataSourceHolder getDataSourceHolder(String name) {
        return dataSources.get(name);
    }

    public void addDataSource(String name, DataSourceHolder dataSourceHolder) {
        dataSources.put(name, dataSourceHolder);

        afterDataSourceChanged(null);
    }

    public Properties getProperties() {
        return properties;
    }

    public long getActiveCount() {
        return activeCount;
    }

    public void notFailSignal() {
        lock.lock();
        try {
            notFail.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void afterDataSourceChanged(Object event) {
        this.balancer.afterDataSourceChanged(null);
    }

    public Connection getConnection() throws SQLException {
        init();

        lock.lock();
        try {
            if (activeCount >= maxPoolSize) {
                notFull.await();
            }

            MultiDataSourceConnection conn = new MultiDataSourceConnection(this, createConnectionId());

            activeCount++;

            return conn;
        } catch (InterruptedException e) {
            throw new SQLException("thread interrupted", e);
        } finally {
            lock.unlock();
        }
    }

    protected void afterConnectionClosed(MultiDataSourceConnection conn) {
        lock.lock();
        try {
            activeCount--;
            notFull.signal();
        } finally {
            lock.unlock();
        }
    }

    public MultiConnectionHolder getRealConnection(MultiDataSourceConnection multiConn, String sql) throws SQLException {
        return balancer.getConnection(multiConn, sql);
    }

    public int getEnabledDataSourceCount() {
        int count = 0;
        for (DataSourceHolder item : this.dataSources.values()) {
            if (item.isEnable()) {
                count++;
            }
        }
        return count;
    }

    public void handleNotAwailableDatasource(DataSourceHolder dataSourceHolder) {
    }

    public long getRetryGetConnectionCount() {
        return retryGetConnectionCount.get();
    }

    public void incrementRetryGetConnectionCount() {
        retryGetConnectionCount.incrementAndGet();
    }

    public Lock getLock() {
        return lock;
    }

    public Condition getNotFail() {
        return notFail;
    }

    public long getBusySkipCount() {
        return busySkipCount.get();
    }

    public void incrementBusySkipCount() {
        busySkipCount.incrementAndGet();
    }

    public String[] getDataSourceNames() {
        return this.dataSources.keySet().toArray(new String[this.dataSources.size()]);
    }

    @Override
    public String getDbType() {
        return null;
    }

    @Override
    public Driver getRawDriver() {
        return null;
    }

    @Override
    public String getUrl() {
        return null;
    }

    @Override
    public String getRawJdbcUrl() {
        return null;
    }

    @Override
    public List<Filter> getProxyFilters() {
        return filters;
    }

    @Override
    public long createResultSetId() {
        return resultSetIdSeed.incrementAndGet();
    }

    @Override
    public long createTransactionId() {
        return transactionIdSeed.incrementAndGet();
    }

    public boolean restartDataSource(String name) {
        DataSourceHolder holder = this.getDataSources().get(name);
        if (holder != null) {
            holder.restart();
            return true;
        }

        return false;
    }

    public long getConfigLoadCount() {
        return configLoadCount.get();
    }

    public long getFailureDetectCount() {
        return failureDetectCount.get();
    }

    public long getFailureDetectPeriodMillis() {
        return failureDetectPeriodMillis;
    }

    public long getConfigLoadPeriodMillis() {
        return configLoadPeriodMillis;
    }

    class ConfigLoadTask implements Runnable {

        @Override
        public void run() {
            if (configLoader != null) {
                try {
                    configLoadCount.incrementAndGet();
                    configLoader.load();
                } catch (Exception e) {
                    LOG.error("config load error", e);
                }
            }
        }
    }

    class FailureDetectTask implements Runnable {

        @Override
        public void run() {
            try {
                failureDetect();
            } catch (Exception ex) {
                LOG.error("failure detect error", ex);
            }
        }

    }
}
