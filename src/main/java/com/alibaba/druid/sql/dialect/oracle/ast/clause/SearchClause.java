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
package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class SearchClause extends OracleSQLObjectImpl {

    public static enum Type {
        DEPTH, BREADTH
    }

    private Type                          type;

    private final List<SQLSelectOrderByItem> items = new ArrayList<SQLSelectOrderByItem>();

    private SQLIdentifierExpr             orderingColumn;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public List<SQLSelectOrderByItem> getItems() {
        return items;
    }
    
    public void addItem(SQLSelectOrderByItem item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public SQLIdentifierExpr getOrderingColumn() {
        return orderingColumn;
    }

    public void setOrderingColumn(SQLIdentifierExpr orderingColumn) {
        if (orderingColumn != null) {
            orderingColumn.setParent(this);
        }
        this.orderingColumn = orderingColumn;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, items);
            acceptChild(visitor, orderingColumn);
        }
        visitor.endVisit(this);
    }

    public SearchClause clone() {
        SearchClause x = new SearchClause();

        x.type = type;

        for (SQLSelectOrderByItem item : items) {
            SQLSelectOrderByItem item2 = item.clone();
            item2.setParent(x);
            x.items.add(item2);
        }

        if (orderingColumn != null) {
            x.setOrderingColumn(orderingColumn.clone());
        }

        return x;
    }
}
