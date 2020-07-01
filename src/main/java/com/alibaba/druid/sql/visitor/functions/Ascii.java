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
import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE_NULL;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;

public class Ascii implements Function {

    public final static Ascii instance = new Ascii();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        if (x.getParameters().size() == 0) {
            return SQLEvalVisitor.EVAL_ERROR;
        }
        SQLExpr param = x.getParameters().get(0);
        param.accept(visitor);
        
        Object paramValue = param.getAttributes().get(EVAL_VALUE);
        if (paramValue == null) {
            return SQLEvalVisitor.EVAL_ERROR;
        }
        
        if (paramValue == EVAL_VALUE_NULL) {
            return EVAL_VALUE_NULL;
        }

        String strValue = paramValue.toString();
        if (strValue.length() == 0) {
            return 0;
        }

        int ascii = strValue.charAt(0);
        return ascii;
    }
}
