/*
 * Copyright 1999-2024 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.Serializable;

/**
 * @author lizongbo
 */
public class SQLSelectExpr extends SQLExprImpl implements SQLReplaceable, Serializable {
    private static final long serialVersionUID = 1L;
    protected SQLSelect sqlSelect;
    protected SQLExpr as;

    public SQLSelectExpr() {
    }

    public SQLSelectExpr(SQLSelect sqlSelect) {
        this.sqlSelect = sqlSelect;
    }

    public SQLSelect getSqlSelect() {
        return sqlSelect;
    }

    public void setSqlSelect(SQLSelect sqlSelect) {
        this.sqlSelect = sqlSelect;
    }

    public SQLExpr getAs() {
        return as;
    }

    public void setAs(SQLExpr as) {
        this.as = as;
    }

    public SQLSelectExpr clone() {
        SQLSelectExpr x = new SQLSelectExpr();
        if (sqlSelect != null) {
            x.setSqlSelect(sqlSelect.clone());
        }
        if (as != null) {
            x.setAs(as.clone());
        }
        x.setParenthesized(parenthesized);
        return x;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.sqlSelect != null) {
                this.sqlSelect.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((sqlSelect == null) ? 0 : sqlSelect.hashCode());
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
        SQLSelectExpr other = (SQLSelectExpr) obj;
        if (sqlSelect == null) {
            if (other.sqlSelect != null) {
                return false;
            }
        } else if (!sqlSelect.equals(other.sqlSelect)) {
            return false;
        }
        return true;
    }

    @Override
    public SQLDataType computeDataType() {
        return SQLBooleanExpr.DATA_TYPE;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        return false;
    }
}
