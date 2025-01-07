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
package com.alibaba.druid.sql.dialect.spark.ast.stmt;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.dialect.spark.visitor.SparkASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SparkCreateScanStatement extends SQLStatementImpl implements SQLCreateStatement {
    private SQLName name;
    private SQLExprTableSource on;
    protected SQLExpr using;
    protected List<SQLAssignItem> options = new ArrayList<SQLAssignItem>();

    public SparkCreateScanStatement() {
        super(DbType.spark);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        if (name != null) {
            name.setParent(this);
        }
        this.name = name;
    }

    public SQLExprTableSource getOn() {
        return on;
    }

    public void setOn(SQLExprTableSource on) {
        if (on != null) {
            on.setParent(this);
        }
        this.on = on;
    }

    public void setOn(SQLName on) {
        this.setOn(new SQLExprTableSource(on));
    }

    public SQLExpr getUsing() {
        return using;
    }

    public void setUsing(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.using = x;
    }

    public List<SQLAssignItem> getOptions() {
        return options;
    }

    public void addOption(SQLAssignItem item) {
        item.setParent(this);
        this.options.add(item);
    }

    public void addOption(String name, SQLExpr value) {
        SQLAssignItem assignItem = new SQLAssignItem(new SQLIdentifierExpr(name), value);
        assignItem.setParent(this);
        addOption(assignItem);
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof SparkASTVisitor) {
            accept0((SparkASTVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    public void accept0(SparkASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, on);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (name != null) {
            children.add(name);
        }
        if (this.on != null) {
            children.add(on);
        }
        return children;
    }
}
