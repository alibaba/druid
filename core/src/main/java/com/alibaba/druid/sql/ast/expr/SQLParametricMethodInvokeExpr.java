/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Ver[Ision 2.0 (the "License");
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

import com.alibaba.druid.sql.ast.SQLExpr;

import java.util.ArrayList;
import java.util.List;

public class SQLParametricMethodInvokeExpr extends SQLMethodInvokeExpr {
    private static final long serialVersionUID = 1L;

    protected final List<SQLExpr> secondArguments = new ArrayList<>();

    public SQLParametricMethodInvokeExpr() {
    }

    public SQLParametricMethodInvokeExpr(String methodName) {
        super(methodName);
    }

    public SQLParametricMethodInvokeExpr(String methodName, long methodNameHashCode64) {
        super(methodName, methodNameHashCode64);
    }

    public SQLParametricMethodInvokeExpr(String methodName, SQLExpr owner) {
        super(methodName, owner);
    }

    public SQLParametricMethodInvokeExpr(String methodName, SQLExpr owner, SQLExpr... params) {
        super(methodName, owner, params);
    }

    public SQLParametricMethodInvokeExpr(String methodName, SQLExpr owner, List<SQLExpr> params) {
        super(methodName, owner, params);
    }

    @Override
    public SQLParametricMethodInvokeExpr clone() {
        SQLParametricMethodInvokeExpr x = new SQLParametricMethodInvokeExpr();
        cloneTo(x);
        return x;
    }

    @Override
    public void cloneTo(SQLMethodInvokeExpr x) {
        super.cloneTo(x);
        for (SQLExpr arg : secondArguments) {
            ((SQLParametricMethodInvokeExpr) x).addSecondArgument(arg.clone());
        }
    }

    public List<SQLExpr> getSecondArguments() {
        return secondArguments;
    }

    public void addSecondArgument(SQLExpr arg) {
        if (arg != null) {
            arg.setParent(this);
        }
        this.secondArguments.add(arg);
    }
}
