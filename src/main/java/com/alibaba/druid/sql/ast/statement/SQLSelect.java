/*
 * Copyright 2011 Alibaba Group.
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

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSelect extends SQLObjectImpl {

    private static final long serialVersionUID = 1L;

    protected SQLSelectQuery  query;
    protected SQLOrderBy      orderBy;

    public SQLSelect(){

    }

    public SQLSelectQuery getQuery() {
        return this.query;
    }

    public void setQuery(SQLSelectQuery query) {
        this.query = query;
    }

    public SQLOrderBy getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public void output(StringBuffer buf) {
        this.query.output(buf);
        buf.append(" ");

        if (this.orderBy != null) this.orderBy.output(buf);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.query);
            acceptChild(visitor, this.orderBy);
        }

        visitor.endVisit(this);
    }

}
