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
package com.alibaba.druid.sql.dialect.oscar.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.dialect.oscar.visitor.OscarASTVisitor;

public class OscarTop extends OscarObjectImpl {
    private SQLExpr expr;
    private boolean percent;
    private boolean withTies;

    public OscarTop() {
    }

    @Override
    public void accept0(OscarASTVisitor visitor) {
    }

    public OscarTop(SQLExpr expr) {
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

    public boolean isPercent() {
        return percent;
    }

    public void setPercent(boolean percent) {
        this.percent = percent;
    }

    public boolean isWithTies() {
        return withTies;
    }

    public void setWithTies(boolean withTies) {
        this.withTies = withTies;
    }

    public OscarTop clone() {
        OscarTop x = new OscarTop();
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        x.percent = percent;
        x.withTies = withTies;
        return x;
    }
}
