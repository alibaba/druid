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

import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.List;
import java.util.ArrayList;

public class SQLShowHistoryStatement extends SQLStatementImpl implements SQLShowStatement {

    protected SQLExprTableSource table;
    private boolean tables;
    private List<SQLAssignItem> properties = new ArrayList<>();
    private List<SQLAssignItem> partitions = new ArrayList<>();

    public SQLShowHistoryStatement() {

    }

    public SQLExprTableSource getTable() {
        return table;
    }

    public void setTable(SQLExprTableSource x) {
        if (x != null) {
            x.setParent(this);
        }
        this.table = x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (table != null) {
                table.accept(visitor);
            }
        }
    }

    public boolean isTables() {
        return tables;
    }

    public void setTables(boolean tables) {
        this.tables = tables;
    }

    public List<SQLAssignItem> getProperties() {
        return properties;
    }

    public List<SQLAssignItem> getPartitions() {
        return partitions;
    }
}
