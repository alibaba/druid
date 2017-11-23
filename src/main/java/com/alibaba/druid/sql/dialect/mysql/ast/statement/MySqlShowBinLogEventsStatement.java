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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowBinLogEventsStatement extends MySqlStatementImpl implements MySqlShowStatement {

    private SQLExpr in;
    private SQLExpr from;
    private SQLLimit limit;

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, in);
            acceptChild(visitor, from);
            acceptChild(visitor, limit);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getIn() {
        return in;
    }

    public void setIn(SQLExpr in) {
        this.in = in;
    }

    public SQLExpr getFrom() {
        return from;
    }

    public void setFrom(SQLExpr from) {
        this.from = from;
    }

    public SQLLimit getLimit() {
        return limit;
    }

    public void setLimit(SQLLimit limit) {
        if (limit != null) {
            limit.setParent(this);
        }
        this.limit = limit;
    }

}
