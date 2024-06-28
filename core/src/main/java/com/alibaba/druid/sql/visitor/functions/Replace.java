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
import com.alibaba.druid.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

/**
 * @author lizongbo
 * @see <a href="https://github.com/alibaba/druid/issues/5477">Issue来源</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-functions.html#function_replace">mysql replace函数</a>
 */
public class Replace implements Function {
    public static final Replace instance = new Replace();

    public Object eval(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        if (x.getArguments().size() != 3) {
            return SQLEvalVisitor.EVAL_ERROR;
        }
        List<String> list = new ArrayList<>();
        for (SQLExpr item : x.getArguments()) {
            item.accept(visitor);
            Object param0Value = item.getAttributes().get(EVAL_VALUE);
            if (param0Value == null) {
                return SQLEvalVisitor.EVAL_ERROR;
            }
            String strValue = null;
            if (param0Value != null && param0Value != SQLEvalVisitor.EVAL_VALUE_NULL) {
                strValue = param0Value.toString();
            }
            list.add(strValue);
        }
        if (list.get(0) == null || list.get(1) == null || list.get(2) == null) {
            return list.get(0);
        }
        //暂时使用java String的replace方法,mysql replace函数是不支持正则表达式的
        return StringUtils.replaceAll(list.get(0), list.get(1), list.get(2));
    }
}
