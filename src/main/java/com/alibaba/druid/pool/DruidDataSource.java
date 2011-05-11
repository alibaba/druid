/*
 * Copyright 2011 Alibaba Group. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;

import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class DruidDataSource extends DruidDataAbstractSource implements DruidDataSourceMBean, Referenceable {

    private static final long                                                    serialVersionUID            = 1L;

    private final ReentrantLock                                                  lock                        = new ReentrantLock();

    private final Condition                                                      notEmpty                    = lock.newCondition();
    private final Condition                                                      notFullActive               = lock.newCondition();
    private final Condition                                                      lowWater                    = lock.newCondition();
    private final Condition                                                      highWater                   = lock.newCondition();
    private final Condition                                                      idleTimeout                 = lock.newCondition();

    // stats
    private long                                                                 connectCount                = 0;
    private long                                                                 closeCount                  = 0;
    private long                                                                 connectErrorCount           = 0;
    private long                                                                 recycleCount                = 0;
    private long                                                                 createConnectionCount       = 0L;
    private long                                                                 destroyCount                = 0;

    // store
    private ConnectionHolder[]                                                   connections;
    private int                                                                  count                       = 0;
    private int                                                                  activeCount                 = 0;

    // threads
    private CreateConnectionThread                                               createConnectionThread;
    private DestroyConnectionThread                                              destoryConnectionThread;

    private boolean                                                              activeConnectionTraceEnable = false;

    private final IdentityHashMap<PoolableConnection, ActiveConnectionTraceInfo> activeConnections           = new IdentityHashMap<PoolableConnection, ActiveConnectionTraceInfo>();

    public DruidDataSource(){
    }

    private void init() throws SQLException {
        if (inited) {
            return;
        }

        try {
            final int maxWaitThreadCount = getMaxWaitThreadCount();
            if (maxWaitThreadCount > 0) {
                if (lock.getQueueLength() > maxWaitThreadCount) {
                    throw new SQLException("maxWaitThreadCount " + maxWaitThreadCount + ", current wait Thread count "
                                           + lock.getQueueLength());
                }
            }

            lock.lockInterruptibly();

            if (inited) {
                return;
            }

            if (this.driverClass != null) {
                this.driverClass = driverClass.trim();
            }

            if (this.jdbcUrl != null) {
                this.jdbcUrl = this.jdbcUrl.trim();
            }

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

            initConnectionFactory();

            if (maxIdle <= 0 || maxIdle < minIdle) {
                throw new IllegalArgumentException("illegal maxPoolSize");
            }

            int capacity = maxIdle + maxActive;

            if (capacity < getInitialSize()) {
                capacity = getInitialSize();
            }

            connections = new ConnectionHolder[capacity];
            // pool = new ConnectionPool(this, this.minIdle, this.maxIdle,
            // this.maxActive, this.maxIdleTimeMillis);

            for (int i = 0, size = getInitialSize(); i < size; ++i) {
                Connection conn = connectionFactory.createConnection();
                conn.setAutoCommit(true);
                connections[count++] = new ConnectionHolder(this, conn);
            }

            createConnectionThread = new CreateConnectionThread("Druid-ConnectionPool-Create");
            destoryConnectionThread = new DestroyConnectionThread("Druid-ConnectionPool-Destory");

            createConnectionThread.start();
            destoryConnectionThread.start();

            inited = true;
        } catch (InterruptedException e) {
            throw new SQLException(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    public boolean isActiveConnectionTraceEnable() {
        return activeConnectionTraceEnable;
    }

    public void setActiveConnectionTraceEnable(boolean connectionTraceEnable) {
        this.activeConnectionTraceEnable = connectionTraceEnable;
    }

    public Set<PoolableConnection> getActiveConnections() {
        return this.activeConnections.keySet();
    }

    private boolean testConnection(Connection conn) {
        String query = getValidationQuery();
        if (query == null || query.length() == 0) {
            return true;
        }

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            return false;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        init();

        try {
            lock.lockInterruptibly();

            connectCount++;

            ConnectionHolder holder;
            for (;;) {
                if (maxWait > 0) {
                    holder = pollLast(maxWait, TimeUnit.MILLISECONDS);
                } else {
                    holder = takeLast();
                }

                if (holder == null) {
                    throw new SQLException("can not get connection");
                }

                if (isTestOnBorrow()) {
                    boolean validate = testConnection(holder.getConnection());
                    if (!validate) {
                        continue;
                    }
                }

                break;
            }

            holder.incrementUseCount();
            activeCount++;

            PoolableConnection poolalbeConnection = new PoolableConnection(holder);

            if (activeConnectionTraceEnable) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                activeConnections.put(poolalbeConnection,
                                      new ActiveConnectionTraceInfo(poolalbeConnection, System.currentTimeMillis(),
                                                                    stackTrace));
            }

            return poolalbeConnection;
        } catch (InterruptedException e) {
            connectErrorCount++;
            throw new SQLException(e.getMessage(), e);
        } catch (SQLException e) {
            connectErrorCount++;
            throw e;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 回收连接
     */
    void recycle(PoolableConnection pooledConnection) throws SQLException {
        final Connection conn = pooledConnection.getConnection();
        try {
            if (activeConnectionTraceEnable) {
                activeConnections.remove(pooledConnection);
            }

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

            if (isTestOnReturn()) {
                boolean validate = testConnection(conn);
                if (!validate) {
                    return;
                }
            }

            boolean isAutoCommit = conn.getAutoCommit();
            boolean isReadOnly = conn.isReadOnly();

            // 第二步，检查是否需要回滚
            if ((!isAutoCommit) && (!isReadOnly)) {
                conn.rollback();
            }

            // 第三步，清楚警告信息，重设autoCommit为true
            conn.clearWarnings();
            if (!isAutoCommit) {
                conn.setAutoCommit(true);
            }

            lock.lockInterruptibly();
            try {
                decrementActiveCount();
                closeCount++;

                // 第四部，加入队列中(putLast)
                putLast(pooledConnection.getConnectionHolder());
                recycleCount++;
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            if (!conn.isClosed()) {
                conn.close();
            }

            throw new SQLException(e.getMessage(), e);
        } catch (SQLException e) {
            lock.lock();
            try {
                decrementActiveCount();
                closeCount++;
            } finally {
                lock.unlock();
            }

            throw e;
        }
    }

    /**
     * close datasource
     */
    public void close() {
        lock.lock();
        try {
            createConnectionThread.interrupt();
            destoryConnectionThread.interrupt();

            for (int i = 0; i < count; ++i) {
                try {
                    connections[i].getConnection().close();
                    connections[i] = null;
                    destroyCount++;
                } catch (Exception ex) {
                    // skip it
                }
            }
            count = 0;
        } finally {
            lock.unlock();
        }
    }

    void incrementCreateCount() {
        createConnectionCount++;
    }

    void decrementActiveCount() {
        activeCount--;
        notFullActive.signal();
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

        connections[count++] = e;

        if (count == maxIdle + 1) {
            highWater.signal();
        }

        notEmpty.signal();
    }

    ConnectionHolder takeLast() throws InterruptedException {
        while (activeCount >= maxActive) {
            notFullActive.await();
        }

        try {
            while (count == 0) {
                notEmpty.await();
            }
        } catch (InterruptedException ie) {
            notEmpty.signal(); // propagate to non-interrupted thread
            throw ie;
        }

        int lastIndex = count - 1;
        ConnectionHolder last = connections[lastIndex];
        connections[lastIndex] = null;
        count--;

        if (lastIndex <= minIdle - 1) {
            lowWater.signal();
        }

        return last;
    }

    ConnectionHolder pollLast(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);

        for (;;) {
            if (activeCount == maxActive) {
                long startNano = System.nanoTime();
                notFullActive.awaitNanos(nanos);
                nanos -= (System.nanoTime() - startNano);
            }

            if (count == 0) {
                notEmpty.awaitNanos(nanos);

                if (count == 0) {
                    return null;
                }
            }

            int lastIndex = count - 1;
            ConnectionHolder last = connections[lastIndex];
            connections[lastIndex] = null;
            count--;

            if (lastIndex == minIdle - 1) {
                lowWater.signal();
            }

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
    public int getPoolingSize() {
        return count;
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
            for (;;) {
                // addLast
                lock.lock();
                try {
                    if (count >= minIdle) {
                        lowWater.await();
                    }

                    Connection connection = connectionFactory.createConnection();
                    ConnectionHolder poolableConnection = new ConnectionHolder(DruidDataSource.this, connection);
                    connections[count++] = poolableConnection;

                    if (count == 1) {
                        notEmpty.signal();
                    }

                } catch (InterruptedException e) {
                    break;
                } catch (SQLException e) {
                    e.printStackTrace();
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
            for (;;) {
                // 从前面开始删除

                try {
                    ConnectionHolder first = null;
                    lock.lock();
                    try {
                        if (count <= maxIdle) {
                            highWater.await();
                            continue;
                        }

                        first = connections[0];

                        long millis = System.currentTimeMillis() - first.getTimeMillis();
                        if (millis > 0) {
                            idleTimeout.await(millis, TimeUnit.MILLISECONDS);
                            continue;
                        }

                        System.arraycopy(connections, 1, connections, 0, count - 1);
                        connections[count - 1] = null;
                    } finally {
                        lock.unlock();
                    }

                    Connection connection = first.getConnection();
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace(); // TODO
                    }

                    destroyCount++;
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }

    public DataSourceProxyConfig getConfig() {
        return null;
    }

    @Override
    public Driver getRawDriver() {
        return driver;
    }

    void initStatement(Statement stmt) throws SQLException {
        if (queryTimeout > 0) {
            stmt.setQueryTimeout(queryTimeout);
        }
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
    public List<String> getActiveConnectionStackTrace() {
        List<String> list = new ArrayList<String>();
        for (ActiveConnectionTraceInfo traceInfo : this.activeConnections.values()) {
            StringBuilder buf = new StringBuilder();
            for (StackTraceElement item : traceInfo.getStackTrace()) {
                buf.append(item.toString());
                buf.append("\n");
            }
            list.add(buf.toString());
        }

        return list;
    }
}
