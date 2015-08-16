/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSelectOrderByItem extends SQLObjectImpl {

    protected SQLExpr                  expr;
    protected String                   collate;
    protected SQLOrderingSpecification type;
    protected NullsOrderType             nullsOrderType;

    public SQLSelectOrderByItem(){

    }

    public SQLSelectOrderByItem(SQLExpr expr){
        this.setExpr(expr);
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    public String getCollate() {
        return collate;
    }

    public void setCollate(String collate) {
        this.collate = collate;
    }

    public SQLOrderingSpecification getType() {
        return this.type;
    }

    public void setType(SQLOrderingSpecification type) {
        this.type = type;
    }
    
    public NullsOrderType getNullsOrderType() {
        return this.nullsOrderType;
    }

    public void setNullsOrderType(NullsOrderType nullsOrderType) {
        this.nullsOrderType = nullsOrderType;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((collate == null) ? 0 : collate.hashCode());
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        SQLSelectOrderByItem other = (SQLSelectOrderByItem) obj;
        if (collate == null) {
            if (other.collate != null) return false;
        } else if (!collate.equals(other.collate)) return false;
        if (expr == null) {
            if (other.expr != null) return false;
        } else if (!expr.equals(other.expr)) return false;
        if (type != other.type) return false;
        return true;
    }

    public static enum NullsOrderType {
        NullsFirst, NullsLast;

        public String toFormalString() {
            if (NullsFirst.equals(this)) {
                return "NULLS FIRST";
            }

            if (NullsLast.equals(this)) {
                return "NULLS LAST";
            }

            throw new IllegalArgumentException();
        }
    }
}
