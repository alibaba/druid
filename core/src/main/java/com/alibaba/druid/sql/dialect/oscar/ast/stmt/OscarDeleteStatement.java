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
package com.alibaba.druid.sql.dialect.oscar.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.dialect.oscar.visitor.OscarASTVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OscarDeleteStatement extends SQLDeleteStatement implements OscarStatement {
    private boolean returning;

    public OscarDeleteStatement() {
        super(DbType.oscar);
    }

    public boolean isReturning() {
        return returning;
    }

    public void setReturning(boolean returning) {
        this.returning = returning;
    }

    public String getAlias() {
        if (tableSource == null) {
            return null;
        }
        return tableSource.getAlias();
    }

    public void setAlias(String alias) {
        this.tableSource.setAlias(alias);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PGASTVisitor) {
            accept0((PGASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    @Override
    public void accept0(OscarASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, with);
            acceptChild(visitor, tableSource);
            acceptChild(visitor, using);
            acceptChild(visitor, where);
        }

        visitor.endVisit(this);
    }

    public OscarDeleteStatement clone() {
        OscarDeleteStatement x = new OscarDeleteStatement();
        cloneTo(x);

        x.returning = returning;

        return x;
    }
}
