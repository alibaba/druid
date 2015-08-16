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

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;


public class Instr implements Function {
    public final static Instr instance = new Instr();
    
    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        if (x.getParameters().size() != 2) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        SQLExpr param0 = x.getParameters().get(0);
        SQLExpr param1 = x.getParameters().get(1);
        param0.accept(visitor);
        param1.accept(visitor);

        Object param0Value = param0.getAttributes().get(EVAL_VALUE);
        Object param1Value = param1.getAttributes().get(EVAL_VALUE);
        if (param0Value == null || param1Value == null) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        String strValue0 = param0Value.toString();
        String strValue1 = param1Value.toString();

        int result = strValue0.indexOf(strValue1) + 1;
        return result;
    }
}
