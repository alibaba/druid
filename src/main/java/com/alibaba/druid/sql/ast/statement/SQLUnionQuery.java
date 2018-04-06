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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLUnionQuery extends SQLObjectImpl implements SQLSelectQuery {

    private boolean          bracket  = false;

    private SQLSelectQuery   left;
    private SQLSelectQuery   right;
    private SQLUnionOperator operator = SQLUnionOperator.UNION;
    private SQLOrderBy       orderBy;

    private SQLLimit         limit;
    private String           dbType;

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

    public SQLSelectQuery getLeft() {
        return left;
    }

    public void setLeft(SQLSelectQuery left) {
        if (left != null) {
            left.setParent(this);
        }
        this.left = left;
    }

    public SQLSelectQuery getRight() {
        return right;
    }

    public void setRight(SQLSelectQuery right) {
        if (right != null) {
            right.setParent(this);
        }
        this.right = right;
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
            acceptChild(visitor, left);
            acceptChild(visitor, right);
            acceptChild(visitor, orderBy);
            acceptChild(visitor, limit);
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
        if (left != null) {
            x.setLeft(left.clone());
        }
        if (right != null) {
            x.setRight(right.clone());
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
        if (left instanceof SQLSelectQueryBlock) {
            return (SQLSelectQueryBlock) left;
        }

        if (left instanceof SQLUnionQuery) {
            return ((SQLUnionQuery) left).getFirstQueryBlock();
        }

        return null;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public void output(StringBuffer buf) {
        SQLASTOutputVisitor visitor = SQLUtils.createOutputVisitor(buf, dbType);
        this.accept(visitor);
    }
}
