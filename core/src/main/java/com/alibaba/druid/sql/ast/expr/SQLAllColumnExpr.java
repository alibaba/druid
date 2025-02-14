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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SQLAllColumnExpr extends SQLExprImpl {
    private transient SQLTableSource resolvedTableSource;

    private SQLExpr owner;
    private List<SQLExpr> except;
    private final List<SQLAliasedExpr> replace = new ArrayList<SQLAliasedExpr>();

    public SQLAllColumnExpr() {
    }

    public void output(StringBuilder buf) {
        if (owner != null) {
            owner.output(buf);
            buf.append('.');
        }
        buf.append('*');
    }

    public SQLExpr getOwner() {
        return owner;
    }

    public void setOwner(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.owner = x;
    }

    public List<SQLExpr> getExcept() {
        return except;
    }

    public List<SQLAliasedExpr> getReplace() {
        return replace;
    }

    public void setExcept(List<SQLExpr> except) {
        this.except = except;
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public int hashCode() {
        return 0;
    }

    public boolean equals(Object o) {
        return o instanceof SQLAllColumnExpr;
    }

    public SQLAllColumnExpr clone() {
        SQLAllColumnExpr x = new SQLAllColumnExpr();
        x.setOwner(owner);

        x.resolvedTableSource = resolvedTableSource;
        return x;
    }

    public SQLTableSource getResolvedTableSource() {
        return resolvedTableSource;
    }

    public void setResolvedTableSource(SQLTableSource resolvedTableSource) {
        this.resolvedTableSource = resolvedTableSource;
    }

    @Override
    public List getChildren() {
        return Collections.emptyList();
    }
}
