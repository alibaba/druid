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
package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLPartition;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSegmentAttributesImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class OracleUsingIndexClause extends OracleSegmentAttributesImpl implements OracleSQLObject {

    private SQLObject           index;
    private Boolean             enable            = null;

    private boolean             computeStatistics = false;
    private boolean             reverse;

    private List<SQLPartition> localPartitionIndex = new ArrayList<SQLPartition>();

    public OracleUsingIndexClause(){

    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, index);
            acceptChild(visitor, tablespace);
            acceptChild(visitor, storage);
        }
        visitor.endVisit(this);
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public boolean isComputeStatistics() {
        return computeStatistics;
    }

    public void setComputeStatistics(boolean computeStatistics) {
        this.computeStatistics = computeStatistics;
    }

    public SQLObject getIndex() {
        return index;
    }

    public void setIndex(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.index = x;
    }

    public void setIndex(SQLCreateIndexStatement x) {
        if (x != null) {
            x.setParent(this);
        }
        this.index = x;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public List<SQLPartition> getLocalPartitionIndex() {
        return localPartitionIndex;
    }

    public void cloneTo(OracleUsingIndexClause x) {
        super.cloneTo(x);
        if (index != null) {
            SQLObject idx = index.clone();
            idx.setParent(x);
            x.index = idx;
        }
        x.enable = enable;
        x.computeStatistics = computeStatistics;
        x.reverse = reverse;

        for (SQLPartition p : localPartitionIndex) {
            SQLPartition p2 = p.clone();
            p2.setParent(x);
            x.localPartitionIndex.add(p2);
        }
    }

    public OracleUsingIndexClause clone() {
        OracleUsingIndexClause x = new OracleUsingIndexClause();
        cloneTo(x);
        return x;
    }
}
