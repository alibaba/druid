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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsStatementImpl;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLAnalyzeTableStatement extends SQLStatementImpl {
    protected final List<SQLExprTableSource> tableSources    = new ArrayList<SQLExprTableSource>();

    private SQLPartitionRef     partition;
    private boolean             forColums = false;
    private boolean             cacheMetadata;
    private boolean             noscan;
    private boolean             computeStatistics;

    private SQLIdentifierExpr   adbSchema; // for ADB
    private List<SQLIdentifierExpr> adbColumns = new ArrayList<SQLIdentifierExpr>(); // for ADB
    private List<SQLIdentifierExpr> adbColumnsGroup = new ArrayList<SQLIdentifierExpr>(); // for ADB
    private SQLExpr adbWhere; // for ADB

    public SQLAnalyzeTableStatement() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.acceptChild(visitor, tableSources);
            this.acceptChild(visitor, partition);
            this.acceptChild(visitor, adbSchema);
            this.acceptChild(visitor, adbColumns);
            this.acceptChild(visitor, adbColumnsGroup);
            this.acceptChild(visitor, adbWhere);
        }
        visitor.endVisit(this);
    }

    public List<SQLExprTableSource> getTables() {
        return tableSources;
    }

    public SQLExprTableSource getTable() {
        if (tableSources.size() == 0) {
            return null;
        }

        if (tableSources.size() == 1) {
            return tableSources.get(0);
        }

        throw new UnsupportedOperationException();
    }

    public void setTable(SQLExprTableSource table) {
        if (table != null) {
            table.setParent(this);
        }

        if (tableSources.size() == 0) {
            if (table == null) {
                return;
            }

            tableSources.add(table);
            return;
        }

        if (tableSources.size() == 1) {
            if (table == null) {
                this.tableSources.remove(0);
            } else {
                tableSources.set(0, table);
            }
            return;
        }

        throw new UnsupportedOperationException();
    }

    public SQLIdentifierExpr getAdbSchema() {
        return adbSchema;
    }

    public void setAdbSchema(SQLIdentifierExpr adbSchema) {
        this.adbSchema = adbSchema;
    }

    public List<SQLIdentifierExpr> getAdbColumns() {
        return adbColumns;
    }

    public void setAdbColumns(List<SQLIdentifierExpr> adbColumns) {
        this.adbColumns = adbColumns;
    }

    public List<SQLIdentifierExpr> getAdbColumnsGroup() {
        return adbColumnsGroup;
    }

    public void setAdbColumnsGroup(List<SQLIdentifierExpr> adbColumnsGroup) {
        this.adbColumnsGroup = adbColumnsGroup;
    }

    public SQLExpr getAdbWhere() {
        return adbWhere;
    }

    public void setAdbWhere(SQLExpr adbWhere) {
        this.adbWhere = adbWhere;
    }

    public void setTable(SQLName table) {
        this.setTable(new SQLExprTableSource(table));
    }

    public SQLPartitionRef getPartition() {
        return partition;
    }

    public void setPartition(SQLPartitionRef x) {
        if (x != null) {
            x.setParent(this);
        }
        this.partition = x;
    }

    public boolean isForColums() {
        return forColums;
    }

    public void setForColums(boolean forColums) {
        this.forColums = forColums;
    }

    public boolean isCacheMetadata() {
        return cacheMetadata;
    }

    public void setCacheMetadata(boolean cacheMetadata) {
        this.cacheMetadata = cacheMetadata;
    }

    public boolean isNoscan() {
        return noscan;
    }

    public void setNoscan(boolean noscan) {
        this.noscan = noscan;
    }

    public boolean isComputeStatistics() {
        return computeStatistics;
    }

    public void setComputeStatistics(boolean computeStatistics) {
        this.computeStatistics = computeStatistics;
    }
}
