/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class SubqueryFactoringClause extends OracleSQLObjectImpl {

    private static final long serialVersionUID = 1L;

    private final List<Entry> entries          = new ArrayList<Entry>();

    public List<Entry> getEntries() {
        return entries;
    }

    public static class Entry extends OracleSQLObjectImpl {

        private static final long   serialVersionUID = 1L;

        private SQLIdentifierExpr   name;
        private final List<SQLName> columns          = new ArrayList<SQLName>();
        private SQLSelectQuery      subQuery;
        private SearchClause        searchClause;
        private CycleClause         cycleClause;

        public CycleClause getCycleClause() {
            return cycleClause;
        }

        public void setCycleClause(CycleClause cycleClause) {
            this.cycleClause = cycleClause;
        }

        public SearchClause getSearchClause() {
            return searchClause;
        }

        public void setSearchClause(SearchClause searchClause) {
            this.searchClause = searchClause;
        }

        public SQLIdentifierExpr getName() {
            return name;
        }

        public void setName(SQLIdentifierExpr name) {
            this.name = name;
        }

        public SQLSelectQuery getSubQuery() {
            return subQuery;
        }

        public void setSubQuery(SQLSelectQuery subQuery) {
            this.subQuery = subQuery;
        }

        public List<SQLName> getColumns() {
            return columns;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, name);
                acceptChild(visitor, columns);
                acceptChild(visitor, subQuery);
            }
            visitor.endVisit(this);
        }

    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, entries);
        }
        visitor.endVisit(this);
    }
}
