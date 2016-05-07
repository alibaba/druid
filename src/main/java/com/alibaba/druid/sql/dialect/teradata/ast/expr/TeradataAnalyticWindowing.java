/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.teradata.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.teradata.visitor.TeradataASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class TeradataAnalyticWindowing extends SQLObjectImpl implements TeradataExpr {

    private Type    type;
    private SQLExpr expr;

    public TeradataAnalyticWindowing(){

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((TeradataASTVisitor) visitor);
    }

    public void accept0(TeradataASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getExpr() {
        return this.expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static enum Type {
        ROWS, RANGE;
    }
}

