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

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterStatement;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PrestoAlterSchemaStatement extends SQLStatementImpl implements PrestoSQLStatement, SQLAlterStatement {
    private SQLName schemaName;

    private SQLIdentifierExpr newName;

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof PrestoVisitor) {
            this.accept0((PrestoVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    @Override
    public void accept0(PrestoVisitor visitor) {
        visitor.visit(this);
    }

    public SQLName getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(SQLName schemaName) {
        this.schemaName = schemaName;
    }

    public SQLIdentifierExpr getNewName() {
        return newName;
    }

    public void setNewName(SQLIdentifierExpr newName) {
        this.newName = newName;
    }
}
