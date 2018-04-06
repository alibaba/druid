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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;

public class SQLExprUtils {

    public static boolean equals(SQLExpr a, SQLExpr b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        Class<?> clazz_a = a.getClass();
        Class<?> clazz_b = b.getClass();
        if (clazz_a != clazz_b) {
            return false;
        }

        if (clazz_a == SQLIdentifierExpr.class) {
            SQLIdentifierExpr x_a = (SQLIdentifierExpr) a;
            SQLIdentifierExpr x_b = (SQLIdentifierExpr) b;
            return x_a.hashCode() == x_b.hashCode();
        }

        if (clazz_a == SQLBinaryOpExpr.class) {
            SQLBinaryOpExpr x_a = (SQLBinaryOpExpr) a;
            SQLBinaryOpExpr x_b = (SQLBinaryOpExpr) b;

            return x_a.equals(x_b);
        }

        return a.equals(b);
    }

    public static boolean isLiteralExpr(SQLExpr expr) {
        if (expr instanceof SQLLiteralExpr) {
            return true;
        }

        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binary = (SQLBinaryOpExpr) expr;
            return isLiteralExpr(binary.left) && isLiteralExpr(binary.right);
        }

        return false;
    }
}
