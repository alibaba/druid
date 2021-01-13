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

/**
 * The information that will be added to an ephemeral node.
 *
 * @author DigitalSonic
 */
public class ZookeeperNodeInfo {
    private String prefix = "";
    private String host;
    private Integer port;
    /**
     * Database can be the following ones:
     * 1. Database in MySQL and PostgreSQL JDBC URL
     * 2. ServiceName or SID in Oracle JDBC URL
     * etc.
     */
    private String database;
    private String username;
    private String password;

    public void setPrefix(String prefix) {
        if (prefix != null && !prefix.trim().isEmpty()) {
            this.prefix = prefix;
            if (!prefix.endsWith(".")) {
                this.prefix = prefix + ".";
            }
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
