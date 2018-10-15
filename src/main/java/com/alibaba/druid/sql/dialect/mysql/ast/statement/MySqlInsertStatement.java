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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlInsertStatement extends SQLInsertStatement {

    private boolean             lowPriority        = false;
    private boolean             delayed            = false;
    private boolean             highPriority       = false;
    private boolean             ignore             = false;
    private boolean             rollbackOnFail     = false;

    private final List<SQLExpr> duplicateKeyUpdate = new ArrayList<SQLExpr>();

    public MySqlInsertStatement() {
        dbType = JdbcConstants.MYSQL;
    }

    public void cloneTo(MySqlInsertStatement x) {
        super.cloneTo(x);
        x.lowPriority = lowPriority;
        x.delayed = delayed;
        x.highPriority = highPriority;
        x.ignore = ignore;
        x.rollbackOnFail = rollbackOnFail;

        for (SQLExpr e : duplicateKeyUpdate) {
            SQLExpr e2 = e.clone();
            e2.setParent(x);
            x.duplicateKeyUpdate.add(e2);
        }
    }

    public List<SQLExpr> getDuplicateKeyUpdate() {
        return duplicateKeyUpdate;
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public boolean isHighPriority() {
        return highPriority;
    }

    public void setHighPriority(boolean highPriority) {
        this.highPriority = highPriority;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isRollbackOnFail() {
        return rollbackOnFail;
    }

    public void setRollbackOnFail(boolean rollbackOnFail) {
        this.rollbackOnFail = rollbackOnFail;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public void output(StringBuffer buf) {
        new MySqlOutputVisitor(buf).visit(this);
    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, getTableSource());
            this.acceptChild(visitor, getColumns());
            this.acceptChild(visitor, getValuesList());
            this.acceptChild(visitor, getQuery());
            this.acceptChild(visitor, getDuplicateKeyUpdate());
        }

        visitor.endVisit(this);
    }

    public SQLInsertStatement clone() {
        MySqlInsertStatement x = new MySqlInsertStatement();
        cloneTo(x);
        return x;
    }
}
