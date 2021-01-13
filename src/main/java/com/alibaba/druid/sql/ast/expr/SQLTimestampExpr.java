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

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;
import com.alibaba.druid.util.MySqlUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SQLTimestampExpr extends SQLExprImpl implements SQLValuableExpr, SQLLiteralExpr {
    public static final SQLDataType DATA_TYPE = new SQLDataTypeImpl("timestamp");

    protected String  literal;
    protected String  timeZone;
    protected boolean withTimeZone = false;

    public SQLTimestampExpr(){
        
    }

    public SQLTimestampExpr(String literal){
        this.literal = literal;
    }

    public SQLTimestampExpr(Date date){
        setLiteral(date);
    }

    public SQLTimestampExpr(Date date, TimeZone timeZone){
        setLiteral(date, timeZone);
    }

    public SQLTimestampExpr clone() {
        SQLTimestampExpr x = new SQLTimestampExpr();
        x.literal = literal;
        x.timeZone = timeZone;
        x.withTimeZone = withTimeZone;
        return x;
    }

    public Date getDate(TimeZone timeZone) {
        if (literal == null || literal.length() == 0) {
            return null;
        }

        return MySqlUtils.parseDate(literal, timeZone);
    }

    public boolean addDay(int delta) {
        if (literal == null) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(literal);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, delta);
            String result_chars = format.format(calendar.getTime());
            setLiteral(result_chars);
            return true;
        } catch (ParseException e) {
            // skip
        }

        return false;
    }

    public boolean addMonth(int delta) {
        if (literal == null) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(literal);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, delta);
            String result_chars = format.format(calendar.getTime());
            setLiteral(result_chars);
            return true;
        } catch (ParseException e) {
            // skip
        }

        return false;
    }

    public boolean addHour(int delta) {
        if (literal == null) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(literal);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, delta);
            String result_chars = format.format(calendar.getTime());
            setLiteral(result_chars);
            return true;
        } catch (ParseException e) {
            // skip
        }

        return false;
    }

    public boolean addMiniute(int delta) {
        if (literal == null) {
            return false;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(literal);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, delta);
            String result_chars = format.format(calendar.getTime());
            setLiteral(result_chars);
            return true;
        } catch (ParseException e) {
            // skip
        }

        return false;
    }

    public String getValue() {
        return literal;
    }

    public String getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        this.literal = literal;
    }

    public void setLiteral(Date x) {
        setLiteral(x, null);
    }

    public void setLiteral(Date x, TimeZone timeZone) {
        if (x == null) {
            this.literal = null;
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        this.literal = format.format(x);
    }

    public String getTimeZone() {
        return this.timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public boolean isWithTimeZone() {
        return withTimeZone;
    }

    public void setWithTimeZone(boolean withTimeZone) {
        this.withTimeZone = withTimeZone;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((literal == null) ? 0 : literal.hashCode());
        result = prime * result + ((timeZone == null) ? 0 : timeZone.hashCode());
        result = prime * result + (withTimeZone ? 1231 : 1237);
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
        SQLTimestampExpr other = (SQLTimestampExpr) obj;
        if (literal == null) {
            if (other.literal != null) {
                return false;
            }
        } else if (!literal.equals(other.literal)) {
            return false;
        }
        if (timeZone == null) {
            if (other.timeZone != null) {
                return false;
            }
        } else if (!timeZone.equals(other.timeZone)) {
            return false;
        }
        if (withTimeZone != other.withTimeZone) {
            return false;
        }
        return true;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }

    public String toString() {
        return SQLUtils.toSQLString(this, (DbType) null);
    }

    public SQLDataType computeDataType() {
        return DATA_TYPE;
    }

    @Override
    public List getChildren() {
        return Collections.emptyList();
    }

    public static boolean check(String str) {
        final int len;
        if (str == null || (len = str.length()) < 14 || len > 23) {
            return false;
        }

        final char c0 = str.charAt(0);
        final char c1 = str.charAt(1);
        final char c2 = str.charAt(2);
        final char c3 = str.charAt(3);
        final char c4 = str.charAt(4);
        final char c5 = str.charAt(5);
        final char c6 = str.charAt(6);
        final char c7 = str.charAt(7);
        final char c8 = str.charAt(8);
        final char c9 = str.charAt(9);
        final char c10 = str.charAt(10);

        // check year
        if (c0 < '1' | c0 > '9') {
            return false;
        }
        if (c1 < '0' | c1 > '9') {
            return false;
        }
        if (c2 < '0' | c2 > '9') {
            return false;
        }
        if (c3 < '0' | c3 > '9') {
            return false;
        }

        int year = (c0 - '0') * 1000 + (c1 - '0') * 100 + (c2 - '0') * 10 + (c3 - '0');
        if (year < 1000) {
            return false;
        }

        if(c4 != '-') {
            return false;
        }

        final int month, day;
        char M0 = 0, M1 = 0, d0 = 0, d1 = 0;
        if (c8 == ' ') {
            if(c6 != '-') {
                return false;
            }

            M1 = c5;
            d1 = c7;
        } else if (c9 == ' ') {
            if(c6 == '-') {
                M1 = c5;
                d0 = c7;
                d1 = c8;
            } else if (c7 == '-') {
                M0 = c5;
                M1 = c6;
                d1 = c8;
            } else {
                return false;
            }
        } else if (c10 == ' ') {
            if (c7 != '-') {
                return false;
            }

            M0 = c5;
            M1 = c6;
            d0 = c8;
            d1 = c9;
        } else {
            return false;
        }

        if (M0 == 0) {
            if (M1 < '0' || M1 > '9') {
                return false;
            }
            month = M1 - '0';
        } else {
            if (M0 != '0' && M0 != '1') {
                return false;
            }
            if (M1 < '0' || M1 > '9') {
                return false;
            }
            month = (M0 - '0') * 10 + M1 - '0';
        }

        if (d0 == 0) {
            if (d1 < '0' || d1 > '9') {
                return false;
            }
            day = d1 - '0';
        } else {
            if (d0 < '0' || d0 > '9') {
                return false;
            }
            if (d1 < '0' || d1 > '9') {
                return false;
            }
            day = (d0 - '0') * 10 + d1 - '0';
        }

        if (month < 1) {
            return false;
        }

        if (day < 1) {
            return false;
        }

        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                if (day > 31) {
                    return false;
                }
                break;
            case 2:
                if (day > 29) {
                    return false;
                }
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                if (day > 30) {
                    return false;
                }
                break;
            default:
                break;
        }

        int index = len - 1;
        if (str.charAt(index) == '0' && str.charAt(index - 1) == '.') {
            index -= 2;
        }
        final char x0 = str.charAt(index--);
        final char x1 = str.charAt(index--);
        final char x2 = str.charAt(index--);
        final char x3 = str.charAt(index--);
        final char x4 = str.charAt(index--);
        final char x5 = str.charAt(index--);
        final char x6 = str.charAt(index--);
        final char x7 = str.charAt(index--);
        final char x8 = str.charAt(index--);
        final char x9 = str.charAt(index--);
        final char x10 = str.charAt(index--);
        final char x11 = str.charAt(index--);
        final char x12 = str.charAt(index--);

        final char h0, h1, m0, m1, s0, s1;

        if (x5 == ' ') {
            if (x1 != ':' || x3 != ':') {
                return false;
            }
            s0 = 0;
            s1 = x0;
            m0 = 0;
            m1 = x2;
            h0 = 0;
            h1 = x4;
        } else if (x6 == ' ') {
            if (x1 == ':') {
                s0 = 0;
                s1 = x0;

                if (x3 == ':') {
                    m0 = 0;
                    m1 = x2;
                    h1 = x4;
                    h0 = x5;
                } else if (x4 == ':') {
                    m0 = x2;
                    m1 = x3;
                    h0 = 0;
                    h1 = x5;
                } else {
                    return false;
                }
            } else if (x2 == ':') {
                s0 = x0;
                s1 = x1;

                if (x4 != ':') {
                    return false;
                }

                m0 = 0;
                m1 = x3;
                h0 = 0;
                h1 = x5;
            } else {
                return false;
            }
        } else if (x7 == ' ') {
            if (x1 == ':') {
                s0 = 0;
                s1 = x0;

                if (x4 != ':') {
                    return false;
                }
                m1 = x2;
                m0 = x3;
                h1 = x5;
                h0 = x6;
            } else if (x2 == ':') {
                s0 = x0;
                s1 = x1;

                if (x4 == ':') {
                    m0 = 0;
                    m1 = x3;
                    h1 = x5;
                    h0 = x6;
                } else if (x5 == ':'){
                    m1 = x3;
                    m0 = x4;
                    h0 = 0;
                    h1 = x6;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else if (x8 == ' ') {
            if (x2 != ':' || x5 != ':') {
                return false;
            }
            s1 = x0;
            s0 = x1;
            m1 = x3;
            m0 = x4;
            h1 = x6;
            h0 = x7;
        } else if (x12 == ' ') {
            if (x3 != '.' || x6 != ':' || x9 != ':') {
                return false;
            }
            s1 = x4;
            s0 = x5;
            m1 = x7;
            m0 = x8;
            h1 = x10;
            h0 = x11;

            int S2 = x0;
            int S1 = x1;
            int S0 = x2;

            if (S0 < '0' || S0 > '9') {
                return false;
            }

            if (S1 < '0' || S1 > '9') {
                return false;
            }

            if (S2 < '0' || S2 > '9') {
                return false;
            }
        } else {
            return false;
        }

        if (h0 == 0) {
            if (h1 < '0' || h1 > '9') {
                return false;
            }
        } else {
            if (h0 < '0' || h0 > '2') {
                return false;
            }
            if (h1 < '0' || h1 > '9') {
                return false;
            }

            int hour = (h0 - '0') * 10 + (h1 - '0');
            if (hour > 24) {
                return false;
            }
        }

        if (m0 == 0) {
            if (m1 < '0' || m1 > '9') {
                return false;
            }
        } else {
            if (m0 < '0' || m0 > '6') {
                return false;
            }
            if (m1 < '0' || m1 > '9') {
                return false;
            }

            int minute = (m0 - '0') * 10 + (m1 - '0');
            if (minute > 60) {
                return false;
            }
        }

        if (s0 == 0) {
            if (s1 < '0' || s1 > '9') {
                return false;
            }
        } else {
            if (s0 < '0' || s0 > '6') {
                return false;
            }
            if (s1 < '0' || s1 > '9') {
                return false;
            }

            int second = (s0 - '0') * 10 + (s1 - '0');
            if (second > 60) {
                return false;
            }
        }

        return true;
    }

    public static SQLTimestampExpr of(String str) {
        return new SQLTimestampExpr(str);
    }
}
