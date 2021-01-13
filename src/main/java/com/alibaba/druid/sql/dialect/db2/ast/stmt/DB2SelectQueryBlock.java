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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.DB2Object;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2ASTVisitor;
import com.alibaba.druid.sql.dialect.db2.visitor.DB2OutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class DB2SelectQueryBlock extends SQLSelectQueryBlock implements DB2Object {
    private Isolation isolation;

    private boolean   forReadOnly;

    private SQLExpr   optimizeFor;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof DB2ASTVisitor) {
            accept0((DB2ASTVisitor) visitor);
            return;
        }

        super.accept0(visitor);
    }

    @Override
    public void accept0(DB2ASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.selectList);
            acceptChild(visitor, this.from);
            acceptChild(visitor, this.where);
            acceptChild(visitor, this.groupBy);
            acceptChild(visitor, this.getFirst());
        }
        visitor.endVisit(this);
    }

    public DB2SelectQueryBlock() {
        dbType = DbType.db2;
    }

    public Isolation getIsolation() {
        return isolation;
    }

    public void setIsolation(Isolation isolation) {
        this.isolation = isolation;
    }

    public boolean isForReadOnly() {
        return forReadOnly;
    }

    public void setForReadOnly(boolean forReadOnly) {
        this.forReadOnly = forReadOnly;
    }

    public SQLExpr getOptimizeFor() {
        return optimizeFor;
    }

    public void setOptimizeFor(SQLExpr optimizeFor) {
        this.optimizeFor = optimizeFor;
    }

    public static enum Isolation {
        RR, RS, CS, UR
    }

    public void limit(int rowCount, int offset) {
        if (offset <= 0) {
            setFirst(new SQLIntegerExpr(rowCount));
        } else {
            throw new UnsupportedOperationException("not support offset");
        }
    }

    public void output(Appendable buf) {
        this.accept(
                new DB2OutputVisitor(buf)
        );
    }
}
