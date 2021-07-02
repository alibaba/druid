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

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.pool.ha.PropertiesUtils;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryForever;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A NodeListener that watches a Zookeeper Path.
 * e.g.
 * Path to watch:
 * <pre>
 * + ha-druid-datasources
 * |---- NodeA
 * |---- NodeB
 * </pre>
 * <p>
 * The Data of NodeA (in Java properties format):
 * <pre>
 * foo.host=xxx.xxx.xxx.xxx
 * foo.port=3308
 * foo.username=foo
 * foo.password=foo_password
 *
 * bar.host=xxx.xxx.xxx.xxx
 * bar.port=3309
 * bar.username=bar
 * bar.password=bar_password
 * bar.database=bar_database
 * </pre>
 *
 * @author DigitalSonic
 */
public class ZookeeperNodeListener extends NodeListener {
    private final static Log LOG = LogFactory.getLog(ZookeeperNodeListener.class);
    private String zkConnectString;
    private String path = "/ha-druid-datasources";
    private Lock lock = new ReentrantLock();
    private boolean privateZkClient = false; // Should I close the client?
    private PathChildrenCache cache;
    private CuratorFramework client;
    /**
     * URL Template, e.g.
     * jdbc:mysql://${host}:${port}/${database}?useUnicode=true
     * ${host}, ${port} and ${database} will be replaced by values in ZK
     * ${host} can also be #{host} and #host#
     */
    private String urlTemplate;

