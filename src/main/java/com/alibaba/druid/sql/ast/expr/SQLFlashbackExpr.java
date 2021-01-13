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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

/**
 * Created by wenshao on 14/06/2017.
 */
public class SQLFlashbackExpr extends SQLExprImpl implements SQLReplaceable {
    private Type type;
    private SQLExpr expr;

    public SQLFlashbackExpr() {

    }

    public SQLFlashbackExpr(Type type, SQLExpr expr) {
        this.type = type;
        this.setExpr(expr);
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        if (expr != null) {
            expr.setParent(this);
        }
        this.expr = expr;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.expr == expr) {
            setExpr((SQLName) target);
            return true;
        }
        return false;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (this.expr != null) {
                this.expr.accept(visitor);
            }
        }
        visitor.endVisit(this);
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(expr);
    }

    public SQLFlashbackExpr clone() {
        SQLFlashbackExpr x = new SQLFlashbackExpr();
        x.type = this.type;
        if (expr != null) {
            x.setExpr(expr.clone());
        }
        return x;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLFlashbackExpr that = (SQLFlashbackExpr) o;

        if (type != that.type) return false;
        return expr != null ? expr.equals(that.expr) : that.expr == null;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (expr != null ? expr.hashCode() : 0);
        return result;
    }

    public static enum Type {
        SCN, TIMESTAMP
    }
}
