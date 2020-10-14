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

public class SQLDoubleExpr extends SQLNumericLiteralExpr implements SQLValuableExpr, Comparable<SQLDoubleExpr> {
    public final static SQLDataType DATA_TYPE = new SQLDataTypeImpl("DOUBLE");

    private double value;

    public SQLDoubleExpr(){

    }

    public SQLDoubleExpr(String value){
        super();
        this.value = Double.parseDouble(value);
    }

    public SQLDoubleExpr(double value){
        super();
        this.value = value;
    }

    public SQLDoubleExpr clone() {
        return new SQLDoubleExpr(value);
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public Double getNumber() {
        return value;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(double value) {
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

        SQLDoubleExpr that = (SQLDoubleExpr) o;

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
            this.setValue(Double.NaN);
            return;
        }

        this.setValue(number.doubleValue());
    }

    @Override
    public int compareTo(SQLDoubleExpr o) {
        return Double.compare(value, o.value);
    }
}
