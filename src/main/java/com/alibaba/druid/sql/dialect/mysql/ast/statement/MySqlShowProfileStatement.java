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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

public class MySqlShowProfileStatement extends MySqlStatementImpl implements MySqlShowStatement {

    private List<Type> types = new ArrayList<Type>();

    private SQLExpr    forQuery;

    private Limit      limit;

    public void accept0(MySqlASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public SQLExpr getForQuery() {
        return forQuery;
    }

    public void setForQuery(SQLExpr forQuery) {
        this.forQuery = forQuery;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public static enum Type {
        ALL("ALL"), BLOCK_IO("BLOCK IO"), CONTEXT_SWITCHES("CONTEXT SWITCHES"), CPU("CPU"), IPC("IPC"),
        MEMORY("MEMORY"), PAGE_FAULTS("PAGE FAULTS"), SOURCE("SOURCE"), SWAPS("SWAPS");

        public final String name;

        Type(String name){
            this.name = name;
        }
    }

}
