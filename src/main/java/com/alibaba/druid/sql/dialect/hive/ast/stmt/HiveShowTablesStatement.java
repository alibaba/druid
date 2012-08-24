/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.hive.ast.stmt;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.hive.ast.HiveStatementImpl;
import com.alibaba.druid.sql.dialect.hive.visitor.HiveASTVisitor;

public class HiveShowTablesStatement extends HiveStatementImpl {

    private static final long serialVersionUID = 1L;
    private SQLCharExpr       pattern;


    public SQLCharExpr getPattern() {
        return pattern;
    }

    public void setPattern(SQLCharExpr pattern) {
        this.pattern = pattern;
    }

    @Override
    public void accept0(HiveASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, pattern);
        }
        visitor.endVisit(this);
    }

}
