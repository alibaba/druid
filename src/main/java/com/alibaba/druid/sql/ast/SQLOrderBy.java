/*
 * Copyright 2011 Alibaba Group. Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and limitations under the
 * License.
 */
package com.alibaba.druid.sql.ast;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectOrderByItem;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

@SuppressWarnings("serial")
public class SQLOrderBy extends SQLObjectImpl {

    protected final List<SQLSelectOrderByItem> items = new ArrayList<SQLSelectOrderByItem>();

    public SQLOrderBy(){

    }

    public List<SQLSelectOrderByItem> getItems() {
        return this.items;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
        }

        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("ORDER ");
        buf.append("BY ");

        int i = 0;
        for (int size = this.items.size(); i < size; ++i) {
            if (i != 0) {
                buf.append(", ");
            }
            ((OracleSelectOrderByItem) this.items.get(i)).output(buf);
        }
    }
}
