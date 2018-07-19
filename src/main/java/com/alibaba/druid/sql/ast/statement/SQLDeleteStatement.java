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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExprGroup;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLDeleteStatement extends SQLStatementImpl implements SQLReplaceable {
    protected SQLWithSubqueryClause  with;

    protected SQLTableSource tableSource;
    protected SQLExpr        where;
    protected SQLTableSource from;
    protected SQLTableSource using;

    protected boolean        only      = false;

    public SQLDeleteStatement(){

    }
    
    public SQLDeleteStatement(String dbType){
        super (dbType);
    }

    protected void cloneTo(SQLDeleteStatement x) {
        if (headHints != null) {
            for (SQLCommentHint h : headHints) {
                SQLCommentHint h2 = h.clone();
                h2.setParent(x);
                x.headHints.add(h2);
            }
        }

        if (with != null) {
            x.setWith(with.clone());
        }

        if (tableSource != null) {
            x.setTableSource(tableSource.clone());
        }
        if (where != null) {
            x.setWhere(where.clone());
        }
        if (from != null) {
            x.setFrom(from.clone());
        }
        if (using != null) {
            x.setUsing(using.clone());
        }
        x.only = only;
    }

    public SQLDeleteStatement clone() {
        SQLDeleteStatement x = new SQLDeleteStatement();
        cloneTo(x);
        return x;
    }

    public SQLTableSource getTableSource() {
        return tableSource;
    }

    public SQLExprTableSource getExprTableSource() {
        return (SQLExprTableSource) getTableSource();
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
        if (this.tableSource instanceof SQLExprTableSource) {
            SQLExprTableSource exprTableSource = (SQLExprTableSource) this.tableSource;
            return (SQLName) exprTableSource.getExpr();
        }

        if (tableSource instanceof SQLSubqueryTableSource) {
            SQLSelectQuery selectQuery = ((SQLSubqueryTableSource) tableSource).getSelect().getQuery();
            if (selectQuery instanceof SQLSelectQueryBlock) {
                SQLTableSource subQueryTableSource = ((SQLSelectQueryBlock) selectQuery).getFrom();
                if (subQueryTableSource instanceof SQLExprTableSource) {
                    SQLExpr subQueryTableSourceExpr = ((SQLExprTableSource) subQueryTableSource).getExpr();
                    return (SQLName) subQueryTableSourceExpr;
                }
            }
        }

        return null;
    }

    public void setTableName(SQLName tableName) {
        this.setTableSource(new SQLExprTableSource(tableName));
    }

    public void setTableName(String name) {
        setTableName(new SQLIdentifierExpr(name));
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

    public String getAlias() {
        return this.tableSource.getAlias();
    }

    public void setAlias(String alias) {
        this.tableSource.setAlias(alias);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, with);
            acceptChild(visitor, tableSource);
            acceptChild(visitor, where);
        }

        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (with != null) {
            children.add(with);
        }
        children.add(tableSource);
        if (where != null) {
            children.add(where);
        }
        return children;
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
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (where == expr) {
            setWhere(target);
            return true;
        }
        return false;
    }

    public boolean isOnly() {
        return only;
    }

    public void setOnly(boolean only) {
        this.only = only;
    }

    public SQLTableSource getUsing() {
        return using;
    }

    public void setUsing(SQLTableSource using) {
        this.using = using;
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

    public boolean addWhere(SQLExpr where) {
        if (where == null) {
            return false;
        }

        this.addCondition(where);
        return true;
    }
}
