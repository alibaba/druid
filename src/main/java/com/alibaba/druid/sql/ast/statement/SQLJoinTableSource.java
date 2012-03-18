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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLJoinTableSource extends SQLTableSourceImpl {

    private static final long serialVersionUID = 1L;

    protected SQLTableSource  left;
    protected JoinType        joinType;
    protected SQLTableSource  right;
    protected SQLExpr         condition;

    public SQLJoinTableSource(String alias){
        super(alias);
    }

    public SQLJoinTableSource(){

    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.left);
            acceptChild(visitor, this.right);
            acceptChild(visitor, this.condition);
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
        this.left = left;
    }

    public SQLTableSource getRight() {
        return this.right;
    }

    public void setRight(SQLTableSource right) {
        this.right = right;
    }

    public SQLExpr getCondition() {
        return this.condition;
    }

    public void setCondition(SQLExpr condition) {
        this.condition = condition;
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

    public static enum JoinType {
        COMMA, JOIN, INNER_JOIN, CROSS_JOIN, NATURAL_JOIN, NATURAL_INNER_JOIN, LEFT_OUTER_JOIN, RIGHT_OUTER_JOIN,
        FULL_OUTER_JOIN;

        public static String toString(JoinType joinType) {
            if (JOIN.equals(joinType)) return "JOIN";
            if (INNER_JOIN.equals(joinType)) return "INNER JOIN";
            if (CROSS_JOIN.equals(joinType)) return "CROSS JOIN";
            if (NATURAL_JOIN.equals(joinType)) return "NATURAL JOIN";
            if (NATURAL_INNER_JOIN.equals(joinType)) return "NATURAL INNER JOIN";
            if (LEFT_OUTER_JOIN.equals(joinType)) return "LEFT JOIN";
            if (RIGHT_OUTER_JOIN.equals(joinType)) return "RIGHT JOIN";
            if (FULL_OUTER_JOIN.equals(joinType)) {
                return "FULL JOIN";
            }
            if (COMMA.equals(joinType)) {
                return ",";
            }
            throw new IllegalStateException("not supported join " + joinType);
        }
    }
}
