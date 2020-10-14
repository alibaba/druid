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

public class SQLWithSubqueryClause extends SQLObjectImpl {

    private Boolean           recursive;
    private final List<Entry> entries = new ArrayList<Entry>();

    public SQLWithSubqueryClause clone() {
        SQLWithSubqueryClause x = new SQLWithSubqueryClause();
        x.recursive = recursive;

        for (Entry entry : entries) {
            Entry entry2 = entry.clone();
            entry2.setParent(x);
            x.entries.add(entry2);
        }

        return x;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void addEntry(Entry entry) {
        if (entry != null) {
            entry.setParent(this);
        }
        this.entries.add(entry);
    }

    public Boolean getRecursive() {
        return recursive;
    }

    public void setRecursive(Boolean recursive) {
        this.recursive = recursive;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            for (int i = 0; i < entries.size(); i++) {
                Entry entry = entries.get(i);
                if (entry != null) {
                    entry.accept(visitor);
                }
            }
        }
        visitor.endVisit(this);
    }

    public static class Entry extends SQLTableSourceImpl implements SQLReplaceable {

        protected final List<SQLName> columns = new ArrayList<SQLName>();
        protected SQLSelect           subQuery;
        protected SQLStatement        returningStatement;
        protected SQLExpr       expr;

        public Entry() {

        }

        public Entry(String alias, SQLSelect select) {
            this.setAlias(alias);
            this.setSubQuery(select);
        }

        public Entry(String alias, SQLExpr expr) {
            this.setAlias(alias);
            this.setExpr(expr);
        }

        public void cloneTo(Entry x) {
            for (SQLName column : columns) {
                SQLName column2 = column.clone();
                column2.setParent(x);
                x.columns.add(column2);
            }

            if (subQuery != null) {
                x.setSubQuery(subQuery.clone());
            }

            if (returningStatement != null) {
                setReturningStatement(returningStatement.clone());
            }

            x.alias = alias;
            x.expr = expr;
        }

        @Override
        public boolean replace(SQLExpr expr, SQLExpr target) {
            if (flashback == expr) {
                setFlashback(target);
                return true;
            }

            for (int i = 0; i < columns.size(); i++) {
                if (columns.get(i) == expr) {
                    target.setParent(this);
                    columns.set(i, (SQLName) expr);
                    return true;
                }
            }
            return false;
        }

        public Entry clone() {
            Entry x = new Entry();
            cloneTo(x);
            return x;
        }

        public SQLExpr getExpr() {
            return expr;
        }

        public void setExpr(SQLExpr expr) {
            this.expr = expr;
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                for (int i = 0; i < columns.size(); i++) {
                    SQLExpr column = columns.get(i);
                    if (column != null) {
                        column.accept(visitor);
                    }
                }

                if (subQuery != null) {
                    subQuery.accept(visitor);
                }

                if (returningStatement != null) {
                    returningStatement.accept(visitor);
                }

                if (expr != null) {
                    expr.accept(visitor);
                }
            }
            visitor.endVisit(this);
        }

        public SQLSelect getSubQuery() {
            return subQuery;
        }

        public void setSubQuery(SQLSelect subQuery) {
            if (subQuery != null) {
                subQuery.setParent(this);
            }
            this.subQuery = subQuery;
        }

        public SQLStatement getReturningStatement() {
            return returningStatement;
        }

        public void setReturningStatement(SQLStatement returningStatement) {
            if (returningStatement != null) {
                returningStatement.setParent(this);
            }
            this.returningStatement = returningStatement;
        }

        public List<SQLName> getColumns() {
            return columns;
        }

        public SQLTableSource findTableSourceWithColumn(long columnNameHash, String columnName, int option) {
            for (SQLName column : columns) {
                if (column.nameHashCode64() == columnNameHash) {
                    return this;
                }
            }

            if (subQuery != null) {
                SQLSelectQueryBlock queryBlock = subQuery.getFirstQueryBlock();
                if (queryBlock != null) {
                    if (queryBlock.findSelectItem(columnNameHash) != null) {
                        return this;
                    }
                }
            }
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;

            Entry entry = (Entry) o;

            if (!columns.equals(entry.columns)) return false;
            if (subQuery != null ? !subQuery.equals(entry.subQuery) : entry.subQuery != null) return false;
            return returningStatement != null ? returningStatement.equals(entry.returningStatement) : entry.returningStatement == null;
        }

        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 31 * result + (columns != null ? columns.hashCode() : 0);
            result = 31 * result + (subQuery != null ? subQuery.hashCode() : 0);
            result = 31 * result + (returningStatement != null ? returningStatement.hashCode() : 0);
            return result;
        }
    }

    public Entry findEntry(long alias_hash) {
        if (alias_hash == 0) {
            return null;
        }

        for (Entry entry : entries) {
            if (entry.aliasHashCode64() == alias_hash) {
                return entry;
            }
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLWithSubqueryClause that = (SQLWithSubqueryClause) o;

        if (recursive != null ? !recursive.equals(that.recursive) : that.recursive != null) return false;
        return entries.equals(that.entries);
    }

    @Override
    public int hashCode() {
        int result = recursive != null ? recursive.hashCode() : 0;
        result = 31 * result + entries.hashCode();
        return result;
    }
}
