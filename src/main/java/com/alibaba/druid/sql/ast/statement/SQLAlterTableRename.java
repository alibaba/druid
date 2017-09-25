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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableRename extends SQLObjectImpl implements SQLAlterTableItem {

    protected SQLExprTableSource to;

    public SQLAlterTableRename() {

    }

    public SQLAlterTableRename(SQLExpr to) {
        this.setTo(to);
    }

    public SQLExprTableSource getTo() {
        return to;
    }

    public SQLName getToName() {
        if (to == null) {
            return null;
        }

        SQLExpr expr = to.expr;

        if (expr instanceof SQLName) {
            return (SQLName) expr;
        }

        return null;
    }

    public void setTo(SQLExprTableSource to) {
        if (to != null) {
            to.setParent(this);
        }
        this.to = to;
    }

    public void setTo(SQLExpr to) {
        this.setTo(new SQLExprTableSource(to));
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, to);
        }
        visitor.endVisit(this);
    }

}
