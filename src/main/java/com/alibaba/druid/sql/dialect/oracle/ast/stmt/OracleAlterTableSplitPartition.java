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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleAlterTableSplitPartition extends OracleAlterTableItem {

    private SQLName                        name;
    private List<SQLExpr>                  at            = new ArrayList<SQLExpr>();
    private List<SQLExpr>                  values        = new ArrayList<SQLExpr>();
    private List<NestedTablePartitionSpec> into          = new ArrayList<NestedTablePartitionSpec>();

    private UpdateIndexesClause            updateIndexes = null;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, at);
            acceptChild(visitor, values);
            acceptChild(visitor, updateIndexes);
        }
        visitor.endVisit(this);
    }

    public UpdateIndexesClause getUpdateIndexes() {
        return updateIndexes;
    }

    public void setUpdateIndexes(UpdateIndexesClause updateIndexes) {
        this.updateIndexes = updateIndexes;
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public List<SQLExpr> getAt() {
        return at;
    }

    public void setAt(List<SQLExpr> at) {
        this.at = at;
    }

    public List<NestedTablePartitionSpec> getInto() {
        return into;
    }

    public void setInto(List<NestedTablePartitionSpec> into) {
        this.into = into;
    }

    public List<SQLExpr> getValues() {
        return values;
    }

    public void setValues(List<SQLExpr> values) {
        this.values = values;
    }

    public static class NestedTablePartitionSpec extends OracleSQLObjectImpl {

        private SQLName         partition;

        private List<SQLObject> segmentAttributeItems = new ArrayList<SQLObject>();

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, partition);
                acceptChild(visitor, segmentAttributeItems);
            }
            visitor.endVisit(this);
        }

        public SQLName getPartition() {
            return partition;
        }

        public void setPartition(SQLName partition) {
            this.partition = partition;
        }

        public List<SQLObject> getSegmentAttributeItems() {
            return segmentAttributeItems;
        }

        public void setSegmentAttributeItems(List<SQLObject> segmentAttributeItems) {
            this.segmentAttributeItems = segmentAttributeItems;
        }

    }

    public static class TableSpaceItem extends OracleSQLObjectImpl {

        private SQLName tablespace;

        public TableSpaceItem(){

        }

        public TableSpaceItem(SQLName tablespace){
            this.tablespace = tablespace;
        }

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, tablespace);
            }
            visitor.endVisit(this);
        }

        public SQLName getTablespace() {
            return tablespace;
        }

        public void setTablespace(SQLName tablespace) {
            this.tablespace = tablespace;
        }
    }

    public static class UpdateIndexesClause extends OracleSQLObjectImpl {

        private List<SQLObject> items = new ArrayList<SQLObject>();

        @Override
        public void accept0(OracleASTVisitor visitor) {
            if (visitor.visit(this)) {
                acceptChild(visitor, items);
            }
            visitor.endVisit(this);
        }

        public List<SQLObject> getItems() {
            return items;
        }

        public void setItems(List<SQLObject> items) {
            this.items = items;
        }

    }
}
