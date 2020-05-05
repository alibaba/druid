/*
 * Copyright 1999-2020 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.pool.ha.node;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.DataSourceCreator;
import com.alibaba.druid.pool.ha.HighAvailableDataSource;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Update the DataSource Connection Pool when notified.
 *
 * @author DigitalSonic
 */
public class PoolUpdater implements Observer {
    public final static int DEFAULT_INTERVAL = 60;
    private final static Log LOG = LogFactory.getLog(PoolUpdater.class);
    private Set<String> nodesToDel = new CopyOnWriteArraySet<String>();
    private HighAvailableDataSource highAvailableDataSource;

    private Lock lock = new ReentrantLock();
    private ScheduledExecutorService executor;
    private int intervalSeconds = DEFAULT_INTERVAL;
    private volatile boolean inited = false;
    private boolean allowEmptyPool = false;

    public PoolUpdater(HighAvailableDataSource highAvailableDataSource) {
        setHighAvailableDataSource(highAvailableDataSource);
    }

    /**
     * Create a ScheduledExecutorService to remove unused DataSources.
     */
    public void init() {
        if (inited) {
            return;
        }
        synchronized (this) {
            if (inited) {
                return;
            }
            if (intervalSeconds < 10) {
                LOG.warn("CAUTION: Purge interval has been set to " + intervalSeconds
                        + ". This value should NOT be too small.");
            }
            if (intervalSeconds <= 0) {
                intervalSeconds = DEFAULT_INTERVAL;
            }
            executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    LOG.debug("Purging the DataSource Pool every " + intervalSeconds + "s.");
                    try {
                        removeDataSources();
                    } catch (Exception e) {
                        LOG.error("Exception occurred while removing DataSources.", e);
                    }
                }
            }, intervalSeconds, intervalSeconds, TimeUnit.SECONDS);
        }
    }

    public void destroy() {
        if (executor == null || executor.isShutdown()) {
            return;
        }
        try {
            executor.shutdown();
        } catch (Exception e) {
            LOG.error("Can NOT shutdown the ScheduledExecutorService.", e);
        }
    }

    /**
     * Process the given NodeEvent[]. Maybe add / delete some nodes.
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!(o instanceof NodeListener)) {
            return;
        }
        if (arg == null || !(arg instanceof NodeEvent[])) {
            return;
        }
        NodeEvent[] events = (NodeEvent[]) arg;

        if (events.length <= 0) {
            return;
        }

        try {
            LOG.info("Waiting for Lock to start processing NodeEvents.");
            lock.lock();
            LOG.info("Start processing the NodeEvent[" + events.length + "].");
            for (NodeEvent e : events) {
                if (e.getType() == NodeEventTypeEnum.ADD) {
                    addNode(e);
                } else if (e.getType() == NodeEventTypeEnum.DELETE) {
                    deleteNode(e);
                }
            }
        } catch (Exception e) {
            LOG.error("Exception occurred while updating Pool.", e);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Remove unused DataSources.
     */
    public void removeDataSources() {
        if (nodesToDel == null || nodesToDel.isEmpty()) {
            return;
        }
        try {
            lock.lock();
            Map<String, DataSource> map = highAvailableDataSource.getDataSourceMap();
            Set<String> copySet = new HashSet<String>(nodesToDel);
            for (String nodeName : copySet) {
                LOG.info("Start removing Node " + nodeName + ".");
                if (!map.containsKey(nodeName)) {
                    LOG.info("Node " + nodeName + " is NOT existed in the map.");
                    cancelBlacklistNode(nodeName);
                    continue;
                }
                DataSource ds = map.get(nodeName);
                if (ds instanceof DruidDataSource) {
                    DruidDataSource dds = (DruidDataSource) ds;
                    int activeCount = dds.getActiveCount(); // CAUTION, activeCount MAYBE changed!
                    if (activeCount > 0) {
                        LOG.warn("Node " + nodeName + " is still running [activeCount=" + activeCount
                                + "], try next time.");
                        continue;
                    } else {
                        LOG.info("Close Node " + nodeName + " and remove it.");
                        try {
                            dds.close();
                        } catch (Exception e) {
                            LOG.error("Exception occurred while closing Node " + nodeName
                                    + ", just remove it.", e);
                        }
                    }
                }
                map.remove(nodeName); // Remove the node directly if it is NOT a DruidDataSource.
                cancelBlacklistNode(nodeName);
            }
        } catch (Exception e) {
            LOG.error("Exception occurred while removing DataSources.", e);
        } finally {
            lock.unlock();
        }
    }

    protected void addNode(NodeEvent event) {
        String nodeName = event.getNodeName();
        String url = event.getUrl();
        String username = event.getUsername();
        String password = event.getPassword();

        Map<String, DataSource> map = highAvailableDataSource.getDataSourceMap();
        if (nodeName == null || nodeName.isEmpty()) {
            return;
        }
        LOG.info("Adding Node " + nodeName + "[url: " + url + ", username: " + username + "].");
        if (map.containsKey(nodeName)) {
            cancelBlacklistNode(nodeName);
            return;
        }
        DruidDataSource dataSource = null;
        try {
            dataSource = DataSourceCreator.create(nodeName, url, username,
                    password, this.highAvailableDataSource);
            map.put(nodeName, dataSource);
            LOG.info("Creating Node " + nodeName + "[url: " + url + ", username: " + username + "].");
        } catch (Exception e) {
            LOG.error("Can NOT create DataSource " + nodeName + ". IGNORE IT.", e);
            JdbcUtils.close(dataSource);
        }
    }

    protected void deleteNode(NodeEvent event) {
        String nodeName = event.getNodeName();
        Map<String, DataSource> map = highAvailableDataSource.getDataSourceMap();
        if (nodeName == null || nodeName.isEmpty() || !map.containsKey(nodeName)) {
            return;
        }
        map = highAvailableDataSource.getAvailableDataSourceMap();
        if (!allowEmptyPool && map.size() == 1 && map.containsKey(nodeName)) {
            LOG.warn(nodeName + " is the only DataSource left, don't remove it.");
            return;
        }
        blacklistNode(nodeName);
    }

    private void cancelBlacklistNode(String nodeName) {
        LOG.info("Cancel the deletion of Node " + nodeName + ".");
        nodesToDel.remove(nodeName);
        highAvailableDataSource.removeBlackList(nodeName);
    }

    private void blacklistNode(String nodeName) {
        LOG.info("Deleting Node " + nodeName + ", just add it into blacklist.");
        nodesToDel.add(nodeName);
        highAvailableDataSource.addBlackList(nodeName);
    }

    public HighAvailableDataSource getHighAvailableDataSource() {
        return highAvailableDataSource;
    }

    public void setHighAvailableDataSource(HighAvailableDataSource highAvailableDataSource) {
        this.highAvailableDataSource = highAvailableDataSource;
    }

    public Set<String> getNodesToDel() {
        return nodesToDel;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public void setIntervalSeconds(int intervalSeconds) {
        this.intervalSeconds = intervalSeconds;
    }

    public boolean isAllowEmptyPool() {
        return allowEmptyPool;
    }

    public void setAllowEmptyPool(boolean allowEmptyPool) {
        this.allowEmptyPool = allowEmptyPool;
    }
}
