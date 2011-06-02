package com.alibaba.druid.jconsole;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.swing.JPanel;

public class ConnectionsPanel extends JPanel {

    private static final long     serialVersionUID = 1L;

    private MBeanServerConnection connection;
    private ObjectName            objectName;

    public ConnectionsPanel(MBeanServerConnection connection, ObjectName objectName){
        super();
        this.connection = connection;
        this.objectName = objectName;
    }

    public MBeanServerConnection getConnection() {
        return connection;
    }

    public ObjectName getObjectName() {
        return objectName;
    }

}
