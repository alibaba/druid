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

import javax.sql.DataSource;

/**
 * Interface for those selector to implement.
 * e.g. Random and Named
 *
 * @author DigitalSonic
 */
public interface DataSourceSelector {
    /**
     * Return a DataSource according to the implemention.
     */
    DataSource get();

    /**
     * Set the target DataSource name to return.
     * Wether to use this or not, it's decided by the implemention.
     */
    void setTarget(String name);

    /**
     * Return the name of this DataSourceSelector.
     * e.g. byName
     */
    String getName();

    /**
     * Init the DataSourceSelector before use it.
     */
    void init();

    /**
     * Destroy the DataSourceSelector, maybe interrupt the Thread.
     */
    void destroy();
}
