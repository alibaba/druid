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
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDbTypedObject;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SQLUnionQuery extends SQLObjectImpl implements SQLSelectQuery, SQLDbTypedObject {

    private boolean          bracket  = false;

    private List<SQLSelectQuery> relations = new ArrayList<SQLSelectQuery>();
    private SQLUnionOperator operator = SQLUnionOperator.UNION;
    private SQLOrderBy       orderBy;

    private SQLLimit         limit;
    private DbType           dbType;

    public SQLUnionOperator getOperator() {
        return operator;
    }

    public void setOperator(SQLUnionOperator operator) {
        this.operator = operator;
    }

    public SQLUnionQuery(){

    }

    public SQLUnionQuery(SQLSelectQuery left, SQLUnionOperator operator, SQLSelectQuery right){
        this.setLeft(left);
        this.operator = operator;
        this.setRight(right);
    }


    public List<SQLSelectQuery> getRelations() {
        return relations;
    }

    public void addRelation(SQLSelectQuery relation) {
        if (relation != null) {
            relation.setParent(this);
        }

        relations.add(relation);
    }

    public SQLSelectQuery getLeft() {
        if (relations.size() == 0) {
            return null;
        }
        return relations.get(0);
    }

    public void setLeft(SQLSelectQuery left) {
        if (left != null) {
            left.setParent(this);
        }

        if (relations.size() == 0) {
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

        if (relations.size() == 0) {
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

    public boolean isBracket() {
        return bracket;
    }

    public void setBracket(boolean bracket) {
        this.bracket = bracket;
    }

    public SQLUnionQuery clone() {
        SQLUnionQuery x = new SQLUnionQuery();

        x.bracket = bracket;

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
        boolean bracket = this.bracket && !(parent instanceof SQLUnionQueryTableSource);

        if (relations.size() > 2) {
            return relations;
        }

        SQLSelectQuery left = getLeft();
        SQLSelectQuery right = getRight();

        if ((!bracket)
                && left instanceof SQLUnionQuery
                && ((SQLUnionQuery) left).getOperator() == operator
                && !right.isBracket()
                && orderBy == null) {

            SQLUnionQuery leftUnion = (SQLUnionQuery) left;

            ArrayList<SQLSelectQuery> rights = new ArrayList<SQLSelectQuery>();
            rights.add(right);

            for (; ; ) {
                SQLSelectQuery leftLeft = leftUnion.getLeft();
                SQLSelectQuery leftRight = leftUnion.getRight();

                if ((!leftUnion.isBracket())
                        && leftUnion.getOrderBy() == null
                        && (!leftLeft.isBracket())
                        && (!leftRight.isBracket())
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLUnionQuery that = (SQLUnionQuery) o;

        if (bracket != that.bracket) return false;
        if (relations != null ? !relations.equals(that.relations) : that.relations != null) return false;
        if (operator != that.operator) return false;
        if (orderBy != null ? !orderBy.equals(that.orderBy) : that.orderBy != null) return false;
        if (limit != null ? !limit.equals(that.limit) : that.limit != null) return false;
        return dbType == that.dbType;
    }

    @Override
    public int hashCode() {
        int result = (bracket ? 1 : 0);
        result = 31 * result + (relations != null ? relations.hashCode() : 0);
        result = 31 * result + (operator != null ? operator.hashCode() : 0);
        result = 31 * result + (orderBy != null ? orderBy.hashCode() : 0);
        result = 31 * result + (limit != null ? limit.hashCode() : 0);
        result = 31 * result + (dbType != null ? dbType.hashCode() : 0);
        return result;
    }
}
