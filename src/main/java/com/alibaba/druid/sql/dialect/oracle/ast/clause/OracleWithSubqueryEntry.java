package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause.Entry;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
public class OracleWithSubqueryEntry extends Entry implements OracleSQLObject {

    private SearchClause searchClause;
    private CycleClause  cycleClause;

    public CycleClause getCycleClause() {
        return cycleClause;
    }

    public void setCycleClause(CycleClause cycleClause) {
        if (cycleClause != null) {
            cycleClause.setParent(this);
        }
        this.cycleClause = cycleClause;
    }

    public SearchClause getSearchClause() {
        return searchClause;
    }

    public void setSearchClause(SearchClause searchClause) {
        if (searchClause != null) {
            searchClause.setParent(this);
        }
        this.searchClause = searchClause;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, columns);
            acceptChild(visitor, subQuery);
            acceptChild(visitor, searchClause);
            acceptChild(visitor, cycleClause);
        }
        visitor.endVisit(this);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    public void cloneTo(OracleWithSubqueryEntry x) {
        super.cloneTo(x);

        if (searchClause != null) {
            setSearchClause(searchClause.clone());
        }

        if (cycleClause != null) {
            setCycleClause(cycleClause.clone());
        }
    }

    public OracleWithSubqueryEntry clone() {
        OracleWithSubqueryEntry x = new OracleWithSubqueryEntry();
        cloneTo(x);
        return x;
    }
}
