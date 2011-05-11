/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLExistsExpr extends SQLExprImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    public boolean not = false;
    public SQLSelect subQuery;

    public SQLExistsExpr() {

    }

    public SQLExistsExpr(SQLSelect subQuery) {

        this.subQuery = subQuery;
    }

    public SQLExistsExpr(SQLSelect subQuery, boolean not) {

        this.subQuery = subQuery;
        this.not = not;
    }

    public boolean isNot() {
        return this.not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public SQLSelect getSubQuery() {
        return this.subQuery;
    }

    public void setSubQuery(SQLSelect subQuery) {
        this.subQuery = subQuery;
    }

    public void output(StringBuffer buf) {
        if (this.not) {
            buf.append("NOT ");
        }
        buf.append("EXISTS (");
        this.subQuery.output(buf);
        buf.append(")");
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.subQuery);
        }

        visitor.endVisit(this);
    }
}
