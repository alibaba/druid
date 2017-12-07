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
package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/**
 * Created by wenshao on 29/05/2017.
 */
public class SQLArgument extends SQLObjectImpl {
    private SQLParameter.ParameterType type;
    private SQLExpr expr;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, expr);
        }

        visitor.endVisit(this);
    }

    public SQLArgument clone() {
        SQLArgument x = new SQLArgument();

        x.type = type;

        if (expr != null) {
            x.setExpr(expr.clone());
        }

        return x;
    }

    public SQLParameter.ParameterType getType() {
        return type;
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setType(SQLParameter.ParameterType type) {
        this.type = type;
    }

    public void setExpr(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.expr = x;
    }
}
