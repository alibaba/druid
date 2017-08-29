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
package com.alibaba.druid.sql.dialect.odps.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSourceImpl;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FNVUtils;

public class OdpsLateralViewTableSource extends SQLTableSourceImpl {

    private SQLTableSource      tableSource;

    private SQLMethodInvokeExpr method;

    private List<SQLName>       columns = new ArrayList<SQLName>(2);

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((OdpsASTVisitor) visitor);
    }

    protected void accept0(OdpsASTVisitor visitor) {
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
        long hash = this.alias_hash();
        if (hash != 0 && hash == alias_hash) {
            return this;
        }

        for (SQLName column : columns) {
            if (column.name_hash_lower() == alias_hash) {
                return this;
            }
        }

        if (tableSource != null) {
            return tableSource.findTableSource(alias_hash);
        }

        return null;
    }

    public SQLTableSource findTableSourceWithColumn(long columnNameHash) {
        for (SQLName column : columns) {
            if (column.name_hash_lower() == columnNameHash) {
                return this;
            }
        }

        if (tableSource != null) {
            return tableSource.findTableSourceWithColumn(columnNameHash);
        }
        return null;
    }
}
