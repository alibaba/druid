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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OdpsShowChangelogsStatement extends SQLStatementImpl {
    protected SQLExprTableSource table;
    private boolean tables;
    private List<SQLAssignItem> properties = new ArrayList<>();
    private List<SQLAssignItem> partitions = new ArrayList<>();
    private SQLExpr id;

    public OdpsShowChangelogsStatement() {
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
        accept0((OdpsASTVisitor) visitor);
    }

    public void accept0(OdpsASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, table);
            acceptChild(visitor, properties);
            acceptChild(visitor, partitions);
            acceptChild(visitor, id);
        }
        visitor.endVisit(this);
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

    public SQLExpr getId() {
        return id;
    }

    public void setId(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.id = x;
    }
}
