package com.alibaba.druid.jconsole;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
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
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

public class SQLPanel extends JPanel {

    private static final long     serialVersionUID = 1L;

    private MBeanServerConnection connection;
    private ObjectInstance        objectInstance;
    private DataSourceInfo        dataSourceInfo;

    private JTable                table;
    private SQLTableModel         tableModel;
    private SQLTableRowSorter     sorter;

    private String[]              columnNames      = { "ID", "File", "Name", "SQL", "ExecCount",

                                                   "TotalTime", "EffectedRows", "FetchRowCount", "Running", "ConcurrentMax", 

                                                   "MaxTimespan", "LastTime", "LastError","ErrorCount",  };

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
                row[columnIndex++] = rowData;
                row[columnIndex++] = rowData.get("File");
                row[columnIndex++] = rowData.get("Name");
                row[columnIndex++] = rowData.get("SQL");
                row[columnIndex++] = rowData.get("ExecuteCount");
                
                row[columnIndex++] = rowData.get("TotalTime");
                row[columnIndex++] = rowData.get("EffectedRowCount");
                row[columnIndex++] = rowData.get("FetchRowCount");
                row[columnIndex++] = rowData.get("RunningCount");
                row[columnIndex++] = rowData.get("ConcurrentMax");
                
                row[columnIndex++] = rowData.get("MaxTimespan");
                row[columnIndex++] = rowData.get("LastTime");
                row[columnIndex++] = rowData.get("LastError");
                row[columnIndex++] = rowData.get("ErrorCount");

                rowList.add(row);
            }

            Object[][] rows = new Object[rowList.size()][];
            rowList.toArray(rows);
            TableColumnModel columnModel = new TableColumnModel();

            tableModel = new SQLTableModel(rows);
            sorter = new SQLTableRowSorter(tableModel);
            table = new JTable(tableModel, columnModel);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
            table.setRowSorter(sorter);
            table.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    tableMouseClicked(e);
                }
            });

            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            JScrollPane tableScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

            this.setLayout(new BorderLayout());
            this.add(tableScrollPane, BorderLayout.CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void refresh() {
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
                row[columnIndex++] = rowData;
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
                row[columnIndex++] = rowData.get("ConcurrentMax");
                row[columnIndex++] = rowData.get("RunningCount");

                rowList.add(row);
            }

            Object[][] rows = new Object[rowList.size()][];
            rowList.toArray(rows);

            int rowCount = tableModel.getRowCount();
            tableModel.setRowData(rows);
            tableModel.fireTableRowsDeleted(0, rowCount - 1);

            tableModel.setRowData(rows);
            tableModel.fireTableRowsInserted(0, rows.length);
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

        CompositeData rowData = (CompositeData) table.getValueAt(rowIndex, 0);

        SQLDetailDialog dialog = new SQLDetailDialog(rowData);

        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int width = (int) screenSize.getWidth();
        int height = (int) screenSize.getHeight();
        dialog.setSize(800, 600);
        int w = dialog.getWidth();
        int h = dialog.getHeight();
        dialog.setLocation((width - w) / 2, (height - h) / 2);
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

        public void setRowData(Object[][] rowData) {
            this.rowData = rowData;
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
                column.setMinWidth(50);
                column.setMaxWidth(50);
                column.setCellRenderer(new IDRender());
            }

            {
                TableColumn column = getColumn(3);
                column.setPreferredWidth(400);
                column.setCellRenderer(new SQLRenderer());
            }

            {
                TableColumn column = getColumn(4);
                column.setPreferredWidth(60);
            }

            {
                TableColumn column = getColumn(11);
                column.setCellRenderer(new DateRenderer());
                column.setPreferredWidth(120);
            }
        }

    }

    static class SQLRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        public SQLRenderer(){
            super();
        }

        public void setValue(Object value) {
            String sql = (String) value;
            String formattedSql = SQLDetailDialog.format(sql);
            setText(sql);
            setToolTipText("<html><pre>" + formattedSql + "</pre></html>");
        }
    }

    static class DateRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;
        DateFormat                formatter;

        public DateRenderer(){
            super();
        }

        public void setValue(Object value) {
            if (formatter == null) {
                formatter = new SimpleDateFormat("MM-dd HH:mm:ss");
            }
            setText((value == null) ? "" : formatter.format(value));
        }
    }

    static class IDRender extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 1L;

        public void setValue(Object value) {
            CompositeData rowData = (CompositeData) value;
            Object id = rowData.get("ID");
            setText((id == null) ? "" : id.toString());
        }
    }

    static class SQLTableRowSorter extends TableRowSorter<SQLTableModel> {

        public SQLTableRowSorter(SQLTableModel model){
            super(model);
        }

        public Comparator<?> getComparator(int column) {
            switch (column) {
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 13:
                    return new LongComparator();
                default:
                    return super.getComparator(column);
            }

        }
    }

    static class LongComparator implements Comparator<Object> {

        @Override
        public int compare(Object a, Object b) {
            long aValue = Long.parseLong(a.toString());
            long bValue = Long.parseLong(b.toString());
            
            if (aValue == bValue) {
                return 0;
            }
            return aValue > bValue ? 1 : -1;
        }
    }
}
