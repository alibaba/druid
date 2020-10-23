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

import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLLateralViewTableSource extends SQLTableSourceImpl {

    private SQLTableSource tableSource;
    private boolean outer;

    private SQLMethodInvokeExpr method;

    private List<SQLName> columns = new ArrayList<SQLName>(2);

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, method);
            acceptChild(visitor, columns);
        }
        visitor.endVisit(this);
    }

    public SQLTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSource = tableSource;
    }

    public SQLMethodInvokeExpr getMethod() {
        return method;
    }

    public void setMethod(SQLMethodInvokeExpr method) {
        if (method != null) {
            method.setParent(this);
        }
        this.method = method;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public void setColumns(List<SQLName> columns) {
        this.columns = columns;
    }

    public SQLTableSource findTableSource(long alias_hash) {
        long hash = this.aliasHashCode64();
        if (hash != 0 && hash == alias_hash) {
            return this;
        }

        for (SQLName column : columns) {
            if (column.nameHashCode64() == alias_hash) {
                return this;
            }
        }

        if (tableSource != null) {
            return tableSource.findTableSource(alias_hash);
        }

        return null;
    }

    public SQLTableSource findTableSourceWithColumn(long columnNameHash, String columnName, int option) {
        for (SQLName column : columns) {
            if (column.nameHashCode64() == columnNameHash) {
                return this;
            }
        }

        if (tableSource != null) {
            return tableSource.findTableSourceWithColumn(columnNameHash, columnName, option);
        }
        return null;
    }

    @Override
    public SQLLateralViewTableSource clone() {

        SQLLateralViewTableSource x = new SQLLateralViewTableSource();

        x.setAlias(this.alias);
        x.outer = outer;

        if (this.tableSource != null) {
            x.setTableSource(this.tableSource.clone());
        }

        if (this.method != null) {
            x.setMethod(this.method.clone());
        }

        for (SQLName column : this.columns) {
            SQLName e2 = column.clone();
            e2.setParent(x);
            x.getColumns().add(e2);
        }

        if (this.flashback != null) {
            x.setFlashback(this.flashback.clone());
        }

        if (this.hints != null) {
            for (SQLHint e : this.hints) {
                SQLHint e2 = e.clone();
                e2.setParent(x);
                x.getHints().add(e2);
            }
        }

        return x;
    }

    public boolean isOuter() {
        return outer;
    }

    public void setOuter(boolean outer) {
        this.outer = outer;
    }
}
