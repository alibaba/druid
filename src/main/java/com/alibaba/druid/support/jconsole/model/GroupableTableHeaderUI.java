/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class GroupableTableHeaderUI extends BasicTableHeaderUI {

    private int m_height;

    private Dimension createHeaderSize(long width) {
        TableColumnModel columnModel = header.getColumnModel();
        width += columnModel.getColumnMargin() * columnModel.getColumnCount();
        if (width > Integer.MAX_VALUE) {
            width = Integer.MAX_VALUE;
        }
        return new Dimension((int) width, getHeaderHeight());
    }

    private int getColCountUnderColGroup(ColumnGroup cg, int iCount) {
        Vector<Object> v = cg.vector;
        for (int i = 0; i < v.size(); i++) {
            Object obj = v.elementAt(i);
            if (obj instanceof ColumnGroup) {
                iCount = getColCountUnderColGroup((ColumnGroup) obj, iCount);
            } else {
                iCount++;
            }
        }
        return iCount;
    }

    public int getHeaderHeight() {
        int height = 0;
        TableColumnModel columnModel = header.getColumnModel();
        for (int column = 0; column < columnModel.getColumnCount(); column++) {
            TableColumn aColumn = columnModel.getColumn(column);
            TableCellRenderer renderer = aColumn.getHeaderRenderer();
            if (renderer == null) {
                renderer = new DefaultTableCellRenderer() {

                    private static final long serialVersionUID = 1L;

                    @Override
                    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                                   boolean hasFocus, int row, int column) {
                        JTableHeader header = table.getTableHeader();
                        if (header != null) {
                            setForeground(header.getForeground());
                            setBackground(header.getBackground());
                            setFont(header.getFont());
                        }
                        setHorizontalAlignment(JLabel.CENTER);
                        setText((value == null) ? "" : value.toString());
                        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                        return this;
                    }
                };
            }
            Component comp = renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false,
                                                                    false, -1, column);
            int cHeight = comp.getPreferredSize().height;
            Enumeration<ColumnGroup> enumeration = ((GroupableTableHeader) header).getColumnGroups(aColumn);
            if (enumeration != null) {
                while (enumeration.hasMoreElements()) {
                    ColumnGroup cGroup = (ColumnGroup) enumeration.nextElement();
                    cHeight += cGroup.getSize(header.getTable()).height;
                }
            }
            height = Math.max(height, cHeight);
        }
        height = Math.max(height, m_height);
        return height;
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        long width = 0;
        Enumeration<TableColumn> enumeration = header.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            TableColumn aColumn = enumeration.nextElement();
            width = width + aColumn.getWidth();
        }

        return createHeaderSize(width);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Rectangle clipBounds = g.getClipBounds();
        if (header.getColumnModel() == null) {
            return;
        }
        ((GroupableTableHeader) header).setColumnMargin();
        int column = 0;
        Dimension size = header.getSize();
        Rectangle cellRect = new Rectangle(0, 0, size.width, size.height);
        Hashtable<ColumnGroup, Rectangle> h = new Hashtable<ColumnGroup, Rectangle>();
        int columnMargin = header.getColumnModel().getColumnMargin();
        Enumeration<TableColumn> enumeration = header.getColumnModel().getColumns();
        while (enumeration.hasMoreElements()) {
            cellRect.height = size.height;
            cellRect.y = 0;
            TableColumn aColumn = enumeration.nextElement();
            Enumeration<ColumnGroup> cGroups = ((GroupableTableHeader) header).getColumnGroups(aColumn);
            if (cGroups != null) {
                int groupHeight = 0;
                while (cGroups.hasMoreElements()) {
                    ColumnGroup cGroup = cGroups.nextElement();
                    Rectangle groupRect = (Rectangle) h.get(cGroup);
                    if (groupRect == null) {
                        groupRect = new Rectangle(cellRect);
                        Dimension d = cGroup.getSize(header.getTable());
                        if (!System.getProperty("java.vm.version").startsWith("1.2")) {
                            int iColCount = getColCountUnderColGroup(cGroup, 0);
                            groupRect.width = d.width - iColCount * columnMargin;
                        } else {
                            groupRect.width = d.width;
                        }
                        groupRect.height = d.height;
                        h.put(cGroup, groupRect);
                    }
                    paintCell(g, groupRect, cGroup);
                    groupHeight += groupRect.height;
                    cellRect.height = size.height - groupHeight;
                    cellRect.y = groupHeight;
                }
            }
            if (!System.getProperty("java.vm.version").startsWith("1.2")) {
                cellRect.width = aColumn.getWidth();
            } else {
                cellRect.width = aColumn.getWidth() + columnMargin;
            }

            if (cellRect.intersects(clipBounds)) {
                paintCell(g, cellRect, column);
            }
            cellRect.x += cellRect.width;
            column++;
        }
    }

    private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
        TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        TableCellRenderer renderer = aColumn.getHeaderRenderer();
        //
        if (renderer == null) {
            renderer = new DefaultTableCellRenderer() {

                private static final long serialVersionUID = 1L;

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    JTableHeader header = table.getTableHeader();
                    if (header != null) {
                        setForeground(header.getForeground());
                        setBackground(header.getBackground());
                        setFont(header.getFont());
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                    setText((value == null) ? "" : value.toString());
                    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                    return this;
                }
            };
        }

        String headerValue = aColumn.getHeaderValue().toString();
        Component component = renderer.getTableCellRendererComponent(header.getTable(), headerValue, false, false, -1,
                                                                     columnIndex);

        rendererPane.add(component);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    private void paintCell(Graphics g, Rectangle cellRect, ColumnGroup cGroup) {
        TableCellRenderer renderer = cGroup.getHeaderRenderer();
        //
        if (renderer == null) {
            renderer = new DefaultTableCellRenderer() {

                private static final long serialVersionUID = 1L;

                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                               boolean hasFocus, int row, int column) {
                    JTableHeader header = table.getTableHeader();
                    if (header != null) {
                        setForeground(header.getForeground());
                        setBackground(header.getBackground());
                        setFont(header.getFont());
                    }
                    setHorizontalAlignment(JLabel.CENTER);
                    setText((value == null) ? "" : value.toString());
                    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                    return this;
                }
            };
        }

        String headerValue = cGroup.getHeaderValue().toString();
        Component component = renderer.getTableCellRendererComponent(header.getTable(), headerValue, false, false, -1,
                                                                     -1);

        rendererPane.add(component);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    public void setHeaderHeight(int iHeight) {
        m_height = iHeight;
    }
}
