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
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.Utils;

import java.math.BigDecimal;

public class SQLNumberExpr extends SQLNumericLiteralExpr implements SQLValuableExpr {
    public final static SQLDataType DATA_TYPE_NUMBER = new SQLDataTypeImpl("number");

    public final static SQLDataType DATA_TYPE_DOUBLE = new SQLDataTypeImpl("double");
    public final static SQLDataType DATA_TYPE_BIGINT = SQLIntegerExpr.DATA_TYPE;

    private Number number;

    private char[] chars;

    public SQLNumberExpr(){

    }

    public SQLNumberExpr(Number number){
        this.number = number;
    }

    public SQLNumberExpr(char[] chars, SQLObject parent){
        this.chars = chars;
        this.parent = parent;
    }

    public SQLNumberExpr(char[] chars){
        this.chars = chars;
    }

    public Number getNumber() {
        if (chars != null && number == null) {
            boolean exp = false;
            for (int i = 0; i < chars.length; i++) {
                char ch = chars[i];
                if (ch == 'e' || ch == 'E') {
                    exp = true;
                }
            }
            if (exp) {
                this.number = Double.parseDouble(new String(chars));
            } else {
                this.number = new BigDecimal(chars);
            }
        }
        return this.number;
    }

    public String getLiteral() {
        if (chars == null) {
            return null;
        }

        return new String(chars);
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

    public void output(Appendable buf) {
        if (buf instanceof StringBuilder) {
            output((StringBuilder) buf);
        } else if (buf instanceof StringBuffer) {
            output((StringBuffer) buf);
        } else {
            super.output(buf);
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
        return DATA_TYPE_NUMBER;
    }

    public static boolean isZero(SQLExpr x) {
        if (x instanceof SQLNumberExpr) {
            Number number = ((SQLNumberExpr) x).getNumber();
            if (number instanceof Integer) {
                return number.intValue() == 0;
            } else if (number instanceof Long) {
                return number.longValue() == 0L;
            }
        }

        return false;
    }

    public static boolean isOne(SQLExpr x) {
        if (x instanceof SQLNumberExpr) {
            Number number = ((SQLNumberExpr) x).getNumber();
            if (number instanceof Integer) {
                return number.intValue() == 1;
            } else if (number instanceof Long) {
                return number.longValue() == 1L;
            }
        }

        return false;
    }
}
