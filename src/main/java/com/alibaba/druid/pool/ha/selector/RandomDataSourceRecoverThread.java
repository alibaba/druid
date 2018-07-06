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
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.sql.Connection;

/**
 * A Thread trying to test if DataSource in blacklist has been recovered.
 *
 * @author DigitalSonic
 */
public class RandomDataSourceRecoverThread implements Runnable {
    private final static Log LOG = LogFactory.getLog(RandomDataSourceRecoverThread.class);
    private RandomDataSourceSelector selector;
    private int sleepSeconds = 30;

    public RandomDataSourceRecoverThread(RandomDataSourceSelector selector) {
        this.selector = selector;
    }

    @Override
    public void run() {
        while (true) {
            if (selector != null && selector.getBlacklist() != null
                    && !selector.getBlacklist().isEmpty()) {
                LOG.info(selector.getBlacklist().size() + " DataSource in blacklist.");
                for (DataSource dataSource : selector.getBlacklist()) {
                    if (!(dataSource instanceof DruidDataSource)) {
                        continue;
                    }
                    tryOneDataSource((DruidDataSource) dataSource);
                }
            }
            sleep();
        }
    }

    private void tryOneDataSource(DruidDataSource dataSource) {
        if (dataSource == null) {
            return;
        }
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            dataSource.validateConnection(connection);
            LOG.info(dataSource.getName() + " is available now.");
            selector.removeBlacklist(dataSource);
        } catch(Exception e) {
            LOG.warn("DataSource[" + dataSource.getName() + "] is still unavailable. Exception: "
                    + e.getMessage());
            if (connection != null) {
                dataSource.discardConnection(connection);
            }
        } finally {
            JdbcUtils.close(connection);
        }
    }

    private void sleep() {
        try {
            Thread.sleep(sleepSeconds * 1000);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    public void setSleepSeconds(int sleepSeconds) {
        this.sleepSeconds = sleepSeconds;
    }
}
