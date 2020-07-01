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
package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLCreateIndexStatement extends SQLStatementImpl implements SQLCreateStatement {

    private SQLName                    name;

    private SQLTableSource             table;

    private List<SQLSelectOrderByItem> items = new ArrayList<SQLSelectOrderByItem>();

    private String                     type;
    
    // for mysql
    private String                     using;

    private SQLExpr                    comment;

    public SQLCreateIndexStatement(){

    }
    
    public SQLCreateIndexStatement(String dbType){
        super (dbType);
    }

    public SQLTableSource getTable() {
        return table;
    }

    public void setTable(SQLName table) {
        this.setTable(new SQLExprTableSource(table));
    }

    public void setTable(SQLTableSource table) {
        this.table = table;
    }

    public String getTableName() {
        if (table instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) table).getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                return ((SQLIdentifierExpr) expr).getName();
            } else if (expr instanceof SQLPropertyExpr) {
                return ((SQLPropertyExpr) expr).getName();
            }
        }

        return null;
    }

    public List<SQLSelectOrderByItem> getItems() {
        return items;
    }

    public void addItem(SQLSelectOrderByItem item) {
        if (item != null) {
            item.setParent(this);
        }
        this.items.add(item);
    }

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getUsing() {
        return using;
    }

    public void setUsing(String using) {
        this.using = using;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, name);
            acceptChild(visitor, table);
            acceptChild(visitor, items);
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        List<SQLObject> children = new ArrayList<SQLObject>();
        if (name != null) {
            children.add(name);
        }

        if (table != null) {
            children.add(table);
        }

        children.addAll(this.items);
        return children;
    }

    public String getSchema() {
        SQLName name = null;
        if (table instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) table).getExpr();
            if (expr instanceof SQLName) {
                name = (SQLName) expr;
            }
        }

        if (name == null) {
            return null;
        }

        if (name instanceof SQLPropertyExpr) {
            return ((SQLPropertyExpr) name).getOwnernName();
        }

        return null;
    }


    public SQLCreateIndexStatement clone() {
        SQLCreateIndexStatement x = new SQLCreateIndexStatement();
        if (name != null) {
            x.setName(name.clone());
        }
        if (table != null) {
            x.setTable(table.clone());
        }
        for (SQLSelectOrderByItem item : items) {
            SQLSelectOrderByItem item2 = item.clone();
            item2.setParent(x);
            x.items.add(item2);
        }
        x.type = type;
        x.using = using;
        if (comment != null) {
            x.setComment(comment.clone());
        }
        return x;
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.comment = x;
    }
}
