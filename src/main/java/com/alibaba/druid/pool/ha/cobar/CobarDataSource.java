/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.pool.ha.cobar;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.DataSourceHolder;
import com.alibaba.druid.pool.ha.MultiDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

public class CobarDataSource extends MultiDataSource {

    private final static Log LOG                                   = LogFactory.getLog(CobarDataSource.class);

    public final static long DEFAULT_FAILURE_DETECT_PERRIOD_MILLIS = 1000 * 3;                                // 3
    // seconds
    public final static long DEFAULT_CONFIG_LOAD_PERRIOD_MILLIS    = 1000 * 60 * 3;                           // 3
    // minutes

    private String           url;

    private String           username;

    private String           password;

    private long             minEvictableIdleTimeMillis            = 1000 * 60 * 3;                           // 3
                                                                                                               // minutes

    private boolean          testWhileIdle                         = true;

    private List<Filter>     proxyFilters                          = new ArrayList<Filter>();
    private String           filters;

    public CobarDataSource(){
        this.setFailureDetector(new CobarFailureDetecter());
        this.setFailureDetectPeriodMillis(DEFAULT_FAILURE_DETECT_PERRIOD_MILLIS);
        this.setConfigLoadPeriodMillis(DEFAULT_CONFIG_LOAD_PERRIOD_MILLIS);
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Filter> getProxyFilters() {
        return proxyFilters;
    }

    public void setProxyFilters(List<Filter> proxyFilters) {
        this.proxyFilters = proxyFilters;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    protected void initInternal() throws SQLException {
        if (url == null || url.isEmpty()) {
            throw new SQLException("");
        }

        if (CobarConfigLoader.isCobar(url)) {
            if (this.getConfigLoader() == null) {
                this.setConfigLoader(new CobarConfigLoader(this));
            }

            final int RETRY_COUNT = 3;
            for (int i = 0; i < RETRY_COUNT; ++i) {
                try {
                    this.getConfigLoader().load();
                    break;
                } catch (Exception ex) {
                    LOG.error("load config error", ex);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }
                }
            }
        } else {
            DataSourceHolder holder = createDataSourceHolder(this.url, 1);
            this.addDataSource("master", holder);
        }
    }

    protected DataSourceHolder createDataSourceHolder(String url, int weight) throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaxActive(getMaxPoolSize());
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis); // 3 minutes
        dataSource.setTestWhileIdle(testWhileIdle);

        if (filters != null && !filters.isEmpty()) {
            dataSource.setFilters(filters);
        }
        dataSource.setProxyFilters(proxyFilters);

        DataSourceHolder holder = new DataSourceHolder(this, dataSource);
        holder.setWeight(weight);
        return holder;
    }

    protected void handleDataSourceDiscard(DataSourceHolder holder) {
        LOG.debug("dataSource close");
        JdbcUtils.close(holder);
    }

}
