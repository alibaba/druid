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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.statement.SQLErrorLoggingClause;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleMultiInsertStatement extends OracleStatementImpl {

    public static enum Option {
        ALL, FIRST
    }

    private SQLSelect     subQuery;
    private Option        option;
    private List<Entry>   entries = new ArrayList<Entry>();
    private List<SQLHint> hints   = new ArrayList<SQLHint>(1);

    public List<SQLHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLHint> hints) {
        this.hints = hints;
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

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public SQLSelect getSubQuery() {
        return subQuery;
    }

    public void setSubQuery(SQLSelect subQuery) {
        this.subQuery = subQuery;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.entries);
            acceptChild(visitor, this.subQuery);
        }
        visitor.endVisit(this);
    }

    public static interface Entry extends OracleSQLObject {

    }

    public static class ConditionalInsertClause extends OracleSQLObjectImpl implements Entry {

        private List<ConditionalInsertClauseItem> items = new ArrayList<ConditionalInsertClauseItem>();
        private InsertIntoClause                  elseItem;

        public InsertIntoClause getElseItem() {
            return elseItem;
        }

        public void setElseItem(InsertIntoClause elseItem) {
            this.elseItem = elseItem;
        }

        public List<ConditionalInsertClauseItem> getItems() {
            return items;
        }

        public void addItem(ConditionalInsertClauseItem item) {
            if (item != null) {
                item.setParent(this);
            }
            this.items.add(item);
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, items);
                acceptChild(visitor, elseItem);
            }
            visitor.endVisit(this);
        }

    }

    public static class ConditionalInsertClauseItem extends OracleSQLObjectImpl {

        private SQLExpr          when;
        private InsertIntoClause then;

        public SQLExpr getWhen() {
            return when;
        }

        public void setWhen(SQLExpr when) {
            this.when = when;
        }

        public InsertIntoClause getThen() {
            return then;
        }

        public void setThen(InsertIntoClause then) {
            this.then = then;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, when);
                acceptChild(visitor, then);
            }
            visitor.endVisit(this);
        }

    }

    public static class InsertIntoClause extends SQLInsertInto implements OracleSQLObject, Entry {

        private OracleReturningClause    returning;
        private SQLErrorLoggingClause errorLogging;

        public InsertIntoClause(){

        }

        public OracleReturningClause getReturning() {
            return returning;
        }

        public void setReturning(OracleReturningClause returning) {
            this.returning = returning;
        }

        public SQLErrorLoggingClause getErrorLogging() {
            return errorLogging;
        }

        public void setErrorLogging(SQLErrorLoggingClause errorLogging) {
            this.errorLogging = errorLogging;
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            this.accept0((OracleASTVisitor) visitor);
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                this.acceptChild(visitor, tableSource);
                this.acceptChild(visitor, columns);
                this.acceptChild(visitor, valuesList);
                this.acceptChild(visitor, query);
                this.acceptChild(visitor, returning);
                this.acceptChild(visitor, errorLogging);
            }

            visitor.endVisit(this);
        }

        public void cloneTo(InsertIntoClause x) {
            super.cloneTo(x);
            if (returning != null) {
                x.setReturning(returning.clone());
            }
            if (errorLogging != null) {
                x.setErrorLogging(errorLogging.clone());
            }
        }

        public InsertIntoClause clone() {
            InsertIntoClause x = new InsertIntoClause();
            cloneTo(x);
            return x;
        }
    }
}
