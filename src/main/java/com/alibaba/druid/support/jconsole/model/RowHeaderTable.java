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

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.util.ArrayList;

/**
 * 用于显示RowHeader的JTable，只需要将其加入JScrollPane的RowHeaderView即可为JTable生成行标题
 */
public class RowHeaderTable extends JTable {
    private static final long serialVersionUID = 1L;

    /**
     * 为JTable添加RowHeader，
     *
     * @param refTable    需要添加rowHeader的JTable
     * @param columnWidth rowHeader的宽度
     */
    public RowHeaderTable(JTable refTable, int columnWidth) {
        this(null, refTable, columnWidth, 1);
    }

    public RowHeaderTable(ArrayList<String> title, JTable refTable, int columnWidth) {
        this(title, refTable, columnWidth, 1);
    }

    public RowHeaderTable(ArrayList<String> title, JTable refTable, int columnWidth, int rowSpan) {
        super(new DefaultTableModel(refTable.getRowCount() / rowSpan, 1));
        this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // 不可以调整列宽
        this.getColumnModel().getColumn(0).setPreferredWidth(columnWidth);
        this.setDefaultRenderer(Object.class, new RowHeaderRenderer(title, refTable, this, rowSpan)); // 设置渲染器
        this.setPreferredScrollableViewportSize(new Dimension(columnWidth, 0));
    }
}
