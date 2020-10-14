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
package com.alibaba.druid.sql.dialect.oracle.ast.clause;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLReplaceable;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class SampleClause extends OracleSQLObjectImpl implements SQLReplaceable {

    private boolean             block   = false;

    private final List<SQLExpr> percent = new ArrayList<SQLExpr>();

    private SQLExpr             seedValue;

    public boolean isBlock() {
        return block;
    }

    public void setBlock(boolean block) {
        this.block = block;
    }

    public List<SQLExpr> getPercent() {
        return percent;
    }

    public void addPercent(SQLExpr x) {
        if (x == null) {
            return;
        }
        x.setParent(this);
        this.percent.add(x);
    }

    public SQLExpr getSeedValue() {
        return seedValue;
    }

    public void setSeedValue(SQLExpr seedValue) {
        if (seedValue != null) {
            seedValue.setParent(this);
        }
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

    @Override
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

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {

        for (int i = percent.size() - 1; i >= 0; i--) {
            if (percent.get(i) == expr) {
                percent.set(i, target);
                return true;
            }
        }

        if (expr == seedValue) {
            setSeedValue(target);
            return true;
        }

        return false;
    }
}
