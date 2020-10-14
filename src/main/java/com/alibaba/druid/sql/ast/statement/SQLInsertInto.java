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
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLInsertInto extends SQLStatementImpl implements SQLReplaceable {
    protected SQLExprTableSource        tableSource;
    protected final List<SQLExpr>       columns = new ArrayList<SQLExpr>();
    protected transient String          columnsString;
    protected transient long            columnsStringHash;
    protected SQLSelect                 query;
    protected final List<ValuesClause>  valuesList = new ArrayList<ValuesClause>();
    protected boolean                   overwrite  = false;
    protected List<SQLAssignItem>       partitions;

    public SQLInsertInto(){

    }

    public void cloneTo(SQLInsertInto x) {
        if (tableSource != null) {
            x.setTableSource(tableSource.clone());
        }
        for (SQLExpr column : columns) {
            SQLExpr column2 = column.clone();
            column2.setParent(x);
            x.columns.add(column2);
        }
        if (query != null) {
            x.setQuery(query.clone());
        }
        for (ValuesClause v : valuesList) {
            ValuesClause v2 = v.clone();
            v2.setParent(x);
            x.valuesList.add(v2);
        }

        if (hint != null) {
            x.setHint(hint.clone());
        }

        x.overwrite = overwrite;
        if (partitions != null) {
            for (SQLAssignItem item : partitions) {
                x.addPartition(item.clone());
            }
        }
    }

    public boolean replace(SQLExpr expr, SQLExpr target) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i) == expr) {
                target.setParent(this);
                columns.set(i, target);
                return true;
            }
        }

        return false;
    }

    public SQLCommentHint getHint() {
        return hint;
    }

    public void setHint(SQLCommentHint x) {
        if (x != null) {
            x.setParent(this);
        }
        this.hint = x;
    }

    public abstract SQLInsertInto clone();

    public String getAlias() {
        return tableSource.getAlias();
    }

    public void setAlias(String alias) {
        this.tableSource.setAlias(alias);
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

    public SQLName getTableName() {
        return (SQLName) tableSource.getExpr();
    }

    public void setTableName(SQLName tableName) {
        this.setTableSource(new SQLExprTableSource(tableName));
    }

    public void setTableSource(SQLName tableName) {
        this.setTableSource(new SQLExprTableSource(tableName));
    }

    public SQLSelect getQuery() {
        return query;
    }

    public void setQuery(SQLSelectQuery query) {
        this.setQuery(new SQLSelect(query));
    }

    public void setQuery(SQLSelect query) {
        if (query != null) {
            query.setParent(this);
        }
        this.query = query;
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

    public ValuesClause getValues() {
        if (valuesList.size() == 0) {
            return null;
        }
        return valuesList.get(0);
    }

    public void setValues(ValuesClause values) {
        if (valuesList.size() == 0) {
            valuesList.add(values);
        } else {
            valuesList.set(0, values);
        }
    }
    
    public List<ValuesClause> getValuesList() {
        return valuesList;
    }

    public void addValueCause(ValuesClause valueClause) {
        if (valueClause != null) {
            valueClause.setParent(this);
        }
        valuesList.add(valueClause);
    }

    public String getColumnsString() {
        return columnsString;
    }

    public long getColumnsStringHash() {
        return columnsStringHash;
    }

    public void setColumnsString(String columnsString, long columnsStringHash) {
        this.columnsString = columnsString;
        this.columnsStringHash = columnsStringHash;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
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
