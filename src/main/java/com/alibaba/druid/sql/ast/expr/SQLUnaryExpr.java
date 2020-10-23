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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class SQLUnaryExpr extends SQLExprImpl implements Serializable, SQLReplaceable {

    private static final long serialVersionUID = 1L;
    private SQLExpr           expr;
    private SQLUnaryOperator  operator;

    public SQLUnaryExpr(){

    }

    public SQLUnaryExpr(SQLUnaryOperator operator, SQLExpr expr){
        this.operator = operator;
        this.setExpr(expr);
    }

    public SQLUnaryExpr clone() {
        SQLUnaryExpr x = new SQLUnaryExpr();
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        x.operator = operator;
        return x;
    }

    public SQLUnaryOperator getOperator() {
        return operator;
    }

    public void setOperator(SQLUnaryOperator operator) {
        this.operator = operator;
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

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.expr != null) {
                this.expr.accept(visitor);
            }
        }

        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(this.expr);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        result = prime * result + ((operator == null) ? 0 : operator.hashCode());
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLUnaryExpr other = (SQLUnaryExpr) obj;
        if (expr == null) {
            if (other.expr != null) {
                return false;
            }
        } else if (!expr.equals(other.expr)) {
            return false;
        }
        if (operator != other.operator) {
            return false;
        }
        return true;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.expr == expr) {
            setExpr(target);
            return true;
        }
        return false;
    }

    public SQLDataType computeDataType() {
        switch (operator) {
            case Plus:
            case Negative:
            case Compl:
            case Not:
                return expr.computeDataType();
            default:
                return null;
        }
    }
}
