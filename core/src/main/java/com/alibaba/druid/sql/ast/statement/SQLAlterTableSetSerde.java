/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SQLAlterTableSetSerde extends SQLObjectImpl implements SQLAlterTableItem {
    private SQLExpr serde;
    private List<SQLAssignItem> serdeProperties = new ArrayList<>();

    public SQLExpr getSerde() {
        return serde;
    }

    public void setSerde(SQLExpr serde) {
        if (serde != null) {
            serde.setParent(this);
        }
        this.serde = serde;
    }

    public List<SQLAssignItem> getSerdeProperties() {
        return serdeProperties;
    }

    public void addSerdeProperties(SQLAssignItem item) {
        item.setParent(this);
        this.serdeProperties.add(item);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, serde);
            acceptChild(visitor, serdeProperties);
        }
        visitor.endVisit(this);
    }
}
