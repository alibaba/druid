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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLExecuteImmediateStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class MySqlExecuteStatement extends SQLExecuteImmediateStatement implements MySqlStatement {
    private final List<SQLExpr> parameters = new ArrayList<SQLExpr>();

    public SQLName getStatementName() {
        return (SQLName) dynamicSql;
    }

    public void setStatementName(SQLName x) {
        super.setDynamicSql(x);
    }

    public List<SQLExpr> getParameters() {
        return parameters;
    }

    public void accept0(MySqlASTVisitor v) {
        if (v.visit(this)) {
            super.acceptChild(v);
            acceptChild(v, parameters);
        }
        v.endVisit(this);
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof MySqlASTVisitor) {
            ((MySqlASTVisitor) v).visit(this);
        } else {
            super.accept0(v);
        }
    }
}
