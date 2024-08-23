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
package com.alibaba.druid.sql.dialect.presto.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class PrestoExecuteStatement extends SQLStatementImpl implements PrestoSQLStatement {
    private SQLName statementName;
    private final List<SQLExpr> parameters = new ArrayList<SQLExpr>();

    public PrestoExecuteStatement() {
        dbType = DbType.presto;
    }

    public SQLName getStatementName() {
        return statementName;
    }

    public void setStatementName(SQLName statementName) {
        this.statementName = statementName;
    }

    public List<SQLExpr> getParameters() {
        return parameters;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof PrestoASTVisitor) {
            this.accept0((PrestoASTVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    @Override
    public void accept0(PrestoASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, statementName);
            acceptChild(visitor, parameters);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (statementName != null) {
            children.add(statementName);
        }
        children.addAll(this.parameters);
        return children;
    }
}
