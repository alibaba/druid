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
package com.alibaba.druid.sql.dialect.mysql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterDatabaseItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class MySqlAlterDatabaseSetOption extends MySqlObjectImpl implements SQLAlterDatabaseItem {

    private List<SQLAssignItem> options = new ArrayList<SQLAssignItem>();
    private SQLName on;

    @Override
    public void accept0(MySqlASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, options);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getOption(String name) {
        for (SQLAssignItem item : options) {
            final SQLExpr target = item.getTarget();
            if (target instanceof SQLIdentifierExpr) {
                if (((SQLIdentifierExpr) target).getName().equalsIgnoreCase(name)) {
                    return item.getValue();
                }
            }
        }
        return null;
    }

    public List<SQLAssignItem> getOptions() {
        return options;
    }

    public SQLName getOn() {
        return on;
    }

    public void setOn(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.on = x;
    }
}
