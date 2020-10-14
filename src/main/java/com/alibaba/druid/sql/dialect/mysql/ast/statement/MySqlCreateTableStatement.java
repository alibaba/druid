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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLBooleanExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.ads.visitor.AdsOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlExprImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlShowColumnOutpuVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySqlCreateTableStatement extends SQLCreateTableStatement implements MySqlStatement {
    private List<SQLCommentHint>   hints        = new ArrayList<SQLCommentHint>();
    private List<SQLCommentHint>   optionHints  = new ArrayList<SQLCommentHint>();
    private SQLName                tableGroup;
    protected SQLExpr              dbPartitionBy;//for drds
    protected SQLExpr              dbPartitions;//for drds
    protected SQLExpr              tablePartitionBy;//for drds
    protected SQLExpr              tablePartitions;//for drds
    protected MySqlExtPartition    exPartition; //for drds
    protected SQLName              storedBy; // for ads
    protected SQLName              distributeByType; // for ads
    protected List<SQLName>        distributeBy = new ArrayList<SQLName>();
    protected boolean              isBroadCast;
    protected Map<String, SQLName> with = new HashMap<String, SQLName>(3); // for ads

    protected SQLName archiveBy; // adb
    protected Boolean withData;

    public MySqlCreateTableStatement(){
        super (DbType.mysql);
    }

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    @Deprecated
    public SQLSelect getQuery() {
        return select;
    }

    @Deprecated
    public void setQuery(SQLSelect query) {
        this.select = query;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof MySqlASTVisitor) {
            accept0((MySqlASTVisitor) visitor);
        } else if (visitor instanceof AdsOutputVisitor) {
            accept0((AdsOutputVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }

    public void accept0(AdsOutputVisitor visitor) {
        if (visitor.visit(this)) {
            for (int i = 0; i < hints.size(); i++) {
                final SQLCommentHint hint = hints.get(i);
                if (hint != null) {
                    hint.accept(visitor);
                }
            }

            if (tableSource != null) {
                tableSource.accept(visitor);
            }

            for (int i = 0; i < tableElementList.size(); i++) {
                final SQLTableElement element = tableElementList.get(i);
                if (element != null) {
                    element.accept(visitor);
                }
            }

            if (like != null) {
                like.accept(visitor);
            }

            if (select != null) {
                select.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            for (int i = 0; i < hints.size(); i++) {
                final SQLCommentHint hint = hints.get(i);
                if (hint != null) {
                    hint.accept(visitor);
                }
            }

            if (tableSource != null) {
                tableSource.accept(visitor);
            }

            for (int i = 0; i < tableElementList.size(); i++) {
                final SQLTableElement element = tableElementList.get(i);
                if (element != null) {
                    element.accept(visitor);
                }
            }

            if (like != null) {
                like.accept(visitor);
            }

            if (select != null) {
                select.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public static class TableSpaceOption extends MySqlExprImpl {

        private SQLName name;
        private SQLExpr storage;

        public SQLName getName() {
            return name;
        }

        public void setName(SQLName name) {
            if (name != null) {
                name.setParent(this);
            }
            this.name = name;
        }

        public SQLExpr getStorage() {
            return storage;
        }

        public void setStorage(SQLExpr storage) {
            if (storage != null) {
                storage.setParent(this);
            }
            this.storage = storage;
        }

        @Override
        public void accept0(MySqlASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, getName());
                acceptChild(visitor, getStorage());
            }
            visitor.endVisit(this);
        }

        public TableSpaceOption clone() {
            TableSpaceOption x = new TableSpaceOption();

            if (name != null) {
                x.setName(name.clone());
            }

            if (storage != null) {
                x.setStorage(storage.clone());
            }

            return x;
        }

        @Override
        public List<SQLObject> getChildren() {
            return null;
        }

    }

    public List<SQLCommentHint> getOptionHints() {
        return optionHints;
    }

    public void setOptionHints(List<SQLCommentHint> optionHints) {
        this.optionHints = optionHints;
    }

    
    public SQLName getTableGroup() {
        return tableGroup;
    }

    public void setTableGroup(SQLName tableGroup) {
        this.tableGroup = tableGroup;
    }

    public void setTableGroup(String tableGroup) {
        if (StringUtils.isEmpty(tableGroup)) {
            this.tableGroup = null;
        } else {
            this.tableGroup = new SQLIdentifierExpr(tableGroup);
        }
    }

    @Override
    public void simplify() {
        tableOptions.clear();
        tblProperties.clear();
        super.simplify();
    }

    public void showCoumns(Appendable out) throws IOException {
        this.accept(new MySqlShowColumnOutpuVisitor(out));
    }

    public List<MySqlKey> getMysqlKeys() {
        List<MySqlKey> mySqlKeys = new ArrayList<MySqlKey>();
        for (SQLTableElement element : this.getTableElementList()) {
            if (element instanceof MySqlKey) {
                mySqlKeys.add((MySqlKey)element);
            }
        }
        return mySqlKeys;
    }

    public List<MySqlTableIndex> getMysqlIndexes() {
        List<MySqlTableIndex> indexList = new ArrayList<MySqlTableIndex>();
        for (SQLTableElement element : this.getTableElementList()) {
            if (element instanceof MySqlTableIndex) {
                indexList.add((MySqlTableIndex)element);
            }
        }
        return indexList;
    }

    public boolean apply(MySqlRenameTableStatement x) {
        for (MySqlRenameTableStatement.Item item : x.getItems()) {
            if (apply(item)) {
                return true;
            }
        }

        return false;
    }

    protected boolean alterApply(SQLAlterTableItem item) {
        if (item instanceof MySqlAlterTableAlterColumn) {
            return apply((MySqlAlterTableAlterColumn) item);

        } else if (item instanceof MySqlAlterTableChangeColumn) {
            return apply((MySqlAlterTableChangeColumn) item);

        } else if (item instanceof SQLAlterCharacter) {
            return apply((SQLAlterCharacter) item);

        } else if (item instanceof MySqlAlterTableModifyColumn) {
            return apply((MySqlAlterTableModifyColumn) item);

        } else if (item instanceof MySqlAlterTableOption) {
            return apply((MySqlAlterTableOption) item);
        }

        return super.alterApply(item);
    }

    public boolean apply(SQLAlterTableAddIndex item) {
        SQLName name = item.getIndexDefinition().getName();
        if (name != null) {
            long nameHashCode = name.nameHashCode64();
            for (int i = 0; i < tableElementList.size(); i++) {
                SQLTableElement e = tableElementList.get(i);
                if (e instanceof MySqlTableIndex) {
                    SQLName name1 = ((MySqlTableIndex) e).getName();
                    if (name1 != null && name1.nameHashCode64() == nameHashCode) {
                        return false;
                    }
                }
            }
        }

        if (item.isUnique()) {
            MySqlUnique x = new MySqlUnique();
            item.cloneTo(x);
            x.setParent(this);
            this.tableElementList.add(x);
            return true;
        }

        if (item.isKey()) {
            MySqlKey x = new MySqlKey();
            item.cloneTo(x);
            x.setParent(this);
            this.tableElementList.add(x);
            return true;
        }

        MySqlTableIndex x = new MySqlTableIndex();
        item.cloneTo(x);
        x.setParent(this);
        this.tableElementList.add(x);
        return true;
    }

    public boolean apply(MySqlAlterTableOption item) {
        addOption(item.getName(), (SQLExpr) item.getValue());
        return true;
    }

    public boolean apply(SQLAlterCharacter item) {
        SQLExpr charset = item.getCharacterSet();
        if (charset != null) {
            addOption("CHARACTER SET", charset);
        }

        SQLExpr collate = item.getCollate();
        if (collate != null) {
            addOption("COLLATE", collate);
        }
        return true;
    }

    public boolean apply(MySqlRenameTableStatement.Item item) {
        if (!SQLUtils.nameEquals((SQLName) item.getName(), this.getName())) {
            return false;
        }
        this.setName((SQLName) item.getTo().clone());
        return true;
    }

    public boolean apply(MySqlAlterTableAlterColumn x) {
        int columnIndex = columnIndexOf(x.getColumn());
        if (columnIndex == -1) {
            return false;
        }

        SQLExpr defaultExpr = x.getDefaultExpr();
        SQLColumnDefinition column = (SQLColumnDefinition) tableElementList.get(columnIndex);

        if (x.isDropDefault()) {
            column.setDefaultExpr(null);
        } else if (defaultExpr != null) {
            column.setDefaultExpr(defaultExpr);
        }

        return true;
    }

    public boolean apply(MySqlAlterTableChangeColumn item) {
        SQLName columnName = item.getColumnName();
        int columnIndex = columnIndexOf(columnName);
        if (columnIndex == -1) {
            return false;
        }

        int afterIndex = columnIndexOf(item.getAfterColumn());
        int beforeIndex = columnIndexOf(item.getFirstColumn());

        int insertIndex = -1;
        if (beforeIndex != -1) {
            insertIndex = beforeIndex;
        } else if (afterIndex != -1) {
            insertIndex = afterIndex + 1;
        } else if (item.isFirst()) {
            insertIndex = 0;
        }

        SQLColumnDefinition column = item.getNewColumnDefinition().clone();
        column.setParent(this);
        if (insertIndex == -1 || insertIndex == columnIndex) {
            tableElementList.set(columnIndex, column);
        } else {
            if (insertIndex > columnIndex) {
                tableElementList.add(insertIndex, column);
                tableElementList.remove(columnIndex);
            } else {
                tableElementList.remove(columnIndex);
                tableElementList.add(insertIndex, column);
            }
        }

        for (int i = 0; i < tableElementList.size(); i++) {
            SQLTableElement e = tableElementList.get(i);
            if(e instanceof MySqlTableIndex) {
                ((MySqlTableIndex) e).applyColumnRename(columnName, column);
            } else if (e instanceof SQLUnique) {
                SQLUnique unique = (SQLUnique) e;
                unique.applyColumnRename(columnName, column);
            }
        }

        return true;
    }

    public boolean apply(MySqlAlterTableModifyColumn item) {
        SQLColumnDefinition column = item.getNewColumnDefinition().clone();
        SQLName columnName = column.getName();

        int columnIndex = columnIndexOf(columnName);
        if (columnIndex == -1) {
            return false;
        }

        int afterIndex = columnIndexOf(item.getAfterColumn());
        int beforeIndex = columnIndexOf(item.getFirstColumn());

        int insertIndex = -1;
        if (beforeIndex != -1) {
            insertIndex = beforeIndex;
        } else if (afterIndex != -1) {
            insertIndex = afterIndex + 1;
        }

        if (item.isFirst()) {
            insertIndex = 0;
        }

        column.setParent(this);
        if (insertIndex == -1 || insertIndex == columnIndex) {
            tableElementList.set(columnIndex, column);
            return true;
        } else {
            if (insertIndex > columnIndex) {
                tableElementList.add(insertIndex, column);
                tableElementList.remove(columnIndex);
            } else {
                tableElementList.remove(columnIndex);
                tableElementList.add(insertIndex, column);
            }
        }

        // Check key length like change.
        // Just old name -> old name.
        for (int i = 0; i < tableElementList.size(); i++) {
            SQLTableElement e = tableElementList.get(i);
            if(e instanceof MySqlTableIndex) {
                ((MySqlTableIndex) e).applyColumnRename(columnName, column);
            } else if (e instanceof SQLUnique) {
                SQLUnique unique = (SQLUnique) e;
                unique.applyColumnRename(columnName, column);
            }
        }

        return true;
    }

    public void cloneTo(MySqlCreateTableStatement x) {
        super.cloneTo(x);
        if (partitioning != null) {
            x.setPartitioning(partitioning.clone());
        }
        for (SQLCommentHint hint : hints) {
            SQLCommentHint h2 = hint.clone();
            h2.setParent(x);
            x.hints.add(h2);
        }
        for (SQLCommentHint hint : optionHints) {
            SQLCommentHint h2 = hint.clone();
            h2.setParent(x);
            x.optionHints.add(h2);
        }
        if (like != null) {
            x.setLike(like.clone());
        }
        if (tableGroup != null) {
            x.setTableGroup(tableGroup.clone());
        }

        if (dbPartitionBy != null) {
            x.setDbPartitionBy(dbPartitionBy.clone());
        }

        if (dbPartitions != null) {
            x.setDbPartitionBy(dbPartitions.clone());
        }

        if (tablePartitionBy != null) {
            x.setTablePartitionBy(tablePartitionBy.clone());
        }

        if (tablePartitions != null) {
            x.setTablePartitions(tablePartitions.clone());
        }

        if (exPartition != null) {
            x.setExPartition(exPartition.clone());
        }

        if (archiveBy != null) {
            x.setArchiveBy(archiveBy.clone());
        }

        if (distributeByType != null) {
            x.setDistributeByType(distributeByType.clone());
        }

        if (distributeByType != null) {
            for (SQLName sqlName : distributeBy) {
                x.getDistributeBy().add(sqlName.clone());
            }
        }

    }

    public MySqlCreateTableStatement clone() {
        MySqlCreateTableStatement x = new MySqlCreateTableStatement();
        cloneTo(x);
        return x;
    }

    public SQLExpr getDbPartitionBy() {
        return dbPartitionBy;
    }

    public void setDbPartitionBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.dbPartitionBy = x;
    }

    public SQLExpr getTablePartitionBy() {
        return tablePartitionBy;
    }

    public void setTablePartitionBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.tablePartitionBy = x;
    }


    public SQLName getDistributeByType() {
        return distributeByType;
    }

    public void setDistributeByType(SQLName distributeByType) {
        this.distributeByType = distributeByType;
    }

    public List<SQLName> getDistributeBy() {
        return distributeBy;
    }

    public SQLExpr getTbpartitions() {
        return tablePartitions;
    }

    public SQLExpr getTablePartitions() {
        return tablePartitions;
    }

    public void setTablePartitions(SQLExpr x) {
        this.tablePartitions = x;
    }

    public SQLExpr getDbpartitions() {
        return dbPartitions;
    }

    public void setDbPartitions(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.dbPartitions = x;
    }

    public MySqlExtPartition getExtPartition() {
        return exPartition;
    }

    public void setExPartition(MySqlExtPartition x) {
        if (x != null) {
            x.setParent(this);
        }
        this.exPartition = x;
    }

    public SQLExpr getDbPartitions() {
        return dbPartitions;
    }

    public SQLName getStoredBy() {
        return storedBy;
    }

    public void setStoredBy(SQLName storedBy) {
        this.storedBy = storedBy;
    }

    public Map<String, SQLName> getWith() {
        return with;
    }

    public boolean isBroadCast() {
        return isBroadCast;
    }

    public void setBroadCast(boolean broadCast) {
        isBroadCast = broadCast;
    }

    public SQLName getArchiveBy() {
        return archiveBy;
    }

    public void setArchiveBy(SQLName archiveBy) {
        this.archiveBy = archiveBy;
    }

    public Boolean getWithData() {
        return withData;
    }

    public void setWithData(Boolean withData) {
        this.withData = withData;
    }
}
