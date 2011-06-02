package com.alibaba.druid.jconsole;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class SQLPanel extends JPanel {

    private static final long     serialVersionUID = 1L;

    private MBeanServerConnection connection;
    private ObjectInstance        objectInstance;
    private DataSourceInfo        dataSourceInfo;

    private JTable                table;

    private String[]              columns          = { "ID", "File", "Name", "SQL", "ExecuteCount", 
                                                       
                                                       "ErrorCount", "TotalTime", "LastTime", "MaxTimespan", "LastError", 
                                                       
                                                       "EffectedRowCount", "FetchRowCount", "MaxTimespanOccurTime", "BatchSizeMax", "BatchSizeTotal", 
                                                       
                                                       "ConcurrentMax", "RunningCount", "LastErrorMessage", "LastErrorClass", "LastErrorStackTrace", 
                                                       
                                                       "LastErrorTime"};

    public SQLPanel(MBeanServerConnection connection, ObjectInstance objectInstance, DataSourceInfo dataSourceInfo){
        super();
        this.connection = connection;
        this.objectInstance = objectInstance;
        this.dataSourceInfo = dataSourceInfo;

        try {
            TabularData connectionTabularData = (TabularData) connection.getAttribute(objectInstance.getObjectName(), "SqlList");

            List<Object[]> rowList = new ArrayList<Object[]>();

            for (Object item : connectionTabularData.values()) {
                CompositeData rowData = (CompositeData) item;

                String url = (String) rowData.get("DataSource");

                if (!dataSourceInfo.getUrl().equals(url)) {
                    continue;
                }

                Object[] row = new Object[columns.length];
                int columnIndex = 0;
                row[columnIndex++] = rowData.get("ID");
                row[columnIndex++] = rowData.get("File");
                row[columnIndex++] = rowData.get("Name");
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
                row[columnIndex++] = rowData.get("LastErrorMessage");
                row[columnIndex++] = rowData.get("LastErrorClass");
                row[columnIndex++] = rowData.get("LastErrorStackTrace");

                row[columnIndex++] = rowData.get("LastErrorTime");

                rowList.add(row);
            }

            Object[][] rows = new Object[rowList.size()][];
            rowList.toArray(rows);
            table = new JTable(rows, columns);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    tableMouseClicked(e);
                }
            });

            JScrollPane tableScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            this.setLayout(new BorderLayout());
            this.add(tableScrollPane, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void tableMouseClicked(MouseEvent e) {
        if (e.getClickCount() < 2) {
            return;
        }

        int rowIndex = table.getSelectedRow();
        if (rowIndex < 0) {
            return;
        }

        Object[] row = new Object[columns.length];
        for (int i = 0; i < row.length; ++i) {
            row[i] = table.getModel().getValueAt(rowIndex, i);
        }

        SQLDetailDialog dialog = new SQLDetailDialog(row);
        dialog.setVisible(true);
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
