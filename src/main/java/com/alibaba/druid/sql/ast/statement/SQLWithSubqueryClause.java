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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

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
    
    public void addEntry(Entry entrie) {
        if (entrie != null) {
            entrie.setParent(this);
        }
        this.entries.add(entrie);
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
            acceptChild(visitor, entries);
        }
        visitor.endVisit(this);
    }

    public static class Entry extends SQLTableSourceImpl {

        protected final List<SQLName> columns = new ArrayList<SQLName>();
        protected SQLSelect           subQuery;
        protected SQLStatement        returningStatement;

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
        }

        public Entry clone() {
            Entry x = new Entry();
            cloneTo(x);
            return x;
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, columns);
                acceptChild(visitor, subQuery);
                acceptChild(visitor, returningStatement);
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

        public SQLTableSource findTableSourceWithColumn(long columnNameHash) {
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
}
