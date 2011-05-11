/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleExtractExpr extends SQLExprImpl {
    private OracleDateTimeUnit unit;
    private SQLExpr from;

    public OracleExtractExpr() {

    }

    public OracleDateTimeUnit getUnit() {
        return this.unit;
    }

    public void setUnit(OracleDateTimeUnit unit) {
        this.unit = unit;
    }

    public SQLExpr getFrom() {
        return this.from;
    }

    public void setFrom(SQLExpr from) {
        this.from = from;
    }

    public void output(StringBuffer buf) {
        buf.append("EXTRACT(");
        buf.append(this.unit.name());
        buf.append(" FROM ");
        this.from.output(buf);
        buf.append(")");
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.from);
        }

        visitor.endVisit(this);
    }
}
