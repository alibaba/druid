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
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * An utility class to create DruidDataSource dynamically.
 *
 * @author DigitalSonic
 */
public class DataSourceCreator {
    private final static Log LOG = LogFactory.getLog(DataSourceCreator.class);

    private Properties properties;
    private List<String> nameList;

    public DataSourceCreator(String file) {
        this(file, "");
    }

    public DataSourceCreator(String file, String propertyPrefix) {
        this.properties = PropertiesUtils.loadProperties(file);
        this.nameList = PropertiesUtils.loadNameList(this.properties, propertyPrefix);
    }

    public Map<String, DataSource> createMap(HighAvailableDataSource haDataSource) {
        Map<String, DataSource> map = new ConcurrentHashMap<String, DataSource>();

        if (nameList == null || nameList.isEmpty()) {
            LOG.error("No DataSource will be created!");
            return map;
        }

        for (String n : nameList) {
            String url = properties.getProperty(n + ".url");
            String username = properties.getProperty(n + ".username");
            String password = properties.getProperty(n + ".password");
            LOG.info("Creating " + n + " with url[" + url + "] and username[" + username + "].");
            DruidDataSource dataSource = null;
            try {
                dataSource = create(n, url, username, password, haDataSource);
                map.put(n, dataSource);
            } catch(Exception e) {
                LOG.error("Can NOT create DruidDataSource for " + n, e);
                if (dataSource != null) {
                    try {
                        dataSource.close();
                    } catch (Exception ex) {
                        LOG.error("Exception occurred while closing the FAILURE DataSource.", ex);
                    }
                }
            }
        }
        LOG.info(map.size() + " DruidDataSource(s) created. ");
        return map;
    }

    protected DruidDataSource create(String name, String url, String username, String password,
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

    public List<String> getNameList() {
        return nameList;
    }
}
