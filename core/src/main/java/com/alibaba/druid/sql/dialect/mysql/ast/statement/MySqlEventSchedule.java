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
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlEventSchedule extends MySqlObjectImpl {
    private SQLExpr at;
    private SQLExpr every;
    private SQLExpr starts;
    private SQLExpr ends;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, at);
            acceptChild(visitor, every);
            acceptChild(visitor, starts);
            acceptChild(visitor, ends);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getAt() {
        return at;
    }

    public void setAt(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.at = x;
    }

    public SQLExpr getEvery() {
        return every;
    }

    public void setEvery(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.every = x;
    }

    public SQLExpr getStarts() {
        return starts;
    }

    public void setStarts(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.starts = x;
    }

    public SQLExpr getEnds() {
        return ends;
    }

    public void setEnds(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.ends = x;
    }
}
