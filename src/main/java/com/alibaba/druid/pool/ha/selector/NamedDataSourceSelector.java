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
package com.alibaba.druid.pool.ha.selector;

import java.util.Map;

import javax.sql.DataSource;

import com.alibaba.druid.pool.ha.HighAvailableDataSource;

/**
 * Use the given name in ThreadLocal variable to choose DataSource.
 *
 * @author DigitalSonic
 */
public class NamedDataSourceSelector implements DataSourceSelector {
    public static final String DEFAULT_NAME = "default";
    private HighAvailableDataSource highAvailableDataSource;
    private ThreadLocal<String> targetDataSourceName = new ThreadLocal<String>();
    private String defaultName = DEFAULT_NAME;

    public NamedDataSourceSelector(HighAvailableDataSource highAvailableDataSource) {
        this.highAvailableDataSource = highAvailableDataSource;
    }

    @Override
    public void init() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public String getName() {
        return DataSourceSelectorEnum.BY_NAME.getName();
    }

    @Override
    public DataSource get() {
        if (highAvailableDataSource == null) {
            return null;
        }

        Map<String, DataSource> dataSourceMap = highAvailableDataSource.getAvailableDataSourceMap();
        if (dataSourceMap == null || dataSourceMap.isEmpty()) {
            return null;
        }
        if (dataSourceMap.size() == 1) {
            for (DataSource v : dataSourceMap.values()) {
                return v;
            }
        }
        String name = getTarget();
        if (name == null) {
            if (dataSourceMap.get(getDefaultName()) != null) {
                return dataSourceMap.get(getDefaultName());
            }
        } else {
            return dataSourceMap.get(name);
        }
        return null;
    }

    @Override
    public void setTarget(String name) {
        targetDataSourceName.set(name);
    }

    public String getTarget() {
        return targetDataSourceName.get();
    }

    public void resetDataSourceName() {
        targetDataSourceName.remove();
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }
}
