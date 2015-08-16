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
package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleRangeExpr extends OracleSQLObjectImpl implements SQLExpr {

    private SQLExpr lowBound;
    private SQLExpr upBound;

    public OracleRangeExpr(){

    }

    public OracleRangeExpr(SQLExpr lowBound, SQLExpr upBound){
        this.lowBound = lowBound;
        this.upBound = upBound;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, lowBound);
            acceptChild(visitor, upBound);
        }
        visitor.endVisit(this);
    }

    public SQLExpr getLowBound() {
        return lowBound;
    }

    public void setLowBound(SQLExpr lowBound) {
        this.lowBound = lowBound;
    }

    public SQLExpr getUpBound() {
        return upBound;
    }

    public void setUpBound(SQLExpr upBound) {
        this.upBound = upBound;
    }

}
