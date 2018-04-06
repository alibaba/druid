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

import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 * 自定义duird监控数据表格模板类
 * 
 * @author yunnysunny[yunnysunny@gmail.com]
 */
public class DruidTableModel implements TableModel {

    /** 数据内容. */
    private ArrayList<LinkedHashMap<String, Object>> list;

    /** 自定义列名集合. */
    private ArrayList<String>                        showKeys;

    public DruidTableModel(ArrayList<LinkedHashMap<String, Object>> list){
        super();
        this.list = list;
        showKeys = null;
    }

    public DruidTableModel(ArrayList<LinkedHashMap<String, Object>> list, ArrayList<String> showKeys){
        super();
        this.list = list;
        this.showKeys = showKeys;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    /*
     * 如果设置了列名，就是列名的长度；否则返回数据的第一条LinkedHashmap的长度
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        int colCount = 0;
        if (showKeys != null) {
            colCount = showKeys.size();
        } else if (list != null) {
            int listLen = list.size();
            if (listLen > 0) {
                colCount = list.get(0).size();
            }
        }
        return colCount;
    }

    /*
     * 如果设置了自定义列名，则使用自定义列名，并且在返回前拆除掉'-'前面的内容；
     * 如果没有设置，则返回第一条内容的LinkedHashMap的键名。
     * @see javax.swing.table.TableModel#getColumnName(int)
     */
    @Override
    public String getColumnName(int columnIndex) {
        if (showKeys != null && showKeys.size() > 0) {
            String keyNow = showKeys.get(columnIndex);
            if (keyNow != null) {
                return keyNow.substring(keyNow.indexOf('-') + 1, keyNow.length());
            }
        }
        if (list != null && list.size() > 0) {
            LinkedHashMap<String, Object> firstElement = list.get(0);
            Object[] keys = firstElement.keySet().toArray();
            return keys[columnIndex].toString();
        }
        return null;
    }

    @Override
    public int getRowCount() {
        return list.size();
    }

    /*
     * 如果设置了自定义列名，则返回当前列数对应的列名在LinkedHashMap中对应的值；
     * 否则，返回当前LinkedHashMap在当前列数位置对应的值。
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (list != null && rowIndex < list.size()) {// 没有超出最大行数
            LinkedHashMap<String, Object> dataNow = list.get(rowIndex);
            if (showKeys != null) {
                int titleLen = showKeys.size();
                if (titleLen > 0 && columnIndex < titleLen) {
                    return dataNow.get(showKeys.get(columnIndex));
                }
            } else {
                Object[] values = dataNow.values().toArray();
                if (columnIndex < values.length) {
                    return values[columnIndex];
                }
            }
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

}
