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
package com.alibaba.druid.sql.dialect.presto.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLAlterFunctionStatement;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PrestoAlterFunctionStatement extends SQLAlterFunctionStatement implements PrestoSQLStatement {
    public PrestoAlterFunctionStatement() {
    }

    private boolean returnsNullOnNullInput;
    private boolean calledOnNullInput;

    public boolean isReturnsNullOnNullInput() {
        return returnsNullOnNullInput;
    }

    public void setReturnsNullOnNullInput(boolean returnsNullOnNullInput) {
        this.returnsNullOnNullInput = returnsNullOnNullInput;
    }

    public boolean isCalledOnNullInput() {
        return calledOnNullInput;
    }

    public void setCalledOnNullInput(boolean calledOnNullInput) {
        this.calledOnNullInput = calledOnNullInput;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof PrestoVisitor) {
            this.accept0((PrestoVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    @Override
    public void accept0(PrestoVisitor visitor) {
        visitor.visit(this);
    }
}
