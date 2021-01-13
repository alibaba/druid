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

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import javax.sql.DataSource;

/**
 * An extend selector based on RandomDataSourceSelector which can stick a DataSource to a Thread in a while.
 *
 * @author DigitalSonic
 * @see RandomDataSourceSelector
 * @see StickyDataSourceHolder
 */
public class StickyRandomDataSourceSelector extends RandomDataSourceSelector {
    private final static Log LOG = LogFactory.getLog(StickyRandomDataSourceSelector.class);

    private ThreadLocal<StickyDataSourceHolder> holders = new ThreadLocal<StickyDataSourceHolder>();

    private int expireSeconds = 5;

    public StickyRandomDataSourceSelector(HighAvailableDataSource highAvailableDataSource) {
        super(highAvailableDataSource);
    }

    @Override
    public String getName() {
        return DataSourceSelectorEnum.STICKY_RANDOM.getName();
    }

    @Override
    public DataSource get() {
        StickyDataSourceHolder holder = holders.get();
        if (holder != null && isAvailable(holder)) {
            LOG.debug("Return the sticky DataSource " + holder.getDataSource().toString() + " directly.");
            return holder.getDataSource();
        }
        LOG.debug("Return a random DataSource.");
        DataSource dataSource = super.get();
        holder = new StickyDataSourceHolder(dataSource);
        holders.remove();
        holders.set(holder);
        return dataSource;
    }

    private boolean isAvailable(StickyDataSourceHolder holder) {
        boolean flag = isValid(holder) && !isExpired(holder);
        if (flag && holder.getDataSource() instanceof DruidDataSource) {
            flag = ((DruidDataSource) holder.getDataSource()).getPoolingCount() > 0;
        }
        return flag;
    }

    private boolean isValid(StickyDataSourceHolder holder) {
        boolean flag = holder.isValid() && !getBlacklist().contains(holder.getDataSource());
        if (!(holder.getDataSource() instanceof DruidDataSource) || !flag) {
            return flag;
        }
        DruidDataSource dataSource = (DruidDataSource) holder.getDataSource();
        return flag && dataSource.getActiveCount() < dataSource.getMaxActive();
    }

    private boolean isExpired(StickyDataSourceHolder holder) {
        return System.currentTimeMillis() - holder.getRetrievingTime() > expireSeconds * 1000;
    }

    public int getExpireSeconds() {
        return expireSeconds;
    }

    public void setExpireSeconds(int expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
}
