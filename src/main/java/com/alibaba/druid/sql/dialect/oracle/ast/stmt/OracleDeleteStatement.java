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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleDeleteStatement extends SQLDeleteStatement {
    private final List<SQLHint>   hints     = new ArrayList<SQLHint>();
    private OracleReturningClause returning = null;

    public OracleDeleteStatement(){
        super (DbType.oracle);
    }

    public OracleReturningClause getReturning() {
        return returning;
    }

    public void setReturning(OracleReturningClause returning) {
        this.returning = returning;
    }

    public List<SQLHint> getHints() {
        return this.hints;
    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.hints);
            acceptChild(visitor, this.tableSource);
            acceptChild(visitor, this.getWhere());
            acceptChild(visitor, returning);
        }

        visitor.endVisit(this);
    }

    public OracleDeleteStatement clone() {
        OracleDeleteStatement x = new OracleDeleteStatement();
        cloneTo(x);

        for (SQLHint hint : hints) {
            SQLHint hint2 = hint.clone();
            hint2.setParent(x);
            x.hints.add(hint2);
        }
        if (returning != null) {
            x.setReturning(returning.clone());
        }

        return x;
    }

}
