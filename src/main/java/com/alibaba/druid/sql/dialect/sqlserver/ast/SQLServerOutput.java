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
package com.alibaba.druid.sql.dialect.sqlserver.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;

public class SQLServerOutput extends SQLServerObjectImpl {

    protected SQLExprTableSource        into;

    protected final List<SQLExpr>       columns    = new ArrayList<SQLExpr>();

    protected final List<SQLSelectItem> selectList = new ArrayList<SQLSelectItem>();

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, selectList);
            this.acceptChild(visitor, into);
            this.acceptChild(visitor, columns);
        }

        visitor.endVisit(this);
    }

    public SQLExprTableSource getInto() {
        return into;
    }

    public void setInto(SQLExprTableSource into) {
        this.into = into;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public List<SQLSelectItem> getSelectList() {
        return selectList;
    }

}
