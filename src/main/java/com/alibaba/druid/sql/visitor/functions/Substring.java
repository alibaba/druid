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
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLValuableExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;

import java.util.List;

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

public class Substring implements Function {

    public final static Substring instance = new Substring();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        List<SQLExpr> params = x.getArguments();
        int paramSize = params.size();

        SQLExpr param0 = params.get(0);

        SQLExpr param1;
        if (paramSize == 1 && x.getFrom() != null) {
            param1 = x.getFrom();
            paramSize = 2;
        } else if (paramSize != 2 && paramSize != 3) {
            return SQLEvalVisitor.EVAL_ERROR;
        } else {
            param1 = params.get(1);
        }

        param0.accept(visitor);
        param1.accept(visitor);

        Object param0Value = param0.getAttributes().get(EVAL_VALUE);
        Object param1Value = param1.getAttributes().get(EVAL_VALUE);
        if (param0Value == null || param1Value == null) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        String str = param0Value.toString();
        int index = ((Number) param1Value).intValue();

        if (paramSize == 2 && x.getFor() == null) {
            if (index <= 0) {
                int lastIndex = str.length() + index;
                return str.substring(lastIndex);
            }

            return str.substring(index - 1);
        }

        SQLExpr param2 = x.getFor();
        if (param2 == null && params.size() > 2) {
            param2 = params.get(2);
        }
        param2.accept(visitor);
        Object param2Value = param2.getAttributes().get(EVAL_VALUE);
        if (param2Value == null) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        int len = ((Number) param2Value).intValue();

        String result;
        if (index <= 0) {
            int lastIndex = str.length() + index;
            result = str.substring(lastIndex);
        } else {
            result = str.substring(index - 1);
        }

        if (len > result.length()) {
            return result;
        }
        return result.substring(0, len);
    }

    public Object eval(SQLMethodInvokeExpr x) {
        List<SQLExpr> parameters = x.getArguments();
        for (SQLExpr parameter : parameters) {
            if (!(parameter instanceof SQLValuableExpr)){
                return null;
            }
        }

        if (parameters.size() == 3) {
            Object p0 = ((SQLValuableExpr) parameters.get(0)).getValue();
            Object p1 = ((SQLValuableExpr) parameters.get(1)).getValue();
            Object p2 = ((SQLValuableExpr) parameters.get(2)).getValue();

            if (p0 instanceof Number) {
                p0 = p0.toString();
            }

            if (p0 instanceof String
                    && p1 instanceof Integer
                    && p2 instanceof Integer) {
                String str = (String) p0;
                int beginIndex = ((Integer) p1).intValue();
                int len = ((Integer) p2).intValue();
                if (len < 0) {
                    return null;
                }

                int start = beginIndex - 1, end = beginIndex - 1 + len;

                if (start < 0 || start >= str.length()) {
                    return null;
                }
                if (end < 0 || end >= str.length()) {
                    return null;
                }

                if (beginIndex > 0 && len > 0) {
                    return str.substring(start, end);
                }
            }
        } else if (parameters.size() == 2) {
            Object p0 = ((SQLValuableExpr) parameters.get(0)).getValue();
            Object p1 = ((SQLValuableExpr) parameters.get(1)).getValue();

            if (p0 instanceof String
                    && p1 instanceof Integer) {
                String str = (String) p0;
                int beginIndex = ((Integer) p1).intValue();
                if (beginIndex < 0 || beginIndex >= str.length()) {
                    return null;
                }
                if (beginIndex > 0) {
                    return str.substring(beginIndex - 1);
                }
            }
        }

        return SQLEvalVisitor.EVAL_ERROR;
    }
}
