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
package com.alibaba.druid.sql.dialect.impala.ast;

import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.impala.visitor.ImpalaASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.JdbcConstants;

import java.util.ArrayList;
import java.util.List;

public class ImpalaMultiInsertStatement extends SQLStatementImpl {

    private SQLTableSource from;

    private List<ImpalaInsert>       items = new ArrayList<ImpalaInsert>();
    
    public ImpalaMultiInsertStatement() {
        dbType = JdbcConstants.HIVE;
    }

    public void setFrom(SQLTableSource from) {
        this.from = from;
    }

    public SQLTableSource getFrom() {
        return from;
    }

    public List<ImpalaInsert> getItems() {
        return items;
    }
    
    public void addItem(ImpalaInsert item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof ImpalaASTVisitor) {
            accept0((ImpalaASTVisitor) visitor);
        } else {
            acceptChild(visitor, from);
            acceptChild(visitor, items);
        }
    }

    public void accept0(ImpalaASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, from);
            acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }
}
