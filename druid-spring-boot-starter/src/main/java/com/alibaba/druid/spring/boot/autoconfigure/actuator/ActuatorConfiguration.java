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
package com.alibaba.druid.spring.boot.autoconfigure.actuator;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.actuator.endpoint.DruidDataSourceMvcEndpoint;
import com.alibaba.druid.spring.boot.autoconfigure.actuator.metadata.DruidDataSourcePoolMetadata;

import org.springframework.boot.actuate.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.actuate.endpoint.mvc.MvcEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * @author lihengming [89921218@qq.com]
 *
 * Spring Boot Actuator support.
 */
public class ActuatorConfiguration {
    /**
     * Register the {@link DataSourcePoolMetadataProvider} instances to support DataSource metrics.
     *
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

    /**
     * Register DruidDataSourceMvcEndpoint to support Spring Boot Actuator Endpoints.
     *
     * @see MvcEndpoint
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnWebApplication
    @ConditionalOnEnabledEndpoint("druid")
    public DruidDataSourceMvcEndpoint druidDataSourceMvcEndpoint() {
        return new DruidDataSourceMvcEndpoint();
    }
}
