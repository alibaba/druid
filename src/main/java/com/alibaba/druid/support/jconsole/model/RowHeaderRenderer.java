package com.alibaba.druid.support.jconsole.model;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.util.ArrayList;

/**
 * 用于显示RowHeader的JTable的渲染器，当选择某行时，该行颜色会发生变化
 */
final class RowHeaderRenderer extends JLabel implements TableCellRenderer, ListSelectionListener {
    private static final long serialVersionUID = 1L;
    private JTable refTable;              // 需要添加rowHeader的JTable
    private JTable tableShow;             // 用于显示rowHeader的JTable
    private ArrayList<String> headerList;
    private int rowHeightNow;
    private int rowSpan;

    public RowHeaderRenderer(JTable refTable, JTable tableShow) {
        this(null, refTable, tableShow, 0);
    }

    public RowHeaderRenderer(ArrayList<String> headerList, JTable refTable, JTable tableShow, int rowSpan) {
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
        setBorder(UIManager.getBorder("TableHeader.cellBorder")); // 设置为TableHeader的边框类型
        setHorizontalAlignment(CENTER); // 让text居中显示
        setBackground(header.getBackground()); // 设置背景色为TableHeader的背景色
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
