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
package com.alibaba.druid.sql.ast.expr;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.Utils;

import java.io.IOException;
import java.math.BigDecimal;

public class SQLNumberExpr extends SQLNumericLiteralExpr implements SQLValuableExpr {
    public final static SQLDataType defaultDataType = new SQLDataTypeImpl("number");

    private Number number;

    private char[] chars;

    public SQLNumberExpr(){

    }

    public SQLNumberExpr(Number number){
        this.number = number;
    }

    public SQLNumberExpr(char[] chars){
        this.chars = chars;
    }

    public Number getNumber() {
        if (chars != null && number == null) {
            this.number = new BigDecimal(chars);
        }
        return this.number;
    }

    public Number getValue() {
        return getNumber();
    }

    public void setNumber(Number number) {
        this.number = number;
        this.chars = null;
    }

    public void output(StringBuilder buf) {
        if (chars != null) {
            buf.append(chars);
        } else {
            buf.append(this.number.toString());
        }
    }

    public void output(StringBuffer buf) {
        if (chars != null) {
            buf.append(chars);
        } else {
            buf.append(this.number.toString());
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        Number number = getNumber();
        if (number == null) {
            return 0;
        }

        return number.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (chars != null && number == null) {
            this.number = new BigDecimal(chars);
        }

        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        SQLNumberExpr other = (SQLNumberExpr) obj;
        return Utils.equals(getNumber(), other.getNumber());
    }

    public SQLNumberExpr clone() {
        SQLNumberExpr x = new SQLNumberExpr();
        x.chars = chars;
        x.number = number;
        return x;
    }

    public SQLDataType computeDataType() {
        return defaultDataType;
    }
}
