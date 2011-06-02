package com.alibaba.druid.jconsole;

import javax.management.MBeanServerConnection;

public class NodeInfo {

    private MBeanServerConnection connection;
    private NodeType              type;
    private Object                data;
    private String                name;

    public NodeInfo(MBeanServerConnection connection, NodeType type, Object data, String name){
        this.connection = connection;
        this.type = type;
        this.data = data;
        this.name = name;
    }

    public MBeanServerConnection getConnection() {
        return connection;
    }

    public NodeType getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
