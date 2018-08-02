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
package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.ParameterizedVisitor;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.Utils;

public class SQLBinaryOpExpr extends SQLExprImpl implements SQLReplaceable, Serializable {

    private static final long   serialVersionUID = 1L;
    protected SQLExpr           left;
    protected SQLExpr           right;
    protected SQLBinaryOperator operator;
    protected String            dbType;

    private boolean             bracket  = false;

    // only for parameterized output
    protected transient List<SQLObject> mergedList;

    public SQLBinaryOpExpr(){

    }

    public SQLBinaryOpExpr(String dbType){
        this.dbType = dbType;
    }

    public SQLBinaryOpExpr(SQLExpr left, SQLBinaryOperator operator, SQLExpr right){
        this(left, operator, right, null);
    }
    
    public SQLBinaryOpExpr(SQLExpr left, SQLBinaryOperator operator, SQLExpr right, String dbType){
        if (left != null) {
            left.setParent(this);
        }
        this.left = left;

        setRight(right);
        this.operator = operator;

        if (dbType == null) {
            if (left instanceof SQLBinaryOpExpr) {
                dbType = ((SQLBinaryOpExpr) left).dbType;
            }
        }

        if (dbType == null) {
            if (right instanceof SQLBinaryOpExpr) {
                dbType = ((SQLBinaryOpExpr) right).dbType;
            }
        }

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

    public boolean isBracket() {
        return bracket;
    }

    public void setBracket(boolean bracket) {
        this.bracket = bracket;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.left);
            acceptChild(visitor, this.right);
        }

