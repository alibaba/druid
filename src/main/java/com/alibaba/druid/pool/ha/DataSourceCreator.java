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

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An utility class to create DruidDataSource dynamically.
 *
 * @author DigitalSonic
 */
public class DataSourceCreator {
    private final static Log LOG = LogFactory.getLog(DataSourceCreator.class);

    private Properties properties = new Properties();
    private List<String> nameList = new ArrayList<String>();

    public DataSourceCreator(String file) {
        loadProperties(file);
        loadNameList();
    }

    public Map<String, DataSource> createMap(HighAvailableDataSource haDataSource) throws SQLException {
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
            DruidDataSource dataSource = create(n, url, username, password, haDataSource);
            map.put(n, dataSource);
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

    private void loadNameList() {
        Set<String> names = new HashSet<String>();
        for (String n : properties.stringPropertyNames()) {
            if (n.contains(".url")) {
                names.add(n.split("\\.url")[0]);
            }
        }
        if (!names.isEmpty()) {
            nameList.addAll(names);
        }
    }

    private void loadProperties(String file) {
        Properties properties = new Properties();
        if (file == null) {
            return;
        }
        InputStream is = null;
        try {
            LOG.debug("Trying to load " + file + " from FileSystem.");
            is = new FileInputStream(file);
        } catch(FileNotFoundException e) {
            LOG.debug("Trying to load " + file + " from Classpath.");
            try {
                is = DataSourceCreator.class.getResourceAsStream(file);
            } catch (Exception ex) {
                LOG.warn("Can not load resource " + file, ex);
            }
        }
        if (is != null) {
            try {
                properties.load(is);
                this.properties = properties;
            } catch(Exception e) {
                LOG.error("Exception occurred while loading " + file, e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
        } else {
            LOG.warn("File " + file + " can't be loaded!");
        }
    }
}
