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
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLReplaceStatement extends SQLStatementImpl {
    protected boolean             lowPriority = false;
    protected boolean             delayed     = false;

    protected SQLExprTableSource  tableSource;
    protected final List<SQLExpr> columns     = new ArrayList<SQLExpr>();
    protected List<SQLInsertStatement.ValuesClause>  valuesList  = new ArrayList<SQLInsertStatement.ValuesClause>();
    protected SQLQueryExpr query;

    protected List<SQLCommentHint>                hints;
    protected List<SQLAssignItem> partitions;

    public SQLName getTableName() {
        if (tableSource == null) {
            return null;
        }

        return (SQLName) tableSource.getExpr();
    }

    public void setTableName(SQLName tableName) {
        this.setTableSource(new SQLExprTableSource(tableName));
    }

    public SQLExprTableSource getTableSource() {
        return tableSource;
    }

    public void setTableSource(SQLExprTableSource tableSource) {
        if (tableSource != null) {
            tableSource.setParent(this);
        }
        this.tableSource = tableSource;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public void addColumn(SQLExpr column) {
        if (column != null) {
            column.setParent(this);
        }
        this.columns.add(column);
    }

    public boolean isLowPriority() {
        return lowPriority;
    }

    public void setLowPriority(boolean lowPriority) {
        this.lowPriority = lowPriority;
    }

    public boolean isDelayed() {
        return delayed;
    }

    public void setDelayed(boolean delayed) {
        this.delayed = delayed;
    }

    public SQLQueryExpr getQuery() {
        return query;
    }

    public void setQuery(SQLQueryExpr query) {
        if (query != null) {
            query.setParent(this);
        }
        this.query = query;
    }

    public List<SQLInsertStatement.ValuesClause> getValuesList() {
        return valuesList;
    }

    @Override
    public SQLStatement clone() {
        SQLReplaceStatement x = new SQLReplaceStatement();
        x.setDbType(this.dbType);

        if (headHints != null) {
            for (SQLCommentHint h : headHints) {
                SQLCommentHint clone = h.clone();
                clone.setParent(x);
                x.headHints.add(clone);
            }
        }

        if (hints != null && !hints.isEmpty()) {
            for (SQLCommentHint h : hints) {
                SQLCommentHint clone = h.clone();
                clone.setParent(x);
                x.getHints().add(clone);
            }
        }

        x.lowPriority = this.lowPriority;
        x.delayed = this.delayed;

        if (this.tableSource != null) {
            x.tableSource = this.tableSource.clone();
        }

        for (SQLInsertStatement.ValuesClause clause : valuesList) {
            x.getValuesList().add(clause.clone());
        }

        for (SQLExpr column : columns) {
            x.addColumn(column.clone());
        }

        if (query != null) {
            x.query = this.query.clone();
        }

        if (partitions != null) {
            for (SQLAssignItem partition : partitions) {
                x.addPartition(partition.clone());
            }
        }

        return x;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSource);
            acceptChild(visitor, columns);
            acceptChild(visitor, valuesList);
            acceptChild(visitor, query);
        }
        visitor.endVisit(this);
    }

    public int getHintsSize() {
        if (hints == null) {
            return 0;
        }

        return hints.size();
    }

    public List<SQLCommentHint> getHints() {
        if (hints == null) {
            hints = new ArrayList<SQLCommentHint>(2);
        }
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    public void addPartition(SQLAssignItem partition) {
        if (partition != null) {
            partition.setParent(this);
        }

        if (partitions == null) {
            partitions = new ArrayList<SQLAssignItem>();
        }

        this.partitions.add(partition);
    }

    public List<SQLAssignItem> getPartitions() {
        return partitions;
    }
}
