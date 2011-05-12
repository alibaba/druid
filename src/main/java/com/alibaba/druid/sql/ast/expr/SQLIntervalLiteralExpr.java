/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

/**
 * TODO
 * 
 * @author WENSHAO
 */
public class SQLIntervalLiteralExpr extends SQLLiteralExpr {

    private static final long serialVersionUID = 1L;

    private Character         sign             = null;

    public Character getSign() {
        return sign;
    }

    public void setSign(Character sign) {
        this.sign = sign;
    }

    public SQLIntervalLiteralExpr(){

    }

    @Override
    public void output(StringBuffer buf) {
        buf.append("INTERVAL");
        if (sign != null) {
            buf.append(sign.charValue());
        }
        throw new RuntimeException("TODO");
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

}
