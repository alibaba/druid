/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.postgresql.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGAttrExpr;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class PGCreateDatabaseStatement extends SQLStatementImpl implements PGSQLStatement, SQLCreateStatement {
    private SQLName name;
    private boolean haveWith;
    private List<PGAttrExpr> stats = new ArrayList<>();

    public PGCreateDatabaseStatement(DbType dbType) {
        super(dbType);
    }

    @Override
    public SQLName getName() { return this.name; }

    public void setName(SQLName dbName) { this.name = dbName; }

    public boolean isHaveWith() { return haveWith; }

    public void setHaveWith(boolean haveWith) { this.haveWith = haveWith; }

    public List<PGAttrExpr> getStats() { return stats; }

    public void setStats(List<PGAttrExpr> stats) { this.stats = stats; }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PGASTVisitor) {
            accept0((PGASTVisitor) visitor);
        }
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.name);
            acceptChild(visitor, this.stats);
        }

        visitor.endVisit(this);
    }
}
