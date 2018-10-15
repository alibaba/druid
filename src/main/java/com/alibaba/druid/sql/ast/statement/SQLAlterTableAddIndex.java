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
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLAlterTableAddIndex extends SQLObjectImpl implements SQLAlterTableItem {

    private boolean                          unique;

    private SQLName                          name;

    private final List<SQLSelectOrderByItem> items = new ArrayList<SQLSelectOrderByItem>();

    private String                           type;

    private String                           using;
    
    private boolean                          key = false;

    protected SQLExpr                        comment;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getName());
            acceptChild(visitor, getItems());
        }
        visitor.endVisit(this);
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
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

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public void cloneTo(MySqlTableIndex x) {
        if (name != null) {
            x.setName(name.clone());
        }
        for (SQLSelectOrderByItem item : items) {
            SQLSelectOrderByItem item2 = item.clone();
            item2.setParent(x);
            x.getColumns().add(item);
        }
        x.setIndexType(type);
    }

    public void cloneTo(MySqlKey x) {
        if (name != null) {
            x.setName(name.clone());
        }
        for (SQLSelectOrderByItem item : items) {
            SQLSelectOrderByItem item2 = item.clone();
            item2.setParent(x);
            x.getColumns().add(item);
        }
        x.setIndexType(type);
    }

    public SQLExpr getComment() {
        return comment;
    }

    public void setComment(SQLExpr comment) {
        if (comment != null) {
            comment.setParent(this);
        }
        this.comment = comment;
    }
}
