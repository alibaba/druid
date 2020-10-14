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

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Arrays;
import java.util.List;

public class SQLCastExpr extends SQLExprImpl implements SQLObjectWithDataType, SQLReplaceable {
    protected boolean     isTry;
    protected SQLExpr     expr;
    protected SQLDataType dataType;

    public SQLCastExpr(){

    }

    public SQLCastExpr(SQLExpr expr, SQLDataType dataType) {
        setExpr(expr);
        setDataType(dataType);
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    public SQLDataType getDataType() {
        return this.dataType;
    }

    public long dateTypeHashCode() {
        if (this.dataType == null) {
            return 0;
        }

        return this.dataType.nameHashCode64();
    }

    public void setDataType(SQLDataType dataType) {
        if (dataType != null) {
            dataType.setParent(this);
        }
        this.dataType = dataType;
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.expr != null) {
                this.expr.accept(visitor);
            }

            if (this.dataType != null) {
                this.dataType.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    @Override
    public List getChildren() {
        return Arrays.asList(this.expr, this.dataType);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLCastExpr castExpr = (SQLCastExpr) o;

        if (isTry != castExpr.isTry) return false;
        if (expr != null ? !expr.equals(castExpr.expr) : castExpr.expr != null) return false;
        return dataType != null ? dataType.equals(castExpr.dataType) : castExpr.dataType == null;
    }

    @Override
    public int hashCode() {
        int result = (isTry ? 1 : 0);
        result = 31 * result + (expr != null ? expr.hashCode() : 0);
        result = 31 * result + (dataType != null ? dataType.hashCode() : 0);
        return result;
    }

    public SQLDataType computeDataType() {
        return dataType;
    }

    public SQLCastExpr clone() {
        SQLCastExpr x = new SQLCastExpr();
        x.isTry = isTry;
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        if (dataType != null) {
            x.setDataType(dataType.clone());
        }
        return x;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.expr == expr) {
            setExpr(target);
            return true;
        }
        return false;
    }

    public boolean isTry() {
        return isTry;
    }

    public void setTry(boolean aTry) {
        isTry = aTry;
    }

    public String toString() {
        return SQLUtils.toSQLString(this);
    }
}
