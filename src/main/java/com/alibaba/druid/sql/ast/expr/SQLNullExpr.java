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

import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE_NULL;

public final class SQLNullExpr extends SQLExprImpl implements SQLLiteralExpr, SQLValuableExpr {

    public SQLNullExpr(){

    }

    public SQLNullExpr(SQLObject parent){
        this.parent = parent;
    }

    public void output(Appendable buf) {
        try {
            buf.append("NULL");
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public int hashCode() {
        return 0;
    }

    public boolean equals(Object o) {
        return o instanceof SQLNullExpr;
    }

    @Override
    public Object getValue() {
        return EVAL_VALUE_NULL;
    }

    public SQLNullExpr clone() {
        return new SQLNullExpr();
    }

    @Override
    public List getChildren() {
        return Collections.emptyList();
    }
}
