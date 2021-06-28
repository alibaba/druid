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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExprGroup;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLUpdateStatement extends SQLStatementImpl implements SQLReplaceable {
    protected SQLWithSubqueryClause with; // for pg

    protected final List<SQLUpdateSetItem> items = new ArrayList<SQLUpdateSetItem>();
    protected SQLExpr                      where;
    protected SQLTableSource               from;

    protected SQLTableSource               tableSource;
    protected List<SQLExpr>                returning;

    // for mysql
    protected SQLOrderBy orderBy;

    public SQLUpdateStatement(){

    }

    public void cloneTo(SQLUpdateStatement x) {
        x.dbType = dbType;
        x.afterSemi = afterSemi;

        if (with != null) {
            x.setWith(with.clone());
            x.with.setParent(x);
        }

        if (where != null) {
            x.where = where.clone();
            x.where.setParent(x);
        }
        if (tableSource != null) {
            x.setTableSource(tableSource.clone());
            x.tableSource.setParent(x);
        }

        for (SQLUpdateSetItem item : items) {
            SQLUpdateSetItem clone = item.clone();
            clone.setParent(x);
            x.getItems().add(clone);
        }

        if (returning != null) {
            for (SQLExpr item : returning) {
                SQLExpr clone = item.clone();
                clone.setParent(x);
                x.getReturning().add(clone);
            }
        }

        if (orderBy != null) {
            x.orderBy = orderBy.clone();
            x.orderBy.setParent(x);
        }
    }

    public SQLUpdateStatement clone() {
        SQLUpdateStatement x = new SQLUpdateStatement();
        cloneTo(x);
        return x;
    }
    
    public SQLUpdateStatement(DbType dbType){
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
            return ((SQLExprTableSource) tableSource).getName();
        }

        if (tableSource instanceof SQLJoinTableSource) {
            SQLTableSource left = ((SQLJoinTableSource) tableSource).getLeft();
            if (left instanceof SQLExprTableSource) {
                return ((SQLExprTableSource) left).getName();
            }
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

    public List<SQLExpr> getReturning() {
        if (returning == null) {
            returning = new ArrayList<SQLExpr>(2);
        }

        return returning;
    }

    public SQLTableSource getFrom() {
        return from;
    }

    public void setFrom(SQLTableSource from) {
        if (from != null) {
            from.setParent(this);
        }
        this.from = from;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor);
        }
        visitor.endVisit(this);
    }

    protected void acceptChild(SQLASTVisitor visitor)
    {
        if (with != null) {
            with.accept(visitor);
        }

        if (tableSource != null) {
            tableSource.accept(visitor);
        }

        if (from != null) {
            from.accept(visitor);
        }

        for (int i = 0; i < items.size(); i++) {
            SQLUpdateSetItem item = items.get(i);
            if (item != null) {
                item.accept(visitor);
            }
        }

        if (where != null) {
            where.accept(visitor);
        }

        if (orderBy != null) {
            orderBy.accept(visitor);
        }
    }

    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (tableSource != null) {
            children.add(tableSource);
        }
        if (from != null) {
            children.add(from);
        }
        children.addAll(this.items);
        if (where != null) {
            children.add(where);
        }
        if (orderBy != null) {
            children.add(orderBy);
        }
        return children;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (where == expr) {
            setWhere(target);
            return true;
        }

        if (returning != null) {
            for (int i = 0; i < returning.size(); i++) {
                if (returning.get(i) == expr) {
                    target.setParent(this);
                    returning.set(i, target);
                    return true;
                }
            }
        }

        return false;
    }


    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        if (orderBy != null) {
            orderBy.setParent(this);
        }
        this.orderBy = orderBy;
    }

    public SQLWithSubqueryClause getWith() {
        return with;
    }

    public void setWith(SQLWithSubqueryClause with) {
        if (with != null) {
            with.setParent(this);
        }
        this.with = with;
    }

    public void addCondition(String conditionSql) {
        if (conditionSql == null || conditionSql.length() == 0) {
            return;
        }

        SQLExpr condition = SQLUtils.toSQLExpr(conditionSql, dbType);
        addCondition(condition);
    }

    public void addCondition(SQLExpr expr) {
        if (expr == null) {
            return;
        }

        this.setWhere(SQLBinaryOpExpr.and(where, expr));
    }

    public boolean removeCondition(String conditionSql) {
        if (conditionSql == null || conditionSql.length() == 0) {
            return false;
        }

        SQLExpr condition = SQLUtils.toSQLExpr(conditionSql, dbType);

        return removeCondition(condition);
    }

    public boolean removeCondition(SQLExpr condition) {
        if (condition == null) {
            return false;
        }

        if (where instanceof SQLBinaryOpExprGroup) {
            SQLBinaryOpExprGroup group = (SQLBinaryOpExprGroup) where;

            int removedCount = 0;
            List<SQLExpr> items = group.getItems();
            for (int i = items.size() - 1; i >= 0; i--) {
                if (items.get(i).equals(condition)) {
                    items.remove(i);
                    removedCount++;
                }
            }
            if (items.size() == 0) {
                where = null;
            }

            return removedCount > 0;
        }

        if (where instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binaryOpWhere = (SQLBinaryOpExpr) where;
            SQLBinaryOperator operator = binaryOpWhere.getOperator();
            if (operator == SQLBinaryOperator.BooleanAnd || operator == SQLBinaryOperator.BooleanOr) {
                List<SQLExpr> items = SQLBinaryOpExpr.split(binaryOpWhere);

                int removedCount = 0;
                for (int i = items.size() - 1; i >= 0; i--) {
                    SQLExpr item = items.get(i);
                    if (item.equals(condition)) {
                        if (SQLUtils.replaceInParent(item, null)) {
                            removedCount++;
                        }
                    }
                }

                return removedCount > 0;
            }
        }

        if (condition.equals(where)) {
            where = null;
            return true;
        }

        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLUpdateStatement that = (SQLUpdateStatement) o;

        if (with != null ? !with.equals(that.with) : that.with != null) return false;
        if (!items.equals(that.items)) return false;
        if (where != null ? !where.equals(that.where) : that.where != null) return false;
        if (from != null ? !from.equals(that.from) : that.from != null) return false;
        if (tableSource != null ? !tableSource.equals(that.tableSource) : that.tableSource != null) return false;
        if (returning != null ? !returning.equals(that.returning) : that.returning != null) return false;
        return orderBy != null ? orderBy.equals(that.orderBy) : that.orderBy == null;
    }

    @Override
    public int hashCode() {
        int result = with != null ? with.hashCode() : 0;
        result = 31 * result + items.hashCode();
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (from != null ? from.hashCode() : 0);
        result = 31 * result + (tableSource != null ? tableSource.hashCode() : 0);
        result = 31 * result + (returning != null ? returning.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        return result;
    }

    public boolean addWhere(SQLExpr where) {
        if (where == null) {
            return false;
        }

        this.addCondition(where);
        return true;
    }
}
