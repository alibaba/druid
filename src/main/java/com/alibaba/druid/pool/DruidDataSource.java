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
package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.vendor.InformixExceptionSorter;
import com.alibaba.druid.pool.vendor.MSSQLValidConnectionChecker;
import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.pool.vendor.OracleValidConnectionChecker;
import com.alibaba.druid.pool.vendor.SybaseExceptionSorter;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.util.ConcurrentIdentityHashMap;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author ljw<ljw2083@alibaba-inc.com>
 * @author wenshao<szujobs@hotmail.com>
 */
public class DruidDataSource extends DruidAbstractDataSource implements DruidDataSourceMBean, ManagedDataSource, Referenceable {

    private final static Log                                                LOG                   = LogFactory.getLog(DruidDataSource.class);

    // global instances
    private static final Object                                             PRESENT               = new Object();
    private static final ConcurrentIdentityHashMap<DruidDataSource, Object> instances             = new ConcurrentIdentityHashMap<DruidDataSource, Object>();

    private static final long                                               serialVersionUID      = 1L;

    private final ReentrantLock                                             lock                  = new ReentrantLock();

    private final Condition                                                 notEmpty              = lock.newCondition();
    private final Condition                                                 notMaxActive          = lock.newCondition();
    private final Condition                                                 lowWater              = lock.newCondition();

    // stats
    private long                                                            connectCount          = 0L;
    private long                                                            closeCount            = 0L;
    private long                                                            connectErrorCount     = 0L;
    private long                                                            recycleCount          = 0L;
    private long                                                            createConnectionCount = 0L;
    private long                                                            destroyCount          = 0L;

    // store
    private ConnectionHolder[]                                              connections;
    private int                                                             poolingCount          = 0;
    private int                                                             activeCount           = 0;

    // threads
    private CreateConnectionThread                                          createConnectionThread;
    private DestroyConnectionThread                                         destoryConnectionThread;

    private final CountDownLatch                                            initedLatch           = new CountDownLatch(2);


    private boolean                                                         enable                = false;

