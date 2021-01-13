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
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.util.HexBin;

import java.io.UnsupportedEncodingException;

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_EXPR;
import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

public class Unhex implements Function {

    public final static Unhex instance = new Unhex();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        if (x.getArguments().size() != 1) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        SQLExpr param0 = x.getArguments().get(0);

        if (param0 instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr paramMethodExpr = (SQLMethodInvokeExpr) param0;
            if (paramMethodExpr.getMethodName().equalsIgnoreCase("hex")) {
                SQLExpr subParamExpr = paramMethodExpr.getArguments().get(0);
                subParamExpr.accept(visitor);

                Object param0Value = subParamExpr.getAttributes().get(EVAL_VALUE);
                if (param0Value == null) {
                    x.putAttribute(EVAL_EXPR, subParamExpr);
                    return SQLEvalVisitor.EVAL_ERROR;
                }

                return param0Value;
            }
        }

        param0.accept(visitor);

        Object param0Value = param0.getAttributes().get(EVAL_VALUE);
        if (param0Value == null) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        if (param0Value instanceof String) {
            byte[] bytes = HexBin.decode((String) param0Value);
            if (bytes == null) {
                return SQLEvalVisitor.EVAL_VALUE_NULL;
            }
            
            String result;
            try {
                result = new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
            return result;
        }

        return SQLEvalVisitor.EVAL_ERROR;
    }
}
