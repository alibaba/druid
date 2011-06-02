package com.alibaba.druid.jconsole;

import java.awt.BorderLayout;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class SQLPanel extends JPanel {

    private static final long     serialVersionUID = 1L;

    private MBeanServerConnection connection;
    private ObjectInstance        objectInstance;
    private DataSourceInfo        dataSourceInfo;

    private JTable                table;

    private String[]              columns          = { "ID", "DataSource", "SQL", "ExecuteCount", "ErrorCount", 
                                                       
                                                       "TotalTime", "LastTime", "MaxTimespan", "LastError", "EffectedRowCount", 
            
            "FetchRowCount", "MaxTimespanOccurTime", "BatchSizeMax", "BatchSizeTotal", "ConcurrentMax", 
            
            "RunningCount", "Name", "File", "LastErrorMessage", "LastErrorClass", 
            
            "LastErrorStackTrace", "LastErrorTime", "DbType" };

    public SQLPanel(MBeanServerConnection connection, ObjectInstance objectInstance, DataSourceInfo dataSourceInfo){
        super();
        this.connection = connection;
        this.objectInstance = objectInstance;
        this.dataSourceInfo = dataSourceInfo;

        try {
            TabularData connectionTabularData = (TabularData) connection.getAttribute(objectInstance.getObjectName(), "SqlList");

            Object[][] rows = new Object[connectionTabularData.size()][];
            int rowIndex = 0;
            for (Object item : connectionTabularData.values()) {
                CompositeData rowData = (CompositeData) item;
                Object[] row = new Object[columns.length];
                int columnIndex = 0;
                row[columnIndex++] = rowData.get("ID");
                row[columnIndex++] = rowData.get("DataSource");
                row[columnIndex++] = rowData.get("SQL");
                row[columnIndex++] = rowData.get("ExecuteCount");
                row[columnIndex++] = rowData.get("ErrorCount");

                row[columnIndex++] = rowData.get("TotalTime");
                row[columnIndex++] = rowData.get("LastTime");
                row[columnIndex++] = rowData.get("MaxTimespan");
                row[columnIndex++] = rowData.get("LastError");
                row[columnIndex++] = rowData.get("EffectedRowCount");

                row[columnIndex++] = rowData.get("FetchRowCount");
                row[columnIndex++] = rowData.get("MaxTimespanOccurTime");
                row[columnIndex++] = rowData.get("BatchSizeMax");
                row[columnIndex++] = rowData.get("BatchSizeTotal");
                row[columnIndex++] = rowData.get("ConcurrentMax");
               
                
                row[columnIndex++] = rowData.get("RunningCount");
                row[columnIndex++] = rowData.get("Name");
                row[columnIndex++] = rowData.get("File");
                row[columnIndex++] = rowData.get("LastErrorMessage");
                row[columnIndex++] = rowData.get("LastErrorClass");
                
                row[columnIndex++] = rowData.get("LastErrorStackTrace");
                row[columnIndex++] = rowData.get("LastErrorTime");
                row[columnIndex++] = rowData.get("DbType");

                rows[rowIndex++] = row;
            }

            table = new JTable(rows, columns);

            JScrollPane tableScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            this.setLayout(new BorderLayout());
            this.add(tableScrollPane, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
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
