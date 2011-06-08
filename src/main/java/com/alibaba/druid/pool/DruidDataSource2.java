package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.LinkedTransferQueue;
import com.alibaba.druid.util.TransferQueue;

public class DruidDataSource2 extends DruidAbstractDataSource {

    private final ReentrantLock                   lock             = new ReentrantLock();
    private final TransferQueue<ConnectionHolder> connections      = new LinkedTransferQueue<ConnectionHolder>();

    private static final long                     serialVersionUID = 1L;

    private final CountDownLatch                  initedLatch      = new CountDownLatch(1);
    private CreateConnectionThread                createConnectionThread;

    @Override
    public Connection getConnection() throws SQLException {
        init();

        return null;
    }

    private void init() throws SQLException {
        if (inited) {
            return;
        }

        try {
            lock.lockInterruptibly();

            if (inited) {
                return;
            }

            if (maxActive <= 0) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            if (maxIdle <= 0 || maxIdle < minIdle) {
                throw new IllegalArgumentException("illegal maxPoolSize");
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

            for (Filter filter : filters) {
                filter.init(this);
            }

            initConnectionFactory();

            int capacity = maxIdle + maxActive;

            if (capacity < getInitialSize()) {
                capacity = getInitialSize();
            }

            // pool = new ConnectionPool(this, this.minIdle, this.maxIdle,
            // this.maxActive, this.maxIdleTimeMillis);

            for (int i = 0, size = getInitialSize(); i < size; ++i) {
                Connection conn = connectionFactory.createConnection();
                conn.setAutoCommit(true);
                connections.add(new ConnectionHolder(this, conn));
            }

            createConnectionThread = new CreateConnectionThread("Druid-ConnectionPool-Create");

            createConnectionThread.start();

            initedLatch.await();
            inited = true;

        } catch (InterruptedException e) {
            throw new SQLException(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return null;
    }

    @Override
    void incrementCreateCount() {

    }

    @Override
    protected void recycle(PoolableConnection pooledConnection) throws SQLException {
    }

    public class CreateConnectionThread extends Thread {

        public CreateConnectionThread(String name){
            super(name);
        }

        public void run() {
            initedLatch.countDown();

            for (;;) {
                // addLast
                lock.lock();
                try {
                    Connection connection = connectionFactory.createConnection();
                    ConnectionHolder poolableConnection = new ConnectionHolder(DruidDataSource2.this, connection);
                    connections.transfer(poolableConnection);
                } catch (InterruptedException e) {
                    break;
                } catch (SQLException e) {
                    printStackTrace(e);

                    if (timeBetweenConnectErrorMillis > 0) {
                        try {
                            Thread.sleep(timeBetweenConnectErrorMillis);
                        } catch (InterruptedException e1) {
                            break;
                        }
                    }
                } catch (RuntimeException e) {
                    printStackTrace(e);
                } catch (Error e) {
                    printStackTrace(e);
                    break;
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    @Override
    public long getConnectCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getCloseCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getConnectErrorCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getPoolingCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getRecycleCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getActiveCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getCreateCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getDestroyCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean isActiveConnectionTraceEnable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setActiveConnectionTraceEnable(boolean connectStackTraceEnable) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<String> getActiveConnectionStackTrace() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getFilterClassNames() {
        // TODO Auto-generated method stub
        return null;
    }
}
