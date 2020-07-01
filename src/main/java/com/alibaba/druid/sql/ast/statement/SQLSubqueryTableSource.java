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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLSubqueryTableSource extends SQLTableSourceImpl {

    protected SQLSelect select;
    protected List<SQLName> columns = new ArrayList<SQLName>();

    public SQLSubqueryTableSource(){

    }

    public SQLSubqueryTableSource(String alias){
        super(alias);
    }

    public SQLSubqueryTableSource(SQLSelect select, String alias){
        super(alias);
        this.setSelect(select);
    }

    public SQLSubqueryTableSource(SQLSelect select){
        this.setSelect(select);
    }

    public SQLSubqueryTableSource(SQLSelectQuery query){
        this(new SQLSelect(query));
    }

    public SQLSelect getSelect() {
        return this.select;
    }

    public void setSelect(SQLSelect select) {
        if (select != null) {
            select.setParent(this);
        }
        this.select = select;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, select);
            acceptChild(visitor, columns);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("(");
        this.select.output(buf);
        buf.append(")");
    }

    public void cloneTo(SQLSubqueryTableSource x) {
        x.alias = alias;

        if (select != null) {
            x.select = select.clone();
        }

        for (SQLName column : columns) {
            SQLName c2 = column.clone();
            c2.setParent(x);
            x.columns.add(c2);
        }
    }

    public SQLSubqueryTableSource clone() {
        SQLSubqueryTableSource x = new SQLSubqueryTableSource();
        cloneTo(x);
        return x;
    }

    public SQLTableSource findTableSourceWithColumn(String columnName) {
        if (select == null) {
            return null;
        }

        SQLSelectQueryBlock queryBlock = select.getFirstQueryBlock();
        if (queryBlock == null) {
            return null;
        }

        if (queryBlock.findSelectItem(columnName) != null) {
            return this;
        }

        return null;
    }

    public SQLTableSource findTableSourceWithColumn(long columnNameHash) {
        if (select == null) {
            return null;
        }

        SQLSelectQueryBlock queryBlock = select.getFirstQueryBlock();
        if (queryBlock == null) {
            return null;
        }

        if (queryBlock.findSelectItem(columnNameHash) != null) {
            return this;
        }

        return null;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public void addColumn(SQLName column) {
        column.setParent(this);
        this.columns.add(column);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLSubqueryTableSource that = (SQLSubqueryTableSource) o;

        return select != null ? select.equals(that.select) : that.select == null;
    }

    @Override
    public int hashCode() {
        return select != null ? select.hashCode() : 0;
    }
}
