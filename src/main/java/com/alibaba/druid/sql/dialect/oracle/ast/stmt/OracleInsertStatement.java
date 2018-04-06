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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.statement.SQLErrorLoggingClause;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class OracleInsertStatement extends SQLInsertStatement implements OracleStatement {

    private OracleReturningClause returning;
    private SQLErrorLoggingClause errorLogging;
    private List<SQLHint>         hints = new ArrayList<SQLHint>();

    public OracleInsertStatement() {
        dbType = JdbcConstants.ORACLE;
    }

    public void cloneTo(OracleInsertStatement x) {
        super.cloneTo(x);
        if (returning != null) {
            x.setReturning(returning.clone());
        }
        if (errorLogging != null) {
            x.setErrorLogging(errorLogging.clone());
        }
        for (SQLHint hint : hints) {
            SQLHint h2 = hint.clone();
            h2.setParent(x);
            x.hints.add(h2);
        }
    }

    public List<SQLHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLHint> hints) {
        this.hints = hints;
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

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, getTableSource());
            this.acceptChild(visitor, getColumns());
            this.acceptChild(visitor, getValues());
            this.acceptChild(visitor, getQuery());
            this.acceptChild(visitor, returning);
            this.acceptChild(visitor, errorLogging);
        }

        visitor.endVisit(this);
    }
    
    public void output(StringBuffer buf) {
    	new OracleOutputVisitor(buf).visit(this);
    }

    public OracleInsertStatement clone() {
        OracleInsertStatement x = new OracleInsertStatement();
        cloneTo(x);
        return x;
    }
}
