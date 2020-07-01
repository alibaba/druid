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

import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlOptimizeStatement extends MySqlStatementImpl {

    private boolean                          noWriteToBinlog = false;
    private boolean                          local           = false;

    protected final List<SQLExprTableSource> tableSources    = new ArrayList<SQLExprTableSource>();

    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, tableSources);
        }
        visitor.endVisit(this);
    }

    public boolean isNoWriteToBinlog() {
        return noWriteToBinlog;
    }

    public void setNoWriteToBinlog(boolean noWriteToBinlog) {
        this.noWriteToBinlog = noWriteToBinlog;
    }

    public boolean isLocal() {
        return local;
    }

    public void setLocal(boolean local) {
        this.local = local;
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
}
