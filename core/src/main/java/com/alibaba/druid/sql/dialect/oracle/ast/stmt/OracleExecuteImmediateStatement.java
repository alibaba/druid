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

import com.alibaba.druid.sql.ast.SQLArgument;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExecuteImmediateStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleExecuteImmediateStatement extends SQLExecuteImmediateStatement implements OracleStatement {
    private final List<SQLArgument> arguments = new ArrayList<SQLArgument>();

    private final List<SQLExpr> returnInto = new ArrayList<SQLExpr>();

    public OracleExecuteImmediateStatement() {
    }

    public OracleExecuteImmediateStatement(String dynamicSql) {
        this.setDynamicSql(dynamicSql);
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof OracleASTVisitor) {
            accept0((OracleASTVisitor) v);
            return;
        }
        super.accept0(v);
    }

    @Override
    public void accept0(OracleASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v);
        }
        v.endVisit(this);
    }

    protected OracleExecuteImmediateStatement cloneTo(OracleExecuteImmediateStatement x) {
        super.cloneTo(x);

        for (SQLArgument arg : arguments) {
            SQLArgument a2 = arg.clone();
            a2.setParent(x);
            x.arguments.add(a2);
        }

        for (SQLExpr e : returnInto) {
            SQLExpr e2 = e.clone();
            e2.setParent(x);
            x.returnInto.add(e2);
        }
        return x;
    }

    public OracleExecuteImmediateStatement clone() {
        OracleExecuteImmediateStatement x = new OracleExecuteImmediateStatement();
        cloneTo(x);
        return x;
    }

    public List<SQLArgument> getArguments() {
        return arguments;
    }

    public List<SQLExpr> getReturnInto() {
        return returnInto;
    }
}
