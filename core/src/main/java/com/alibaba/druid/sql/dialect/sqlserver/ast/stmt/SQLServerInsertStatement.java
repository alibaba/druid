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
package com.alibaba.druid.sql.dialect.sqlserver.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLTop;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerObject;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerOutput;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLServerInsertStatement extends SQLInsertStatement implements SQLServerObject {
    private boolean defaultValues;

    private SQLTop top;

    private SQLServerOutput output;

    public SQLServerInsertStatement() {
        dbType = DbType.sqlserver;
    }

    public void cloneTo(SQLServerInsertStatement x) {
        super.cloneTo(x);
        x.defaultValues = defaultValues;
        if (top != null) {
            x.setTop(top.clone());
        }
        if (output != null) {
            x.setOutput(output.clone());
        }
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof SQLServerASTVisitor) {
            accept0((SQLServerASTVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, getTop());
            this.acceptChild(visitor, getTableSource());
            this.acceptChild(visitor, getColumns());
            this.acceptChild(visitor, getOutput());
            this.acceptChild(visitor, getValuesList());
            this.acceptChild(visitor, getQuery());
        }

        visitor.endVisit(this);
    }

    public boolean isDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(boolean defaultValues) {
        this.defaultValues = defaultValues;
    }

    public SQLServerOutput getOutput() {
        return output;
    }

    public void setOutput(SQLServerOutput output) {
        this.output = output;
    }

    public SQLTop getTop() {
        return top;
    }

    public void setTop(SQLTop top) {
        if (top != null) {
            top.setParent(this);
            top.setParentheses(true);
        }
        this.top = top;
    }

    public SQLServerInsertStatement clone() {
        SQLServerInsertStatement x = new SQLServerInsertStatement();
        cloneTo(x);
        return x;
    }
}
