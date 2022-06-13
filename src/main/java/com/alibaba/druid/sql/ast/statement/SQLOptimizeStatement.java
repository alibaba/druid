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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLOptimizeStatement extends SQLStatementImpl {
    protected final List<SQLExprTableSource> tableSources = new ArrayList<SQLExprTableSource>();
    protected SQLName cluster;

    boolean isFinal;
    boolean deduplicate;
    SQLExpr deduplicateBy;

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    public boolean isDeduplicate() {
        return deduplicate;
    }

    public void setDeduplicate(boolean deduplicate) {
        this.deduplicate = deduplicate;
    }

    public SQLExpr getDeduplicateBy() {
        return deduplicateBy;
    }

    public void setDeduplicateBy(SQLExpr deduplicateBy) {
        this.deduplicateBy = deduplicateBy;
    }

    public List<SQLExprTableSource> getTableSources() {
        return tableSources;
    }

    public void addTableSource(SQLExprTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSources.add(tableSource);
    }

    public SQLName getCluster() {
        return cluster;
    }

    public void setCluster(SQLName cluster) {
        this.cluster = cluster;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSources);
            acceptChild(visitor, cluster);
        }
        visitor.endVisit(this);
    }

}
