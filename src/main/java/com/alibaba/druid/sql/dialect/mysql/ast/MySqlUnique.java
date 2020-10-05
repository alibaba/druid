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
package com.alibaba.druid.sql.dialect.mysql.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlUnique extends MySqlKey {

    public MySqlUnique(){

    }

    protected void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.getName());
            acceptChild(visitor, this.getColumns());
        }
        visitor.endVisit(this);
    }

    public MySqlUnique clone() {
        MySqlUnique x = new MySqlUnique();
        cloneTo(x);
        return x;
    }

    public SQLExpr getDbPartitionBy() {
        return indexDefinition.getDbPartitionBy();
    }

    public void setDbPartitionBy(SQLExpr x) {
        indexDefinition.setDbPartitionBy(x);
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
}
