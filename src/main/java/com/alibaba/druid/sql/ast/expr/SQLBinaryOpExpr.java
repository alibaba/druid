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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLBinaryOpExpr extends SQLExprImpl implements Serializable {

    private static final long serialVersionUID = 1L;
    private SQLExpr           left;
    private SQLExpr           right;
    private SQLBinaryOperator operator;
    private String            dbType;

    public SQLBinaryOpExpr(){

    }

    public SQLBinaryOpExpr(String dbType){
        this.dbType = dbType;
    }

    public SQLBinaryOpExpr(SQLExpr left, SQLBinaryOperator operator, SQLExpr right){
        this(left, operator, right, null);
    }
    
    public SQLBinaryOpExpr(SQLExpr left, SQLBinaryOperator operator, SQLExpr right, String dbType){
        setLeft(left);
        setRight(right);
        this.operator = operator;
        this.dbType = dbType;
    }

    public SQLBinaryOpExpr(SQLExpr left, SQLExpr right, SQLBinaryOperator operator){

        setLeft(left);
        setRight(right);
        this.operator = operator;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public SQLExpr getLeft() {
        return this.left;
    }

    public void setLeft(SQLExpr left) {
        if (left != null) {
            left.setParent(this);
        }
        this.left = left;
    }

    public SQLExpr getRight() {
        return this.right;
    }

    public void setRight(SQLExpr right) {
        if (right != null) {
            right.setParent(this);
        }
        this.right = right;
    }

    public SQLBinaryOperator getOperator() {
        return this.operator;
    }

    public void setOperator(SQLBinaryOperator operator) {
        this.operator = operator;
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

    public SQLBinaryOpExpr clone() {
        return new SQLBinaryOpExpr(left, operator, right, dbType);
    }

    public String toString() {
        return SQLUtils.toSQLString(this, getDbType());
    }

    public static SQLExpr combine(List<SQLExpr> items, SQLBinaryOperator op) {
        if (items == null || op == null) {
            return null;
        }

        int size = items.size();
        if (size == 0) {
            return null;
        }

        if (size == 1) {
            return items.get(0);
        }

        SQLBinaryOpExpr expr = new SQLBinaryOpExpr(items.get(0), op, items.get(1));

        for (int i = 2; i < size; ++i) {
            SQLExpr item = items.get(i);
            expr = new SQLBinaryOpExpr(expr, op, item);
        }

        return expr;
    }

    public static List<SQLExpr> split(SQLBinaryOpExpr x) {
        return split(x, x.getOperator());
    }

    public static List<SQLExpr> split(SQLBinaryOpExpr x, SQLBinaryOperator op) {
        if (x.getOperator() != op) {
            List<SQLExpr> groupList = new ArrayList<SQLExpr>(1);
            groupList.add(x);
            return groupList;
        }

        List<SQLExpr> groupList = new ArrayList<SQLExpr>();
        split(groupList, x, op);
        return groupList;
    }

    public static void split(List<SQLExpr> outList, SQLExpr expr, SQLBinaryOperator op) {
        if (expr == null) {
            return;
        }

        if (!(expr instanceof SQLBinaryOpExpr)) {
            outList.add(expr);
            return;
        }

        SQLBinaryOpExpr binaryExpr = (SQLBinaryOpExpr) expr;

        if (binaryExpr.getOperator() != op) {
            outList.add(binaryExpr);
            return;
        }

        List<SQLExpr> rightList = new ArrayList<SQLExpr>();
        rightList.add(binaryExpr.getRight());
        for (SQLExpr left = binaryExpr.getLeft();;) {
            if (left instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr leftBinary = (SQLBinaryOpExpr) left;
                if (leftBinary.operator == op) {
                    left = ((SQLBinaryOpExpr) leftBinary).getLeft();
                    rightList.add(leftBinary.getRight());
                } else {
                    outList.add(leftBinary);
                    break;
                }
            } else {
                outList.add(left);
                break;
            }
        }

        for (int i = rightList.size() - 1; i >= 0; --i) {
            SQLExpr right  = rightList.get(i);

            if (right instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr binaryRight = (SQLBinaryOpExpr) right;
                if (binaryRight.operator == op) {
                    {
                        SQLExpr rightLeft = binaryRight.getLeft();
                        if (rightLeft instanceof SQLBinaryOpExpr) {
                            SQLBinaryOpExpr rightLeftBinary = (SQLBinaryOpExpr) rightLeft;
                            if (rightLeftBinary.operator == op) {
                                split(outList, rightLeftBinary, op);
                            } else {
                                outList.add(rightLeftBinary);
                            }
                        } else {
                            outList.add(rightLeft);
                        }
                    }
                    {
                        SQLExpr rightRight = binaryRight.getRight();
                        if (rightRight instanceof SQLBinaryOpExpr) {
                            SQLBinaryOpExpr rightRightBinary = (SQLBinaryOpExpr) rightRight;
                            if (rightRightBinary.operator == op) {
                                split(outList, rightRightBinary, op);
                            } else {
                                outList.add(rightRightBinary);
                            }
                        } else {
                            outList.add(rightRight);
                        }
                    }
                } else {
                    outList.add(binaryRight);
                }
            } else {
                outList.add(right);
            }
        }
    }

    public static SQLExpr and(SQLExpr a, SQLExpr b) {
        if (a == null) {
            return b;
        }

        if (b == null) {
            return a;
        }

        if (b instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr bb = (SQLBinaryOpExpr) b;
            if (bb.operator == SQLBinaryOperator.BooleanAnd) {
                return and(and(a, bb.left), bb.right);
            }
        }

        return new SQLBinaryOpExpr(a, SQLBinaryOperator.BooleanAnd, b);
    }

    public static SQLBinaryOpExpr isNotNull(SQLExpr expr) {
        return new SQLBinaryOpExpr(expr, SQLBinaryOperator.IsNot, new SQLNullExpr());
    }

    public static SQLBinaryOpExpr isNull(SQLExpr expr) {
        return new SQLBinaryOpExpr(expr, SQLBinaryOperator.Is, new SQLNullExpr());
    }
}
