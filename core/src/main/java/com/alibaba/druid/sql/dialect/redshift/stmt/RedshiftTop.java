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
package com.alibaba.druid.sql.dialect.redshift.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.dialect.redshift.visitor.RedshiftASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class RedshiftTop extends RedshiftObjectImpl implements SQLReplaceable {
    private SQLExpr expr;

    private boolean withTies;
    private boolean parentheses;

    public RedshiftTop() {
    }

    public RedshiftTop(SQLExpr expr) {
        this.setExpr(expr);
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    public void setExpr(int expr) {
        this.setExpr(new SQLIntegerExpr(expr));
    }

    public boolean isWithTies() {
        return withTies;
    }

    public void setWithTies(boolean withTies) {
        this.withTies = withTies;
    }

    public boolean isParentheses() {
        return parentheses;
    }

    public void setParentheses(boolean parentheses) {
        this.parentheses = parentheses;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof RedshiftASTVisitor) {
            accept0((RedshiftASTVisitor) v);
        }
    }

    public RedshiftTop clone() {
        RedshiftTop x = new RedshiftTop();
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        x.withTies = withTies;
        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (expr == this.expr) {
            this.expr = target;
        }
        return false;
    }

    @Override
    public void accept0(RedshiftASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
