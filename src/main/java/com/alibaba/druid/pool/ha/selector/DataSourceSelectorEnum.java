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

import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * An enum holding the names and classes of DataSourceSelector.
 *
 * @author DigitalSonic
 */
public enum DataSourceSelectorEnum {
    BY_NAME("byName", NamedDataSourceSelector.class),
    RANDOM("random", RandomDataSourceSelector.class),
    STICKY_RANDOM("stickyRandom", StickyRandomDataSourceSelector.class);

    private final static Log LOG = LogFactory.getLog(DataSourceSelectorEnum.class);
    private String name;
    private Class<? extends DataSourceSelector> clazz;

    DataSourceSelectorEnum(String name, Class<? extends DataSourceSelector> clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    /**
     * Create a new instance of the DataSourceSelector represented by this enum.
     *
     * @return null if dataSource is not given or exception occurred while creating new instance
     */
    public DataSourceSelector newInstance(HighAvailableDataSource dataSource) {
        if (dataSource == null) {
            LOG.warn("You should provide an instance of HighAvailableDataSource!");
            return null;
        }

        DataSourceSelector selector = null;
        try {
            selector = clazz.getDeclaredConstructor(HighAvailableDataSource.class).newInstance(dataSource);
        } catch (Exception e) {
            LOG.error("Can not create new instance of " + clazz.getName(), e);
        }
        return selector;
    }

    public String getName() {
        return name;
    }

    public Class<? extends DataSourceSelector> getClazz() {
        return clazz;
    }
}