    public DruidDataSource(){
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    private void init() throws SQLException {
        if (inited) {
            return;
        }

        lock.lock();
        try {
            if (inited) {
                return;
            }

            this.id = DruidDriver.createDataSourceId();
            
            enable = true;

            if (maxActive <= 0) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }
            
            if (maxActive < minIdle) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            if (maxIdle <= 0 || maxIdle < minIdle) {
                throw new IllegalArgumentException("illegal maxPoolSize");
            }
            
            if (getInitialSize() > maxActive) {
                throw new IllegalArgumentException("illegal initialSize");
            }

            if (this.driverClass != null) {
                this.driverClass = driverClass.trim();
            }

            if (this.jdbcUrl != null) {
                this.jdbcUrl = this.jdbcUrl.trim();
            }

            if (this.driver == null) {
                if (this.driverClass == null || this.driverClass.isEmpty()) {
                    this.driverClass = JdbcUtils.getDriverClassName(this.jdbcUrl);
                }

                try {
                    driver = (Driver) Class.forName(this.driverClass).newInstance();
                } catch (IllegalAccessException e) {
                    throw new SQLException(e.getMessage(), e);
                } catch (InstantiationException e) {
                    throw new SQLException(e.getMessage(), e);
                } catch (ClassNotFoundException e) {
                    throw new SQLException(e.getMessage(), e);
                }
            } else {
                if (this.driverClass == null) {
                    this.driverClass = driver.getClass().getName();
                }
            }

            this.dbType = JdbcUtils.getDbType(jdbcUrl, driverClass.getClass().getName());

            String realDriverClassName = driver.getClass().getName();
            if (realDriverClassName.equals("com.mysql.jdbc.Driver")) {
                this.validConnectionChecker = new MySqlValidConnectionChecker();
                this.exceptionSoter = new MySqlExceptionSorter();

            } else if (realDriverClassName.equals("oracle.jdbc.driver.OracleDriver")) {
                this.validConnectionChecker = new OracleValidConnectionChecker();
                this.exceptionSoter = new OracleExceptionSorter();

            } else if (realDriverClassName.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver")) {
                this.validConnectionChecker = new MSSQLValidConnectionChecker();

            } else if (realDriverClassName.equals("com.informix.jdbc.IfxDriver")) {
                this.exceptionSoter = new InformixExceptionSorter();

            } else if (realDriverClassName.equals("com.sybase.jdbc2.jdbc.SybDriver")) {
                this.exceptionSoter = new SybaseExceptionSorter();
            }

            for (Filter filter : filters) {
                filter.init(this);
            }

            initConnectionFactory();

            connections = new ConnectionHolder[maxActive];

            SQLException connectError = null;

            try {
                // 初始化连接
                for (int i = 0, size = getInitialSize(); i < size; ++i) {
                    Connection conn = connectionFactory.createConnection();
                    conn.setAutoCommit(true);
                    connections[poolingCount++] = new ConnectionHolder(this, conn);
                }
            } catch (SQLException ex) {
                LOG.error("init datasource error", ex);
                connectError = ex;
            }

            createConnectionThread = new CreateConnectionThread("Druid-ConnectionPool-Create");
            createConnectionThread.setDaemon(true);
            destoryConnectionThread = new DestroyConnectionThread("Druid-ConnectionPool-Destory");
            destoryConnectionThread.setDaemon(true);

            createConnectionThread.start();
            destoryConnectionThread.start();

            initedLatch.await();
            inited = true;

            createdTime = new Date();
            instances.put(this, PRESENT);

            if (connectError != null && poolingCount == 0) {
                throw connectError;
            }
        } catch (InterruptedException e) {
            throw new SQLException(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        init();

        final int maxWaitThreadCount = getMaxWaitThreadCount();
        if (maxWaitThreadCount > 0) {
            if (lock.getQueueLength() > maxWaitThreadCount) {
                throw new SQLException("maxWaitThreadCount " + maxWaitThreadCount + ", current wait Thread count "
                                       + lock.getQueueLength());
            }
        }

        for (;;) {
            PoolableConnection poolalbeConnection = getConnectionInternal();

            if (isTestOnBorrow()) {
                boolean validate = testConnectionInternal(poolalbeConnection.getConnection());
                if (!validate) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("skip not validate connection.");
                    }

                    Connection realConnection = poolalbeConnection.getConnection();
                    JdbcUtils.close(realConnection);
                    this.decrementActiveCountWithLock();
                    continue;
                }
                poolalbeConnection.getConnectionHolder().setLastCheckTimeMillis(System.currentTimeMillis());
            } else {
                Connection realConnection = poolalbeConnection.getConnection();
                if (realConnection.isClosed()) {
                    JdbcUtils.close(realConnection);
                    this.decrementActiveCountWithLock();
                    continue;
                }

                if (isTestWhileIdle()) {
                    long idleMillis = System.currentTimeMillis()
                                      - poolalbeConnection.getConnectionHolder().getLastActiveTimeMillis();
                    if (idleMillis >= this.getTimeBetweenEvictionRunsMillis()) {
                        boolean validate = testConnectionInternal(poolalbeConnection.getConnection());
                        if (!validate) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("skip not validate connection.");
                            }

                            JdbcUtils.close(realConnection);
                            this.decrementActiveCountWithLock();
                            continue;
                        }
                        poolalbeConnection.getConnectionHolder().setLastCheckTimeMillis(System.currentTimeMillis());
                    }
                }
            }

