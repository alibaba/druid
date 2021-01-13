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

import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public final class SQLPropertyExpr extends SQLExprImpl implements SQLName, SQLReplaceable, Comparable<SQLPropertyExpr> {
    private   SQLExpr             owner;
    private   String              name;

    protected long                nameHashCod64;
    protected long                hashCode64;

    protected SQLObject           resolvedColumn;
    protected SQLObject           resolvedOwnerObject;

    public SQLPropertyExpr(String owner2, String owner, String name){
        this(new SQLPropertyExpr(owner2, owner), name);
    }

    public SQLPropertyExpr(String owner, String name){
        this(new SQLIdentifierExpr(owner), name);
    }

    public SQLPropertyExpr(SQLExpr owner, String name){
        setOwner(owner);
        this.name = name;
    }

    public SQLPropertyExpr(SQLExpr owner, String name, long nameHashCod64){
        setOwner(owner);
        this.name = name;
        this.nameHashCod64 = nameHashCod64;
    }

    public SQLPropertyExpr(){

    }

    public String getSimpleName() {
        return name;
    }

    public SQLExpr getOwner() {
        return this.owner;
    }

    @Deprecated
    public String getOwnernName() {
        if (owner instanceof SQLName) {
            return ((SQLName) owner).toString();
        }

        return null;
    }

    public String getOwnerName() {
        if (owner instanceof SQLName) {
            return ((SQLName) owner).toString();
        }

        return null;
    }

    public void setOwner(SQLExpr owner) {
        if (owner != null) {
            owner.setParent(this);
        }

        if (parent instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) parent;
            propertyExpr.computeHashCode64();
        }

        this.owner = owner;
        this.hashCode64 = 0;
    }

    protected void computeHashCode64() {
        long hash;
        if (owner instanceof SQLName) {
            hash = ((SQLName) owner).hashCode64();

            hash ^= '.';
            hash *= FnvHash.PRIME;
        } else if (owner == null){
            hash = FnvHash.BASIC;
        } else {
            hash = FnvHash.fnv1a_64_lower(owner.toString());

            hash ^= '.';
            hash *= FnvHash.PRIME;
        }
        hash = FnvHash.hashCode64(hash, name);
        hashCode64 = hash;
    }

    public void setOwner(String owner) {
        if (owner == null) {
            this.owner = null;
            return;
        }

        if (owner.indexOf('.') != -1) {
            SQLExpr ownerExpr = SQLUtils.toSQLExpr(owner);
            this.setOwner(ownerExpr);
        } else {
            this.setOwner(new SQLIdentifierExpr(owner));
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.hashCode64 = 0;
        this.nameHashCod64 = 0;

        if (parent instanceof SQLPropertyExpr) {
            SQLPropertyExpr propertyExpr = (SQLPropertyExpr) parent;
            propertyExpr.computeHashCode64();
        }
    }

    public void output(Appendable buf) {
        try {
            this.owner.output(buf);
            buf.append(".");
            buf.append(this.name);
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.owner != null) {
                this.owner.accept(visitor);
            }
        }

        visitor.endVisit(this);
    }

    @Override
    public List getChildren() {
        return Collections.singletonList(this.owner);
    }

    @Override
    public int hashCode() {
        long hash = hashCode64();
        return (int)(hash ^ (hash >>> 32));
    }

    public long hashCode64() {
        if (hashCode64 == 0) {
            computeHashCode64();
        }

        return hashCode64;
    }

    public boolean equals(SQLIdentifierExpr other) {
        if (other == null) {
            return false;
        }

        if (this.nameHashCode64() != other.nameHashCode64()) {
            return false;
        }

        return resolvedOwnerObject != null
                && resolvedOwnerObject == other.getResolvedOwnerObject()
                && resolvedColumn != null
                && resolvedColumn == other.getResolvedColumn();
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
        if (this.nameHashCode64() != other.nameHashCode64()) {
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
        SQLExpr owner_x = null;
        if (owner != null) {
            owner_x = owner.clone();
        }

        SQLPropertyExpr x = new SQLPropertyExpr(owner_x, name, nameHashCod64);

        x.hashCode64 = hashCode64;
        x.resolvedColumn = resolvedColumn;
        x.resolvedOwnerObject = resolvedOwnerObject;

        return x;
    }

    public boolean matchOwner(String alias) {
        if (owner instanceof SQLIdentifierExpr) {
            return ((SQLIdentifierExpr) owner).getName().equalsIgnoreCase(alias);
        }

        return false;
    }

    public long nameHashCode64() {
        if (nameHashCod64 == 0
                && name != null) {
            nameHashCod64 = FnvHash.hashCode64(name);
        }
        return nameHashCod64;
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
        if (resolvedColumn instanceof SQLColumnDefinition) {
            return (SQLColumnDefinition) resolvedColumn;
        }

        if (resolvedColumn instanceof SQLSelectItem) {
            SQLSelectItem selectItem = (SQLSelectItem) resolvedColumn;
            final SQLExpr expr = selectItem.getExpr();
            if (expr instanceof SQLIdentifierExpr) {
                return ((SQLIdentifierExpr) expr).getResolvedColumn();
            } else if (expr instanceof SQLPropertyExpr) {
                return ((SQLPropertyExpr) expr).getResolvedColumn();
            }
        }
        
        return null;
    }

    public void setResolvedColumn(SQLColumnDefinition resolvedColumn) {
        this.resolvedColumn = resolvedColumn;
    }

    public void setResolvedColumn(SQLSelectItem resolvedColumn) {
        this.resolvedColumn = resolvedColumn;
    }

    public SQLTableSource getResolvedTableSource() {
        if (resolvedOwnerObject instanceof SQLTableSource) {
            return (SQLTableSource) resolvedOwnerObject;
        }

        return null;
    }

    public void setResolvedTableSource(SQLTableSource resolvedTableSource) {
        this.resolvedOwnerObject = resolvedTableSource;
    }

    public void setResolvedProcedure(SQLCreateProcedureStatement stmt) {
        this.resolvedOwnerObject = stmt;
    }

    public void setResolvedOwnerObject(SQLObject resolvedOwnerObject) {
        this.resolvedOwnerObject = resolvedOwnerObject;
    }

    public SQLCreateProcedureStatement getResolvedProcudure() {
        if (this.resolvedOwnerObject instanceof SQLCreateProcedureStatement) {
            return (SQLCreateProcedureStatement) this.resolvedOwnerObject;
        }

        return null;
    }

    public SQLObject getResolvedOwnerObject() {
        return resolvedOwnerObject;
    }

    public SQLDataType computeDataType() {
        if (resolvedColumn instanceof SQLColumnDefinition
                && resolvedColumn != null) {
            return ((SQLColumnDefinition) resolvedColumn).getDataType();
        }

        if (resolvedColumn instanceof SQLSelectItem
                && resolvedColumn != null) {
            return ((SQLSelectItem) resolvedColumn).computeDataType();
        }

        if (resolvedOwnerObject == null) {
            return null;
        }

        if (resolvedOwnerObject instanceof SQLSubqueryTableSource) {
            SQLSelect select = ((SQLSubqueryTableSource) resolvedOwnerObject).getSelect();
            SQLSelectQueryBlock queryBlock = select.getFirstQueryBlock();
            if (queryBlock == null) {
                return null;
            }
            SQLSelectItem selectItem = queryBlock.findSelectItem(nameHashCode64());
            if (selectItem != null) {
                return selectItem.computeDataType();
            }
        } else if (resolvedOwnerObject instanceof SQLUnionQueryTableSource) {
            SQLSelectQueryBlock queryBlock = ((SQLUnionQueryTableSource) resolvedOwnerObject).getUnion().getFirstQueryBlock();
            if (queryBlock == null) {
                return null;
            }
            SQLSelectItem selectItem = queryBlock.findSelectItem(nameHashCode64());
            if (selectItem != null) {
                return selectItem.computeDataType();
            }
        } else if (resolvedOwnerObject instanceof SQLExprTableSource) {
            SQLExpr expr = ((SQLExprTableSource) resolvedOwnerObject).getExpr();
            if (expr != null) {

            }
        }

        return null;
    }

    public boolean nameEquals(String name) {
        return SQLUtils.nameEquals(this.name, name);
    }

    public SQLPropertyExpr simplify() {
        String normalizedName = SQLUtils.normalize(name);
        SQLExpr normalizedOwner = this.owner;
        if (normalizedOwner instanceof SQLIdentifierExpr) {
            normalizedOwner = ((SQLIdentifierExpr) normalizedOwner).simplify();
        }

        if (normalizedName != name || normalizedOwner != owner) {
            return new SQLPropertyExpr(normalizedOwner, normalizedName, hashCode64);
        }

        return this;
    }

    public String toString() {
        if (owner == null) {
            return this.name;
        }

        return owner.toString() + '.' + name;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (expr == owner) {
            setOwner(target);
            return true;
        }
        return false;
    }

    @Override
    public int compareTo(SQLPropertyExpr o) {
        int r = SQLExprComparor.compareTo(owner, o.owner);
        if (r != 0) {
            return r;
        }

        return name.compareTo(o.name);
    }
}
