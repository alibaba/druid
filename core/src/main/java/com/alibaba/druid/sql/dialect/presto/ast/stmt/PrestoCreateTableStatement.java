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
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PrestoCreateTableStatement extends SQLCreateTableStatement implements PrestoSQLStatement {
    public PrestoCreateTableStatement() {
        this.dbType = DbType.hive;
    }

    public PrestoCreateTableStatement(DbType dbType) {
        this.dbType = dbType;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof PrestoVisitor) {
            this.accept0((PrestoVisitor) v);
        }
        super.accept0(v);
    }

    @Override
    public void accept0(PrestoVisitor visitor) {
        visitor.visit(this);
    }

    protected void acceptChild(SQLASTVisitor v) {
        super.acceptChild(v);
    }

    public void cloneTo(PrestoCreateTableStatement x) {
        super.cloneTo(x);
    }

    public PrestoCreateTableStatement clone() {
        PrestoCreateTableStatement x = new PrestoCreateTableStatement();
        cloneTo(x);
        return x;
    }
}
