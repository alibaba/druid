/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
import java.util.List;

import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlChecksumTableStatement extends MySqlStatementImpl {
    private final List<SQLTableSource> tables = new ArrayList<SQLTableSource>();

    private Boolean isQuickOrExtended;

    public MySqlChecksumTableStatement() {

    }

    public void addTable(SQLTableSource table) {
        if (table == null) {
            return;
        }

        table.setParent(this);
        tables.add(table);
    }

    public List<SQLTableSource> getTables() {
        return tables;
    }

    public Boolean isQuickOrExtended() {
        return isQuickOrExtended;
    }

    public void setQuickOrExtended(Boolean quickOrExtended) {
        isQuickOrExtended = quickOrExtended;
    }

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tables);
        }
        visitor.endVisit(this);
    }
}
