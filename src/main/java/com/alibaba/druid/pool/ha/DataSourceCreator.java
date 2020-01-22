/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.pool.ha;

import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * An utility class to create DruidDataSource dynamically.
 *
 * @author DigitalSonic
 */
public class DataSourceCreator {

    public static DruidDataSource create(String name, String url, String username, String password,
                                         HighAvailableDataSource haDataSource) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setName(name + "-" + System.identityHashCode(dataSource));
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        dataSource.setDriverClassName(haDataSource.getDriverClassName());
        dataSource.setConnectProperties(haDataSource.getConnectProperties());
        dataSource.setConnectionProperties(haDataSource.getConnectionProperties());

        dataSource.setInitialSize(haDataSource.getInitialSize());
        dataSource.setMaxActive(haDataSource.getMaxActive());
        dataSource.setMinIdle(haDataSource.getMinIdle());
        dataSource.setMaxWait(haDataSource.getMaxWait());

        dataSource.setValidationQuery(haDataSource.getValidationQuery());
        dataSource.setValidationQueryTimeout(haDataSource.getValidationQueryTimeout());
        dataSource.setTestOnBorrow(haDataSource.isTestOnBorrow());
        dataSource.setTestOnReturn(haDataSource.isTestOnReturn());
        dataSource.setTestWhileIdle(haDataSource.isTestWhileIdle());

        dataSource.setPoolPreparedStatements(haDataSource.isPoolPreparedStatements());
        dataSource.setSharePreparedStatements(haDataSource.isSharePreparedStatements());
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(
                haDataSource.getMaxPoolPreparedStatementPerConnectionSize());

        dataSource.setQueryTimeout(haDataSource.getQueryTimeout());
        dataSource.setTransactionQueryTimeout(haDataSource.getTransactionQueryTimeout());

        dataSource.setTimeBetweenEvictionRunsMillis(haDataSource.getTimeBetweenEvictionRunsMillis());
        dataSource.setMinEvictableIdleTimeMillis(haDataSource.getMinEvictableIdleTimeMillis());
        dataSource.setMaxEvictableIdleTimeMillis(haDataSource.getMaxEvictableIdleTimeMillis());
        dataSource.setPhyTimeoutMillis(haDataSource.getPhyTimeoutMillis());
        dataSource.setTimeBetweenConnectErrorMillis(haDataSource.getTimeBetweenConnectErrorMillis());

        dataSource.setRemoveAbandoned(haDataSource.isRemoveAbandoned());
        dataSource.setRemoveAbandonedTimeoutMillis(haDataSource.getRemoveAbandonedTimeoutMillis());
        dataSource.setLogAbandoned(haDataSource.isLogAbandoned());

        dataSource.setProxyFilters(haDataSource.getProxyFilters());
        dataSource.setFilters(haDataSource.getFilters());
        dataSource.setLogWriter(haDataSource.getLogWriter());

        dataSource.init();

        return dataSource;
    }
}
