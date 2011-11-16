/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLBinaryOpExpr extends SQLExprImpl implements Serializable {

    private static final long serialVersionUID = 1L;
    public SQLExpr            left;
    public SQLExpr            right;
    public SQLBinaryOperator  operator;

    public SQLBinaryOpExpr(){

    }

    public SQLBinaryOpExpr(SQLExpr left, SQLBinaryOperator operator, SQLExpr right){

        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public SQLBinaryOpExpr(SQLExpr left, SQLExpr right, SQLBinaryOperator operator){

        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public SQLExpr getLeft() {
        return this.left;
    }

    public void setLeft(SQLExpr left) {
        this.left = left;
    }

    public SQLExpr getRight() {
        return this.right;
    }

    public void setRight(SQLExpr right) {
        this.right = right;
    }

    public SQLBinaryOperator getOperator() {
        return this.operator;
    }

    public void setOperator(SQLBinaryOperator operator) {
        this.operator = operator;
    }

    public void output(StringBuffer buf) {
        this.left.output(buf);
        buf.append(" ");
        buf.append(this.operator.name);
        buf.append(" ");
        this.right.output(buf);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.left);
            acceptChild(visitor, this.right);
        }

        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((operator == null) ? 0 : operator.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
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
        if (!(obj instanceof SQLBinaryOpExpr)) {
            return false;
        }
        SQLBinaryOpExpr other = (SQLBinaryOpExpr) obj;
        if (left == null) {
            if (other.left != null) {
                return false;
            }
        } else if (!left.equals(other.left)) {
            return false;
        }
        if (operator != other.operator) {
            return false;
        }
        if (right == null) {
            if (other.right != null) {
                return false;
            }
        } else if (!right.equals(other.right)) {
            return false;
        }
        return true;
    }

}
