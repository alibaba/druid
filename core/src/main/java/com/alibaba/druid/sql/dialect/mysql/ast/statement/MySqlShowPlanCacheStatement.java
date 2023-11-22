/*
 * Copyright 1999-2023 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowPlanCacheStatement extends MySqlStatementImpl implements MySqlShowStatement {
    private SQLSelect select;

    public MySqlShowPlanCacheStatement() {
    }

    public MySqlShowPlanCacheStatement(SQLSelect select) {
        setSelect(select);
    }

    public void accept0(MySqlASTVisitor v) {
        if (v.visit(this)) {
            if (select != null) {
                select.accept(v);
            }
        }
        v.endVisit(this);
    }

    public SQLSelect getSelect() {
        return select;
    }

    public void setSelect(SQLSelect x) {
        if (x != null) {
            x.setParent(this);
        }
        this.select = x;
    }
}
