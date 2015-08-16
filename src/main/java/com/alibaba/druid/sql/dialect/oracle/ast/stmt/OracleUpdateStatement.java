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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class OracleUpdateStatement extends SQLUpdateStatement implements OracleStatement {

    private final List<SQLHint> hints         = new ArrayList<SQLHint>(1);
    private boolean             only          = false;
    private String              alias;
    private SQLExpr             where;

    private List<SQLExpr>       returning     = new ArrayList<SQLExpr>();
    private List<SQLExpr>       returningInto = new ArrayList<SQLExpr>();

    public OracleUpdateStatement(){
        super (JdbcConstants.ORACLE);
    }

    public List<SQLExpr> getReturning() {
        return returning;
    }

    public void setReturning(List<SQLExpr> returning) {
        this.returning = returning;
    }

    public List<SQLExpr> getReturningInto() {
        return returningInto;
    }

    public void setReturningInto(List<SQLExpr> returningInto) {
        this.returningInto = returningInto;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OracleASTVisitor) {
            accept0((OracleASTVisitor) visitor);
            return;
        }

        super.accept(visitor);
    }

    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.hints);
            acceptChild(visitor, tableSource);
            acceptChild(visitor, items);
            acceptChild(visitor, where);
            acceptChild(visitor, returning);
            acceptChild(visitor, returningInto);
        }

        visitor.endVisit(this);
    }

    public String getAlias() {
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public SQLExpr getWhere() {
        return this.where;
    }

    public void setWhere(SQLExpr where) {
        this.where = where;
    }

    public boolean isOnly() {
        return this.only;
    }

    public void setOnly(boolean only) {
        this.only = only;
    }

    public List<SQLHint> getHints() {
        return this.hints;
    }
}
