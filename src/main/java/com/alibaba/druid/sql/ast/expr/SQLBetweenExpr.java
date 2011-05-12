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
package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLBetweenExpr extends SQLExprImpl implements Serializable {

    private static final long serialVersionUID = 1L;
    public SQLExpr            testExpr;
    private boolean           not;
    public SQLExpr            beginExpr;
    public SQLExpr            endExpr;

    public SQLBetweenExpr(){

    }

    public SQLBetweenExpr(SQLExpr testExpr, SQLExpr beginExpr, SQLExpr endExpr){

        this.testExpr = testExpr;
        this.beginExpr = beginExpr;
        this.endExpr = endExpr;
    }

    public SQLBetweenExpr(SQLExpr testExpr, boolean not, SQLExpr beginExpr, SQLExpr endExpr){

        this.testExpr = testExpr;
        this.not = not;
        this.beginExpr = beginExpr;
        this.endExpr = endExpr;
    }

    public void output(StringBuffer buf) {
        this.testExpr.output(buf);
        if (this.not) buf.append(" NOT BETWEEN ");
        else {
            buf.append(" BETWEEN ");
        }
        this.beginExpr.output(buf);
        buf.append(" AND ");
        this.endExpr.output(buf);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.testExpr);
            acceptChild(visitor, this.beginExpr);
            acceptChild(visitor, this.endExpr);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getTestExpr() {
        return this.testExpr;
    }

    public void setTestExpr(SQLExpr testExpr) {
        this.testExpr = testExpr;
    }

    public boolean isNot() {
        return this.not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public SQLExpr getBeginExpr() {
        return this.beginExpr;
    }

    public void setBeginExpr(SQLExpr beginExpr) {
        this.beginExpr = beginExpr;
    }

    public SQLExpr getEndExpr() {
        return this.endExpr;
    }

    public void setEndExpr(SQLExpr endExpr) {
        this.endExpr = endExpr;
    }
}
