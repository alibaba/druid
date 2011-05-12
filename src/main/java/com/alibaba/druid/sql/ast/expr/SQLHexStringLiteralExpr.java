/*
 * Copyright 2011 Alibaba Group.
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

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLHexStringLiteralExpr extends SQLLiteralExpr {

    private static final long serialVersionUID = 1L;

    private String            value;

    public SQLHexStringLiteralExpr(){

    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public void output(StringBuffer buf) {
        buf.append("x'");
        buf.append(value);
        buf.append("'");
    }
}
