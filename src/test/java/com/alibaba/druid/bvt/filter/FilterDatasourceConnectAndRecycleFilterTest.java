package com.alibaba.druid.bvt.filter;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;

public class FilterDatasourceConnectAndRecycleFilterTest extends TestCase {

    TestFilter      filter     = new TestFilter();
    List<Filter>    filterList = new ArrayList<Filter>();
    private DruidDataSource dataSource = new DruidDataSource();

    protected void setUp() throws Exception {
        filterList.add(filter);
        dataSource.setProxyFilters(filterList);
        dataSource.setUrl("jdbc:mock:");
    }
    
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test() throws Exception {

        Assert.assertEquals(0, filter.getDataSourceConnectCount());
        Assert.assertEquals(0, filter.getDataSourceRecycleCount());
        Connection conn = dataSource.getConnection();
        Assert.assertEquals(1, filter.getDataSourceConnectCount());
        Assert.assertEquals(0, filter.getDataSourceRecycleCount());
        conn.close();
        Assert.assertEquals(1, filter.getDataSourceConnectCount());
        Assert.assertEquals(1, filter.getDataSourceRecycleCount());
    }

    public static class TestFilter extends FilterAdapter {

        private AtomicLong dataSourceConnectCount = new AtomicLong();
        private AtomicLong dataSourceRecycleCount = new AtomicLong();

        @Override
        public void dataSource_recycle(FilterChain chain, DruidPooledConnection connection) throws SQLException {
            chain.dataSource_recycle(connection);
            dataSourceRecycleCount.incrementAndGet();
        }

        @Override
        public DruidPooledConnection dataSource_connect(FilterChain chain, DruidDataSource dataSource,
                                                        long maxWaitMillis) throws SQLException {
            dataSourceConnectCount.incrementAndGet();
            return chain.dataSource_connect(dataSource, maxWaitMillis);
        }

        public long getDataSourceConnectCount() {
            return dataSourceConnectCount.get();
        }

        public long getDataSourceRecycleCount() {
            return dataSourceRecycleCount.get();
        }
    }
}
