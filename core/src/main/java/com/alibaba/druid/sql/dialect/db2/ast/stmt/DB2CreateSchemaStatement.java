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
package com.alibaba.druid.sql.dialect.db2.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.db2.ast.DB2Statement;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class DB2CreateSchemaStatement extends SQLStatementImpl implements DB2Statement, SQLCreateStatement {
    private SQLName schemaName;
    private List<SQLCreateStatement> createStatements = new ArrayList<>();

    public SQLName getName() {
        return this.getSchemaName();
    }

    public SQLName getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(SQLName schemaName) {
        this.schemaName = schemaName;
    }

    public List<SQLCreateStatement> getCreateStatements() {
        return createStatements;
    }

    public void setCreateStatements(List<SQLCreateStatement> createStatements) {
        this.createStatements = createStatements;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof DB2ASTVisitor) {
            accept0((DB2ASTVisitor) visitor);
        }
    }

    @Override
    public void accept0(DB2ASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.schemaName);
        }

        if (this.createStatements != null && !this.createStatements.isEmpty()) {
            for (SQLCreateStatement stat : this.createStatements) {
                acceptChild(visitor, stat);
            }
        }

        visitor.endVisit(this);
    }
}
