/*
 * Copyright 2022 Alibaba Group Holding Ltd.
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
// Created on 2022年1月14日
// $Id$

package com.alibaba.druid.spring.boot.autoconfigure.support.actuator;

import javax.sql.DataSource;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadata;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;

/**
 * @author zero3h
 */
@Configuration
@ConditionalOnClass({ Health.class, DruidDataSource.class })
public class DruidDataSourcePoolMetadataProvider {

    /**
     * @return
     */
    @Bean
    public DataSourcePoolMetadataProvider druidPoolDataSourceMetadataProvider() {
        return new DataSourcePoolMetadataProvider() {

            /*
             * (non-Javadoc)
             * @see org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvider#
             * getDataSourcePoolMetadata(javax.sql.DataSource)
             */
            @Override
            public DataSourcePoolMetadata getDataSourcePoolMetadata(DataSource dataSource) {
                if (dataSource instanceof DruidDataSource) {
                    return new DruidDataSourcePoolMetadata((DruidDataSource) dataSource);
                }
                return null;
            }
        };
    }
}
