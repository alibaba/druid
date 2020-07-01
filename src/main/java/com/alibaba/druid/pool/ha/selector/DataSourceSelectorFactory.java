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

/**
 * A Factory pattern for DataSourceSelector.
 *
 * @author DigitalSonic
 */
public class DataSourceSelectorFactory {
    /**
     * Get a new instance of the given selector name.
     *
     * @return null if the given name do not represent a DataSourceSelector
     */
    public static DataSourceSelector getSelector(String name, HighAvailableDataSource highAvailableDataSource) {
        for (DataSourceSelectorEnum e : DataSourceSelectorEnum.values()) {
            if (e.getName().equalsIgnoreCase(name)) {
                return e.newInstance(highAvailableDataSource);
            }
        }
        return null;
    }
}
