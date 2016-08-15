/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.proxy;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyImpl;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcStatManager;

public class CounterFilterTest extends TestCase {

    String sql = "SELECT 1";

    protected void tearDown() throws Exception {
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }
    
    public void test_countFilter() throws Exception {
        DataSourceProxyConfig config = new DataSourceProxyConfig();
        config.setUrl("");

        DataSourceProxyImpl dataSource = new DataSourceProxyImpl(null, config);
        JdbcDataSourceStat dataSourceStat = dataSource.getDataSourceStat();
        
        StatFilter filter = new StatFilter();
        filter.init(dataSource);

        dataSourceStat.reset();

        Assert.assertNull(StatFilter.getStatFilter(dataSource));
        Assert.assertNull(dataSourceStat.getSqlStat(Integer.MAX_VALUE));
        Assert.assertNull(dataSourceStat.getConnectionStat().getConnectLastTime());

        FilterChain chain = new FilterChainImpl(dataSource) {

            public ConnectionProxy connection_connect(Properties info) throws SQLException {
                throw new SQLException();
            }
        };

        Exception error = null;
        try {
            filter.connection_connect(chain, new Properties());
        } catch (SQLException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
        Assert.assertEquals(1, dataSourceStat.getConnectionStat().getConnectErrorCount());
        Assert.assertNotNull(dataSourceStat.getConnectionStat().getConnectLastTime());
    }

    public void test_count_filter() throws Exception {
        DataSourceProxyConfig config = new DataSourceProxyConfig();
        config.setUrl("");
        config.setRawUrl("jdbc:mock:");

        StatFilter filter = new StatFilter();

        MockDriver driver = new MockDriver();
        DataSourceProxyImpl dataSource = new DataSourceProxyImpl(driver, config);

        filter.init(dataSource);
        config.getFilters().add(filter);

        Connection conn = dataSource.connect(null);

        Statement stmt = conn.createStatement();
        ResultSetProxy rs = (ResultSetProxy) stmt.executeQuery(sql);
        rs.close();
        stmt.close();

        conn.close();
        conn.close();
        
        dataSource.getCompositeData();
        dataSource.getProperties();
        dataSource.getDataSourceMBeanDomain();
    }

}
