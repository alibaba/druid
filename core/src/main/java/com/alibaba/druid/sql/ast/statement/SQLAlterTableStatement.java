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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLAlterTableStatement extends SQLStatementImpl implements SQLDDLStatement, SQLAlterStatement {
    private boolean only;
    private SQLExprTableSource tableSource;
    private List<SQLAlterTableItem> items = new ArrayList<SQLAlterTableItem>();

    // for mysql
    private boolean ignore;
    private boolean online;
    private boolean offline;

    private boolean updateGlobalIndexes;
    private boolean invalidateGlobalIndexes;

    private boolean removePatiting;
    private boolean upgradePatiting;
    private List<SQLAssignItem> tableOptions = new ArrayList<>();
    private List<SQLName> unsetTableOptions = new ArrayList<>();
    private SQLPartitionBy partition;

    // odps
    private boolean mergeSmallFiles;
    protected boolean range;
    protected final List<SQLSelectOrderByItem> clusteredBy = new ArrayList<SQLSelectOrderByItem>();
    protected final List<SQLSelectOrderByItem> sortedBy = new ArrayList<SQLSelectOrderByItem>();
    protected int buckets;
    protected int shards;

    private boolean ifExists;
    private boolean notClustered;

    // clickhouse
    private SQLName on;

    public SQLAlterTableStatement() {
    }

    public SQLAlterTableStatement(DbType dbType) {
        super(dbType);
    }

    public boolean isOnly() {
        return only;
    }

    public void setOnly(boolean only) {
        this.only = only;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isIfExists() {
        return ifExists;
    }

    public void setIfExists(boolean ifExists) {
        this.ifExists = ifExists;
    }

    public boolean isRemovePatiting() {
        return removePatiting;
    }

    public void setRemovePatiting(boolean removePatiting) {
        this.removePatiting = removePatiting;
    }

    public boolean isUpgradePatiting() {
        return upgradePatiting;
    }

    public void setUpgradePatiting(boolean upgradePatiting) {
        this.upgradePatiting = upgradePatiting;
    }

    public boolean isUpdateGlobalIndexes() {
        return updateGlobalIndexes;
    }

    public void setUpdateGlobalIndexes(boolean updateGlobalIndexes) {
        this.updateGlobalIndexes = updateGlobalIndexes;
    }

    public boolean isInvalidateGlobalIndexes() {
        return invalidateGlobalIndexes;
    }

    public void setInvalidateGlobalIndexes(boolean invalidateGlobalIndexes) {
        this.invalidateGlobalIndexes = invalidateGlobalIndexes;
    }

    public boolean isMergeSmallFiles() {
        return mergeSmallFiles;
    }

    public void setMergeSmallFiles(boolean mergeSmallFiles) {
        this.mergeSmallFiles = mergeSmallFiles;
    }

    public List<SQLAlterTableItem> getItems() {
        return items;
    }

    public void addItem(SQLAlterTableItem item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        this.tableSource = tableSource;
    }

    public void setTableSource(SQLExpr table) {
        this.setTableSource(new SQLExprTableSource(table));
    }

    public SQLName getName() {
        if (getTableSource() == null) {
            return null;
        }
        return (SQLName) getTableSource().getExpr();
    }

    public long nameHashCode64() {
        if (getTableSource() == null) {
            return 0L;
        }
        return ((SQLName) getTableSource().getExpr()).nameHashCode64();
    }

    public void setName(SQLName name) {
        this.setTableSource(new SQLExprTableSource(name));
    }

    public List<SQLAssignItem> getTableOptions() {
        return tableOptions;
    }

    public List<SQLName> getUnsetTableOptions() {
        return unsetTableOptions;
    }

    public SQLPartitionBy getPartition() {
        return partition;
    }

    public void setPartition(SQLPartitionBy partition) {
        this.partition = partition;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getTableSource());
            acceptChild(visitor, getItems());
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (tableSource != null) {
            children.add(tableSource);
        }
        children.addAll(this.items);
        return children;
    }

    public String getTableName() {
        if (tableSource == null) {
            return null;
        }
        SQLExpr expr = ((SQLExprTableSource) tableSource).getExpr();
        if (expr instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) expr).getName();
        } else if (expr instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) expr).getName();
        }

        return null;
    }

    public String getSchema() {
        SQLName name = getName();
        if (name == null) {
            return null;
        }

        if (name instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) name).getOwnernName();
        }

        return null;
    }

    public void setItems(List<SQLAlterTableItem> items) {
        this.items = items;
    }

    public boolean isRange() {
        return range;
    }

    public void setRange(boolean range) {
        this.range = range;
    }

    public List<SQLSelectOrderByItem> getClusteredBy() {
        return clusteredBy;
    }

    public void addClusteredByItem(SQLSelectOrderByItem item) {
        item.setParent(this);
        this.clusteredBy.add(item);
    }

    public List<SQLSelectOrderByItem> getSortedBy() {
        return sortedBy;
    }

    public void addSortedByItem(SQLSelectOrderByItem item) {
        item.setParent(this);
        this.sortedBy.add(item);
    }

    public int getBuckets() {
        return buckets;
    }

    public void setBuckets(int buckets) {
        this.buckets = buckets;
    }

    public int getShards() {
        return shards;
    }

    public void setShards(int shards) {
        this.shards = shards;
    }

    public boolean isNotClustered() {
        return notClustered;
    }

    public void setNotClustered(boolean notClustered) {
        this.notClustered = notClustered;
    }

    @Override
    public DDLObjectType getDDLObjectType() {
        return DDLObjectType.TABLE;
    }

    public SQLName getOn() {
        return on;
    }

    public void setOn(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.on = x;
    }
}
