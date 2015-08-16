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
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class ColumnGroup {

    /** 合并的JTableHeader的Renderer */
    protected TableCellRenderer renderer = null;
    /** 合并的单元格的各个实际的最小单元格存储结构 */
    protected Vector<Object>    vector   = null;
    /** 合并后单元格显示的文本信息 */
    protected String            text     = null;
    /** 合并的单元格内部两个最小JTableHeader的间隙,其实就是去掉线后那个Border */
    private int                 margin   = 0;

    public ColumnGroup(String text){
        this(null, text);
    }

    public ColumnGroup(TableCellRenderer renderer, String text){
        if (renderer == null) {
            this.renderer = new DefaultTableCellRenderer() {

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
                    this.setText((value == null) ? "" : value.toString());
                    setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                    return this;
                }
            };
        } else {
            this.renderer = renderer;
        }
        this.text = text;
        vector = new Vector<Object>();
    }

    /**
     * 增加单元格
     * 
     * @param obj
     */
    public void add(Object obj) {
        if (obj == null) return;
        vector.addElement(obj);
    }

    /**
     * 根据JTable的某一列取得它的所有的包含列,
     * 
     * @param column
     * @param group
     * @return
     */
    public Vector<ColumnGroup> getColumnGroups(TableColumn column, Vector<ColumnGroup> group) {
        // 通过递归判断列到底属于那个ColumnGroup
        group.addElement(this);
        if (vector.contains(column)) return group;
        Enumeration<Object> enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj instanceof ColumnGroup) {
                @SuppressWarnings("unchecked")
                Vector<ColumnGroup> groups = ((ColumnGroup) obj).getColumnGroups(column,
                                                                                 (Vector<ColumnGroup>) group.clone());
                if (groups != null) {
                    return groups;
                }
            }
        }
        return null;
    }

    public TableCellRenderer getHeaderRenderer() {
        return renderer;
    }

    public Object getHeaderValue() {
        return text;
    }

    /**
     * 取得合并后的单元格的大小
     * 
     * @return
     */
    public int getSize() {
        return vector == null ? 0 : vector.size();
    }

    /**
     * 取得合并后的单元格的大小,这个方法需要计算,首先 是取得一个没有合并的最小单元格的JTableHeader 的大小,<br/>
     * 通过Renderer取得组件
     * 
     * @return
     */
    public Dimension getSize(JTable table) {
        Component comp = renderer.getTableCellRendererComponent(table, getHeaderValue(), false, false, -1, -1);
        int height = comp.getPreferredSize().height;
        int width = 0;
        // 宽度需要计算合并的还要加上间隙
        Enumeration<Object> enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj instanceof TableColumn) {
                TableColumn aColumn = (TableColumn) obj;
                width += aColumn.getWidth();
                width += margin;
            } else {
                width += ((ColumnGroup) obj).getSize(table).width;
            }
        }
        return new Dimension(width, height);
    }

    public java.lang.String getText() {
        return text;
    }

    public boolean removeColumn(ColumnGroup ptg, TableColumn tc) {
        boolean retFlag = false;
        if (tc != null) {
            for (int i = 0; i < ptg.vector.size(); i++) {
                Object tmpObj = ptg.vector.get(i);
                if (tmpObj instanceof ColumnGroup) {
                    retFlag = removeColumn((ColumnGroup) tmpObj, tc);
                    if (retFlag) {
                        break;
                    }
                } else if (tmpObj instanceof TableColumn) {
                    if (tmpObj == tc) {
                        ptg.vector.remove(i);
                        retFlag = true;
                        break;
                    }
                }
            }
        }
        return retFlag;
    }

    public boolean removeColumnGrp(ColumnGroup ptg, ColumnGroup tg) {
        boolean retFlag = false;
        if (tg != null) {
            for (int i = 0; i < ptg.vector.size(); i++) {
                Object tmpObj = ptg.vector.get(i);
                if (tmpObj instanceof ColumnGroup) {
                    if (tmpObj == tg) {
                        ptg.vector.remove(i);
                        retFlag = true;
                        break;
                    } else {
                        retFlag = removeColumnGrp((ColumnGroup) tmpObj, tg);
                        if (retFlag) {
                            break;
                        }

                    }
                } else if (tmpObj instanceof TableColumn) {
                    break;
                }
            }
        }
        return retFlag;
    }

    public void setColumnMargin(int margin) {
        this.margin = margin;
        Enumeration<Object> enumeration = vector.elements();
        while (enumeration.hasMoreElements()) {
            Object obj = enumeration.nextElement();
            if (obj instanceof ColumnGroup) {
                ((ColumnGroup) obj).setColumnMargin(margin);
            }
        }
    }

    public void setHeaderRenderer(TableCellRenderer renderer) {
        if (renderer != null) {
            this.renderer = renderer;
        }
    }

    public void setText(java.lang.String newText) {
        text = newText;
    }
}
