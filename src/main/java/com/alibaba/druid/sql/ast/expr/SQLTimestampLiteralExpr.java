/*
 * Copyright 2011 Alibaba Group.
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

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLTimestampLiteralExpr extends SQLLiteralExpr {
    private static final long serialVersionUID = 1L;

    private SQLDateLiteralValue dateValue;
    private SQLTimeLiteralValue timeValue;
    private SQLTimeZoneIntervalValue timeZoneValue;

    public SQLTimestampLiteralExpr() {

    }

    public SQLDateLiteralValue getDateValue() {
        return dateValue;
    }

    public void setDateValue(SQLDateLiteralValue dateValue) {
        this.dateValue = dateValue;
    }

    public SQLTimeLiteralValue getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(SQLTimeLiteralValue timeValue) {
        this.timeValue = timeValue;
    }

    public SQLTimeZoneIntervalValue getTimeZoneValue() {
        return timeZoneValue;
    }

    public void setTimeZoneValue(SQLTimeZoneIntervalValue timeZoneValue) {
        this.timeZoneValue = timeZoneValue;
    }

    public void output(StringBuffer buf) {
        buf.append("TIMESTAMP'");
        this.dateValue.output(buf);
        buf.append(' ');
        this.timeValue.output(buf);
        if (timeZoneValue != null) {
            timeZoneValue.output(buf);
        }
        buf.append("'");
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

}
