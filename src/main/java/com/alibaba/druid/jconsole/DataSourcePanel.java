package com.alibaba.druid.jconsole;

import java.awt.BorderLayout;
import java.awt.Panel;

import javax.swing.JScrollPane;
import javax.swing.JTable;

public class DataSourcePanel extends Panel {

    private static final long serialVersionUID = 1L;

    private DataSourceInfo    dataSourceInfo;

    private JTable            table;

    private String[]          columns          = new String[] { "Name", "Value" };

    public DataSourcePanel(DataSourceInfo dataSourceInfo){
        this.dataSourceInfo = dataSourceInfo;

        Object[][] rows = new Object[][] { 
                                           { "ID", dataSourceInfo.getId() }, 
                                           { "Name", dataSourceInfo.getName() }, 
                                           { "URL", dataSourceInfo.getUrl() },
                                           { "Filters", dataSourceInfo.getFilters() }, 
                                           { "CreatedTime", dataSourceInfo.getCreatedTime() }, 
                                           { "RawDriverClassName", dataSourceInfo.getRawDriverClassName() }, 
                                           { "RawUrl", dataSourceInfo.getRawUrl() }, 
                                           { "RawDriverMajorVersion", dataSourceInfo.getRawDriverMajorVersion() }, 
                                           { "RawDriverMinorVersion", dataSourceInfo.getRawDriverMinorVersion() }, 
                                           { "Properties", dataSourceInfo.getProperties() }, 
                };
        table = new JTable(rows, columns);

        JScrollPane tableScrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        this.setLayout(new BorderLayout());
        this.add(tableScrollPane, BorderLayout.CENTER);
    }

    public DataSourceInfo getDataSourceInfo() {
        return dataSourceInfo;
    }

    public void setDataSourceInfo(DataSourceInfo dataSourceInfo) {
        this.dataSourceInfo = dataSourceInfo;
    }

}
