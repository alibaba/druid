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

public class SQLCreateIndexStatement extends SQLStatementImpl implements SQLCreateStatement, SQLIndex {

    private SQLIndexDefinition indexDefinition = new SQLIndexDefinition();

    private boolean concurrently; // for pg
    protected SQLName tablespace; // for oracle
    protected boolean deferedRebuild;
    protected SQLTableSource in;
    protected SQLExternalRecordFormat rowFormat;
    protected SQLName storedAs;
    protected List<SQLAssignItem> properties = new ArrayList<SQLAssignItem>();
    protected List<SQLAssignItem> tableProperties = new ArrayList<SQLAssignItem>();
    protected boolean storing;
    protected boolean ifNotExists;

    public SQLCreateIndexStatement() {
        indexDefinition.setParent(this);
    }

    public SQLCreateIndexStatement(DbType dbType) {
        super(dbType);
        indexDefinition.setParent(this);
    }

    public SQLIndexDefinition getIndexDefinition() {
        return indexDefinition;
    }

    public SQLTableSource getTable() {
        return indexDefinition.getTable();
    }

    public void setTable(SQLName table) {
        this.setTable(new SQLExprTableSource(table));
    }

    public void setTable(SQLTableSource table) {
        indexDefinition.setTable(table);
    }

    public String getTableName() {
        if (indexDefinition.getTable() instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) indexDefinition.getTable()).getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                return ((SQLIdentifierExpr) expr).getName();
            } else if (expr instanceof SQLPropertyExpr) {
                return ((SQLPropertyExpr) expr).getName();
            }
        }

        return null;
    }

    public List<SQLSelectOrderByItem> getItems() {
        return indexDefinition.getColumns();
    }

    public void addItem(SQLSelectOrderByItem item) {
        if (item != null) {
            item.setParent(this);
        }
        indexDefinition.getColumns().add(item);
    }

    public SQLName getName() {
        return indexDefinition.getName();
    }

    public void setName(SQLName name) {
        indexDefinition.setName(name);
    }

    public String getType() {
        return indexDefinition.getType();
    }

    public void setType(String type) {
        indexDefinition.setType(type);
    }

    public String getUsing() {
        return indexDefinition.hasOptions() ? indexDefinition.getOptions().getIndexType() : null;
    }

    public void setUsing(String using) {
        indexDefinition.getOptions().setIndexType(using);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, indexDefinition.getName());
            acceptChild(visitor, indexDefinition.getTable());
            acceptChild(visitor, indexDefinition.getColumns());
            acceptChild(visitor, tablespace);
            acceptChild(visitor, in);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (indexDefinition.getName() != null) {
            children.add(indexDefinition.getName());
        }

        if (indexDefinition.getTable() != null) {
            children.add(indexDefinition.getTable());
        }

        children.addAll(indexDefinition.getColumns());
        return children;
    }

    public String getSchema() {
        SQLName name = null;
        if (indexDefinition.getTable() instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) indexDefinition.getTable()).getExpr();
            if (expr instanceof SQLName) {
                name = (SQLName) expr;
            }
        }

        if (name == null) {
            return null;
        }

        if (name instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) name).getOwnernName();
        }

        return null;
    }


    public SQLCreateIndexStatement clone() {
        SQLCreateIndexStatement x = new SQLCreateIndexStatement();
        indexDefinition.cloneTo(x.indexDefinition);
        x.setIfNotExists(ifNotExists);
        return x;
    }

    public SQLExpr getComment() {
        return indexDefinition.hasOptions() ? indexDefinition.getOptions().getComment() : null;
    }

    public void setComment(SQLExpr x) {
        indexDefinition.getOptions().setComment(x);
    }

    public SQLName getTablespace() {
        return tablespace;
    }

    public void setTablespace(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.tablespace = x;
    }

    public boolean isConcurrently() {
        return concurrently;
    }

    public void setConcurrently(boolean concurrently) {
        this.concurrently = concurrently;
    }

    public List<SQLAssignItem> getOptions() {
        return indexDefinition.getCompatibleOptions();
    }

    public boolean isDeferedRebuild() {
        return deferedRebuild;
    }

    public void setDeferedRebuild(boolean deferedRebuild) {
        this.deferedRebuild = deferedRebuild;
    }

    public SQLTableSource getIn() {
        return in;
    }

    public void setIn(SQLName x) {
        if (x == null) {
            this.in = null;
            return;
        }
        setIn(new SQLExprTableSource(x));
    }

    public void setIn(SQLTableSource x) {
        if (x != null) {
            x.setParent(this);
        }
        this.in = x;
    }

    public SQLName getStoredAs() {
        return storedAs;
    }

    public void setStoredAs(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.storedAs = x;
    }

    public SQLExternalRecordFormat getRowFormat() {
        return rowFormat;
    }

    public void setRowFormat(SQLExternalRecordFormat x) {
        if (x != null) {
            x.setParent(this);
        }
        this.rowFormat = x;
    }

    public List<SQLAssignItem> getProperties() {
        return properties;
    }

    public List<SQLAssignItem> getTableProperties() {
        return tableProperties;
    }

    public void addOption(String name, SQLExpr value) {
        SQLAssignItem assignItem = new SQLAssignItem(new SQLIdentifierExpr(name), value);
        assignItem.setParent(this);
        // Add both with same object.
        indexDefinition.getOptions().getOtherOptions().add(assignItem);
        indexDefinition.getCompatibleOptions().add(assignItem);
    }

    public boolean isGlobal() {
        return indexDefinition.isGlobal();
    }

    public void setGlobal(boolean global) {
        indexDefinition.setGlobal(global);
    }

    public boolean isLocal() {
        return indexDefinition.isLocal();
    }

    public void setLocal(boolean local) {
        indexDefinition.setLocal(local);
    }

    public SQLExpr getDbPartitionBy() {
        return indexDefinition.getDbPartitionBy();
    }

    public void setDbPartitionBy(SQLExpr x) {
        indexDefinition.setDbPartitionBy(x);
    }

    public SQLExpr getTablePartitions() {
        return indexDefinition.getTbPartitions();
    }

    public void setTablePartitions(SQLExpr x) {
        indexDefinition.setTbPartitions(x);
    }

    public SQLExpr getTablePartitionBy() {
        return indexDefinition.getTbPartitionBy();
    }

    public void setTablePartitionBy(SQLExpr x) {
        indexDefinition.setTbPartitionBy(x);
    }

    public boolean isStoring() {
        return storing;
    }

    public void setStoring(boolean storing) {
        this.storing = storing;
    }

    @Override
    public List<SQLName> getCovering() {
        return indexDefinition.getCovering();
    }

    @Override
    public List<SQLSelectOrderByItem> getColumns() {
        return indexDefinition.getColumns();
    }

    public boolean isIfNotExists() {
        return ifNotExists;
    }

    public void setIfNotExists(boolean ifNotExists) {
        this.ifNotExists = ifNotExists;
    }
}
