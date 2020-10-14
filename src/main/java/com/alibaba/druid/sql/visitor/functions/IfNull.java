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
package com.alibaba.druid.sql.visitor.functions;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;

import java.util.List;

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_ERROR;
import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

public class IfNull implements Function {

    public final static IfNull instance = new IfNull();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        final List<SQLExpr> arguments = x.getArguments();
        if (arguments.size() != 2) {
            return EVAL_ERROR;
        }

        SQLExpr condition = arguments.get(0);
        condition.accept(visitor);

        Object itemValue = condition.getAttributes().get(EVAL_VALUE);
        if (itemValue == null) {
            SQLExpr valueExpr = arguments.get(1);
            valueExpr.accept(visitor);
            return valueExpr.getAttributes().get(EVAL_VALUE);
        } else  {
            return itemValue;
        }
    }

    public Object eval(SQLMethodInvokeExpr x) {
        List<SQLExpr> arguments = x.getArguments();

        if (arguments.size() != 2) {
            return EVAL_ERROR;
        }

        SQLExpr condition = arguments.get(0);
        SQLExpr valueExpr = arguments.get(1);

        if (condition instanceof SQLValuableExpr && valueExpr instanceof SQLValuableExpr) {
            Object itemValue = ((SQLValuableExpr) condition).getValue();
            if (itemValue == null || itemValue == SQLEvalVisitor.EVAL_VALUE_NULL) {
                return ((SQLValuableExpr) valueExpr).getValue();
            } else {
                return itemValue;
            }
        }
        return EVAL_ERROR;
    }
}
