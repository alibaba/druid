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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLPartitioningClause;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;

@SuppressWarnings("serial")
public class MySqlCreateTableStatement extends SQLCreateTableStatement {

    private boolean               ifNotExiists = false;

    private Map<String, String>   tableOptions = new LinkedHashMap<String, String>();

    protected SQLSelect           query;

    private SQLPartitioningClause partitioning;

    public MySqlCreateTableStatement(){

    }

    private List<SQLCommentHint> hints = new ArrayList<SQLCommentHint>();

    public List<SQLCommentHint> getHints() {
        return hints;
    }

    public void setHints(List<SQLCommentHint> hints) {
        this.hints = hints;
    }

    public void setTableOptions(Map<String, String> tableOptions) {
        this.tableOptions = tableOptions;
    }

    public SQLPartitioningClause getPartitioning() {
        return partitioning;
    }

    public void setPartitioning(SQLPartitioningClause partitioning) {
        this.partitioning = partitioning;
    }

    public Map<String, String> getTableOptions() {
        return tableOptions;
    }

    public SQLSelect getQuery() {
        return query;
    }

    public void setQuery(SQLSelect query) {
        this.query = query;
    }

    @Override
    public void output(StringBuffer buf) {
        if (Type.GLOBAL_TEMPORARY.equals(this.type)) {
            buf.append("CREATE TEMPORARY TABLE ");
        } else {
            buf.append("CREATE TABLE ");
        }

        if (ifNotExiists) {
            buf.append("IF NOT EXISTS ");
        }

        this.tableSource.output(buf);
        buf.append(" ");
        buf.append("(");
        for (int i = 0, size = tableElementList.size(); i < size; ++i) {
            if (i != 0) {
                buf.append(", ");
            }
            tableElementList.get(i).output(buf);
        }
        buf.append(")");

        if (query != null) {
            buf.append(" ");
            query.output(buf);
        }
    }

    public boolean isIfNotExiists() {
        return ifNotExiists;
    }

    public void setIfNotExiists(boolean ifNotExiists) {
        this.ifNotExiists = ifNotExiists;
    }

}
