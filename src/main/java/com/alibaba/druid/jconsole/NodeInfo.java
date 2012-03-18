package com.alibaba.druid.jconsole;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;

public class NodeInfo {

    private MBeanServerConnection connection;
    private ObjectInstance        objectInstance;
    private NodeType              type;
    private Object                data;
    private String                name;

    public NodeInfo(MBeanServerConnection connection, ObjectInstance objectInstance, NodeType type, Object data,
                    String name){
        this.connection = connection;
        this.objectInstance = objectInstance;
        this.type = type;
        this.data = data;
        this.name = name;
    }

    public ObjectInstance getObjectInstance() {
        return objectInstance;
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
