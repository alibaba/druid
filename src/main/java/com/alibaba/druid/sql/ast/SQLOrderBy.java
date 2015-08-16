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
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLOrderBy extends SQLObjectImpl {

    protected final List<SQLSelectOrderByItem> items = new ArrayList<SQLSelectOrderByItem>();

    public SQLOrderBy(){

    }

    public SQLOrderBy(SQLExpr expr){
        SQLSelectOrderByItem item = new SQLSelectOrderByItem(expr);
        addItem(item);
    }

    public void addItem(SQLSelectOrderByItem item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public List<SQLSelectOrderByItem> getItems() {
        return this.items;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
        }

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + items.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SQLOrderBy other = (SQLOrderBy) obj;
        return items.equals(other.items);
    }

    public void addItem(SQLExpr expr, SQLOrderingSpecification type) {
        SQLSelectOrderByItem item = createItem();
        item.setExpr(expr);
        item.setType(type);
        addItem(item);
    }

    protected SQLSelectOrderByItem createItem() {
        return new SQLSelectOrderByItem();
    }
}
