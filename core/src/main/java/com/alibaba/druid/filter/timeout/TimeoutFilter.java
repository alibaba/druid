package com.alibaba.druid.filter.timeout;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidAbstractDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;

import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.*;

public class TimeoutFilter extends FilterAdapter {
    static ExecutorService executorService = Executors.newFixedThreadPool(1);

    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        DataSourceProxy dataSource = chain.getDataSource();
        if (!(dataSource instanceof DruidAbstractDataSource)) {
            throw new IllegalArgumentException("timeout filter can only use on druid datasource.");
        }
        long maxConnectTime = ((DruidAbstractDataSource) dataSource).getMaxConnect();
        if (maxConnectTime < 0) {
            return chain.connection_connect(info);
        }
        Future<ConnectionProxy> future = executorService.submit(() -> chain.connection_connect(info));
        try {
            return future.get(maxConnectTime, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            String errMessage = String.format("create The creation time exceeded the limit time milliseconds %d", maxConnectTime);
            throw new DruidRuntimeException(errMessage, e);
        }
    }
}