    /**
     * Init a PathChildrenCache to watch the given path.
     */
    @Override
    public void init() {
        checkParameters();
        super.init();
        if (client == null) {
            client = CuratorFrameworkFactory.builder()
                    .canBeReadOnly(true)
                    .connectionTimeoutMs(5000)
                    .connectString(zkConnectString)
                    .retryPolicy(new RetryForever(10000))
                    .sessionTimeoutMs(30000)
                    .build();
            client.start();
            privateZkClient = true;
        }
        cache = new PathChildrenCache(client, path, true);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                try {
                    LOG.info("Receive an event: " + event.getType());
                    lock.lock();
                    PathChildrenCacheEvent.Type eventType = event.getType();
                    switch (eventType) {
                        case CHILD_REMOVED:
                            updateSingleNode(event, NodeEventTypeEnum.DELETE);
                            break;
                        case CHILD_ADDED:
                            updateSingleNode(event, NodeEventTypeEnum.ADD);
                            break;
                        case CONNECTION_RECONNECTED:
                            refreshAllNodes();
                            break;
                        default:
                            // CHILD_UPDATED
                            // INITIALIZED
                            // CONNECTION_LOST
                            // CONNECTION_SUSPENDED
                            LOG.info("Received a PathChildrenCacheEvent, IGNORE it: " + event);
                    }
                } finally {
                    lock.unlock();
                    LOG.info("Finish the processing of event: " + event.getType());
                }
            }
        });
        try {
            // Use BUILD_INITIAL_CACHE to force build cache in the current Thread.
            // We don't use POST_INITIALIZED_EVENT, so there's no INITIALIZED event.
            cache.start(PathChildrenCache.StartMode.BUILD_INITIAL_CACHE);
        } catch (Exception e) {
            LOG.error("Can't start PathChildrenCache", e);
        }
    }

    /**
     * Close PathChildrenCache and CuratorFramework.
     */
    @Override
    public void destroy() {
        if (cache != null) {
            try {
                cache.close();
            } catch (IOException e) {
                LOG.error("IOException occurred while closing PathChildrenCache.", e);
            }
        }
        if (client != null && privateZkClient) {
            client.close();
        }
    }

    /**
     * Build Properties from PathChildrenCache.
     * Should be called after init().
     *
     * @see #getPropertiesFromCache()
     */
    @Override
    public List<NodeEvent> refresh() {
        try {
            lock.lock();
            Properties properties = getPropertiesFromCache();
            List<NodeEvent> events = NodeEvent.getEventsByDiffProperties(getProperties(), properties);
            if (events != null && !events.isEmpty()) {
                setProperties(properties);
            }
            return events;
        } finally {
            lock.unlock();
        }
    }

    private void checkParameters() {
        if (client == null && StringUtils.isEmpty(zkConnectString)) {
            throw new DruidRuntimeException("ZK Client is NULL, Please set the zkConnectString.");
        }
        if (StringUtils.isEmpty(path)) {
            throw new DruidRuntimeException("Please set the ZooKeeper node path.");
        }
        if (StringUtils.isEmpty(urlTemplate)) {
            throw new DruidRuntimeException("Please set the urlTemplate.");
        }
    }

    private void updateSingleNode(PathChildrenCacheEvent event, NodeEventTypeEnum type) {
        ChildData data = event.getData();
        String nodeName = getNodeName(data);
        List<String> names = new ArrayList<String>();
        names.add(getPrefix() + "." + nodeName);
        Properties properties = getPropertiesFromChildData(data);
        List<NodeEvent> events = NodeEvent.generateEvents(properties, names, type);

        if (events.isEmpty()) {
            return;
        }
        if (type == NodeEventTypeEnum.ADD) {
            getProperties().putAll(properties);
        } else {
            for (String n : properties.stringPropertyNames()) {
                getProperties().remove(n);
            }
        }
        super.update(events);
    }

    private void refreshAllNodes() {
        try {
            if (client.checkExists().forPath(path) == null) {
                LOG.warn("PATH[" + path + "] is NOT existed, can NOT refresh nodes.");
                return;
            }
            cache.rebuild();
            Properties properties = getPropertiesFromCache();
            List<NodeEvent> events = NodeEvent.getEventsByDiffProperties(getProperties(), properties);
            if (events != null && !events.isEmpty()) {
                setProperties(properties);
                super.update(events);
            }
        } catch (Exception e) {
            LOG.error("Can NOT refresh Cache Nodes.", e);
        }
    }

    private Properties getPropertiesFromCache() {
        List<ChildData> data = cache.getCurrentData();
        Properties properties = new Properties();
        for (ChildData c : data) {
            properties.putAll(getPropertiesFromChildData(c));
        }
        return properties;
    }

    private Properties getPropertiesFromChildData(ChildData data) {
        String dataPrefix = getPrefix();
        Properties properties = new Properties();
        if (data == null) {
            return properties;
        }
        String nodeName = getNodeName(data);
        String str = new String(data.getData());
        Properties full = new Properties();
        try {
            full.load(new StringReader(str));
        } catch (IOException e) {
            LOG.error("Can't load Properties from String. " + str, e);
        }
        Properties filtered = PropertiesUtils.filterPrefix(full, dataPrefix);
        for (String n : filtered.stringPropertyNames()) {
            properties.setProperty(
                    n.replaceFirst(dataPrefix, dataPrefix + "\\." + nodeName),
                    filtered.getProperty(n));
        }
        if (!properties.containsKey(dataPrefix + "." + nodeName + ".url")) {
            properties.setProperty(dataPrefix + "." + nodeName + ".url", formatUrl(filtered));
        }
        return properties;
    }

    private String formatUrl(Properties properties) {
        String url = urlTemplate;
        String dataPrefix = getPrefix();
        if (properties.containsKey(dataPrefix + ".host")) {
            url = url.replace("${host}", properties.getProperty(dataPrefix + ".host"));
            url = url.replace("#{host}", properties.getProperty(dataPrefix + ".host"));
            url = url.replace("#host#", properties.getProperty(dataPrefix + ".host"));
        }
        if (properties.containsKey(dataPrefix + ".port")) {
            url = url.replace("${port}", properties.getProperty(dataPrefix + ".port"));
            url = url.replace("#{port}", properties.getProperty(dataPrefix + ".port"));
            url = url.replace("#port#", properties.getProperty(dataPrefix + ".port"));
        }
        if (properties.containsKey(dataPrefix + ".database")) {
            url = url.replace("${database}", properties.getProperty(dataPrefix + ".database"));
            url = url.replace("#{database}", properties.getProperty(dataPrefix + ".database"));
            url = url.replace("#database#", properties.getProperty(dataPrefix + ".database"));
        }
        return url;
    }

    private String getNodeName(ChildData data) {
        String eventZkPath = data.getPath();
        if (eventZkPath.startsWith(path + "/")) {
            return eventZkPath.substring(path.length() + 1);
        } else {
            return eventZkPath;
        }
    }

    public void setClient(CuratorFramework client) {
        if (client != null) {
            this.client = client;
            privateZkClient = false;
        }
    }

    public CuratorFramework getClient() {
        return client;
    }

    public String getZkConnectString() {
        return zkConnectString;
    }

    public void setZkConnectString(String zkConnectString) {
        this.zkConnectString = zkConnectString;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }
}
