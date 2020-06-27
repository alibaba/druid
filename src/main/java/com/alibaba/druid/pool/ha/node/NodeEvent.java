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

import com.alibaba.druid.pool.ha.PropertiesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Define the detail of a node update event.
 *
 * @author DigitalSonic
 */
public class NodeEvent {
    private NodeEventTypeEnum type;
    private String nodeName;
    private String url;
    private String username;
    private String password;

    /**
     * Diff the given two Properties.
     *
     * @return A List of AddEvent and DelEvent
     */
    public static List<NodeEvent> getEventsByDiffProperties(Properties previous, Properties next) {
        List<String> prevNames = PropertiesUtils.loadNameList(previous, "");
        List<String> nextNames = PropertiesUtils.loadNameList(next, "");

        List<String> namesToAdd = new ArrayList<String>();
        List<String> namesToDel = new ArrayList<String>();
        for (String n : prevNames) {
            if (n != null && !n.trim().isEmpty() && !nextNames.contains(n)) {
                namesToDel.add(n);
            }
        }
        for (String n : nextNames) {
            if (n != null && !n.trim().isEmpty() && !prevNames.contains(n)) {
                namesToAdd.add(n);
            }
        }

        List<NodeEvent> list = new ArrayList<NodeEvent>();
        list.addAll(generateEvents(next, namesToAdd, NodeEventTypeEnum.ADD));
        list.addAll(generateEvents(previous, namesToDel, NodeEventTypeEnum.DELETE));

        return list;
    }

    public static List<NodeEvent> generateEvents(Properties properties, List<String> names, NodeEventTypeEnum type) {
        List<NodeEvent> list = new ArrayList<NodeEvent>();
        for (String n : names) {
            NodeEvent event = new NodeEvent();
            event.setType(type);
            event.setNodeName(n);
            event.setUrl(properties.getProperty(n + ".url"));
            event.setUsername(properties.getProperty(n + ".username"));
            event.setPassword(properties.getProperty(n + ".password"));
            list.add(event);
        }
        return list;
    }

    @Override
    public String toString() {
        String str = "NodeEvent{" +
                "type=" + type +
                ", nodeName='" + nodeName + '\'' +
                ", url='" + url + '\'' +
                ", username='" + username + '\'';
        if (password != null) {
            str += ", password.length=" + password.length();
        }
        str += '}';
        return str;
    }

    public NodeEventTypeEnum getType() {
        return type;
    }

    public void setType(NodeEventTypeEnum type) {
        this.type = type;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
