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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLMergeStatement extends SQLStatementImpl {
    private final List<SQLHint> hints = new ArrayList<>();

    private SQLTableSource into;
    private SQLTableSource using;
    private SQLExpr on;
    private List<When> whens = new ArrayList<>();
    private SQLErrorLoggingClause errorLoggingClause;

    public void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, into);
            acceptChild(visitor, using);
            acceptChild(visitor, on);
            acceptChild(visitor, whens);
            acceptChild(visitor, errorLoggingClause);
        }
        visitor.endVisit(this);
    }

    public String getAlias() {
        return into.getAlias();
    }

    public SQLTableSource getInto() {
        return into;
    }

    public void setInto(SQLName into) {
        this.setInto(new SQLExprTableSource(into));
    }

    public void setInto(SQLTableSource into) {
        if (into != null) {
            into.setParent(this);
        }
        this.into = into;
    }

    public SQLTableSource getUsing() {
        return using;
    }

    public void setUsing(SQLTableSource using) {
        this.using = using;
    }

    public SQLExpr getOn() {
        return on;
    }

    public void setOn(SQLExpr on) {
        this.on = on;
    }

    public void addWhen(When when) {
        when.setParent(this);
        this.whens.add(when);
    }

    public List<When> getWhens() {
        return whens;
    }

    public SQLErrorLoggingClause getErrorLoggingClause() {
        return errorLoggingClause;
    }

    public void setErrorLoggingClause(SQLErrorLoggingClause errorLoggingClause) {
        this.errorLoggingClause = errorLoggingClause;
    }

    public List<SQLHint> getHints() {
        return hints;
    }

    public static class WhenUpdate extends When {
        private final List<SQLUpdateSetItem> items = new ArrayList<>();
        public List<SQLUpdateSetItem> getItems() {
            return items;
        }

        public WhenUpdate() {
        }

        public WhenUpdate(boolean not, SQLName by, SQLExpr where) {
            super(not, by, where);
        }

        public void addItem(SQLUpdateSetItem item) {
            if (item != null) {
                item.setParent(this);
            }
            this.items.add(item);
        }

        public SQLUpdateSetItem findItemByColumn(SQLExpr column) {
            if (column == null) {
                return null;
            }
            for (SQLUpdateSetItem item : items) {
                if (item.getColumn().equals(column)) {
                    return item;
                }
            }
            return null;
        }

        @Override
        public void accept0(SQLASTVisitor v) {
            if (v.visit(this)) {
                acceptChild(v, items);
                acceptChild(v, where);
            }
            v.endVisit(this);
        }

        protected void cloneTo(WhenUpdate x) {
            super.cloneTo(x);
            for (SQLUpdateSetItem item : items) {
                x.addItem(item.clone());
            }
        }

        public WhenUpdate cloneTo() {
            WhenUpdate x = new WhenUpdate();
            cloneTo(x);
            return x;
        }

        @Override
        public boolean replace(SQLExpr expr, SQLExpr target) {
            boolean isSuccess = false;
            if (expr instanceof SQLUpdateSetItem && target instanceof SQLUpdateSetItem) {
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i) == expr) {
                        target.setParent(this);
                        items.set(i, (SQLUpdateSetItem) target);
                        isSuccess = true;
                    }
                }
            }
            return isSuccess || super.replace(expr, target);
        }
    }

    public static class WhenInsert extends When {
        private boolean insertRow;
        private List<SQLExpr> columns = new ArrayList<SQLExpr>();
        private List<SQLExpr> values = new ArrayList<SQLExpr>();

        public WhenInsert() {
        }

        public WhenInsert(boolean not, SQLName by, SQLExpr where) {
            super(not, by, where);
        }

        @Override
        public void accept0(SQLASTVisitor v) {
            if (v.visit(this)) {
                acceptChild(v, by);
                acceptChild(v, where);
                acceptChild(v, columns);
                acceptChild(v, values);
            }
            v.endVisit(this);
        }

        public List<SQLExpr> getColumns() {
            return columns;
        }

        public void addColumn(SQLExpr column) {
            column.setParent(this);
            columns.add(column);
        }

        public void addValue(SQLExpr value) {
            value.setParent(this);
            values.add(value);
        }

        public void setColumns(List<SQLExpr> columns) {
            this.columns = columns;
        }

        public boolean isInsertRow() {
            return insertRow;
        }

        public void setInsertRow(boolean insertRow) {
            this.insertRow = insertRow;
        }

        public List<SQLExpr> getValues() {
            return values;
        }

        public void setValues(List<SQLExpr> values) {
            this.values = values;
        }

        protected void cloneTo(WhenInsert x) {
            super.cloneTo(x);
            for (SQLExpr column : columns) {
                x.addColumn(column.clone());
            }
            for (SQLExpr value : values) {
                x.addValue(value.clone());
            }
        }

        public WhenInsert cloneTo() {
            WhenInsert x = new WhenInsert();
            cloneTo(x);
            return x;
        }

        @Override
        public boolean replace(SQLExpr expr, SQLExpr target) {
            boolean isSuccess = false;
            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i) == expr) {
                    target.setParent(this);
                    columns.set(i, target);
                    isSuccess = true;
                }
            }

            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) == expr) {
                    target.setParent(this);
                    values.set(i, target);
                    isSuccess = true;
                }
            }
            return isSuccess || super.replace(expr, target);
        }
    }

    public static class WhenDelete extends When {
        public WhenDelete() {
        }

        public WhenDelete(boolean not, SQLName by, SQLExpr where) {
            super(not, by, where);
        }

        @Override
        protected void accept0(SQLASTVisitor v) {
            if (v.visit(this)) {
                acceptChild(v, by);
                acceptChild(v, where);
            }
        }

        public WhenDelete cloneTo() {
            WhenDelete x = new WhenDelete();
            cloneTo(x);
            return x;
        }
    }

    public abstract static class When extends SQLObjectImpl implements SQLReplaceable {
        protected boolean not;
        protected SQLName by;
        protected SQLExpr where;

        public When() {
        }

        public When(boolean not, SQLName by, SQLExpr where) {
            this.not = not;
            this.by = by;
            this.where = where;
        }

        protected void cloneTo(When x) {
            x.not = this.not;
            if (by != null) {
                x.by = by.clone();
            }
            if (where != null) {
                x.where = where.clone();
            }
        }

        public boolean isNot() {
            return not;
        }

        public void setNot(boolean not) {
            this.not = not;
        }

        public SQLName getBy() {
            return by;
        }

        public void setBy(SQLName x) {
            if (x != null) {
                x.setParent(this);
            }
            this.by = x;
        }

        public SQLExpr getWhere() {
            return where;
        }

        public void setWhere(SQLExpr x) {
            if (x != null) {
                x.setParent(this);
            }
            this.where = x;
        }

        public boolean replace(SQLExpr expr, SQLExpr target) {
            if (this.where == expr) {
                target.setParent(this);
                this.where = target;
                return true;
            }
            return false;
        }
    }
}
