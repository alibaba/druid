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
package com.alibaba.druid.sql.dialect.presto.visitor;

import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.expr.SQLDecimalExpr;
import com.alibaba.druid.sql.dialect.phoenix.visitor.PhoenixASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;

import java.math.BigDecimal;

/**
 * presto 的输出的视图信息
 *
 * @author zhangcanlong
 * @since 2022-01-07
 */
public class PrestoOutputVisitor extends SQLASTOutputVisitor implements PhoenixASTVisitor {
    public PrestoOutputVisitor(Appendable appender) {
        super(appender);
    }

    public PrestoOutputVisitor(Appendable appender, boolean parameterized) {
        super(appender, parameterized);
    }

    @Override
    public boolean visit(SQLLimit x) {
        if (x.getOffset() != null) {
            this.print0(this.ucase ? " OFFSET " : " offset ");
            x.getOffset().accept(this);
        }
        this.print0(this.ucase ? " LIMIT " : " limit ");
        x.getRowCount().accept(this);
        return false;
    }

    public boolean visit(SQLDecimalExpr x) {
        BigDecimal value = x.getValue();
        print0(ucase ? "DECIMAL '" : "decimal '");
        print(value.toString());
        print('\'');

        return false;
    }
}
