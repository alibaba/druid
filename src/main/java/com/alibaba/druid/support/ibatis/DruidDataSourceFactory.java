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
package com.alibaba.druid.support.ibatis;

import java.util.Map;

import javax.sql.DataSource;

public class DruidDataSourceFactory implements com.ibatis.sqlmap.engine.datasource.DataSourceFactory {

    private DataSource dataSource;

    @SuppressWarnings("rawtypes")
    public void initialize(Map map) {
        try {
            dataSource = com.alibaba.druid.pool.DruidDataSourceFactory.createDataSource(map);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("init data source error", e);
        }
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
