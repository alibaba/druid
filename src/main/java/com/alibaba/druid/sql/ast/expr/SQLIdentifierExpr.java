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
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

public class SQLIdentifierExpr extends SQLExprImpl implements SQLName {
    protected         String    name;
    private transient String    lowerName;
    private transient long      hashCode64;

    private transient SQLObject resolvedColumn;
    private transient SQLObject resolvedOwnerObject;

    public SQLIdentifierExpr(){

    }

    public SQLIdentifierExpr(String name){
        this.name = name;
    }

    public SQLIdentifierExpr(String name, long hash_lower){
        this.name = name;
        this.hashCode64 = hash_lower;
    }

    public String getSimpleName() {
        return name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.lowerName = null;
        this.hashCode64 = 0L;
    }

    public String getLowerName() {
        if (lowerName == null && name != null) {
            lowerName = name.toLowerCase();
        }
        return lowerName;
    }

    public long nameHashCode64() {
        return hashCode64();
    }

    @Override
    public long hashCode64() {
        if (hashCode64 == 0
                && name != null) {
            hashCode64 = FnvHash.hashCode64(name);
        }
        return hashCode64;
    }

    public void output(StringBuffer buf) {
        buf.append(this.name);
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        long value = hashCode64();
        return (int)(value ^ (value >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SQLIdentifierExpr)) {
            return false;
        }

        SQLIdentifierExpr other = (SQLIdentifierExpr) obj;
        return this.hashCode64() == other.hashCode64();
    }

    public String toString() {
        return this.name;
    }

    public SQLIdentifierExpr clone() {
        SQLIdentifierExpr x = new SQLIdentifierExpr(this.name, hashCode64);
        x.resolvedColumn = resolvedColumn;
        x.resolvedOwnerObject = resolvedOwnerObject;
        return x;
    }

    public String normalizedName() {
        return SQLUtils.normalize(name);
    }

    public SQLColumnDefinition getResolvedColumn() {
        if (resolvedColumn instanceof SQLColumnDefinition) {
            return (SQLColumnDefinition) resolvedColumn;
        }

        return null;
    }

    public void setResolvedColumn(SQLColumnDefinition resolvedColumn) {
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

    public SQLObject getResolvedOwnerObject() {
        return resolvedOwnerObject;
    }

    public void setResolvedOwnerObject(SQLObject resolvedOwnerObject) {
        this.resolvedOwnerObject = resolvedOwnerObject;
    }

    public SQLParameter getResolvedParameter() {
        if (resolvedColumn instanceof SQLParameter) {
            return (SQLParameter) this.resolvedColumn;
        }
        return null;
    }

    public void setResolvedParameter(SQLParameter resolvedParameter) {
        this.resolvedColumn = resolvedParameter;
    }

    public SQLDeclareItem getResolvedDeclareItem() {
        if (resolvedColumn instanceof SQLDeclareItem) {
            return (SQLDeclareItem) this.resolvedColumn;
        }
        return null;
    }

    public void setResolvedDeclareItem(SQLDeclareItem resolvedDeclareItem) {
        this.resolvedColumn = resolvedDeclareItem;
    }

    public SQLDataType computeDataType() {
        SQLColumnDefinition resolvedColumn = getResolvedColumn();
        if (resolvedColumn != null) {
            return resolvedColumn.getDataType();
        }

        if (resolvedOwnerObject != null
                && resolvedOwnerObject instanceof SQLSubqueryTableSource) {
            SQLSelect select = ((SQLSubqueryTableSource) resolvedOwnerObject).getSelect();
            SQLSelectQueryBlock queryBlock = select.getFirstQueryBlock();
            if (queryBlock == null) {
                return null;
            }
            SQLSelectItem selectItem = queryBlock.findSelectItem(nameHashCode64());
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
