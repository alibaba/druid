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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.management.ObjectName;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DataSourceAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.valid.DataSourceFailureDetecter;
import com.alibaba.druid.pool.ha.valid.DefaultDataSourceFailureDetecter;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.util.JdbcUtils;

public abstract class MultiDataSource extends DataSourceAdapter implements MultiDataSourceMBean, DataSourceProxy {

    private Properties                             properties                       = new Properties();

    private final AtomicLong                       connectionIdSeed                 = new AtomicLong();
    private final AtomicLong                       statementIdSeed                  = new AtomicLong();
    private final AtomicLong                       resultSetIdSeed                  = new AtomicLong();
    private final AtomicLong                       transactionIdSeed                = new AtomicLong();

    protected DataSourceFailureDetecter               validDataSourceChecker           = new DefaultDataSourceFailureDetecter();
    private long                                   validDataSourceCheckPeriodMillis = 3000;

    private int                                    schedulerThreadCount             = 3;
    private ScheduledExecutorService               scheduler;

    private boolean                                inited                           = false;
    private final Lock                             lock                             = new ReentrantLock();

    private ConcurrentMap<String, DataSourceHolder> dataSources                      = new ConcurrentHashMap<String, DataSourceHolder>();

    private ObjectName                             objectName;

    private List<Filter>                           filters                          = new ArrayList<Filter>();

    private boolean                                enable;

    private String                                 name;

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

    public void init() {
        if (inited) {
            return;
        }

        lock.lock();
        try {
            if (inited) {
                return;
            }

            scheduler = Executors.newScheduledThreadPool(schedulerThreadCount);
            scheduler.scheduleAtFixedRate(new FailureDetectTask(), validDataSourceCheckPeriodMillis,
                                          validDataSourceCheckPeriodMillis, TimeUnit.MILLISECONDS);
            inited = true;

            MultiDataSourceStatManager.add(this);
        } finally {
            lock.unlock();
        }
    }

    public void resetStat() {

    }

    protected void close() {
        scheduler.shutdownNow();

        Object[] items = this.getDataSources().values().toArray();
        for (Object item : items) {
            JdbcUtils.close((DruidDataSource) item);
        }

        MultiDataSourceStatManager.remove(this);
    }

    public void failureDetect() {
        for (DataSourceHolder dataSourceHolder : getDataSources().values()) {
            boolean isValid = validDataSourceChecker.isValid(dataSourceHolder.getDataSource());
            if (!isValid) {
                handleNotAwailableDatasource(dataSourceHolder);
            }
        }
    }

    protected ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public long getValidDataSourceCheckPeriodMillis() {
        return validDataSourceCheckPeriodMillis;
    }

    public void setValidDataSourceCheckPeriodMillis(long validDataSourceCheckPeriodMillis) {
        this.validDataSourceCheckPeriodMillis = validDataSourceCheckPeriodMillis;
    }

    public DataSourceFailureDetecter getValidDataSourceChecker() {
        return validDataSourceChecker;
    }

    public void setValidDataSourceChecker(DataSourceFailureDetecter validDataSourceChecker) {
        this.validDataSourceChecker = validDataSourceChecker;
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

    public Properties getProperties() {
        return properties;
    }

    public Connection getConnection() throws SQLException {
        init();

        return new MultiDataSourceConnection(this, createConnectionId());
    }

    public abstract Connection getConnectionInternal(MultiDataSourceConnection conn, String sql) throws SQLException;

    public void handleNotAwailableDatasource(DataSourceHolder dataSourceHolder) {
        dataSourceHolder.setEnable(false);
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

    class FailureDetectTask implements Runnable {

        @Override
        public void run() {
            failureDetect();
        }

    }
}
