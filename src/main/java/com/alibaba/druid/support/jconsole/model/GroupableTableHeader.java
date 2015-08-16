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

import java.util.Enumeration;
import java.util.Vector;

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class GroupableTableHeader extends JTableHeader {

    private static final long     serialVersionUID = 1L;
    // private static final String uiClassID = "GroupableTableHeaderUI";
    protected Vector<ColumnGroup> columnGroups     = null;

    public GroupableTableHeader(TableColumnModel model){
        super(model);
        setUI(new GroupableTableHeaderUI());
        setReorderingAllowed(false);
        setRequestFocusEnabled(false);
    }

    public void addColumnGroup(ColumnGroup g) {
        if (columnGroups == null) {
            columnGroups = new Vector<ColumnGroup>();
        }
        columnGroups.addElement(g);
    }

    public void clearColumnGroups() {
        columnGroups = null;
    }

    public ColumnGroup[] getColumnGroups() {
        ColumnGroup[] retg = null;
        if (columnGroups.size() > 0) {
            retg = new ColumnGroup[columnGroups.size()];
            columnGroups.copyInto(retg);
        }
        return retg;
    }

    public Enumeration<ColumnGroup> getColumnGroups(TableColumn col) {
        if (columnGroups == null) {
            return null;
        }
        Enumeration<ColumnGroup> enum1 = columnGroups.elements();
        while (enum1.hasMoreElements()) {
            ColumnGroup cGroup = (ColumnGroup) enum1.nextElement();
            Vector<ColumnGroup> v_ret = cGroup.getColumnGroups(col, new Vector<ColumnGroup>());
            if (v_ret != null) {
                return v_ret.elements();
            }
        }
        return null;
    }

    public boolean isFocusTraversable() {
        return super.isFocusable() && isRequestFocusEnabled();
    }

    public void setColumnMargin() {
        if (columnGroups == null) {
            return;
        }
        int columnMargin = getColumnModel().getColumnMargin();
        Enumeration<ColumnGroup> enum1 = columnGroups.elements();
        while (enum1.hasMoreElements()) {
            ColumnGroup cGroup = (ColumnGroup) enum1.nextElement();
            cGroup.setColumnMargin(columnMargin);
        }
    }

    public void setReorderingAllowed(boolean b) {
        reorderingAllowed = b;
    }
}
