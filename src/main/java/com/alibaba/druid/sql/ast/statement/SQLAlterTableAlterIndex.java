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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.List;

public class SQLAlterTableAlterIndex extends SQLObjectImpl implements SQLAlterTableItem, SQLIndex {

    private SQLIndexDefinition indexDefinition = new SQLIndexDefinition();

    public SQLAlterTableAlterIndex() {
        indexDefinition.setParent(this);
    }

    public SQLIndexDefinition getIndexDefinition() {
        return indexDefinition;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (indexDefinition.getName() != null) {
                indexDefinition.getName().accept(visitor);
            }

            for (int i = 0; i < getColumns().size(); i++) {
                final SQLSelectOrderByItem item = getColumns().get(i);
                if (item != null) {
                    item.accept(visitor);
                }
            }
            for (int i = 0; i < getCovering().size(); i++) {
                final SQLName item = getCovering().get(i);
                if (item != null) {
                    item.accept(visitor);
                }
            }
        }
        visitor.endVisit(this);
    }

    public boolean isUnique() {
        return indexDefinition.getType() != null && indexDefinition.getType().equalsIgnoreCase("UNIQUE");
    }

    public void setUnique(boolean unique) {
        indexDefinition.setType("UNIQUE");
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
        if (indexDefinition.hasOptions()) {
            return indexDefinition.getOptions().getIndexType();
        }
        return null;
    }

    public void setUsing(String using) {
        indexDefinition.getOptions().setIndexType(using);
    }

    public boolean isKey() {
        return indexDefinition.isKey();
    }

    public void setKey(boolean key) {
        indexDefinition.setKey(key);
    }

    public void cloneTo(MySqlTableIndex x) {
        indexDefinition.cloneTo(x.getIndexDefinition());
    }

    public void cloneTo(MySqlKey x) {
        indexDefinition.cloneTo(x.getIndexDefinition());
    }

    public SQLExpr getComment() {
        if (indexDefinition.hasOptions()) {
            return indexDefinition.getOptions().getComment();
        }
        return null;
    }

    public void setComment(SQLExpr comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        indexDefinition.getOptions().setComment(comment);
    }

    public SQLExpr getKeyBlockSize() {
        if (indexDefinition.hasOptions()) {
            return indexDefinition.getOptions().getKeyBlockSize();
        }
        return null;
    }

    public void setKeyBlockSize(SQLExpr keyBlockSize) {
        indexDefinition.getOptions().setKeyBlockSize(keyBlockSize);
    }

    public String getParserName() {
        if (indexDefinition.hasOptions()) {
            return indexDefinition.getOptions().getParserName();
        }
        return null;
    }

    public void setParserName(String parserName) {
        indexDefinition.getOptions().setParserName(parserName);
    }

    public boolean isHashMapType() {
        return indexDefinition.isHashMapType();
    }

    public void setHashMapType(boolean hashMapType) {
        indexDefinition.setHashMapType(hashMapType);
    }

    protected SQLExpr getOption(long hash64) {
        return indexDefinition.getOption(hash64);
    }

    public String getDistanceMeasure() {
        return indexDefinition.getDistanceMeasure();
    }

    public String getAlgorithm() {
        return indexDefinition.getAlgorithm();
    }

    public void addOption(String name, SQLExpr value) {
        indexDefinition.addOption(name, value);
    }

    public List<SQLAssignItem> getOptions() {
        return indexDefinition.getCompatibleOptions();
    }

    public boolean isGlobal() {
        return indexDefinition.isGlobal();
    }

    public void setGlobal(boolean global) {
        indexDefinition.setGlobal(global);
    }

    public SQLExpr getDbPartitionBy() {
        return indexDefinition.getDbPartitionBy();
    }

    public void setDbPartitionBy(SQLExpr x) {
        indexDefinition.setDbPartitionBy(x);
    }

    public SQLExpr getTablePartitionBy() {
        return indexDefinition.getTbPartitionBy();
    }

    public void setTablePartitionBy(SQLExpr x) {
        indexDefinition.setTbPartitionBy(x);
    }

    public SQLExpr getTablePartitions() {
        return indexDefinition.getTbPartitions();
    }

    public void setTablePartitions(SQLExpr x) {
        indexDefinition.setTbPartitions(x);
    }

    @Override
    public List<SQLName> getCovering() {
        return indexDefinition.getCovering();
    }

    @Override
    public List<SQLSelectOrderByItem> getColumns() {
        return indexDefinition.getColumns();
    }
}
