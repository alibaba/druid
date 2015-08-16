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
package com.alibaba.druid.sql.dialect.sqlserver.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerOutput;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.dialect.sqlserver.visitor.SQLServerASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

public class SQLServerUpdateStatement extends SQLUpdateStatement implements SQLServerStatement {

    private SQLServerTop    top;
    private SQLTableSource  from;
    private SQLServerOutput output;
    
    public SQLServerUpdateStatement(){
        super (JdbcConstants.SQL_SERVER);
    }

    public SQLServerTop getTop() {
        return top;
    }

    public void setTop(SQLServerTop top) {
        this.top = top;
    }

    public SQLTableSource getFrom() {
        return from;
    }

    public void setFrom(SQLTableSource from) {
        this.from = from;
    }

    public SQLServerOutput getOutput() {
        return output;
    }

    public void setOutput(SQLServerOutput output) {
        this.output = output;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((SQLServerASTVisitor) visitor);
    }

    @Override
    public void accept0(SQLServerASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, top);
            acceptChild(visitor, tableSource);
            acceptChild(visitor, items);
            acceptChild(visitor, output);
            acceptChild(visitor, from);
            acceptChild(visitor, where);
        }
        visitor.endVisit(this);
    }

}
