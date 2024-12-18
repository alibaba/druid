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
package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLDbTypedObject;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.*;

public class SQLUnionQuery extends SQLSelectQueryBase implements SQLDbTypedObject {
    protected SQLWithSubqueryClause with;
    private List<SQLSelectQuery> relations = new ArrayList<SQLSelectQuery>();
    private SQLUnionOperator operator = SQLUnionOperator.UNION;
    private SQLOrderBy orderBy;

    private SQLLimit limit;
    private DbType dbType;

    public SQLUnionOperator getOperator() {
        return operator;
    }

    public void setOperator(SQLUnionOperator operator) {
        this.operator = operator;
    }

    public SQLUnionQuery() {
    }

    public SQLUnionQuery(DbType dbType) {
        this.dbType = dbType;
    }

    public SQLUnionQuery(SQLSelectQuery left, SQLUnionOperator operator, SQLSelectQuery right) {
        this.setLeft(left);
        this.operator = operator;
        this.setRight(right);
    }

    public List<SQLSelectQuery> getRelations() {
        return relations;
    }

    public boolean isEmpty() {
        return relations.isEmpty();
    }

    public void addRelation(SQLSelectQuery relation) {
        if (relation != null) {
            relation.setParent(this);
        }

        relations.add(relation);
    }

    public SQLSelectQuery getLeft() {
        if (relations.isEmpty()) {
            return null;
        }
        return relations.get(0);
    }

    public void setLeft(SQLSelectQuery left) {
        if (left != null) {
            left.setParent(this);
        }

        if (relations.isEmpty()) {
            relations.add(left);
        } else if (relations.size() <= 2) {
            relations.set(0, left);
        } else {
            throw new UnsupportedOperationException("multi-union");
        }
    }

    public SQLSelectQuery getRight() {
        if (relations.size() < 2) {
            return null;
        }
        if (relations.size() == 2) {
            return relations.get(1);
        }

        throw new UnsupportedOperationException("multi-union");
    }

    public void setRight(SQLSelectQuery right) {
        if (right != null) {
            right.setParent(this);
        }

        if (relations.isEmpty()) {
            relations.add(null);
        }

        if (relations.size() == 1) {
            relations.add(right);
        } else if (relations.size() == 2) {
            relations.set(1, right);
        } else {
            throw new UnsupportedOperationException("multi-union");
        }
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        if (orderBy != null) {
            orderBy.setParent(this);
        }
        this.orderBy = orderBy;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (with != null) {
                with.accept(visitor);
            }

            for (SQLSelectQuery relation : relations) {
                relation.accept(visitor);
            }

            if (orderBy != null) {
                orderBy.accept(visitor);
            }

            if (limit != null) {
                limit.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    public SQLLimit getLimit() {
        return limit;
    }

    public void setLimit(SQLLimit limit) {
        if (limit != null) {
            limit.setParent(this);
        }
        this.limit = limit;
    }

    public SQLUnionQuery clone() {
        SQLUnionQuery x = new SQLUnionQuery();

        x.parenthesized = parenthesized;
        if (with != null) {
            x.setWith(with.clone());
        }

        for (SQLSelectQuery relation : relations) {
            SQLSelectQuery r = relation.clone();
            r.setParent(x);
            x.relations.add(r);
        }

        x.operator = operator;

        if (orderBy != null) {
            x.setOrderBy(orderBy.clone());
        }

        if (limit != null) {
            x.setLimit(limit.clone());
        }

        x.dbType = dbType;

        return x;
    }

    public SQLSelectQueryBlock getFirstQueryBlock() {
        SQLSelectQuery left = getLeft();

        if (left instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) left;
        }

        if (left instanceof SQLUnionQuery) {
            return ((SQLUnionQuery) left).getFirstQueryBlock();
        }

        return null;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public boolean replace(SQLSelectQuery cmp, SQLSelectQuery target) {
        for (int i = 0; i < relations.size(); i++) {
            SQLSelectQuery r = relations.get(i);
            if (r == cmp) {
                if (cmp != null) {
                    cmp.setParent(this);
                }
                relations.set(i, cmp);
                return true;
            }
        }

        return false;
    }

    public List<SQLSelectQuery> getChildren() {
        boolean bracket = this.parenthesized && !(parent instanceof SQLUnionQueryTableSource);

        if (relations.size() > 2) {
            return relations;
        }

        SQLSelectQuery left = getLeft();
        SQLSelectQuery right = getRight();

        if ((!bracket)
                && left instanceof SQLUnionQuery
                && ((SQLUnionQuery) left).getOperator() == operator
                && !right.isParenthesized()
                && orderBy == null) {
            SQLUnionQuery leftUnion = (SQLUnionQuery) left;

            ArrayList<SQLSelectQuery> rights = new ArrayList<SQLSelectQuery>();
            rights.add(right);

            for (; ; ) {
                SQLSelectQuery leftLeft = leftUnion.getLeft();
                SQLSelectQuery leftRight = leftUnion.getRight();

                if ((!leftUnion.isParenthesized())
                        && leftUnion.getOrderBy() == null
                        && (!leftLeft.isParenthesized())
                        && (!leftRight.isParenthesized())
                        && leftLeft instanceof SQLUnionQuery
                        && ((SQLUnionQuery) leftLeft).getOperator() == operator) {
                    rights.add(leftRight);
                    leftUnion = (SQLUnionQuery) leftLeft;
                    continue;
                } else {
                    rights.add(leftRight);
                    rights.add(leftLeft);
                }
                break;
            }
            Collections.reverse(rights);

            return rights;
        }

        return Arrays.asList(left, right);
    }

    public SQLWithSubqueryClause getWith() {
        return with;
    }

    public void setWith(SQLWithSubqueryClause x) {
        if (x != null) {
            x.setParent(this);
        }
        this.with = x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SQLUnionQuery that = (SQLUnionQuery) o;

        if (parenthesized != that.parenthesized) {
            return false;
        }
        if (!Objects.equals(with, that.with)) {
            return false;
        }
        if (!Objects.equals(relations, that.relations)) {
            return false;
        }
        if (operator != that.operator) {
            return false;
        }
        if (!Objects.equals(orderBy, that.orderBy)) {
            return false;
        }
        if (!Objects.equals(limit, that.limit)) {
            return false;
        }
        return dbType == that.dbType;
    }

    @Override
    public int hashCode() {
        int result = (parenthesized ? 1 : 0);
        result = 31 * result + (with != null ? with.hashCode() : 0);
        result = 31 * result + (relations != null ? relations.hashCode() : 0);
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        result = 31 * result + (dbType != null ? dbType.hashCode() : 0);
        return result;
    }
}
