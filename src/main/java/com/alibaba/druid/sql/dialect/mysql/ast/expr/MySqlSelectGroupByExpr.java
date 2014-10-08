/*
 * Copyright 2014 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.druid.sql.dialect.mysql.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class MySqlSelectGroupByExpr extends SQLExprImpl implements MySqlExpr {

    protected SQLExpr                  expr;
    protected SQLOrderingSpecification type;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        MySqlASTVisitor mysqlVisitor = (MySqlASTVisitor) visitor;
        if (mysqlVisitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }

        mysqlVisitor.endVisit(this);
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        expr.setParent(this);
        this.expr = expr;
    }

    public SQLOrderingSpecification getType() {
        return type;
    }

    public void setType(SQLOrderingSpecification type) {
        this.type = type;
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
        MySqlSelectGroupByExpr other = (MySqlSelectGroupByExpr) obj;
        if (expr != other.expr) {
            return false;
        }
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

}
