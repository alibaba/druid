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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLDumpStatement extends SQLStatementImpl {
    private boolean overwrite;
    private SQLExprTableSource into;

    private SQLSelect select;

    public SQLDumpStatement() {

    }

    public SQLSelect getSelect() {
        return select;
    }

    public void setSelect(SQLSelect x) {
        if (x != null) {
            x.setParent(this);
        }

        this.select = x;
    }

    public SQLExprTableSource getInto() {
        return into;
    }

    public void setInto(SQLExpr x) {
        if (x == null) {
            return;
        }

        setInto(new SQLExprTableSource(x));
    }

    public void setInto(SQLExprTableSource x) {
        if (x != null) {
            x.setParent(this);
        }

        this.into = x;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (into != null) {
                into.accept(visitor);
            }

            if (select != null) {
                select.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }
}
