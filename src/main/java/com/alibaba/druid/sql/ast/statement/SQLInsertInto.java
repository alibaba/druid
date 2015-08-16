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
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;

public abstract class SQLInsertInto extends SQLObjectImpl {

    protected SQLExprTableSource  tableSource;

    protected final List<SQLExpr> columns = new ArrayList<SQLExpr>();
    protected ValuesClause        values;
    protected SQLSelect           query;

    public SQLInsertInto(){

    }

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

    public void setQuery(SQLSelect query) {
        this.query = query;
    }

    public List<SQLExpr> getColumns() {
        return columns;
    }

    public ValuesClause getValues() {
        return values;
    }

    public void setValues(ValuesClause values) {
        this.values = values;
    }
}
