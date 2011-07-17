package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.DruidDataSource;

public class HADataSource extends MultiDataSource implements DataSource {

    private final static Log              LOG                     = LogFactory.getLog(HADataSource.class);

    private final AtomicInteger           connectionIdSeed        = new AtomicInteger();
    private final AtomicInteger           statementIdSeed         = new AtomicInteger();
    private final AtomicInteger           indexErrorCount         = new AtomicInteger();

    protected final List<DruidDataSource> notAvailableDatasources = new CopyOnWriteArrayList<DruidDataSource>();

    public HADataSource(){
    }

    @Override
    public Connection getConnection() throws SQLException {
        int requestNumber = connectionIdSeed.getAndIncrement();

        return new HAConnection(this, requestNumber);
    }

    public int createStatementId() {
        return statementIdSeed.getAndIncrement();
    }

    public Connection getConnectionInternal(int requestNumber) throws SQLException {
        int tryCount = 0;

        for (;;) {
            int size = dataSources.size();

            if (size == 0) {
                throw new SQLException("can not get connection, no availabe datasources");
            }

            int index = requestNumber % size;

            DruidDataSource dataSource = null;

            try {
                // 处理并发时的错误
                dataSource = dataSources.get(index);
            } catch (Exception ex) {
                indexErrorCount.incrementAndGet();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getDataSource error, index : " + index, ex);
                }
                continue;
            }

            assert dataSource != null;

            if (!dataSource.isEnable()) {
                handleNotAwailableDatasource(dataSource);
                continue;
            }

            Connection conn = null;

            try {
                tryCount++;
                conn = dataSource.getConnection();
            } catch (SQLException ex) {
                LOG.error("getConnection error", ex);

                if (tryCount >= size) {
                    throw ex;
                }

                continue;
            }

            return conn;
        }
    }

    void handleNotAwailableDatasource(DruidDataSource dataSource) {
        boolean removed = dataSources.remove(dataSource);
        if (removed) {
            notAvailableDatasources.add(dataSource);
        }
    }

    public long getIndexErrorCount() {
        return indexErrorCount.get();
    }

}
