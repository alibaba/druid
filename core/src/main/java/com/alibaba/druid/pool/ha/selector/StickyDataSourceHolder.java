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
 * A class holding DataSource reference and retrieving time.
 *
 * @author DigitalSonic
 */
public class StickyDataSourceHolder {
    private long retrievingTime = System.currentTimeMillis();
    private DataSource dataSource;

    public StickyDataSourceHolder() {
    }

    public StickyDataSourceHolder(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean isValid() {
        return retrievingTime > 0 && dataSource != null;
    }

    public long getRetrievingTime() {
        return retrievingTime;
    }

    public void setRetrievingTime(long retrievingTime) {
        this.retrievingTime = retrievingTime;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
