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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLCreateViewStatement extends SQLStatementImpl implements SQLCreateStatement {

    private boolean     orReplace   = false;
    private boolean     force       = false;
    // protected SQLName   name;
    protected SQLSelect subQuery;
    protected boolean   ifNotExists = false;

    protected String    algorithm;
    protected SQLName   definer;
    protected String    sqlSecurity;

    protected SQLExprTableSource tableSource;

    protected final List<SQLTableElement> columns = new ArrayList<SQLTableElement>();

    private boolean withCheckOption;
    private boolean withCascaded;
    private boolean withLocal;
    private boolean withReadOnly;

    private SQLLiteralExpr comment;

    public SQLCreateViewStatement(){

    }

    public SQLCreateViewStatement(String dbType){
        super(dbType);
    }

    public String computeName() {
        if (tableSource == null) {
            return null;
        }

        SQLExpr expr = tableSource.getExpr();
        if (expr instanceof SQLName) {
            String name = ((SQLName) expr).getSimpleName();
            return SQLUtils.normalize(name);
        }

        return null;
    }

    public String getSchema() {
        SQLName name = getName();
        if (name == null) {
            return null;
        }

        if (name instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) name).getOwnernName();
        }

        return null;
    }

    public boolean isOrReplace() {
        return orReplace;
    }

    public void setOrReplace(boolean orReplace) {
        this.orReplace = orReplace;
    }

    public SQLName getName() {
        if (tableSource == null) {
            return null;
        }

        return (SQLName) tableSource.getExpr();
    }

    public void setName(SQLName name) {
        this.setTableSource(new SQLExprTableSource(name));
    }

    public void setName(String name) {
        this.setName(new SQLIdentifierExpr(name));
    }

    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSource = tableSource;
    }

    public boolean isWithCheckOption() {
        return withCheckOption;
    }

    public void setWithCheckOption(boolean withCheckOption) {
        this.withCheckOption = withCheckOption;
    }

    public boolean isWithCascaded() {
        return withCascaded;
    }

    public void setWithCascaded(boolean withCascaded) {
        this.withCascaded = withCascaded;
    }

    public boolean isWithLocal() {
        return withLocal;
    }

    public void setWithLocal(boolean withLocal) {
        this.withLocal = withLocal;
    }

    public boolean isWithReadOnly() {
        return withReadOnly;
    }

    public void setWithReadOnly(boolean withReadOnly) {
        this.withReadOnly = withReadOnly;
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

    public List<SQLTableElement> getColumns() {
        return columns;
    }
    
    public void addColumn(SQLTableElement column) {
        if (column != null) {
            column.setParent(this);
        }
        this.columns.add(column);
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }

    public SQLLiteralExpr getComment() {
        return comment;
    }

    public void setComment(SQLLiteralExpr comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public SQLName getDefiner() {
        return definer;
    }

    public void setDefiner(SQLName definer) {
        if (definer != null) {
            definer.setParent(this);
        }
        this.definer = definer;
    }

    public String getSqlSecurity() {
        return sqlSecurity;
    }

    public void setSqlSecurity(String sqlSecurity) {
        this.sqlSecurity = sqlSecurity;
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(boolean force) {
        this.force = force;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.tableSource);
            acceptChild(visitor, this.columns);
            acceptChild(visitor, this.comment);
            acceptChild(visitor, this.subQuery);
        }
        visitor.endVisit(this);
    }

    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (tableSource != null) {
            children.add(tableSource);
        }
        children.addAll(this.columns);
        if (comment != null) {
            children.add(comment);
        }
        if (subQuery != null) {
            children.add(subQuery);
        }
        return children;
    }

    public static enum Level {
                              CASCADED, LOCAL
    }

    public static class Column extends SQLObjectImpl {

        private SQLExpr     expr;
        private SQLCharExpr comment;

        public SQLExpr getExpr() {
            return expr;
        }

        public void setExpr(SQLExpr expr) {
            if (expr != null) {
                expr.setParent(this);
            }
            this.expr = expr;
        }

        public SQLCharExpr getComment() {
            return comment;
        }

        public void setComment(SQLCharExpr comment) {
            if (comment != null) {
                comment.setParent(this);
            }
            this.comment = comment;
        }

        @Override
        protected void accept0(SQLASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, expr);
                acceptChild(visitor, comment);
            }
        }
    }


    public SQLCreateViewStatement clone() {
        SQLCreateViewStatement x = new SQLCreateViewStatement();

        x.orReplace = orReplace;
        x.force = force;
        if (subQuery != null) {
            x.setSubQuery(subQuery.clone());
        }
        x.ifNotExists = ifNotExists;

        x.algorithm = algorithm;
        if (definer != null) {
            x.setDefiner(definer.clone());
        }
        x.sqlSecurity = sqlSecurity;
        if (tableSource != null) {
            x.setTableSource(tableSource.clone());
        }
        for (SQLTableElement column : columns) {
            SQLTableElement column2 = column.clone();
            column2.setParent(x);
            x.columns.add(column2);
        }
        x.withCheckOption = withCheckOption;
        x.withCascaded = withCascaded;
        x.withLocal = withLocal;
        x.withReadOnly = withReadOnly;

        if (comment != null) {
            x.setComment(comment.clone());
        }

        return x;
    }
}
