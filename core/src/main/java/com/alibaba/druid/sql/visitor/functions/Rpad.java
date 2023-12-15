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

import java.util.List;

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

/**
 * @author lizongbo
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_rpad">MySQL rapd</a>
 */
public class Rpad implements Function {
    public static final Rpad instance = new Rpad();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        List<SQLExpr> params = x.getArguments();
        int paramSize = params.size();
        if (paramSize != 3) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        SQLExpr param0 = params.get(0);
        SQLExpr param1 = params.get(1);
        SQLExpr param2 = params.get(2);

        param0.accept(visitor);
        param1.accept(visitor);
        param2.accept(visitor);

        Object param0Value = param0.getAttributes().get(EVAL_VALUE);
        Object param1Value = param1.getAttributes().get(EVAL_VALUE);
        Object param2Value = param2.getAttributes().get(EVAL_VALUE);
        if (param0Value == null || param1Value == null || param2Value == null) {
            return SQLEvalVisitor.EVAL_ERROR;
        }

        String strValue0 = param0Value.toString();
        int len = ((Number) param1Value).intValue();
        String strValue1 = param2Value.toString();

        if (strValue0.length() > len) {
            return strValue0.substring(0, len);
        }
        if (strValue0.length() == len) {
            return strValue0;
        }
        StringBuilder result = new StringBuilder(strValue0);
        while (result.length() < len) {
            result.append(strValue1);
        }
        if (result.length() > len) {
            result.delete(len, result.length());
        }
        return result.toString();
    }
}
