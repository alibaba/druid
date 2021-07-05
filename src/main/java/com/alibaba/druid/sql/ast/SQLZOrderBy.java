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
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public final class SQLZOrderBy extends SQLObjectImpl implements SQLReplaceable {

    protected final List<SQLSelectOrderByItem> items = new ArrayList<SQLSelectOrderByItem>();

    // for postgres
    private boolean                            sibings;

    public SQLZOrderBy(){

    }

    public SQLZOrderBy(SQLExpr expr){
        SQLSelectOrderByItem item = new SQLSelectOrderByItem(expr);
        addItem(item);
    }

    public SQLZOrderBy(SQLExpr expr, SQLOrderingSpecification type){
        SQLSelectOrderByItem item = new SQLSelectOrderByItem(expr, type);
        addItem(item);
    }

    public void addItem(SQLSelectOrderByItem item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public void addItem(SQLExpr item) {
        addItem(new SQLSelectOrderByItem(item));
    }

    public List<SQLSelectOrderByItem> getItems() {
        return this.items;
    }
    
    public boolean isSibings() {
        return this.sibings;
    }

    public void setSibings(boolean sibings) {
        this.sibings = sibings;
    }

    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            for (int i = 0; i < this.items.size(); i++) {
                final SQLSelectOrderByItem item = this.items.get(i);
                item.accept(v);
            }
        }

        v.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLZOrderBy order = (SQLZOrderBy) o;

        if (sibings != order.sibings) return false;
        return items.equals(order.items);
    }

    @Override
    public int hashCode() {
        int result = items.hashCode();
        result = 31 * result + (sibings ? 1 : 0);
        return result;
    }

    public void addItem(SQLExpr expr, SQLOrderingSpecification type) {
        SQLSelectOrderByItem item = createItem();
        item.setExpr(expr);
        item.setType(type);
        addItem(item);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {

        for (SQLSelectOrderByItem item : items) {
            if(item.replace(expr, target)) {
                return true;
            }
        }

        return false;
    }

    protected SQLSelectOrderByItem createItem() {
        return new SQLSelectOrderByItem();
    }

    public SQLZOrderBy clone() {
        SQLZOrderBy x = new SQLZOrderBy();

        for (SQLSelectOrderByItem item : items) {
            SQLSelectOrderByItem item1 = item.clone();
            item1.setParent(x);
            x.items.add(item1);
        }

        x.sibings = sibings;

        return x;
    }
}
