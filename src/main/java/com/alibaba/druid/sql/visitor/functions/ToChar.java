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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_ERROR;
import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

public class ToChar implements Function {

    public final static ToChar instance = new ToChar();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        final List<SQLExpr> arguments = x.getArguments();
        if (arguments.size() != 2) {
            return EVAL_ERROR;
        }

        for (SQLExpr arg : arguments) {
            arg.accept(visitor);
        }

        Object v0 = arguments.get(0).getAttributes().get(EVAL_VALUE);
        Object v1 = arguments.get(1).getAttributes().get(EVAL_VALUE);
        if (v0 instanceof Date && v1 instanceof String) {
            Date date = ((Date) v0);

            String format = ((SQLCharExpr) arguments.get(1)).getText();
            if (format.equals("yyyymmdd")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                return dateFormat.format(date);
            }
        }

        return null;
    }
}
