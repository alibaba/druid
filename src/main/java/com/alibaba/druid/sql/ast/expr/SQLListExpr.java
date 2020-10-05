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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLListExpr extends SQLExprImpl implements SQLReplaceable  {

    private final List<SQLExpr> items;

    public SQLListExpr() {
        items = new ArrayList<SQLExpr>();
    }

    public SQLListExpr(SQLExpr... items) {
        this.items = new ArrayList<SQLExpr>(items.length);
        for (SQLExpr item : items) {
            item.setParent(this);
            this.items.add(item);
        }
    }

    public List<SQLExpr> getItems() {
        return items;
    }
    
    public void addItem(SQLExpr item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            for (int i = 0; i < items.size(); i++) {
                SQLExpr item = items.get(i);
                if (item != null) {
                    item.accept(visitor);
                }
            }
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLListExpr other = (SQLListExpr) obj;
        if (!items.equals(other.items)) {
            return false;
        }
        return true;
    }

    public SQLListExpr clone() {
        SQLListExpr x = new SQLListExpr();
        for (SQLExpr item : items) {
            SQLExpr item2 = item.clone();
            item2.setParent(x);
            x.items.add(item2);
        }
        return x;
    }

    public List getChildren() {
        return this.items;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == expr) {
                target.setParent(this);
                items.set(i, target);
                return true;
            }
        }
        return false;
    }
}
