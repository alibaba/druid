/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dbLink == null) ? 0 : dbLink.hashCode());
        result = prime * result + ((partition == null) ? 0 : partition.hashCode());
        result = prime * result + ((partitionFor == null) ? 0 : partitionFor.hashCode());
        result = prime * result + ((subPartition == null) ? 0 : subPartition.hashCode());
        result = prime * result + ((subPartitionFor == null) ? 0 : subPartitionFor.hashCode());
        result = prime * result + ((table == null) ? 0 : table.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        OracleTableExpr other = (OracleTableExpr) obj;
        if (dbLink == null) {
            if (other.dbLink != null) {
                return false;
            }
        } else if (!dbLink.equals(other.dbLink)) {
            return false;
        }
        if (partition == null) {
            if (other.partition != null) {
                return false;
            }
        } else if (!partition.equals(other.partition)) {
            return false;
        }
        if (partitionFor == null) {
            if (other.partitionFor != null) {
                return false;
            }
        } else if (!partitionFor.equals(other.partitionFor)) {
            return false;
        }
        if (subPartition == null) {
            if (other.subPartition != null) {
                return false;
            }
        } else if (!subPartition.equals(other.subPartition)) {
            return false;
        }
        if (subPartitionFor == null) {
            if (other.subPartitionFor != null) {
                return false;
            }
        } else if (!subPartitionFor.equals(other.subPartitionFor)) {
            return false;
        }
        if (table == null) {
            if (other.table != null) {
                return false;
            }
        } else if (!table.equals(other.table)) {
            return false;
        }
        return true;
    }
}
