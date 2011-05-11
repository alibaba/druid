/*
 * Copyright 2011 Alibaba Group. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLUnaryExpr extends SQLExprImpl implements Serializable {

    private static final long serialVersionUID = 1L;
    private SQLExpr           expr;
    private SQLUnaryOperator  operator;

    public SQLUnaryExpr(){

    }

    public SQLUnaryExpr(SQLUnaryOperator operator, SQLExpr expr){
        this.operator = operator;
        this.expr = expr;
    }

    public SQLUnaryOperator getOperator() {
        return operator;
    }

    public void setOperator(SQLUnaryOperator operator) {
        this.operator = operator;
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    @Override
    public void output(StringBuffer buf) {
        buf.append(" NOT ");
        this.expr.output(buf);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }

        visitor.endVisit(this);
    }
}
