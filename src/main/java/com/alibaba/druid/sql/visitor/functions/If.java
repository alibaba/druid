/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_ERROR;
import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;

public class If implements Function {

    public final static If instance = new If();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        final List<SQLExpr> parameters = x.getParameters();
        if (parameters.size() == 0) {
            return EVAL_ERROR;
        }

        SQLExpr condition = parameters.get(0);
        condition.accept(visitor);
        Object itemValue = condition.getAttributes().get(EVAL_VALUE);
        if (itemValue == null) {
            return null;
        }
        if (Boolean.TRUE == itemValue || !SQLEvalVisitorUtils.eq(itemValue, 0)) {
            SQLExpr trueExpr = parameters.get(1);
            trueExpr.accept(visitor);
            return trueExpr.getAttributes().get(EVAL_VALUE);
        } else {
            SQLExpr falseExpr = parameters.get(2);
            falseExpr.accept(visitor);
            return falseExpr.getAttributes().get(EVAL_VALUE);
        }
    }
}
