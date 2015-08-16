/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleErrorLoggingClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleMergeStatement extends OracleStatementImpl {

    private final List<SQLHint>      hints = new ArrayList<SQLHint>();

    private SQLName                  into;
    private String                   alias;
    private SQLTableSource           using;
    private SQLExpr                  on;
    private MergeUpdateClause        updateClause;
    private MergeInsertClause        insertClause;
    private OracleErrorLoggingClause errorLoggingClause;

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, into);
            acceptChild(visitor, using);
            acceptChild(visitor, on);
            acceptChild(visitor, updateClause);
            acceptChild(visitor, insertClause);
            acceptChild(visitor, errorLoggingClause);
        }
        visitor.endVisit(this);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public SQLName getInto() {
        return into;
    }

    public void setInto(SQLName into) {
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

    public MergeUpdateClause getUpdateClause() {
        return updateClause;
    }

    public void setUpdateClause(MergeUpdateClause updateClause) {
        this.updateClause = updateClause;
    }

    public MergeInsertClause getInsertClause() {
        return insertClause;
    }

    public void setInsertClause(MergeInsertClause insertClause) {
        this.insertClause = insertClause;
    }

    public OracleErrorLoggingClause getErrorLoggingClause() {
        return errorLoggingClause;
    }

    public void setErrorLoggingClause(OracleErrorLoggingClause errorLoggingClause) {
        this.errorLoggingClause = errorLoggingClause;
    }

    public List<SQLHint> getHints() {
        return hints;
    }

    public static class MergeUpdateClause extends OracleSQLObjectImpl {

        private List<SQLUpdateSetItem> items = new ArrayList<SQLUpdateSetItem>();
        private SQLExpr                where;
        private SQLExpr                deleteWhere;

        public List<SQLUpdateSetItem> getItems() {
            return items;
        }

        public void setItems(List<SQLUpdateSetItem> items) {
            this.items = items;
        }

        public SQLExpr getWhere() {
            return where;
        }

        public void setWhere(SQLExpr where) {
            this.where = where;
        }

        public SQLExpr getDeleteWhere() {
            return deleteWhere;
        }

        public void setDeleteWhere(SQLExpr deleteWhere) {
            this.deleteWhere = deleteWhere;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, items);
                acceptChild(visitor, where);
                acceptChild(visitor, deleteWhere);
            }
            visitor.endVisit(this);
        }

    }

    public static class MergeInsertClause extends OracleSQLObjectImpl {

        private List<SQLExpr> columns = new ArrayList<SQLExpr>();
        private List<SQLExpr> values  = new ArrayList<SQLExpr>();
        private SQLExpr       where;

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, columns);
                acceptChild(visitor, columns);
                acceptChild(visitor, columns);
            }
            visitor.endVisit(this);
        }

        public List<SQLExpr> getColumns() {
            return columns;
        }

        public void setColumns(List<SQLExpr> columns) {
            this.columns = columns;
        }

        public List<SQLExpr> getValues() {
            return values;
        }

        public void setValues(List<SQLExpr> values) {
            this.values = values;
        }

        public SQLExpr getWhere() {
            return where;
        }

        public void setWhere(SQLExpr where) {
            this.where = where;
        }

    }
}
