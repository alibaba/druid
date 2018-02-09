/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;

public class Insert implements Function {

    public final static Insert instance = new Insert();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        if (x.getParameters().size() != 4) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        SQLExpr param0 = x.getParameters().get(0);
        SQLExpr param1 = x.getParameters().get(1);
        SQLExpr param2 = x.getParameters().get(2);
        SQLExpr param3 = x.getParameters().get(3);
        param0.accept(visitor);
        param1.accept(visitor);
        param2.accept(visitor);
        param3.accept(visitor);

        Object param0Value = param0.getAttributes().get(EVAL_VALUE);
        Object param1Value = param1.getAttributes().get(EVAL_VALUE);
        Object param2Value = param2.getAttributes().get(EVAL_VALUE);
        Object param3Value = param3.getAttributes().get(EVAL_VALUE);

        if (!(param0Value instanceof String)) {
            return SQLEvalVisitor.EVAL_ERROR;
        }
        if (!(param1Value instanceof Number)) {
            return SQLEvalVisitor.EVAL_ERROR;
        }
        if (!(param2Value instanceof Number)) {
            return SQLEvalVisitor.EVAL_ERROR;
        }
        if (!(param3Value instanceof String)) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        String str = (String) param0Value;
        int pos = ((Number) param1Value).intValue();
        int len = ((Number) param2Value).intValue();
        String newstr = (String) param3Value;
        
        if (pos <= 0) {
            return str;
        }
        
        if (pos == 1) {
            if (len > str.length()) {
                return newstr;
            }
            return newstr + str.substring(len);
        }
        
        String first = str.substring(0, pos - 1);
        if (pos + len - 1 > str.length()) {
            return first + newstr;
        }
        
        return first + newstr + str.substring(pos + len - 1);
    }
}
