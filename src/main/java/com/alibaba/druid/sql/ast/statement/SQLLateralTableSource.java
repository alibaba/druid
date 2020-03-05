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

import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLLateralTableSource extends SQLTableSourceImpl {

    private SQLMethodInvokeExpr method;
    private SQLSelect select;

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            if (select != null) {
                acceptChild(visitor, select);
            }
            if (method != null) {
                acceptChild(visitor, method);
            }
        }
        visitor.endVisit(this);
    }

    public SQLMethodInvokeExpr getMethod() {
        return method;
    }

    public void setMethod(SQLMethodInvokeExpr method) {
        if (method != null) {
            method.setParent(this);
        }
        this.method = method;
    }

    public SQLSelect getSelect() {
        return select;
    }

    public void setSelect(SQLSelect select) {
        if (select != null) {
            select.setParent(this);
        }
        this.select = select;
    }
}