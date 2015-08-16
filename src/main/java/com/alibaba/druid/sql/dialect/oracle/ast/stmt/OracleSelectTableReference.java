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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.FlashbackQueryClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.PartitionExtensionClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.SampleClause;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleSelectTableReference extends SQLExprTableSource implements OracleSelectTableSource {

    private boolean                    only = false;
    protected OracleSelectPivotBase    pivot;

    protected PartitionExtensionClause partition;
    protected SampleClause             sampleClause;

    protected FlashbackQueryClause     flashback;

    public OracleSelectTableReference(){

    }

    public FlashbackQueryClause getFlashback() {
        return flashback;
    }

    public void setFlashback(FlashbackQueryClause flashback) {
        this.flashback = flashback;
    }

    public PartitionExtensionClause getPartition() {
        return partition;
    }

    public void setPartition(PartitionExtensionClause partition) {
        this.partition = partition;
    }

    public boolean isOnly() {
        return this.only;
    }

    public void setOnly(boolean only) {
        this.only = only;
    }

    public SampleClause getSampleClause() {
        return sampleClause;
    }

    public void setSampleClause(SampleClause sampleClause) {
        this.sampleClause = sampleClause;
    }

    public OracleSelectPivotBase getPivot() {
        return pivot;
    }

    public void setPivot(OracleSelectPivotBase pivot) {
        this.pivot = pivot;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
            acceptChild(visitor, this.partition);
            acceptChild(visitor, this.sampleClause);
            acceptChild(visitor, this.pivot);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        if (this.only) {
            buf.append("ONLY (");
            this.expr.output(buf);
            buf.append(")");
        } else {
            this.expr.output(buf);
        }

        if (this.pivot != null) {
            buf.append(" ");
            this.pivot.output(buf);
        }

        if ((this.alias != null) && (this.alias.length() != 0)) {
            buf.append(this.alias);
        }
    }
    
    public String toString () {
        return SQLUtils.toOracleString(this);
    }
}
