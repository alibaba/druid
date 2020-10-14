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

import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class SQLIntegerExpr extends SQLNumericLiteralExpr implements SQLValuableExpr, Comparable<SQLIntegerExpr> {
    public static final SQLDataType DATA_TYPE = new SQLDataTypeImpl("bigint");

    private Number number;
    private String type;

    public SQLIntegerExpr(Number number){
        this.number = number;
    }


    public SQLIntegerExpr(Number number, SQLObject parent){
        this.number = number;
        this.parent = parent;
    }

    public SQLIntegerExpr(){

    }

    public Number getNumber() {
        return this.number;
    }

    public void setNumber(Number number) {
        this.number = number;
    }

    public void output(Appendable buf) {
        try {
            buf.append(this.number.toString());
        } catch (IOException ex) {
            throw new FastsqlException("output error", ex);
        }
    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((number == null) ? 0 : number.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SQLIntegerExpr other = (SQLIntegerExpr) obj;
        if (number == null) {
            if (other.number != null) {
                return false;
            }
        } else if (!number.equals(other.number)) {
            return false;
        }
        return true;
    }

    @Override
    public Object getValue() {
        return this.number;
    }

    public SQLIntegerExpr clone() {
        return new SQLIntegerExpr(this.number);
    }

    public SQLDataType computeDataType() {
        return DATA_TYPE;
    }

    public void decrement() {
        if (number instanceof Integer) {
            number = Integer.valueOf((Integer) number.intValue() - 1);
        } else if (number instanceof Long) {
            number = Long.valueOf((Long) number.longValue() - 1);
        } else {
            throw new FastsqlException("decrement not support.");
        }
    }

    public static boolean isZero(SQLExpr expr) {
        if (expr instanceof SQLIntegerExpr) {
            Number number = ((SQLIntegerExpr) expr).getNumber();
            return number != null && number.intValue() == 0;
        }
        return false;
    }

    public static SQLIntegerExpr substract(SQLIntegerExpr a, SQLIntegerExpr b) {
        int val = a.number.intValue() - b.number.intValue();
        return new SQLIntegerExpr(val);
    }

    public static SQLIntegerExpr least(SQLIntegerExpr a, SQLIntegerExpr b) {
        if (a == null) {
            return b;
        }

        if (a.number.intValue() <= b.number.intValue()) {
            return a;
        }

        return b;
    }

    public static SQLIntegerExpr greatst(SQLIntegerExpr a, SQLIntegerExpr b) {
        if (a.number.intValue() >= b.number.intValue()) {
            return a;
        }

        return b;
    }

    public static SQLIntegerExpr ofIntOrLong(long value) {
        if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
            return new SQLIntegerExpr((int) value);
        }

        return new SQLIntegerExpr(value);
    }

    public static SQLIntegerExpr add(long a, long b) {
        long r = a + b;

        if (a > 0 && b > 0 && r <= 0) {
            return new SQLIntegerExpr(BigInteger.valueOf(a).add(BigInteger.valueOf(b)));
        }

        return new SQLIntegerExpr(r);
    }

    @Override
    public int compareTo(SQLIntegerExpr o) {
        if (this.number instanceof Integer && o.number instanceof Integer) {
            return ((Integer) this.number).compareTo((Integer) o.number);
        }

        if (this.number instanceof Long && o.number instanceof Long) {
            return ((Long) this.number).compareTo((Long) o.number);
        }

        if (this.number instanceof BigDecimal && o.number instanceof BigDecimal) {
            return ((BigDecimal) this.number).compareTo((BigDecimal) o.number);
        }

        if (this.number instanceof Float && o.number instanceof Float) {
            return ((Float) this.number).compareTo((Float) o.number);
        }

        if (this.number instanceof Double && o.number instanceof Double) {
            return ((Float) this.number).compareTo((Float) o.number);
        }

        return -1;
    }
}
