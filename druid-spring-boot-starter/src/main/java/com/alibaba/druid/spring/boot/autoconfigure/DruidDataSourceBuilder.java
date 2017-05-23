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
import org.springframework.beans.MutablePropertyValues;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.jdbc.DatabaseDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * @author lihengming [89921218@qq.com]
 */
public class DruidDataSourceBuilder {

    private Map<String, String> properties = new HashMap<String, String>();

    public static DruidDataSourceBuilder create() {
        return new DruidDataSourceBuilder();
    }

    public DruidDataSource build() {
        DruidDataSource dataSource = new DruidDataSource();
        maybeGetDriverClassName();
        bind(dataSource);
        return dataSource;
    }

    //use spring boot relaxed binding by reflection config druid . detail see Spring Boot Reference Relaxed binding section.
    private void bind(DruidDataSource result) {
        MutablePropertyValues properties = new MutablePropertyValues(this.properties);
        new RelaxedDataBinder(result)
                .withAlias("url", "jdbcUrl")
                .withAlias("username", "user")
                .bind(properties);
    }

    private void maybeGetDriverClassName() {
        if (!this.properties.containsKey("driverClassName")
                && this.properties.containsKey("url")) {
            String url = this.properties.get("url");
            String driverClass = DatabaseDriver.fromJdbcUrl(url).getDriverClassName();
            this.properties.put("driverClassName", driverClass);
        }
    }
}
