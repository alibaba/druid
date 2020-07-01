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
package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import java.util.Collections;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleSQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleSizeExpr extends OracleSQLObjectImpl implements OracleExpr {

    private SQLExpr value;
    private Unit    unit;

    public OracleSizeExpr(){

    }

    public OracleSizeExpr(SQLExpr value, Unit unit){
        super();
        this.value = value;
        this.unit = unit;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, value);
        }
        visitor.endVisit(this);
    }

    public List<SQLObject> getChildren() {
        return Collections.<SQLObject>singletonList(value);
    }

    public SQLExpr getValue() {
        return value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public static enum Unit {
        K, M, G, T, P, E
    }

    public OracleSizeExpr clone() {
        OracleSizeExpr x = new OracleSizeExpr();

        if (value != null) {
            x.setValue(value.clone());
        }
        x.unit = unit;

        return x;
    }
}
