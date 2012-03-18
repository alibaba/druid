/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleAggregateExpr extends SQLAggregateExpr implements Serializable, OracleExpr {

    private boolean           unique           = false;
    private static final long serialVersionUID = 1L;
    private OracleAnalytic    over;
    private boolean           ignoreNulls      = false;

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isIgnoreNulls() {
        return this.ignoreNulls;
    }

    public void setIgnoreNulls(boolean ignoreNulls) {
        this.ignoreNulls = ignoreNulls;
    }

    public OracleAggregateExpr(String methodName){
        super(methodName);
    }

    public OracleAggregateExpr(String methodName, int option){
        super(methodName, option);
    }

    public OracleAnalytic getOver() {
        return this.over;
    }

    public void setOver(OracleAnalytic over) {
        this.over = over;
    }

    public void output(StringBuffer buf) {
        buf.append(this.methodName);
        buf.append("(");
        int i = 0;
        for (int size = this.arguments.size(); i < size; ++i) {
            ((SQLExpr) this.arguments.get(i)).output(buf);
        }
        buf.append(")");

        if (this.over != null) {
            buf.append(" ");
            this.over.output(buf);
        }
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OracleASTVisitor) {
            this.accept0((OracleASTVisitor) visitor);
        } else {
            if (visitor.visit(this)) {
                acceptChild(visitor, this.arguments);
                acceptChild(visitor, this.over);
            }
            visitor.endVisit(this);
        }
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.arguments);
            acceptChild(visitor, this.over);
        }
        visitor.endVisit(this);
    }
}