        visitor.endVisit(this);
    }

    @Override
    public List getChildren() {
        return Arrays.asList(this.left, this.right);
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

        return operator == other.operator
                && SQLExprUtils.equals(left, other.left)
                &&  SQLExprUtils.equals(right, other.right);
    }

    public boolean equals(SQLBinaryOpExpr other) {
        return operator == other.operator
                && SQLExprUtils.equals(left, other.left)
                &&  SQLExprUtils.equals(right, other.right);
    }


    public boolean equalsIgoreOrder(SQLBinaryOpExpr other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }

        if (operator != other.operator) {
            return false;
        }

        return (Utils.equals(this.left, other.left)
                    && Utils.equals(this.right, other.right))
                || (Utils.equals(this.left, other.right)
                    && Utils.equals(this.right, other.left));
    }

    public SQLBinaryOpExpr clone() {
        SQLBinaryOpExpr x = new SQLBinaryOpExpr();

        if (left != null) {
            x.setLeft(left.clone());
        }
        if (right != null) {
            x.setRight(right.clone());
        }
        x.operator = operator;
        x.dbType = dbType;
        x.bracket = bracket;

        return x;
    }

    public String toString() {
        return SQLUtils.toSQLString(this, getDbType());
    }

    public void output(StringBuffer buf) {
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(buf, dbType);
        this.accept(visitor);
    }

    public static SQLExpr combine(List<? extends SQLExpr> items, SQLBinaryOperator op) {
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

    public static SQLExpr andIfNotExists(SQLExpr a, SQLExpr b) {
        if (a == null) {
            return b;
        }

        if (b == null) {
            return a;
        }

        List<SQLExpr> groupListA = new ArrayList<SQLExpr>();
        List<SQLExpr> groupListB = new ArrayList<SQLExpr>();
        split(groupListA, a, SQLBinaryOperator.BooleanAnd);
        split(groupListB, a, SQLBinaryOperator.BooleanAnd);

        for (SQLExpr itemB : groupListB) {
            boolean exist = false;
            for (SQLExpr itemA : groupListA) {
                if (itemA.equals(itemB)) {
                    exist = true;
                } else if (itemA instanceof SQLBinaryOpExpr
                        && itemB instanceof SQLBinaryOpExpr) {
                    if (((SQLBinaryOpExpr) itemA).equalsIgoreOrder((SQLBinaryOpExpr) itemB)) {
                        exist = true;
                    }
                }
            }
            if (!exist) {
                groupListA.add(itemB);
            }
        }
        return combine(groupListA, SQLBinaryOperator.BooleanAnd);
    }

    public static SQLBinaryOpExpr isNotNull(SQLExpr expr) {
        return new SQLBinaryOpExpr(expr, SQLBinaryOperator.IsNot, new SQLNullExpr());
    }

    public static SQLBinaryOpExpr isNull(SQLExpr expr) {
        return new SQLBinaryOpExpr(expr, SQLBinaryOperator.Is, new SQLNullExpr());
    }

    public boolean replace(SQLExpr expr, SQLExpr taget) {
        SQLObject parent = getParent();

        if (left == expr) {
            if (taget == null) {
                if (parent instanceof SQLReplaceable) {
                    return ((SQLReplaceable) parent).replace(this, right);
                } else {
                    return false;
                }
            }
            this.setLeft(taget);
            return true;
        }

        if (right == expr) {
            if (taget == null) {
                if (parent instanceof SQLReplaceable) {
                    return ((SQLReplaceable) parent).replace(this, left);
                } else {
                    return false;
                }
            }
            this.setRight(taget);
            return true;
        }

        return false;
    }

    public SQLExpr other(SQLExpr x) {
        if (x == left) {
            return right;
        }

        if (x == right) {
            return left;
        }

        return null;
    }

    public boolean contains(SQLExpr item) {
        if (item instanceof SQLBinaryOpExpr) {
            if (this.equalsIgoreOrder((SQLBinaryOpExpr) item)) {
                return true;
            }

            return left.equals(item) || right.equals(item);
        }

        return false;
    }

    public SQLDataType computeDataType() {
        if (operator != null && operator.isRelational()) {
            return SQLBooleanExpr.DEFAULT_DATA_TYPE;
        }

        SQLDataType leftDataType = null, rightDataType = null;
        if (left != null) {
            leftDataType = left.computeDataType();
        }
        if (right != null) {
            rightDataType = right.computeDataType();
        }

        if (operator == SQLBinaryOperator.Concat) {
            if (leftDataType != null) {
                return leftDataType;
            }
            if (rightDataType != null) {
                return rightDataType;
            }
            return SQLCharExpr.DEFAULT_DATA_TYPE;
        }

        return null;
    }

    public boolean conditionContainsTable(String alias) {
        if (left == null || right == null) {
            return false;
        }

        if (left instanceof SQLPropertyExpr) {
            if (((SQLPropertyExpr) left).matchOwner(alias)) {
                return true;
            }
        } else if (left instanceof SQLBinaryOpExpr) {
            if (((SQLBinaryOpExpr) left).conditionContainsTable(alias)) {
                return true;
            }
        }

        if (right instanceof SQLPropertyExpr) {
            if (((SQLPropertyExpr) right).matchOwner(alias)) {
                return true;
            }
        } else if (right instanceof SQLBinaryOpExpr) {
            return ((SQLBinaryOpExpr) right).conditionContainsTable(alias);
        }

        return false;
    }

    public boolean conditionContainsColumn(String column) {
        if (left == null || right == null) {
            return false;
        }

        if (left instanceof SQLIdentifierExpr) {
            if (((SQLIdentifierExpr) left).nameEquals(column)) {
                return true;
            }
        } else if (right instanceof SQLIdentifierExpr) {
            if (((SQLIdentifierExpr) right).nameEquals(column)) {
                return true;
            }
        }

        return false;
    }

    /**
     * only for parameterized output
     * @param v
     * @param x
     * @return
     */
    public static SQLBinaryOpExpr merge(ParameterizedVisitor v, SQLBinaryOpExpr x) {
        SQLObject parent = x.parent;

        for (;;) {
            if (x.right instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr rightBinary = (SQLBinaryOpExpr) x.right;
                if (x.left instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr leftBinaryExpr = (SQLBinaryOpExpr) x.left;
                    if (SQLExprUtils.equals(leftBinaryExpr.right, rightBinary)) {
                        x = leftBinaryExpr;
                        v.incrementReplaceCunt();
                        continue;
                    }
                }
                SQLExpr mergedRight = merge(v, rightBinary);
                if (mergedRight != x.right) {
                    x = new SQLBinaryOpExpr(x.left, x.operator, mergedRight);
                    v.incrementReplaceCunt();
                }

                x.setParent(parent);
            }

            break;
        }

        if (x.left instanceof SQLBinaryOpExpr) {
            SQLExpr mergedLeft = merge(v, (SQLBinaryOpExpr) x.left);
            if (mergedLeft != x.left) {
                SQLBinaryOpExpr tmp = new SQLBinaryOpExpr(mergedLeft, x.operator, x.right);
                tmp.setParent(parent);
                x = tmp;
                v.incrementReplaceCunt();
            }
        }

        // ID = ? OR ID = ? => ID = ?
        if (x.operator == SQLBinaryOperator.BooleanOr) {
            if ((x.left instanceof SQLBinaryOpExpr) && (x.right instanceof SQLBinaryOpExpr)) {
                SQLBinaryOpExpr leftBinary = (SQLBinaryOpExpr) x.left;
                SQLBinaryOpExpr rightBinary = (SQLBinaryOpExpr) x.right;

                if (mergeEqual(leftBinary, rightBinary)) {
                    v.incrementReplaceCunt();
                    leftBinary.setParent(x.parent);
                    leftBinary.addMergedItem(rightBinary);
                    return leftBinary;
                }

                if (SQLExprUtils.isLiteralExpr(leftBinary.left) //
                        && leftBinary.operator == SQLBinaryOperator.BooleanOr) {
                    if (mergeEqual(leftBinary.right, x.right)) {
                        v.incrementReplaceCunt();
                        leftBinary.addMergedItem(rightBinary);
                        return leftBinary;
                    }
                }
            }
        }

        return x;
    }

    /**
     * only for parameterized output
     * @param item
     * @return
     */
    private void addMergedItem(SQLBinaryOpExpr item) {
        if (mergedList == null) {
            mergedList = new ArrayList<SQLObject>();
        }
        mergedList.add(item);
    }

    /**
     * only for parameterized output
     * @return
     */
    public List<SQLObject> getMergedList() {
        return mergedList;
    }

    /**
     * only for parameterized output
     * @param a
     * @param b
     * @return
     */
    private static boolean mergeEqual(SQLExpr a, SQLExpr b) {
        if (!(a instanceof SQLBinaryOpExpr)) {
            return false;
        }
        if (!(b instanceof SQLBinaryOpExpr)) {
            return false;
        }

        SQLBinaryOpExpr binaryA = (SQLBinaryOpExpr) a;
        SQLBinaryOpExpr binaryB = (SQLBinaryOpExpr) b;

        if (binaryA.getOperator() != SQLBinaryOperator.Equality) {
            return false;
        }

        if (binaryB.getOperator() != SQLBinaryOperator.Equality) {
            return false;
        }

        if (!(binaryA.getRight() instanceof SQLLiteralExpr || binaryA.getRight() instanceof SQLVariantRefExpr)) {
            return false;
        }

        if (!(binaryB.getRight() instanceof SQLLiteralExpr || binaryB.getRight() instanceof SQLVariantRefExpr)) {
            return false;
        }

        return binaryA.getLeft().toString().equals(binaryB.getLeft().toString());
    }
}
