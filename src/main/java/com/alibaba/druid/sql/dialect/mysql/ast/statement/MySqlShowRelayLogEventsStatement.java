/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowRelayLogEventsStatement extends MySqlStatementImpl implements MySqlShowStatement {

    private SQLExpr logName;
    private SQLExpr from;
    private Limit   limit;

    public SQLExpr getLogName() {
        return logName;
    }

    public void setLogName(SQLExpr logName) {
        this.logName = logName;
    }

    public SQLExpr getFrom() {
        return from;
    }

    public void setFrom(SQLExpr from) {
        this.from = from;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, logName);
            acceptChild(visitor, from);
            acceptChild(visitor, limit);
        }
        visitor.endVisit(this);
    }
}
