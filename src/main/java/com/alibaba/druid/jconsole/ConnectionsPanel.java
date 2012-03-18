package com.alibaba.druid.jconsole;

import java.awt.BorderLayout;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;

public class ConnectionsPanel extends JPanel {

    private final static Log      LOG              = LogFactory.getLog(ConnectionsPanel.class);

    private static final long     serialVersionUID = 1L;

    private MBeanServerConnection connection;
    private ObjectInstance        objectInstance;
    private DataSourceInfo        dataSourceInfo;

    private JTable                table;

    private String[]              columns          = new String[] { "id", "connectTime", "connectTimespan",
            "establishTime", "aliveTimespan", "lastSql", "lastError", "lastErrorTime", "connectStatckTrace",
            "lastStatementStackTrace", "dataSource" };

    public ConnectionsPanel(MBeanServerConnection connection, ObjectInstance objectInstance,
                            DataSourceInfo dataSourceInfo){
        super();
        this.connection = connection;
        this.objectInstance = objectInstance;
        this.dataSourceInfo = dataSourceInfo;

        try {
            TabularData connectionTabularData = (TabularData) connection.getAttribute(objectInstance.getObjectName(),
                                                                                      "ConnectionList");

            Object[][] rows = new Object[connectionTabularData.size()][];
            int rowIndex = 0;
            for (Object item : connectionTabularData.values()) {
                CompositeData rowData = (CompositeData) item;
                Object[] row = new Object[columns.length];
                row[0] = rowData.get("id");
                row[1] = rowData.get("connectTime");
                row[2] = rowData.get("connectTimespan");
                row[3] = rowData.get("establishTime");
                row[4] = rowData.get("aliveTimespan");

                row[5] = rowData.get("lastSql");
                row[6] = rowData.get("lastError");
                row[7] = rowData.get("lastErrorTime");
                row[8] = rowData.get("connectStatckTrace");
                row[9] = rowData.get("lastStatementStackTrace");

                row[10] = rowData.get("dataSource");

                rows[rowIndex++] = row;
            }

            table = new JTable(rows, columns);

            JScrollPane tableScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                          JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            this.setLayout(new BorderLayout());
            this.add(tableScrollPane, BorderLayout.CENTER);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public MBeanServerConnection getConnection() {
        return connection;
    }

    public ObjectInstance getObjectInstance() {
        return objectInstance;
    }

    public DataSourceInfo getDataSourceInfo() {
        return dataSourceInfo;
    }

}
