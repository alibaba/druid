/*
 * Copyright 2011 Alibaba Group.
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
    private static final long serialVersionUID = 1L;

    private SQLName tableName;

    private final List<SQLUpdateSetItem> items = new ArrayList<SQLUpdateSetItem>();
    private SQLExpr where;

    public SQLUpdateStatement() {

    }

    public SQLName getTableName() {
        return tableName;
    }

    public void setTableName(SQLName tableName) {
        this.tableName = tableName;
    }

    public SQLExpr getWhere() {
        return where;
    }

    public void setWhere(SQLExpr where) {
        this.where = where;
    }

    public List<SQLUpdateSetItem> getItems() {
        return items;
    }

    @Override
    public void output(StringBuffer buf) {
        buf.append("UPDATE ");

        this.tableName.output(buf);

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
            acceptChild(visitor, tableName);
            acceptChild(visitor, items);
            acceptChild(visitor, where);
        }
        visitor.endVisit(this);
    }
}
