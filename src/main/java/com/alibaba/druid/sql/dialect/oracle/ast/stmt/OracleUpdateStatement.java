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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleHint;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleUpdateStatement extends SQLStatementImpl {
    private static final long serialVersionUID = 1L;

    private final List<OracleHint> hints = new ArrayList<OracleHint>(1);
    private boolean only = false;
    private SQLExpr table;
    private String alias;
    private OracleUpdateSetClause setClause;
    private SQLExpr where;

    public OracleUpdateStatement() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof OracleASTVisitor) {
            accept0((OracleASTVisitor) visitor);
            return;
        }

        super.accept(visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.hints);
            acceptChild(visitor, this.table);
            acceptChild(visitor, this.setClause);
            acceptChild(visitor, this.where);
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

    public OracleUpdateSetClause getSetClause() {
        return this.setClause;
    }

    public void setSetClause(OracleUpdateSetClause setClause) {
        this.setClause = setClause;
    }

    public boolean isOnly() {
        return this.only;
    }

    public void setOnly(boolean only) {
        this.only = only;
    }

    public List<OracleHint> getHints() {
        return this.hints;
    }

    public SQLExpr getTable() {
        return this.table;
    }

    public void setTable(SQLExpr table) {
        this.table = table;
    }
}
