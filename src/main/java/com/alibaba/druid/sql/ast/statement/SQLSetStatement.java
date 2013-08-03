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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSetStatement extends SQLStatementImpl {

    private List<SQLAssignItem> items = new ArrayList<SQLAssignItem>();

    public SQLSetStatement(){
    }

    public SQLSetStatement(SQLExpr target, SQLExpr value){
        this.items.add(new SQLAssignItem(target, value));
    }

    public List<SQLAssignItem> getItems() {
        return items;
    }

    public void setItems(List<SQLAssignItem> items) {
        this.items = items;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
        }
        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("SET ");

        for (int i = 0; i < items.size(); ++i) {
            if (i != 0) {
                buf.append(", ");
            }

            SQLAssignItem item = items.get(i);
            item.output(buf);
        }
    }
}
