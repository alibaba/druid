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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlTableIndex extends MySqlObjectImpl implements SQLTableElement {

    private SQLName                    name;
    private String                     indexType;
    private List<SQLSelectOrderByItem> columns = new ArrayList<SQLSelectOrderByItem>();

    public MySqlTableIndex(){

    }

    public SQLName getName() {
        return name;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public List<SQLSelectOrderByItem> getColumns() {
        return columns;
    }
    
    public void addColumn(SQLSelectOrderByItem column) {
        if (column != null) {
            column.setParent(this);
        }
        this.columns.add(column);
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, columns);
        }
        visitor.endVisit(this);
    }

    public MySqlTableIndex clone() {
        MySqlTableIndex x = new MySqlTableIndex();
        if (name != null) {
            x.setName(name.clone());
        }
        x.indexType = indexType;
        for (SQLSelectOrderByItem column : columns) {
            SQLSelectOrderByItem c2 = column.clone();
            c2.setParent(x);
            x.columns.add(c2);
        }
        return x;
    }

    public boolean applyColumnRename(SQLName columnName, SQLName to) {
        for (SQLSelectOrderByItem orderByItem : columns) {
            SQLExpr expr = orderByItem.getExpr();
            if (expr instanceof SQLName
                    && SQLUtils.nameEquals((SQLName) expr, columnName)) {
                orderByItem.setExpr(to.clone());
                return true;
            }
        }
        return false;
    }

    public boolean applyDropColumn(SQLName columnName) {
        for (int i = columns.size() - 1; i >= 0; i--) {
            SQLExpr expr = columns.get(i).getExpr();
            if (expr instanceof SQLName
                    && SQLUtils.nameEquals((SQLName) expr, columnName)) {
                columns.remove(i);
                return true;
            }
            if (expr instanceof SQLMethodInvokeExpr
                    && SQLUtils.nameEquals(((SQLMethodInvokeExpr) expr).getMethodName(), columnName.getSimpleName())) {
                columns.remove(i);
                return true;
            }
        }
        return false;
    }
}
