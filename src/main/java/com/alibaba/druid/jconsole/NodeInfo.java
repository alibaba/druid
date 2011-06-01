package com.alibaba.druid.jconsole;

import javax.management.MBeanServerConnection;

public class NodeInfo {

    private MBeanServerConnection connection;
    private NodeType              type;

    public NodeInfo(MBeanServerConnection connection, NodeType type){
        this.connection = connection;
        this.type = type;
    }

    public MBeanServerConnection getConnection() {
        return connection;
    }

    public NodeType getType() {
        return type;
    }

}
