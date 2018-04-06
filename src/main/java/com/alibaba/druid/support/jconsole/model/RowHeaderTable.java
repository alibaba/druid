/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.support.jconsole.model;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;

/**
 * 用于显示RowHeader的JTable，只需要将其加入JScrollPane的RowHeaderView即可为JTable生成行标题
 */
public class RowHeaderTable extends JTable {

    private static final long serialVersionUID = 1L;

    /**
     * 为JTable添加RowHeader，
     * 
     * @param refTable 需要添加rowHeader的JTable
     * @param columnWidth rowHeader的宽度
     */
    public RowHeaderTable(JTable refTable, int columnWidth){
        this(null, refTable, columnWidth, 1);
    }

    public RowHeaderTable(ArrayList<String> title, JTable refTable, int columnWidth){
        this(title, refTable, columnWidth, 1);
    }

    public RowHeaderTable(ArrayList<String> title, JTable refTable, int columnWidth, int rowSpan){
        super(new DefaultTableModel(refTable.getRowCount() / rowSpan, 1));
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);// 不可以调整列宽
        this.getColumnModel().getColumn(0).setPreferredWidth(columnWidth);
        this.setDefaultRenderer(Object.class, new RowHeaderRenderer(title, refTable, this, rowSpan));// 设置渲染器
        this.setPreferredScrollableViewportSize(new Dimension(columnWidth, 0));
    }
}

/**
 * 用于显示RowHeader的JTable的渲染器，当选择某行时，该行颜色会发生变化
 */
final class RowHeaderRenderer extends JLabel implements TableCellRenderer, ListSelectionListener {

    private static final long serialVersionUID = 1L;
    private JTable            refTable;              // 需要添加rowHeader的JTable
    private JTable            tableShow;             // 用于显示rowHeader的JTable
    private ArrayList<String> headerList;
    private int               rowHeightNow;
    private int               rowSpan;

    public RowHeaderRenderer(JTable refTable, JTable tableShow){
        this(null, refTable, tableShow, 0);
    }

    public RowHeaderRenderer(ArrayList<String> headerList, JTable refTable, JTable tableShow, int rowSpan){
        this.headerList = headerList;
        this.refTable = refTable;
        this.tableShow = tableShow;
        // 增加监听器，实现当在refTable中选择行时，RowHeader会发生颜色变化
        ListSelectionModel listModel = refTable.getSelectionModel();
        listModel.addListSelectionListener(this);
        rowHeightNow = refTable.getRowCount() * refTable.getRowHeight();
        this.rowSpan = rowSpan;
        if (rowSpan > 1) {
            rowHeightNow = rowSpan * refTable.getRowHeight();
        }
    }

    public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus,
                                                   int row, int col) {
        int rowCountNow = refTable.getRowCount() / rowSpan;
        ((DefaultTableModel) table.getModel()).setRowCount(rowCountNow);
        JTableHeader header = refTable.getTableHeader();
        this.setOpaque(true);
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));// 设置为TableHeader的边框类型
        setHorizontalAlignment(CENTER);// 让text居中显示
        setBackground(header.getBackground());// 设置背景色为TableHeader的背景色
        if (isSelect(row)) // 当选取单元格时,在row header上设置成选取颜色
        {
            setForeground(Color.white);
            setBackground(Color.lightGray);
        } else {
            setForeground(header.getForeground());
        }
        setFont(header.getFont());
        if (row <= rowCountNow) {
            showCol(row);
        }
        return this;
    }

    private void showCol(int row) {

        String text = null;
        if (headerList != null && row < headerList.size()) {
            text = headerList.get(row);
        } else {
            text = String.valueOf(row + 1);
        }
        if (rowSpan > 1) {
            setText(text);
            this.tableShow.setRowHeight(row, rowHeightNow);
        } else {
            setText(text);
        }

    }

    public void valueChanged(ListSelectionEvent e) {
        this.tableShow.repaint();
    }

    private boolean isSelect(int row) {
        int[] sel = refTable.getSelectedRows();
        if (rowSpan <= 1) {
            for (int item : sel) {
                if (item == row) {
                    return true;
                }
            }
        } else {
            for (int item : sel) {
                if (item / rowSpan == row) {
                    return true;
                }
            }
        }

        return false;
    }
}
