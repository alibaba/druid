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
package com.alibaba.druid.sql.dialect.sqlserver.ast;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSourceImpl;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLServerSelectPivotTableSource extends SQLTableSourceImpl {

    protected SQLServerSelectBasePivot pivot;

    protected SQLTableSource tableSource;

    public SQLServerSelectBasePivot getPivot() {
        return pivot;
    }

    public void setPivot(SQLServerSelectBasePivot pivot) {
        this.pivot = pivot;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof SQLServerASTVisitor) {
            this.accept0((SQLServerASTVisitor) visitor);
        }
    }

    private void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, pivot);
        }
        visitor.endVisit(this);
    }

    public SQLTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLTableSource tableSource) {
        this.tableSource = tableSource;
    }

    @Override
    public String toString() {
        return SQLUtils.toSQLServerString(this);
    }
}
