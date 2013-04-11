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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSubqueryTableSource extends SQLTableSourceImpl {

    private static final long serialVersionUID = 1L;

    protected SQLSelect       select;

    public SQLSubqueryTableSource(){

    }

    public SQLSubqueryTableSource(String alias){
        super(alias);
    }

    public SQLSubqueryTableSource(SQLSelect select, String alias){
        super(alias);
        this.setSelect(select);
    }

    public SQLSubqueryTableSource(SQLSelect select){
        this.setSelect(select);
    }

    public SQLSelect getSelect() {
        return this.select;
    }

    public void setSelect(SQLSelect select) {
        if (select != null) {
            select.setParent(this);
        }
        this.select = select;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, select);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("(");
        this.select.output(buf);
        buf.append(")");
    }
}
