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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLMergeStatement extends SQLStatementImpl {

    private final List<SQLHint>      hints = new ArrayList<SQLHint>();

    private SQLTableSource           into;
    private String                   alias;
    private SQLTableSource           using;
    private SQLExpr                  on;
    private MergeUpdateClause        updateClause;
    private MergeInsertClause        insertClause;
    private SQLErrorLoggingClause errorLoggingClause;

    public void accept0(SQLASTVisitor visitor) {
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

    public SQLErrorLoggingClause getErrorLoggingClause() {
        return errorLoggingClause;
    }

    public void setErrorLoggingClause(SQLErrorLoggingClause errorLoggingClause) {
        this.errorLoggingClause = errorLoggingClause;
    }

    public List<SQLHint> getHints() {
        return hints;
    }

    public static class MergeUpdateClause extends SQLObjectImpl {

        private List<SQLUpdateSetItem> items = new ArrayList<SQLUpdateSetItem>();
        private SQLExpr                where;
        private SQLExpr                deleteWhere;

        public List<SQLUpdateSetItem> getItems() {
            return items;
        }

        public void addItem(SQLUpdateSetItem item) {
            if (item != null) {
                item.setParent(this);
            }
            this.items.add(item);
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
        public void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, items);
                acceptChild(visitor, where);
                acceptChild(visitor, deleteWhere);
            }
            visitor.endVisit(this);
        }

    }

    public static class MergeInsertClause extends SQLObjectImpl {

        private List<SQLExpr> columns = new ArrayList<SQLExpr>();
        private List<SQLExpr> values  = new ArrayList<SQLExpr>();
        private SQLExpr       where;

        @Override
        public void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, columns);
                acceptChild(visitor, values);
                acceptChild(visitor, where);
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
