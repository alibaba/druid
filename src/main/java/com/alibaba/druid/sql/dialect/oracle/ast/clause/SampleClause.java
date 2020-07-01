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
package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class SampleClause extends OracleSQLObjectImpl {

    private boolean       block   = false;

    private List<SQLExpr> percent = new ArrayList<SQLExpr>();

    private SQLExpr       seedValue;

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public List<SQLExpr> getPercent() {
        return percent;
    }

    public void setPercent(List<SQLExpr> percent) {
        this.percent = percent;
    }

    public SQLExpr getSeedValue() {
        return seedValue;
    }

    public void setSeedValue(SQLExpr seedValue) {
        this.seedValue = seedValue;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, seedValue);
            acceptChild(visitor, percent);
        }
        visitor.endVisit(this);
    }

    public SampleClause clone() {
        SampleClause x = new SampleClause();

        x.block = block;

        for (SQLExpr item : percent) {
            SQLExpr item1 = item.clone();
            item1.setParent(x);
            x.percent.add(item1);
        }

        if (seedValue != null) {
            x.setSeedValue(seedValue.clone());
        }

        return x;
    }
}
