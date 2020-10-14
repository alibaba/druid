/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.List;

public class SQLUnique extends SQLConstraintImpl implements SQLUniqueConstraint, SQLTableElement {

    protected final SQLIndexDefinition indexDefinition = new SQLIndexDefinition();

    public SQLUnique(){
        indexDefinition.setParent(this);
    }

    // Override name and comment in constraint impl.
    @Override
    public SQLName getName() {
        return indexDefinition.getName();
    }

    @Override
    public void setName(SQLName name) {
        indexDefinition.setName(name);
    }

    @Override
    public void setName(String name) {
        this.setName(new SQLIdentifierExpr(name));
    }

    @Override
    public SQLExpr getComment() {
        if (indexDefinition.hasOptions()) {
            return indexDefinition.getOptions().getComment();
        }
        return null;
    }

    @Override
    public void setComment(SQLExpr x) {
        indexDefinition.getOptions().setComment(x);
    }

    public SQLIndexDefinition getIndexDefinition() {
        return indexDefinition;
    }

    public List<SQLSelectOrderByItem> getColumns() {
        return indexDefinition.getColumns();
    }
    
    public void addColumn(SQLExpr column) {
        if (column == null) {
            return;
        }

        addColumn(new SQLSelectOrderByItem(column));
    }

    public void addColumn(SQLSelectOrderByItem column) {
        if (column != null) {
            column.setParent(this);
        }
        indexDefinition.getColumns().add(column);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getName());
            acceptChild(visitor, getColumns());
            acceptChild(visitor, getCovering());
        }
        visitor.endVisit(this);
    }

    public boolean containsColumn(String column) {
        for (SQLSelectOrderByItem item : getColumns()) {
            SQLExpr expr = item.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                if (SQLUtils.nameEquals(((SQLIdentifierExpr) expr).getName(), column)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsColumn(long columnNameHash) {
        for (SQLSelectOrderByItem item : getColumns()) {
            SQLExpr expr = item.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                if (((SQLIdentifierExpr) expr).nameHashCode64() == columnNameHash) {
                    return true;
                }
            }
        }
        return false;
    }

    public void cloneTo(SQLUnique x) {
        super.cloneTo(x);

        indexDefinition.cloneTo(x.indexDefinition);
    }

    public SQLUnique clone() {
        SQLUnique x = new SQLUnique();
        cloneTo(x);
        return x;
    }

    public void simplify() {
        super.simplify();

        for (SQLSelectOrderByItem item : getColumns()) {
            SQLExpr column = item.getExpr();
            if (column instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) column;
                String columnName = identExpr.getName();
                String normalized = SQLUtils.normalize(columnName, dbType);
                if (normalized != columnName) {
                    item.setExpr(new SQLIdentifierExpr(columnName));
                }
            }
        }
    }

    public boolean applyColumnRename(SQLName columnName, SQLColumnDefinition to) {
        for (SQLSelectOrderByItem orderByItem : getColumns()) {
            SQLExpr expr = orderByItem.getExpr();
            if (expr instanceof SQLName
                    && SQLUtils.nameEquals((SQLName) expr, columnName)) {
                orderByItem.setExpr(to.getName().clone());
                return true;
            }

            if (expr instanceof SQLMethodInvokeExpr
                    && SQLUtils.nameEquals(((SQLMethodInvokeExpr) expr).getMethodName(), columnName.getSimpleName())) {
                // More complex when with key length.
                if (1 == ((SQLMethodInvokeExpr) expr).getArguments().size() &&
                        ((SQLMethodInvokeExpr) expr).getArguments().get(0) instanceof SQLIntegerExpr) {
                    if (to.getDataType().hasKeyLength() &&
                            1 == to.getDataType().getArguments().size() &&
                            to.getDataType().getArguments().get(0) instanceof SQLIntegerExpr) {
                        int newKeyLength = ((SQLIntegerExpr)to.getDataType().getArguments().get(0)).getNumber().intValue();
                        int oldKeyLength = ((SQLIntegerExpr)((SQLMethodInvokeExpr) expr).getArguments().get(0)).getNumber().intValue();
                        if (newKeyLength > oldKeyLength) {
                            // Change name and keep key length.
                            ((SQLMethodInvokeExpr) expr).setMethodName(to.getName().getSimpleName());
                            return true;
                        }
                    }
                    // Remove key length.
                    orderByItem.setExpr(to.getName().clone());
                    return true;
                }
            }
        }
        return false;
    }

    public boolean applyDropColumn(SQLName columnName) {
        for (int i = getColumns().size() - 1; i >= 0; i--) {
            SQLExpr expr = getColumns().get(i).getExpr();
            if (expr instanceof SQLName
                    && SQLUtils.nameEquals((SQLName) expr, columnName)) {
                getColumns().remove(i);
                return true;
            }

            if (expr instanceof SQLMethodInvokeExpr
                    && SQLUtils.nameEquals(((SQLMethodInvokeExpr) expr).getMethodName(), columnName.getSimpleName())) {
                getColumns().remove(i);
                return true;
            }
        }
        return false;
    }

    public List<SQLName> getCovering() {
        return indexDefinition.getCovering();
    }
}
