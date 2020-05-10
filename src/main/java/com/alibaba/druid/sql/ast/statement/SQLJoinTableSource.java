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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.FnvHash;

public class SQLJoinTableSource extends SQLTableSourceImpl implements SQLReplaceable {

    protected SQLTableSource      left;
    protected JoinType            joinType;
    protected SQLTableSource      right;
    protected SQLExpr             condition;
    protected final List<SQLExpr> using = new ArrayList<SQLExpr>();


    protected boolean             natural = false;

    public SQLJoinTableSource(String alias){
        super(alias);
    }

    public SQLJoinTableSource(){

    }

    public SQLJoinTableSource(SQLTableSource left, JoinType joinType, SQLTableSource right, SQLExpr condition){
        this.setLeft(left);
        this.setJoinType(joinType);
        this.setRight(right);
        this.setCondition(condition);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.left);
            acceptChild(visitor, this.right);
            acceptChild(visitor, this.condition);
            acceptChild(visitor, this.using);
        }

        visitor.endVisit(this);
    }

    public JoinType getJoinType() {
        return this.joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public SQLTableSource getLeft() {
        return this.left;
    }

    public void setLeft(SQLTableSource left) {
        if (left != null) {
            left.setParent(this);
        }
        this.left = left;
    }

    public void setLeft(String tableName, String alias) {
        SQLExprTableSource tableSource;
        if (tableName == null || tableName.length() == 0) {
            tableSource = null;
        } else {
            tableSource = new SQLExprTableSource(new SQLIdentifierExpr(tableName), alias);
        }
        this.setLeft(tableSource);
    }

    public void setRight(String tableName, String alias) {
        SQLExprTableSource tableSource;
        if (tableName == null || tableName.length() == 0) {
            tableSource = null;
        } else {
            tableSource = new SQLExprTableSource(new SQLIdentifierExpr(tableName), alias);
        }
        this.setRight(tableSource);
    }

    public SQLTableSource getRight() {
        return this.right;
    }

    public void setRight(SQLTableSource right) {
        if (right != null) {
            right.setParent(this);
        }
        this.right = right;
    }

    public SQLExpr getCondition() {
        return this.condition;
    }

    public void setCondition(SQLExpr condition) {
        if (condition != null) {
            condition.setParent(this);
        }
        this.condition = condition;
    }

    public void addCondition(SQLExpr condition) {
        if (this.condition == null) {
            this.condition = condition;
            setImplicitJoinToCross();
            return;
        }

        this.condition = SQLBinaryOpExpr.and(this.condition, condition);
    }

    public void setImplicitJoinToCross() {
        if (joinType == JoinType.COMMA) {
            joinType = JoinType.CROSS_JOIN;
        }
        if (left instanceof SQLJoinTableSource) {
            ((SQLJoinTableSource) left).setImplicitJoinToCross();
        }

        if (right instanceof SQLJoinTableSource) {
            ((SQLJoinTableSource) right).setImplicitJoinToCross();
        }
    }

    public void addConditionn(SQLExpr condition) {
        this.condition = SQLBinaryOpExpr.and(this.condition, condition);
    }

    public void addConditionnIfAbsent(SQLExpr condition) {
        if (this.containsCondition(condition)) {
            return;
        }
        this.condition = SQLBinaryOpExpr.and(this.condition, condition);
    }

    public boolean containsCondition(SQLExpr condition) {
        if (this.condition == null) {
            return false;
        }

        if (this.condition.equals(condition)) {
            return false;
        }

        if (this.condition instanceof SQLBinaryOpExpr) {
            return ((SQLBinaryOpExpr) this.condition).contains(condition);
        }

        return false;
    }

    public List<SQLExpr> getUsing() {
        return this.using;
    }

    public boolean isNatural() {
        return natural;
    }

    public void setNatural(boolean natural) {
        this.natural = natural;
    }

    public void output(StringBuffer buf) {
        this.left.output(buf);
        buf.append(' ');
        buf.append(JoinType.toString(this.joinType));
        buf.append(' ');
        this.right.output(buf);

        if (this.condition != null) {
            buf.append(" ON ");
            this.condition.output(buf);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLJoinTableSource that = (SQLJoinTableSource) o;

        if (natural != that.natural) return false;
        if (left != null ? !left.equals(that.left) : that.left != null) return false;
        if (joinType != that.joinType) return false;
        if (right != null ? !right.equals(that.right) : that.right != null) return false;
        if (condition != null ? !condition.equals(that.condition) : that.condition != null) return false;
        return using != null ? using.equals(that.using) : that.using == null;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (condition == expr) {
            setCondition(target);
            return true;
        }

        return false;
    }

    public static enum JoinType {
        COMMA(","), //
        JOIN("JOIN"), //
        INNER_JOIN("INNER JOIN"), //
        CROSS_JOIN("CROSS JOIN"), //
        NATURAL_CROSS_JOIN("NATURAL CROSS JOIN"), //
        NATURAL_JOIN("NATURAL JOIN"), //
        NATURAL_INNER_JOIN("NATURAL INNER JOIN"), //
        LEFT_OUTER_JOIN("LEFT JOIN"), //
        LEFT_SEMI_JOIN("LEFT SEMI JOIN"), //
        LEFT_ANTI_JOIN("LEFT ANTI JOIN"), //
        RIGHT_OUTER_JOIN("RIGHT JOIN"), //
        FULL_OUTER_JOIN("FULL JOIN"),//
        STRAIGHT_JOIN("STRAIGHT_JOIN"), //
        OUTER_APPLY("OUTER APPLY"),//
        CROSS_APPLY("CROSS APPLY");

        public final String name;
        public final String name_lcase;

        JoinType(String name){
            this.name = name;
            this.name_lcase = name.toLowerCase();
        }

        public static String toString(JoinType joinType) {
            return joinType.name;
        }
    }


    public void cloneTo(SQLJoinTableSource x) {
        x.alias = alias;

        if (left != null) {
            x.setLeft(left.clone());
        }

        x.joinType = joinType;

        if (right != null) {
            x.setRight(right.clone());
        }

        if(condition != null){
            x.setCondition(condition.clone());
        }

        for (SQLExpr item : using) {
            SQLExpr item2 = item.clone();
            item2.setParent(x);
            x.using.add(item2);
        }

        x.natural = natural;
    }

    public SQLJoinTableSource clone() {
        SQLJoinTableSource x = new SQLJoinTableSource();
        cloneTo(x);
        return x;
    }

    public void reverse() {
        SQLTableSource temp = left;
        left = right;
        right = temp;

        if (left instanceof SQLJoinTableSource) {
            ((SQLJoinTableSource) left).reverse();
        }

        if (right instanceof SQLJoinTableSource) {
            ((SQLJoinTableSource) right).reverse();
        }
    }

    /**
     * a inner_join (b inner_join c) -&lt; a inner_join b innre_join c
     */
    public void rearrangement() {
        if (joinType != JoinType.COMMA && joinType != JoinType.INNER_JOIN) {
            return;
        }
        if (right instanceof SQLJoinTableSource) {
            SQLJoinTableSource rightJoin = (SQLJoinTableSource) right;

            if (rightJoin.joinType != JoinType.COMMA && rightJoin.joinType != JoinType.INNER_JOIN) {
                return;
            }

            SQLTableSource a = left;
            SQLTableSource b = rightJoin.getLeft();
            SQLTableSource c = rightJoin.getRight();
            SQLExpr on_ab = condition;
            SQLExpr on_bc = rightJoin.condition;

            setLeft(rightJoin);
            rightJoin.setLeft(a);
            rightJoin.setRight(b);


            boolean on_ab_match = false;
            if (on_ab instanceof SQLBinaryOpExpr) {
                SQLBinaryOpExpr on_ab_binaryOpExpr = (SQLBinaryOpExpr) on_ab;
                if (on_ab_binaryOpExpr.getLeft() instanceof SQLPropertyExpr
                        && on_ab_binaryOpExpr.getRight() instanceof SQLPropertyExpr) {
                    String leftOwnerName = ((SQLPropertyExpr) on_ab_binaryOpExpr.getLeft()).getOwnernName();
                    String rightOwnerName = ((SQLPropertyExpr) on_ab_binaryOpExpr.getRight()).getOwnernName();

                    if (rightJoin.containsAlias(leftOwnerName) && rightJoin.containsAlias(rightOwnerName)) {
                        on_ab_match = true;
                    }
                }
            }

            if (on_ab_match) {
                rightJoin.setCondition(on_ab);
            } else {
                rightJoin.setCondition(null);
                on_bc = SQLBinaryOpExpr.and(on_bc, on_ab);
            }

            setRight(c);
            setCondition(on_bc);
        }
    }

    public boolean contains(SQLTableSource tableSource, SQLExpr condition) {
        if (right.equals(tableSource)) {
            if (this.condition == condition) {
                return true;
            }

            return this.condition != null && this.condition.equals(condition);
        }

        if (left instanceof SQLJoinTableSource) {
            SQLJoinTableSource joinLeft = (SQLJoinTableSource) left;

            if (tableSource instanceof SQLJoinTableSource) {
                SQLJoinTableSource join = (SQLJoinTableSource) tableSource;

                if (join.right.equals(right) && this.condition.equals(condition) && joinLeft.right.equals(join.left)) {
                    return true;
                }
            }

            return joinLeft.contains(tableSource, condition);
        }

        return false;
    }

    public boolean contains(SQLTableSource tableSource, SQLExpr condition, JoinType joinType) {
        if (right.equals(tableSource)) {
            if (this.condition == condition) {
                return true;
            }

            return this.condition != null && this.condition.equals(condition) && this.joinType == joinType;
        }

        if (left instanceof SQLJoinTableSource) {
            SQLJoinTableSource joinLeft = (SQLJoinTableSource) left;

            if (tableSource instanceof SQLJoinTableSource) {
                SQLJoinTableSource join = (SQLJoinTableSource) tableSource;

                if (join.right.equals(right)
                        && this.condition != null && this.condition.equals(join.condition)
                        && joinLeft.right.equals(join.left)
                        && this.joinType == join.joinType
                        && joinLeft.condition != null && joinLeft.condition.equals(condition)
                        && joinLeft.joinType == joinType) {
                    return true;
                }
            }

            return joinLeft.contains(tableSource, condition, joinType);
        }

        return false;
    }

    public SQLJoinTableSource findJoin(SQLTableSource tableSource, JoinType joinType) {
        if (right.equals(tableSource)) {
            if (this.joinType == joinType) {
                return this;
            }
            return null;
        }

        if (left instanceof SQLJoinTableSource) {
            return ((SQLJoinTableSource) left).findJoin(tableSource, joinType);
        }

        return null;
    }

    public boolean containsAlias(String alias) {
        if (SQLUtils.nameEquals(this.alias, alias)) {
            return true;
        }

        if (left != null && left.containsAlias(alias)) {
            return true;
        }

        if (right != null && right.containsAlias(alias)) {
            return true;
        }

        return false;
    }

    public SQLColumnDefinition findColumn(String columnName) {
        long hash = FnvHash.hashCode64(columnName);
        return findColumn(hash);
    }

    public SQLColumnDefinition findColumn(long columnNameHash) {
        if (left != null) {
            SQLColumnDefinition column = left.findColumn(columnNameHash);
            if (column != null) {
                return column;
            }
        }

        if (right != null) {
            return right.findColumn(columnNameHash);
        }

        return null;
    }

    @Override
    public SQLTableSource findTableSourceWithColumn(String columnName) {
        long hash = FnvHash.hashCode64(columnName);
        return findTableSourceWithColumn(hash);
    }

    public SQLTableSource findTableSourceWithColumn(long columnNameHash) {
        if (left != null) {
            SQLTableSource tableSource = left.findTableSourceWithColumn(columnNameHash);
            if (tableSource != null) {
                return tableSource;
            }
        }

        if (right != null) {
            return right.findTableSourceWithColumn(columnNameHash);
        }

        return null;
    }

    public boolean match(String alias_a, String alias_b) {
        if (left == null || right == null) {
            return false;
        }

        if (left.containsAlias(alias_a)
                && right.containsAlias(alias_b)) {
            return true;
        }

        return right.containsAlias(alias_a)
                && left.containsAlias(alias_b);
    }

    public boolean conditionContainsTable(String alias) {
        if (condition == null) {
            return false;
        }

        if (condition instanceof SQLBinaryOpExpr) {
            return ((SQLBinaryOpExpr) condition).conditionContainsTable(alias);
        }

        return false;
    }

    public SQLJoinTableSource join(SQLTableSource right, JoinType joinType, SQLExpr condition) {
        SQLJoinTableSource joined = new SQLJoinTableSource(this, joinType, right, condition);
        return joined;
    }

    public SQLTableSource findTableSource(long alias_hash) {
        if (alias_hash == 0) {
            return null;
        }

        if (aliasHashCode64() == alias_hash) {
            return this;
        }

        SQLTableSource result = left.findTableSource(alias_hash);
        if (result != null) {
            return result;
        }

        return right.findTableSource(alias_hash);
    }

    public SQLTableSource other(SQLTableSource x) {
        if (left == x) {
            return right;
        }

        if (right == x) {
            return left;
        }

        return null;
    }

    public SQLObject resolveColum(long columnNameHash) {
        if (left != null) {
            SQLObject column = left.resolveColum(columnNameHash);
            if (column != null) {
                return column;
            }
        }

        if (right != null) {
            return right.resolveColum(columnNameHash);
        }

        return null;
    }
}
