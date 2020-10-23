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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLIndex;
import com.alibaba.druid.sql.ast.SQLIndexDefinition;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObject;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.List;

public class MySqlTableIndex extends SQLConstraintImpl implements SQLTableElement, SQLIndex, MySqlObject {

    private SQLIndexDefinition indexDefinition = new SQLIndexDefinition();

    public MySqlTableIndex() {
        indexDefinition.setParent(this);
    }

    public SQLIndexDefinition getIndexDefinition() {
        return indexDefinition;
    }

    public SQLName getName() {
        return indexDefinition.getName();
    }

    public String getIndexType() {
        return indexDefinition.getType();
    }

    public void setIndexType(String indexType) {
        indexDefinition.setType(indexType);
    }

    public void setName(SQLName name) {
        indexDefinition.setName(name);
    }

    public List<SQLSelectOrderByItem> getColumns() {
        return indexDefinition.getColumns();
    }

    public void addColumn(SQLSelectOrderByItem column) {
        if (column != null) {
            column.setParent(this);
        }
        this.indexDefinition.getColumns().add(column);
    }

    public void accept0(SQLASTVisitor visitor) {
        accept0((MySqlASTVisitor) visitor);
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (indexDefinition.getName() != null) {
                indexDefinition.getName().accept(visitor);
            }

            for (int i = 0; i < indexDefinition.getColumns().size(); i++) {
                final SQLSelectOrderByItem item = indexDefinition.getColumns().get(i);
                if (item != null) {
                    item.accept(visitor);
                }
            }

            for (int i = 0; i < indexDefinition.getCovering().size(); i++) {
                final SQLName item = indexDefinition.getCovering().get(i);
                if (item != null) {
                    item.accept(visitor);
                }
            }
        }
        visitor.endVisit(this);
    }

    public MySqlTableIndex clone() {
        MySqlTableIndex x = new MySqlTableIndex();
        indexDefinition.cloneTo(x.indexDefinition);
        return x;
    }

    public boolean applyColumnRename(SQLName columnName, SQLColumnDefinition to) {
        for (SQLSelectOrderByItem orderByItem : getColumns()) {
            SQLExpr expr = orderByItem.getExpr();
            if (expr instanceof SQLName
                    && SQLUtils.nameEquals((SQLName) expr, columnName)) {
                orderByItem.setExpr(to.getName().clone());
                return true;
            }

            if (expr instanceof SQLMethodInvokeExpr
                    && SQLUtils.nameEquals(((SQLMethodInvokeExpr) expr).getMethodName(), columnName.getSimpleName())) {
                // More complex when with key length.
                if (1 == ((SQLMethodInvokeExpr) expr).getArguments().size() &&
                        ((SQLMethodInvokeExpr) expr).getArguments().get(0) instanceof SQLIntegerExpr) {
                    if (to.getDataType().hasKeyLength() &&
                            1 == to.getDataType().getArguments().size() &&
                            to.getDataType().getArguments().get(0) instanceof SQLIntegerExpr) {
                        int newKeyLength = ((SQLIntegerExpr)to.getDataType().getArguments().get(0)).getNumber().intValue();
                        int oldKeyLength = ((SQLIntegerExpr)((SQLMethodInvokeExpr) expr).getArguments().get(0)).getNumber().intValue();
                        if (newKeyLength > oldKeyLength) {
                            // Change name and keep key length.
                            ((SQLMethodInvokeExpr) expr).setMethodName(to.getName().getSimpleName());
                            return true;
                        }
                    }
                    // Remove key length.
                    orderByItem.setExpr(to.getName().clone());
                    return true;
                }
            }
        }
        return false;
    }

    public boolean applyDropColumn(SQLName columnName) {
        for (int i = indexDefinition.getColumns().size() - 1; i >= 0; i--) {
            SQLExpr expr = indexDefinition.getColumns().get(i).getExpr();
            if (expr instanceof SQLName
                    && SQLUtils.nameEquals((SQLName) expr, columnName)) {
                indexDefinition.getColumns().remove(i);
                return true;
            }
            if (expr instanceof SQLMethodInvokeExpr
                    && SQLUtils.nameEquals(((SQLMethodInvokeExpr) expr).getMethodName(), columnName.getSimpleName())) {
                indexDefinition.getColumns().remove(i);
                return true;
            }
        }
        return false;
    }

    public void addOption(String name, SQLExpr value) {
        indexDefinition.addOption(name, value);
    }

    public SQLExpr getOption(String name) {
        return indexDefinition.getOption(name);
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

    public List<SQLAssignItem> getOptions() {
        return indexDefinition.getCompatibleOptions();
    }

    public SQLExpr getComment() {
        return indexDefinition.getOptions().getComment();
    }

    public void setComment(SQLExpr x) {
        this.indexDefinition.getOptions().setComment(x);
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

    public void setCovering(List<SQLName> covering) {
        indexDefinition.setCovering(covering);
    }

    public boolean isGlobal() {
        return this.indexDefinition.isGlobal();
    }

    public void setGlobal(boolean global) {
        this.indexDefinition.setGlobal(global);
    }

    public boolean isLocal() {
        return this.indexDefinition.isLocal();
    }

    public void setLocal(boolean local) {
        this.indexDefinition.setLocal(local);
    }

    @Override
    public List<SQLName> getCovering() {
        return indexDefinition.getCovering();
    }

    public SQLName getIndexAnalyzerName() {
        return indexDefinition.getIndexAnalyzerName();
    }

    public void setIndexAnalyzerName(SQLName indexAnalyzerName) {
        this.indexDefinition.setIndexAnalyzerName(indexAnalyzerName);
    }

    public SQLName getQueryAnalyzerName() {
        return indexDefinition.getQueryAnalyzerName();
    }

    public void setQueryAnalyzerName(SQLName queryAnalyzerName) {
        this.indexDefinition.setQueryAnalyzerName(queryAnalyzerName);
    }

    public SQLName getWithDicName() {
        return indexDefinition.getWithDicName();
    }

    public void setWithDicName(SQLName withDicName) {
        this.indexDefinition.setWithDicName(withDicName);
    }

    public SQLName getAnalyzerName() {
        return indexDefinition.getAnalyzerName();
    }

    public void setAnalyzerName(SQLName analyzerName) {
        this.indexDefinition.setAnalyzerName(analyzerName);
    }
}
