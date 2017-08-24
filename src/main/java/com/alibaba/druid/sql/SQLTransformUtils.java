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
package com.alibaba.druid.sql;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;

import java.util.List;

public class SQLTransformUtils {
    public static SQLExpr transformDecode(SQLMethodInvokeExpr x) {
        if (x == null) {
            return null;
        }

        if (!"decode".equalsIgnoreCase(x.getMethodName())) {
            throw new IllegalArgumentException(x.getMethodName());
        }

        List<SQLExpr> parameters = x.getParameters();
        SQLCaseExpr caseExpr = new SQLCaseExpr();
        caseExpr.setParent(x.getParent());
        caseExpr.setValueExpr(parameters.get(0));

        if (parameters.size() == 4) {
            SQLExpr param1 = parameters.get(1);

            x.setMethodName("if");

            SQLBinaryOpExpr condition;
            if (param1 instanceof SQLNullExpr) {
                condition = new SQLBinaryOpExpr(parameters.get(0), SQLBinaryOperator.Is, param1);
            } else {
                condition = new SQLBinaryOpExpr(parameters.get(0), SQLBinaryOperator.Equality, param1);
            }
            condition.setParent(x);
            parameters.set(0, condition);
            parameters.set(1, parameters.get(2));
            parameters.set(2, parameters.get(3));
            parameters.remove(3);
            return x;
        }

        for (int i = 1; i + 1 < parameters.size(); i += 2) {
            SQLCaseExpr.Item item = new SQLCaseExpr.Item();
            SQLExpr conditionExpr = parameters.get(i);

            item.setConditionExpr(conditionExpr);

            SQLExpr valueExpr = parameters.get(i + 1);

            if (valueExpr instanceof SQLMethodInvokeExpr) {
                SQLMethodInvokeExpr methodInvokeExpr = (SQLMethodInvokeExpr) valueExpr;
                if ("decode".equalsIgnoreCase(methodInvokeExpr.getMethodName())) {
                    valueExpr = transformDecode(methodInvokeExpr);
                }
            }

            item.setValueExpr(valueExpr);
            caseExpr.addItem(item);
        }

        if (parameters.size() % 2 == 0) {
            SQLExpr defaultExpr = parameters.get(parameters.size() - 1);

            if (defaultExpr instanceof SQLMethodInvokeExpr) {
                SQLMethodInvokeExpr methodInvokeExpr = (SQLMethodInvokeExpr) defaultExpr;
                if ("decode".equalsIgnoreCase(methodInvokeExpr.getMethodName())) {
                    defaultExpr = transformDecode(methodInvokeExpr);
                }
            }

            caseExpr.setElseExpr(defaultExpr);
        }

        return caseExpr;
    }
}
