/*
 * Copyright 2011 Alibaba Group. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleTableExpr extends SQLExprImpl {

    private static final long   serialVersionUID = 1L;

    private SQLExpr             table;
    private String              dbLink;
    private SQLName             partition;
    private SQLName             subPartition;
    private final List<SQLName> partitionFor     = new ArrayList<SQLName>(1);
    private final List<SQLName> subPartitionFor  = new ArrayList<SQLName>(1);

    public OracleTableExpr(){

    }

    public SQLName getPartition() {
        return this.partition;
    }

    public void setPartition(SQLName partition) {
        this.partition = partition;
    }

    public SQLName getSubPartition() {
        return this.subPartition;
    }

    public void setSubPartition(SQLName subPartition) {
        this.subPartition = subPartition;
    }

    public List<SQLName> getSubPartitionFor() {
        return this.subPartitionFor;
    }

    public List<SQLName> getPartitionFor() {
        return this.partitionFor;
    }

    public SQLExpr getTable() {
        return this.table;
    }

    public void setTable(SQLExpr table) {
        this.table = table;
    }

    public String getDbLink() {
        return this.dbLink;
    }

    public void setDbLink(String dbLink) {
        this.dbLink = dbLink;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.table);
        }

        visitor.endVisit(this);
    }
}
