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
import com.alibaba.druid.sql.ast.expr.SQLDateExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLEvalVisitor;
import com.alibaba.druid.sql.visitor.SQLEvalVisitorUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_ERROR;
import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

public class ToDate implements Function {

    public final static ToDate instance = new ToDate();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        final List<SQLExpr> arguments = x.getArguments();
        if (arguments.size() == 0) {
            return EVAL_ERROR;
        }

        if (arguments.size() == 2
                && arguments.get(0) instanceof SQLCharExpr
                && arguments.get(1) instanceof SQLCharExpr) {
            String chars = ((SQLCharExpr) arguments.get(0)).getText();
            String format = ((SQLCharExpr) arguments.get(1)).getText();
            if (format.equals("yyyymmdd")) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                try {
                    return dateFormat.parse(chars);
                } catch (ParseException e) {
                    // skip
                    return false;
                }
            }
        }

        return null;
    }
}
