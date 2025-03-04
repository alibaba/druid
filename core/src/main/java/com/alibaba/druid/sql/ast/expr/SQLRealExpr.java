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

public class SQLRealExpr extends SQLNumericLiteralExpr implements SQLValuableExpr {
    public SQLRealExpr() {
        super(new SQLDataTypeImpl(SQLDataType.Constants.REAL));
    }

    public SQLRealExpr(float value) {
        this();
        this.value = value;
    }

    public SQLRealExpr(String value) {
        this();
        this.value = Float.valueOf(value);
    }

    public SQLRealExpr clone() {
        return new SQLRealExpr(getValue());
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public Number getNumber() {
        return getValue();
    }

    public Float getValue() {
        return (Float) value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SQLRealExpr that = (SQLRealExpr) o;

        return Float.compare(that.getValue(), getValue()) == 0;
    }

    @Override
    public int hashCode() {
        return (getValue() != +0.0f ? Float.floatToIntBits(getValue()) : 0);
    }

    @Override
    public void setNumber(Number number) {
        if (number == null) {
            value = null;
            return;
        }

        this.setValue(number.floatValue());
    }

}
