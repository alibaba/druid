/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.spring.boot.autoconfigure;


import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.core.env.Environment;

import java.sql.SQLException;

/**
 * @author lihengming [89921218@qq.com]
 */
public class DruidDataSourceBuilder {

    public static DruidDataSourceBuilder create() {
        return new DruidDataSourceBuilder();
    }

    @Deprecated
    public DruidDataSource build() {
        return new DruidDataSource();
    }

    /**
     * Use Spring Environment by specify configuration properties prefix to build DruidDataSource.
     * <p>
     * Environment support .properties, .yml ,command etc.
     * <p>
     * Properties key style support max-active,MAX_ACTIVE,maxActive etc.
     * <p>
     * Please read the spring documents for details.
     * @param env    Spring Environment
     * @param prefix Spring Boot configuration properties prefix
     * @return DruidDataSource
     */
    public DruidDataSource build(Environment env, String prefix) {
        DruidDataSourceProperties properties = new DruidDataSourceProperties();
        properties.setUrl(env.getProperty(prefix + "url"));
        properties.setUsername(env.getProperty(prefix + "username"));
        properties.setPassword(env.getProperty(prefix + "password"));
        properties.setDriverClassName(env.getProperty(prefix + "driver-class-name"));
        properties.setInitialSize(env.getProperty(prefix + "initial-size", Integer.class));
        properties.setMaxActive(env.getProperty(prefix + "max-active", Integer.class));
        properties.setMinIdle(env.getProperty(prefix + "min-idle", Integer.class));
        properties.setMaxWait(env.getProperty(prefix + "max-wait", Long.class));
        properties.setPoolPreparedStatements(env.getProperty(prefix + "pool-prepared-statements", Boolean.class));
        properties.setMaxOpenPreparedStatements(env.getProperty(prefix + "max-open-prepared-statements", Integer.class));
        properties.setMaxPoolPreparedStatementPerConnectionSize(
                env.getProperty(prefix + "max-pool-prepared-statement-per-connection-size", Integer.class));
        properties.setValidationQuery(env.getProperty(prefix + "validation-query"));
        properties.setValidationQueryTimeout(env.getProperty(prefix + "validation-query-timeout", Integer.class));
        properties.setTestOnBorrow(env.getProperty(prefix + "test-on-borrow", Boolean.class));
        properties.setTestOnReturn(env.getProperty(prefix + "test-on-return", Boolean.class));
        properties.setTestWhileIdle(env.getProperty(prefix + "test-while-idle", Boolean.class));
        properties.setTimeBetweenEvictionRunsMillis(env.getProperty(prefix + "time-between-eviction-runs-millis", Long.class));
        properties.setMinEvictableIdleTimeMillis(env.getProperty(prefix + "min-evictable-idle-time-millis", Long.class));
        properties.setMaxEvictableIdleTimeMillis(env.getProperty(prefix + "max-evictable-idle-time-millis", Long.class));
        properties.setFilters(env.getProperty(prefix + "filters"));
        return build(properties);
    }


    DruidDataSource build(DruidDataSourceProperties properties) {
        DruidDataSource dataSource = new DruidDataSource();
        if (properties.getUrl() != null) {
            dataSource.setUrl(properties.getUrl());
        }
        if (properties.getUsername() != null) {
            dataSource.setUsername(properties.getUsername());
        }
        if (properties.getPassword() != null) {
            dataSource.setPassword(properties.getPassword());
        }
        if (properties.getDriverClassName() != null) {
            dataSource.setDriverClassName(properties.getDriverClassName());
        }
        if (properties.getInitialSize() != null) {
            dataSource.setInitialSize(properties.getInitialSize());
        }
        if (properties.getMaxActive() != null) {
            dataSource.setMaxActive(properties.getMaxActive());
        }
        if (properties.getMinIdle() != null) {
            dataSource.setMinIdle(properties.getMinIdle());
        }
        if (properties.getMaxWait() != null) {
            dataSource.setMaxWait(properties.getMaxWait());
        }
        if (properties.getPoolPreparedStatements() != null) {
            dataSource.setPoolPreparedStatements(properties.getPoolPreparedStatements());
        }
        if (properties.getMaxOpenPreparedStatements() != null) {
            dataSource.setMaxOpenPreparedStatements(properties.getMaxOpenPreparedStatements());
        }
        if (properties.getMaxPoolPreparedStatementPerConnectionSize() != null) {
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(properties.getMaxPoolPreparedStatementPerConnectionSize());
        }
        if (properties.getValidationQuery() != null) {
            dataSource.setValidationQuery(properties.getValidationQuery());
        }
        if (properties.getValidationQueryTimeout() != null) {
            dataSource.setValidationQueryTimeout(properties.getValidationQueryTimeout());
        }
        if (properties.getTestWhileIdle() != null) {
            dataSource.setTestWhileIdle(properties.getTestWhileIdle());
        }
        if (properties.getTestOnBorrow() != null) {
            dataSource.setTestOnBorrow(properties.getTestOnBorrow());
        }
        if (properties.getTestOnReturn() != null) {
            dataSource.setTestOnReturn(properties.getTestOnReturn());
        }
        if (properties.getTimeBetweenEvictionRunsMillis() != null) {
            dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());
        }
        if (properties.getMinEvictableIdleTimeMillis() != null) {
            dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        }
        if (properties.getMaxEvictableIdleTimeMillis() != null) {
            dataSource.setMaxEvictableIdleTimeMillis(properties.getMaxEvictableIdleTimeMillis());
        }
        try {
            if (properties.getFilters() != null) {
                dataSource.setFilters(properties.getFilters());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dataSource;
    }
}
