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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLFetchStatement extends SQLStatementImpl implements SQLReplaceable {

    private SQLName       cursorName;
    private boolean       bulkCollect;
    private List<SQLExpr> into = new ArrayList<SQLExpr>();
    private SQLLimit      limit;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, cursorName);
            acceptChild(visitor, into);
        }
        visitor.endVisit(this);
    }

    public SQLName getCursorName() {
        return cursorName;
    }

    public void setCursorName(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.cursorName = x;
    }

    public SQLLimit getLimit() {
        return limit;
    }

    public void setLimit(SQLLimit x) {
        if (x != null) {
            x.setParent(this);
        }
        this.limit = x;
    }

    public List<SQLExpr> getInto() {
        return into;
    }

    public void setInto(List<SQLExpr> into) {
        this.into = into;
    }

    public boolean isBulkCollect() {
        return bulkCollect;
    }

    public void setBulkCollect(boolean bulkCollect) {
        this.bulkCollect = bulkCollect;
    }

    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (cursorName == expr) {
            setCursorName((SQLName) target);
            return true;
        }

        return false;
    }
}
