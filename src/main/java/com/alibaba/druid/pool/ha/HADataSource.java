package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.DruidDataSource;

public class HADataSource extends MultiDataSource implements DataSource {

    private final static Log LOG          = LogFactory.getLog(HADataSource.class);

    private AtomicInteger    requestCount = new AtomicInteger();

    @Override
    public Connection getConnection() throws SQLException {
        int tryCount = 0;
        for (;;) {
            int requestNumber = requestCount.getAndIncrement();
            int size = dataSources.size();
            int index = requestNumber % size;

            DruidDataSource dataSource = dataSources.get(index);
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

            if (conn != null) {
                if (!dataSource.isTestOnBorrow()) {
                    dataSource.testConnection(conn);
                }
            }

            return conn;
        }
    }

}
