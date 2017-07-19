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
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * @author lihengming [89921218@qq.com]
 */
@Configuration
@ConditionalOnClass(com.alibaba.druid.pool.DruidDataSource.class)
@EnableConfigurationProperties({DruidStatProperties.class, DruidDataSourceProperties.class})
@Import({DruidSpringAopConfiguration.class, DruidStatViewServletConfiguration.class, DruidWebStatFilterConfiguration.class})
public class DruidDataSourceAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource(DruidDataSourceProperties properties, Environment env) {

        //if not found prefix 'spring.datasource.druid' jdbc properties ,'spring.datasource' prefix jdbc properties will be used.
        if (properties.getUsername() == null) {
            properties.setUsername(env.getProperty("spring.datasource.username"));
        }
        if (properties.getPassword() == null) {
            properties.setPassword(env.getProperty("spring.datasource.password"));
        }
        if (properties.getUrl() == null) {
            properties.setUrl(env.getProperty("spring.datasource.url"));
        }
        if (properties.getDriverClassName() == null) {
            properties.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        }

        DruidDataSource dataSource = DruidDataSourceBuilder
                .create()
                .build(properties);

        return dataSource;
    }

    /**
     * Register the {@link DataSourcePoolMetadataProvider} instances to support DataSource metrics.
     * @see DataSourcePoolMetadataProvidersConfiguration
     */
    @Bean
    public DataSourcePoolMetadataProvider druidDataSourcePoolMetadataProvider() {
        return new DataSourcePoolMetadataProvider() {
            @Override
            public DataSourcePoolMetadata getDataSourcePoolMetadata(DataSource dataSource) {
                if (dataSource instanceof DruidDataSource) {
                    return new DruidDataSourcePoolMetadata((DruidDataSource)dataSource);
                }
                return null;
            }
        };
    }
}
