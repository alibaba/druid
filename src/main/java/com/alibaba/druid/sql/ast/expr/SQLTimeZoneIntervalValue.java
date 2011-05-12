/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

public class SQLTimeZoneIntervalValue {

    private SQLUnaryOperator sign = SQLUnaryOperator.Plus;
    private int              hours;
    private int              minutes;

    public SQLUnaryOperator getSign() {
        return sign;
    }

    public void setSign(SQLUnaryOperator sign) {
        this.sign = sign;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void output(StringBuffer buf) {
        if (sign == SQLUnaryOperator.Plus) {
            buf.append('+');
        } else {
            buf.append('-');
        }
        buf.append(hours);
        buf.append(':');
        buf.append(minutes);
    }
}
