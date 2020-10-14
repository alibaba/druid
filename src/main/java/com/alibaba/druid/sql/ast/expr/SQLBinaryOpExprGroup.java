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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SQLBinaryOpExprGroup extends SQLExprImpl implements SQLReplaceable {
    private final SQLBinaryOperator operator;
    private final List<SQLExpr>     items = new ArrayList<SQLExpr>();
    private DbType                  dbType;

    public SQLBinaryOpExprGroup(SQLBinaryOperator operator) {
        this.operator = operator;
    }

    public SQLBinaryOpExprGroup(SQLBinaryOperator operator, DbType dbType) {
        this.operator = operator;
        this.dbType = dbType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLBinaryOpExprGroup that = (SQLBinaryOpExprGroup) o;

        if (operator != that.operator) return false;
        return items.equals(that.items);
    }

    @Override
    public int hashCode() {
        int result = operator != null ? operator.hashCode() : 0;
        result = 31 * result + items.hashCode();
        return result;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            for (int i = 0; i < this.items.size(); i++) {
                SQLExpr item = this.items.get(i);
                item.accept(visitor);
            }
        }

        visitor.endVisit(this);
    }

    @Override
    public SQLExpr clone() {
        SQLBinaryOpExprGroup x = new SQLBinaryOpExprGroup(operator);

        for (SQLExpr item : items) {
            SQLExpr item2 = item.clone();
            item2.setParent(this);
            x.items.add(item2);
        }

        return x;
    }

    @Override
    public List getChildren() {
        return items;
    }

    public void add(SQLExpr item) {
        add(items.size(), item);
    }

    public void add(int index, SQLExpr item) {
        if (item instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) item;
            if (binaryOpExpr.getOperator() == operator) {
                add(binaryOpExpr.getLeft());
                add(binaryOpExpr.getRight());
                return;
            }
        } else if (item instanceof SQLBinaryOpExprGroup) {
            SQLBinaryOpExprGroup group = (SQLBinaryOpExprGroup) item;
            if (group.operator == this.operator) {
                for (SQLExpr sqlExpr : group.getItems()) {
                    add(sqlExpr);
                }
                return;
            }
        }

        if (item != null) {
            item.setParent(this);
        }
        this.items.add(index, item);
    }

    public List<SQLExpr> getItems() {
        return this.items;
    }

    public SQLBinaryOperator getOperator() {
        return operator;
    }

    public String toString() {
        return SQLUtils.toSQLString(this, dbType);
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        boolean replaced = false;
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) == expr) {
                if (target == null) {
                    items.remove(i);
                } else {
                    if (target instanceof SQLBinaryOpExpr && ((SQLBinaryOpExpr) target).getOperator() == operator) {
                        items.remove(i);
                        List<SQLExpr> list = SQLBinaryOpExpr.split(target, operator);
                        for (int j = 0; j < list.size(); j++) {
                            SQLExpr o = list.get(j);
                            o.setParent(this);
                            items.add(i + j, o);
                        }
                    } else {
                        target.setParent(this);
                        items.set(i, target);
                    }
                }
                replaced = true;
            }
        }

        if (items.size() == 1 && replaced) {
            SQLUtils.replaceInParent(this, items.get(0));
        }

        if (items.size() == 0) {
            SQLUtils.replaceInParent(this, null);
        }

        return replaced;
    }

    public void optimize() {
        List<Integer> dupIndexList = null;

        Set<SQLExpr> itemSet = new LinkedHashSet<SQLExpr>();
        for (int i = 0; i < items.size(); i++) {
            if (!itemSet.add(items.get(i))) {
                if (dupIndexList == null) {
                    dupIndexList = new ArrayList<Integer>();
                }
                dupIndexList.add(i);
            }
        }

        if (dupIndexList != null) {
            for (int i = dupIndexList.size() - 1; i >= 0; i--) {
                int index = dupIndexList.get(i);
                items.remove(index);
            }
        }
    }
}
