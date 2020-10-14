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

import com.alibaba.druid.DbType;
import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLBigIntExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLArchiveTableStatement extends SQLStatementImpl {
    private SQLName table;

    private SQLName type;
    private List<SQLIntegerExpr> spIdList = new ArrayList<SQLIntegerExpr>();
    private List<SQLIntegerExpr> pIdList = new ArrayList<SQLIntegerExpr>();

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.table);
            acceptChild(visitor, this.type);

            acceptChild(visitor, spIdList);
            acceptChild(visitor, pIdList);
        }
        visitor.endVisit(this);
    }

    public SQLName getTable() {
        return table;
    }

    public void setTable(SQLName table) {
        if (table != null) {
            table.setParent(this);
        }
        this.table = table;
    }

    public SQLName getType() {
        return type;
    }

    public void setType(SQLName type) {
        if (type != null) {
            type.setParent(this);
        }
        this.type = type;
    }

    public List<SQLIntegerExpr> getSpIdList() {
        return spIdList;
    }


    public List<SQLIntegerExpr> getpIdList() {
        return pIdList;
    }
}
