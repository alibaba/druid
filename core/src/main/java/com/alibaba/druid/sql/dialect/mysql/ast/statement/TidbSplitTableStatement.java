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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lizongbo
 * @see  <a href="https://docs.pingcap.com/zh/tidb/stable/sql-statement-split-region">...</a>
 */
public class TidbSplitTableStatement extends MySqlStatementImpl {
    //region for
    private boolean splitSyntaxOptionRegionFor;
    //partition
    private boolean splitSyntaxOptionPartition;

    private SQLExprTableSource tableName;

    private List<SQLExpr> partitionNameListOptions = new ArrayList<>();

    private SQLName indexName;

    private List<List<SQLExpr>> splitOptionBys = new ArrayList<>();
    private List<SQLExpr> splitOptionBetween;
    private List<SQLExpr> splitOptionAnd;
    private long splitOptionRegions;

    public boolean isSplitSyntaxOptionRegionFor() {
        return splitSyntaxOptionRegionFor;
    }

    public void setSplitSyntaxOptionRegionFor(boolean splitSyntaxOptionRegionFor) {
        this.splitSyntaxOptionRegionFor = splitSyntaxOptionRegionFor;
    }

    public boolean isSplitSyntaxOptionPartition() {
        return splitSyntaxOptionPartition;
    }

    public void setSplitSyntaxOptionPartition(boolean splitSyntaxOptionPartition) {
        this.splitSyntaxOptionPartition = splitSyntaxOptionPartition;
    }

    public SQLExprTableSource getTableName() {
        return tableName;
    }

    public void setTableName(SQLExprTableSource tableName) {
        this.tableName = tableName;
    }

    public List<SQLExpr> getPartitionNameListOptions() {
        return partitionNameListOptions;
    }

    public void setPartitionNameListOptions(List<SQLExpr> partitionNameListOptions) {
        this.partitionNameListOptions = partitionNameListOptions;
    }

    public SQLName getIndexName() {
        return indexName;
    }

    public void setIndexName(SQLName indexName) {
        this.indexName = indexName;
    }

    public List<List<SQLExpr>> getSplitOptionBys() {
        return splitOptionBys;
    }

    public void setSplitOptionBys(List<List<SQLExpr>> splitOptionBys) {
        this.splitOptionBys = splitOptionBys;
    }

    public List<SQLExpr> getSplitOptionBetween() {
        return splitOptionBetween;
    }

    public void setSplitOptionBetween(List<SQLExpr> splitOptionBetween) {
        this.splitOptionBetween = splitOptionBetween;
    }

    public List<SQLExpr> getSplitOptionAnd() {
        return splitOptionAnd;
    }

    public void setSplitOptionAnd(List<SQLExpr> splitOptionAnd) {
        this.splitOptionAnd = splitOptionAnd;
    }

    public long getSplitOptionRegions() {
        return splitOptionRegions;
    }

    public void setSplitOptionRegions(long splitOptionRegions) {
        this.splitOptionRegions = splitOptionRegions;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            this.getTableName().accept(visitor);
        }
        visitor.endVisit(this);
    }
}
