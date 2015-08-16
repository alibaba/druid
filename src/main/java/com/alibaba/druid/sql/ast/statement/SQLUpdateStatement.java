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
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLUpdateStatement extends SQLStatementImpl {

    protected final List<SQLUpdateSetItem> items = new ArrayList<SQLUpdateSetItem>();
    protected SQLExpr                      where;

    protected SQLTableSource               tableSource;

    public SQLUpdateStatement(){

    }
    
    public SQLUpdateStatement(String dbType){
        super (dbType);
    }

    public SQLTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExpr expr) {
        this.setTableSource(new SQLExprTableSource(expr));
    }

    public void setTableSource(SQLTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSource = tableSource;
    }

    public SQLName getTableName() {
        if (tableSource instanceof SQLExprTableSource) {
            SQLExprTableSource exprTableSource = (SQLExprTableSource) tableSource;
            return (SQLName) exprTableSource.getExpr();
        }
        return null;
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr where) {
        if (where != null) {
            where.setParent(this);
        }
        this.where = where;
    }

    public List<SQLUpdateSetItem> getItems() {
        return items;
    }
    
    public void addItem(SQLUpdateSetItem item) {
        this.items.add(item);
        item.setParent(this);
    }

    @Override
    public void output(StringBuffer buf) {
        buf.append("UPDATE ");

        this.tableSource.output(buf);

        buf.append(" SET ");
        for (int i = 0, size = items.size(); i < size; ++i) {
            if (i != 0) {
                buf.append(", ");
            }
            items.get(i).output(buf);
        }

        if (this.where != null) {
            buf.append(" WHERE ");
            this.where.output(buf);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, items);
            acceptChild(visitor, where);
        }
        visitor.endVisit(this);
    }
}
