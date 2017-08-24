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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLPropertyExpr extends SQLExprImpl implements SQLName {

    private SQLExpr owner;
    private String  name;

    private transient SQLColumnDefinition resolvedColumn;
    private transient SQLTableSource resolvedTableSource;

    public SQLPropertyExpr(String owner, String name){
        this(new SQLIdentifierExpr(owner), name);
    }

    public SQLPropertyExpr(SQLExpr owner, String name){
        setOwner(owner);
        this.name = name;
    }

    public SQLPropertyExpr(){

    }

    public String getSimpleName() {
        return name;
    }

    public SQLExpr getOwner() {
        return this.owner;
    }

    public String getOwnernName() {
        if (owner instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) owner).getName();
        }

        return null;
    }

    public void setOwner(SQLExpr owner) {
        if (owner != null) {
            owner.setParent(this);
        }
        this.owner = owner;
    }

    public void setOwner(String owner) {
        this.setOwner(new SQLIdentifierExpr(owner));
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void output(StringBuffer buf) {
        this.owner.output(buf);
        buf.append(".");
        buf.append(this.name);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.owner);
        }

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((owner == null) ? 0 : owner.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SQLPropertyExpr)) {
            return false;
        }
        SQLPropertyExpr other = (SQLPropertyExpr) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (owner == null) {
            if (other.owner != null) {
                return false;
            }
        } else if (!owner.equals(other.owner)) {
            return false;
        }
        return true;
    }

    public SQLPropertyExpr clone() {
        SQLPropertyExpr x = new SQLPropertyExpr();
        x.name = this.name;
        if (owner != null) {
            x.setOwner(owner.clone());
        }

        x.resolvedColumn = resolvedColumn;
        x.resolvedTableSource = resolvedTableSource;

        return x;
    }

    public boolean matchOwner(String alias) {
        if (owner instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) owner).getName().equalsIgnoreCase(alias);
        }

        return false;
    }

    public String normalizedName() {

        String ownerName;
        if (owner instanceof SQLIdentifierExpr) {
            ownerName = ((SQLIdentifierExpr) owner).normalizedName();
        } else if (owner instanceof SQLPropertyExpr) {
            ownerName = ((SQLPropertyExpr) owner).normalizedName();
        } else {
            ownerName = owner.toString();
        }

        return ownerName + '.' + SQLUtils.normalize(name);
    }

    public SQLColumnDefinition getResolvedColumn() {
        return resolvedColumn;
    }

    public void setResolvedColumn(SQLColumnDefinition resolvedColumn) {
        this.resolvedColumn = resolvedColumn;
    }

    public SQLTableSource getResolvedTableSource() {
        return resolvedTableSource;
    }

    public void setResolvedTableSource(SQLTableSource resolvedTableSource) {
        this.resolvedTableSource = resolvedTableSource;
    }

    public SQLDataType computeDataType() {
        if (resolvedColumn != null) {
            return resolvedColumn.getDataType();
        }

        if (resolvedTableSource != null
                && resolvedTableSource instanceof SQLSubqueryTableSource) {
            SQLSelect select = ((SQLSubqueryTableSource) resolvedTableSource).getSelect();
            SQLSelectQueryBlock queryBlock = select.getFirstQueryBlock();
            if (queryBlock == null) {
                return null;
            }
            SQLSelectItem selectItem = queryBlock.findSelectItem(name);
            if (selectItem != null) {
                return selectItem.computeDataType();
            }
        }

        return null;
    }

    public boolean nameEquals(String name) {
        return SQLUtils.nameEquals(this.name, name);
    }
}