            if (isRemoveAbandoned()) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                activeConnections.put(poolalbeConnection,
                                      new ActiveConnectionTraceInfo(poolalbeConnection, System.currentTimeMillis(),
                                                                    stackTrace));
            }

            return poolalbeConnection;
        }
    }

    private PoolableConnection getConnectionInternal() throws SQLException {
        PoolableConnection poolalbeConnection;

        lock.lock();
        try {
            if (!enable) {
                connectErrorCount++;
                throw new DataSourceDisableException();
            }
            
            connectCount++;

            ConnectionHolder holder;

            if (maxWait > 0) {
                holder = pollLast(maxWait, TimeUnit.MILLISECONDS);
            } else {
                holder = takeLast();
            }

            if (holder == null) {
                throw new SQLException("can not get connection");
            }

            holder.incrementUseCount();
            activeCount++;

            poolalbeConnection = new PoolableConnection(holder);
        } catch (InterruptedException e) {
            connectErrorCount++;
            throw new SQLException(e.getMessage(), e);
        } catch (SQLException e) {
            connectErrorCount++;
            throw e;
        } finally {
            lock.unlock();
        }
        return poolalbeConnection;
    }
    
    public void handleConnectionException(PoolableConnection pooledConnection, Throwable t) throws SQLException {
        final ConnectionHolder holder = pooledConnection.getConnectionHolder();

        errorCount.incrementAndGet();

        if (t instanceof SQLException) {
            SQLException sqlEx = (SQLException) t;

            // broadcastConnectionError
            ConnectionEvent event = new ConnectionEvent(pooledConnection, sqlEx);
            for (ConnectionEventListener eventListener : holder.getConnectionEventListeners()) {
                eventListener.connectionErrorOccurred(event);
            }

            // exceptionSorter.isExceptionFatal
            if (exceptionSoter != null && exceptionSoter.isExceptionFatal(sqlEx)) {
                decrementActiveCountWithLock();
                JdbcUtils.close(holder.getConnection());
                pooledConnection.disable();
            }

            throw sqlEx;
        } else {
            throw new SQLException("Error", t);
        }
    }

    /**
     * 回收连接
     */
    protected void recycle(PoolableConnection pooledConnection) throws SQLException {
        final Connection conn = pooledConnection.getConnection();
        final ConnectionHolder holder = pooledConnection.getConnectionHolder();

        assert holder != null;

        try {
            // 第一步，检查连接是否关闭
            if (conn == null || conn.isClosed()) {
                lock.lock();
                try {
                    decrementActiveCount();
                    closeCount++;
                } finally {
                    lock.unlock();
                }
                return;
            }

            final boolean isAutoCommit = conn.getAutoCommit();
            final boolean isReadOnly = conn.isReadOnly();

            // 第二步，检查是否需要回滚
            if ((!isAutoCommit) && (!isReadOnly)) {
                conn.rollback();
            }

            // 第三步，清楚警告信息，重设autoCommit为true
            conn.clearWarnings();
            if (!isAutoCommit) {
                conn.setAutoCommit(true);
            }

            if (isTestOnReturn()) {
                boolean validate = testConnectionInternal(conn);
                if (!validate) {
                    lock.lock();
                    try {
                        JdbcUtils.close(conn);
                        destroyCount++;
                        closeCount++;
                        decrementActiveCount();
                    } finally {
                        lock.unlock();
                    }
                    return;
                }
                holder.setLastCheckTimeMillis(System.currentTimeMillis());
            }

            lock.lock();
            try {
                decrementActiveCount();
                closeCount++;

                // 第六步，加入队列中(putLast)
                putLast(holder);
                recycleCount++;
            } finally {
                lock.unlock();
            }
        } catch (Throwable e) {
            JdbcUtils.close(conn);
            
            lock.lock();
            try {
                decrementActiveCount();
                closeCount++;
            } finally {
                lock.unlock();
            }
            
            throw new SQLException("recyle error", e);
        }
    }

    /**
     * close datasource
     */
    public void close() {
        lock.lock();
        try {
            if (createConnectionThread != null) {
                createConnectionThread.interrupt();
            }

            if (destoryConnectionThread != null) {
                destoryConnectionThread.interrupt();
            }

            for (int i = 0; i < poolingCount; ++i) {
                try {
                    connections[i].getConnection().close();
                    connections[i] = null;
                    destroyCount++;
                } catch (Exception ex) {
                    LOG.warn("close connection error", ex);
                }
            }
            poolingCount = 0;
            instances.remove(this);
        } finally {
            lock.unlock();
        }
    }

    void incrementCreateCount() {
        createConnectionCount++;
    }

    void decrementActiveCount() {
        activeCount--;
        notMaxActive.signal();
    }

    void decrementActiveCountWithLock() {
        lock.lock();
        try {
            decrementActiveCount();
        } finally {
            lock.unlock();
        }
    }

    void putLast(ConnectionHolder e) throws InterruptedException {
        if (e == null) {
            throw new NullPointerException();
        }

        e.setLastActiveTimeMillis(System.currentTimeMillis());
        connections[poolingCount++] = e;

        notEmpty.signal();
    }

    ConnectionHolder takeLast() throws InterruptedException {
        while (activeCount >= maxActive) {
            try {
                notMaxActive.await(); // signal by recycle
            } catch (InterruptedException ie) {
                notMaxActive.signal(); // propagate to non-interrupted thread
                throw ie;
            }
        }

        try {
            while (poolingCount == 0) {
                lowWater.signal(); // send signal to CreateThread create connection
                notEmpty.await(); // signal by recycle or creator
            }
        } catch (InterruptedException ie) {
            notEmpty.signal(); // propagate to non-interrupted thread
            throw ie;
        }

        poolingCount--;
        ConnectionHolder last = connections[poolingCount];
        connections[poolingCount] = null;

        return last;
    }

    ConnectionHolder pollLast(long timeout, TimeUnit unit) throws InterruptedException {
        long estimate = unit.toNanos(timeout);

        for (;;) {
            while (activeCount >= maxActive) {
                if (estimate <= 0) {
                    return null;
                }
                
                try {
                    estimate = notMaxActive.awaitNanos(estimate); // signal by recycle
                } catch (InterruptedException ie) {
                    notMaxActive.signal(); // propagate to non-interrupted thread
                    throw ie;
                }
            }

            if (poolingCount == 0) {
                lowWater.signal(); // send signal to CreateThread create connection
                
                if (estimate <= 0) {
                    return null;
                }
                
                try {
                    estimate = notEmpty.awaitNanos(estimate); // signal by recycle or creator
                } catch (InterruptedException ie) {
                    notEmpty.signal(); // propagate to non-interrupted thread
                    throw ie;
                }
                
                if (poolingCount == 0) {
                    if (estimate > 0) {
                        continue;
                    }

                    return null;
                }
            }

            poolingCount--;
            ConnectionHolder last = connections[poolingCount];
            connections[poolingCount] = null;

            return last;
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported by DruidDataSource");
    }

    public long getCreateCount() {
        return createConnectionCount;
    }

    public long getDestroyCount() {
        return destroyCount;
    }

    public long getConnectCount() {
        return connectCount;
    }

    public long getCloseCount() {
        return closeCount;
    }

    public long getConnectErrorCount() {
        return connectErrorCount;
    }

    @Override
    public int getPoolingCount() {
        return poolingCount;
    }

    public long getRecycleCount() {
        return recycleCount;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public class CreateConnectionThread extends Thread {

        public CreateConnectionThread(String name){
            super(name);
        }

        public void run() {
            initedLatch.countDown();

            final int MAX_ERRRO_TRY = 3;
            int errorCount = 0;
            for (;;) {
                // addLast
                lock.lock();
                try {
                    // 必须存在线程等待，才创建连接
                    int waitThreadCount = lock.getWaitQueueLength(notEmpty); 
                    
                    if (poolingCount - waitThreadCount >= 0) {
                        lowWater.await();
                        continue;
                    }

                    // 防止创建超过maxActive数量的连接
                    int max = maxActive;;
                    if (activeCount + poolingCount >= max) {
                        lowWater.await();
                        continue;
                    }

                    Connection connection = connectionFactory.createConnection();
                    ConnectionHolder poolableConnection = new ConnectionHolder(DruidDataSource.this, connection);
                    connections[poolingCount++] = poolableConnection;

                    errorCount = 0; // reset errorCount

                    notEmpty.signal();

                } catch (InterruptedException e) {
                    break;
                } catch (SQLException e) {
                    LOG.error("create connection error", e);

                    errorCount++;

                    if (errorCount > MAX_ERRRO_TRY && timeBetweenConnectErrorMillis > 0) {
                        try {
                            Thread.sleep(timeBetweenConnectErrorMillis);
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                } catch (RuntimeException e) {
                    LOG.error("create connection error", e);
                } catch (Error e) {
                    LOG.error("create connection error", e);
                    break;
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public class DestroyConnectionThread extends Thread {

        public DestroyConnectionThread(String name){
            super(name);
        }

        public void run() {
            initedLatch.countDown();

            for (;;) {
                // 从前面开始删除
                try {
                    if (timeBetweenEvictionRunsMillis > 0) {
                        Thread.sleep(timeBetweenEvictionRunsMillis);
                    } else {
                        Thread.sleep(1000); //
                    }

                    if (Thread.interrupted()) {
                        break;
                    }

                    if (poolingCount <= 0) {
                        Thread.sleep(minEvictableIdleTimeMillis);
                        continue;
                    }

                    shrink(true);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public DataSourceProxyConfig getConfig() {
        return null;
    }

    /** Instance key */
    protected String instanceKey = null;

    public Reference getReference() throws NamingException {
        final String className = getClass().getName();
        final String factoryName = className + "Factory"; // XXX: not robust
        Reference ref = new Reference(className, factoryName, null);
        ref.add(new StringRefAddr("instanceKey", instanceKey));
        return ref;
    }

    static class ActiveConnectionTraceInfo {

        private final PoolableConnection  connection;
        private final long                connectTime;
        private final StackTraceElement[] stackTrace;

        public ActiveConnectionTraceInfo(PoolableConnection connection, long connectTime, StackTraceElement[] stackTrace){
            super();
            this.connection = connection;
            this.connectTime = connectTime;
            this.stackTrace = stackTrace;
        }

        public PoolableConnection getConnection() {
            return connection;
        }

        public long getConnectTime() {
            return connectTime;
        }

        public StackTraceElement[] getStackTrace() {
            return stackTrace;
        }
    }

    @Override
    public List<String> getFilterClassNames() {
        List<String> names = new ArrayList<String>();
        for (Filter filter : filters) {
            names.add(filter.getClass().getName());
        }
        return names;
    }

    public static Set<DruidDataSource> getInstances() {
        return instances.keySet();
    }

    public int getRawDriverMajorVersion() {
        int version = -1;
        if (this.driver != null) {
            version = driver.getMajorVersion();
        }
        return version;
    }

    public int getRawDriverMinorVersion() {
        int version = -1;
        if (this.driver != null) {
            version = driver.getMinorVersion();
        }
        return version;
    }

    public String getProperties() {
        if (this.connectionProperties == null) {
            return null;
        }

        Properties properties = new Properties(this.connectionProperties);
        if (properties.contains("password")) {
            properties.put("password", "******");
        }
        return properties.toString();
    }

    @Override
    public void shrink() {
        shrink(false);
    }

    public void shrink(boolean checkTime) {
        final List<ConnectionHolder> evictList = new ArrayList<ConnectionHolder>();
        lock.lock();
        try {
            final int checkCount = poolingCount - minIdle;
            for (int i = 0; i < checkCount; ++i) {
                ConnectionHolder connection = connections[i];

                if (checkTime) {
                    long idleMillis = System.currentTimeMillis() - connection.getLastActiveTimeMillis();
                    if (idleMillis >= minEvictableIdleTimeMillis) {
                        evictList.add(connection);
                    } else {
                        break;
                    }
                } else {
                    evictList.add(connection);
                }
            }
            
            int removeCount = evictList.size();
            if (removeCount > 0) {
                System.arraycopy(connections, removeCount, connections, 0, poolingCount - removeCount);
                Arrays.fill(connections, poolingCount - removeCount, poolingCount, null);
                poolingCount -= removeCount;
            }
        } finally {
            lock.unlock();
        }

        for (ConnectionHolder item : evictList) {
            Connection connection = item.getConnection();
            JdbcUtils.close(connection);
            destroyCount++;
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("{");

        buf.append("\n\tCreateTime:\"");
        buf.append(JdbcUtils.toString(getCreatedTime()));
        buf.append("\"");

        buf.append(",\n\tActiveCount:");
        buf.append(getActiveCount());

        buf.append(",\n\tPoolingCount:");
        buf.append(getPoolingCount());

        buf.append(",\n\tCreateCount:");
        buf.append(getCreateCount());

        buf.append(",\n\tDestroyCount:");
        buf.append(getDestroyCount());

        buf.append(",\n\tCloseCount:");
        buf.append(getCloseCount());

        buf.append(",\n\tConnectCount:");
        buf.append(getConnectCount());

        buf.append(",\n\tConnections:[");
        for (int i = 0; i < poolingCount; ++i) {
            ConnectionHolder conn = connections[i];
            if (conn != null) {
                if (i != 0) {
                    buf.append(",");
                }
                buf.append("\n\t\t");
                buf.append(conn.toString());
            }
        }
        buf.append("\n\t]");

        buf.append("\n}");

        return buf.toString();
    }
}
