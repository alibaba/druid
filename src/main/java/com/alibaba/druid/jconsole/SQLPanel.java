package com.alibaba.druid.jconsole;

import java.awt.BorderLayout;
import java.awt.Component;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class SQLPanel extends JPanel {

    private static final long     serialVersionUID = 1L;

    private MBeanServerConnection connection;
    private ObjectInstance        objectInstance;
    private DataSourceInfo        dataSourceInfo;

    private JTable                table;

    private String[]              columnNames      = { "ID", "File", "Name", "SQL", "ExecuteCount",

                                                   "ErrorCount", "TotalTime", "LastTime", "MaxTimespan", "LastError",

                                                   "EffectedRowCount", "FetchRowCount", "MaxTimespanOccurTime", "BatchSizeMax", "BatchSizeTotal",

                                                   "ConcurrentMax", "RunningCount", "LastErrorMessage", "LastErrorClass", "LastErrorStackTrace",

                                                   "LastErrorTime" };

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

                Object[] row = new Object[columnNames.length];
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
            TableColumnModel columnModel = new TableColumnModel();

            table = new JTable(new SQLTableModel(rows), columnModel);

            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

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

    public class IDCellRender extends DefaultTableCellRenderer implements TableCellRenderer {

        private static final long serialVersionUID = 1L;

        public IDCellRender(){
            this.addMouseListener(new MouseAdapter() {

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }
            });
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.setText(value.toString());
            return this;
        }

    }

    class SQLTableModel extends AbstractTableModel {

        private static final long serialVersionUID = 1L;

        private Object[][]        rowData;

        public SQLTableModel(Object[][] rows){
            this.rowData = rows;
        }

        public String getColumnName(int column) {
            return columnNames[column].toString();
        }

        public int getRowCount() {
            return rowData.length;
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public Object getValueAt(int row, int col) {
            return rowData[row][col];
        }

        public boolean isCellEditable(int row, int column) {
            if (column == 0) {
                return false;
            }

            return true;
        }

        public void setValueAt(Object value, int row, int col) {
            rowData[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }

    class TableColumnModel extends DefaultTableColumnModel {

        private static final long serialVersionUID = 1L;

        public TableColumnModel(){
            for (int i = 0; i < columnNames.length; ++i) {
                TableColumn column = new TableColumn();
                column.setModelIndex(i);
                column.setHeaderValue(columnNames[i]);
                this.addColumn(column);
            }

            {
                TableColumn column = getColumn(0);
                column.setCellRenderer(new IDCellRender());
            }
        }
    }
}
