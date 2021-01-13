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

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.nodes.GroupMember;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A register which is used to register a node to an ephemeral node.
 *
 * @author DigitalSonic
 */
public class ZookeeperNodeRegister {
    private final static Log LOG = LogFactory.getLog(ZookeeperNodeRegister.class);
    private String zkConnectString;
    private String path = "/ha-druid-datasources";
    private CuratorFramework client;
    private GroupMember member;
    private boolean privateZkClient = false; // Should I close the client?
    private Lock lock = new ReentrantLock();

    /**
     * Init a CuratorFramework if there's no CuratorFramework provided.
     */
    public void init() {
        if (client == null) {
            client = CuratorFrameworkFactory.builder()
                    .connectionTimeoutMs(3000)
                    .connectString(zkConnectString)
                    .retryPolicy(new ExponentialBackoffRetry(5000, 3, 30000))
                    .sessionTimeoutMs(30000)
                    .build();
            client.start();
            privateZkClient = true;
        }
    }

    /**
     * Register a Node which has a Properties as the payload.
     * <pre>
     * CAUTION: only one node can be registered,
     *          if you want to register another one,
     *          call deregister first
     * </pre>
     *
     * @param payload The information used to generate the payload Properties
     * @return true, register successfully; false, skip the registeration
     */
    public boolean register(String nodeId, List<ZookeeperNodeInfo> payload) {
        if (payload == null || payload.isEmpty()) {
            return false;
        }
        try {
            lock.lock();
            createPathIfNotExisted();
            if (member != null) {
                LOG.warn("GroupMember has already registered. Please deregister first.");
                return false;
            }
            String payloadString = getPropertiesString(payload);
            member = new GroupMember(client, path, nodeId, payloadString.getBytes());
            member.start();
            LOG.info("Register Node["+ nodeId + "] in path[" + path + "].");
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Close the current GroupMember.
     */
    public void deregister() {
        if (member != null) {
            member.close();
            member = null;
        }
        if (client != null && privateZkClient) {
            client.close();
        }
    }

    /**
     * @see #deregister()
     */
    public void destroy() {
        deregister();
    }

    private void createPathIfNotExisted() {
        try {
            if (client.checkExists().forPath(path) == null) {
                LOG.info("Path[" + path + "] is NOT existed, create it.");
                client.create().creatingParentsIfNeeded().forPath(path);
            }
        } catch(Exception e) {
            LOG.error("Can NOT check the path.", e);
        }
    }

    private String getPropertiesString(List<ZookeeperNodeInfo> payload) {
        Properties properties = new Properties();
        for (ZookeeperNodeInfo n : payload) {
            if (n.getHost() != null) {
                properties.setProperty(n.getPrefix() + "host", n.getHost());
            }
            if (n.getPort() != null) {
                properties.setProperty(n.getPrefix() + "port", n.getPort().toString());
            }
            if (n.getDatabase() != null) {
                properties.setProperty(n.getPrefix() + "database", n.getDatabase());
            }
            if (n.getUsername() != null) {
                properties.setProperty(n.getPrefix() + "username", n.getUsername());
            }
            if (n.getPassword() != null) {
                properties.setProperty(n.getPrefix() + "password", n.getPassword());
            }
        }
        StringWriter sw = new StringWriter();
        try {
            properties.store(sw, "");
        } catch (IOException e) {
            LOG.error("Why Properties.store goes wrong?", e);
        }
        return sw.toString();
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
}
