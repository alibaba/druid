/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;

public class OdpsReadStatement extends OdpsStatementImpl {

    private SQLExprTableSource  table;
    private List<SQLAssignItem> partition = new ArrayList<SQLAssignItem>();
    private List<SQLName>       columns   = new ArrayList<SQLName>();
    private SQLExpr             rowCount;

    @Override
    protected void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, table);
            this.acceptChild(visitor, columns);
            this.acceptChild(visitor, partition);
            this.acceptChild(visitor, rowCount);
        }
        visitor.endVisit(this);
    }

    public SQLExprTableSource getTable() {
        return table;
    }

    public void setTable(SQLExprTableSource table) {
        if (table != null) {
            table.setParent(table);
        }
        this.table = table;
    }

    public void setTable(SQLName table) {
        this.setTable(new SQLExprTableSource(table));
    }

    public List<SQLAssignItem> getPartition() {
        return partition;
    }

    public List<SQLName> getColumns() {
        return columns;
    }

    public SQLExpr getRowCount() {
        return rowCount;
    }

    public void setRowCount(SQLExpr rowCount) {
        this.rowCount = rowCount;
    }

}
