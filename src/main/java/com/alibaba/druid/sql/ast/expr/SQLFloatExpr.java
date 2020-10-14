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

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.Collections;
import java.util.List;

public class SQLFloatExpr extends SQLNumericLiteralExpr implements SQLValuableExpr, Comparable<SQLFloatExpr> {
    public final static SQLDataType DATA_TYPE = new SQLDataTypeImpl("FLOAT");

    private float value;

    public SQLFloatExpr(){

    }

    public SQLFloatExpr(String value){
        super();
        this.value = Float.parseFloat(value);
    }

    public SQLFloatExpr(float value){
        super();
        this.value = value;
    }

    public SQLFloatExpr clone() {
        return new SQLFloatExpr(value);
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public Float getNumber() {
        return value;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLFloatExpr that = (SQLFloatExpr) o;

        return Double.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        long temp = Double.doubleToLongBits(value);
        return (int) (temp ^ (temp >>> 32));
    }

    @Override
    public void setNumber(Number number) {
        if (number == null) {
            this.setValue(Float.NaN);
            return;
        }

        this.setValue(number.floatValue());
    }

    @Override
    public int compareTo(SQLFloatExpr o) {
        return Float.compare(value, o.value);
    }
}
