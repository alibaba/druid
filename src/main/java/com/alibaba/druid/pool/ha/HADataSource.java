package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.DruidDataSource;

public class HADataSource extends MultiDataSource implements DataSource {

    private final static Log              LOG                     = LogFactory.getLog(HADataSource.class);

    private AtomicInteger                 requestCount            = new AtomicInteger();

    protected final List<DruidDataSource> notAvailableDatasources = new CopyOnWriteArrayList<DruidDataSource>();
    
    public HADataSource() {
        super(new ArrayList<DruidDataSource>());
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        int tryCount = 0;
        int requestNumber = requestCount.getAndIncrement();

        for (;;) {
            int size = dataSources.size();
            int index = requestNumber % size;

            DruidDataSource dataSource = dataSources.get(index);

            if (!dataSource.isEnable()) {
                boolean removed = dataSources.remove(dataSource);
                if (removed) {
                    notAvailableDatasources.add(dataSource);
                }
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

}
