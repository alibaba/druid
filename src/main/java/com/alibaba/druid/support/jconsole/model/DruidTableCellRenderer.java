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

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 * @author yunnysunny[yunnysunny@gmail.com]
 * */
public class DruidTableCellRenderer extends JLabel implements TableCellRenderer {

    private static final long serialVersionUID = 1L;

    // This method is called each time a cell in a column
    // using this renderer needs to be rendered.
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                                                   int row, int column) {

        if ((column == 0)) {
            setBackground(Color.lightGray);
        }
        this.setOpaque(true);

        if (value != null) {
            setText(value.toString());
        } else {
            setText((row + 1) + "");
        }

        // Since the renderer is a component, return itself
        return this;
    }
}
