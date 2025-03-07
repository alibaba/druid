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
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SQLDateTimeExpr extends SQLDateTypeExpr {
    public SQLDateTimeExpr() {
        super(new SQLDataTypeImpl(SQLDataType.Constants.DATETIME));
    }

    public SQLDateTimeExpr(Date now, TimeZone timeZone) {
        this();
        setValue(now, timeZone);
    }

    public void setValue(Date x, TimeZone timeZone) {
        if (x == null) {
            this.value = null;
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        String text = format.format(x);
        setValue(text);
    }

    public SQLDateTimeExpr(String literal) {
        this();
        this.setValue(literal);
    }

    public void setValue(String literal) {
        value = literal;
    }
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.dataType);
        }
        visitor.endVisit(this);
    }

    @Override
    public String getValue() {
        return (String) value;
    }

    public SQLDateTimeExpr clone() {
        SQLDateTimeExpr x = new SQLDateTimeExpr();
        x.value = this.value;

        return x;
    }

    public static long supportDbTypes = DbType.of(
            DbType.mysql,
            DbType.oracle,
            DbType.presto,
            DbType.trino,
            DbType.postgresql,
            DbType.mariadb,
            DbType.tidb,
            DbType.polardbx
    );

    public static boolean isSupport(DbType dbType) {
        return (dbType.mask & supportDbTypes) != 0;
    }

    public static boolean check(String str) {
        if (str == null || str.length() != 8) {
            return false;
        }

        if (str.charAt(2) != ':' && str.charAt(5) != ':') {
            return false;
        }

        char c0 = str.charAt(0);
        char c1 = str.charAt(1);
        char c3 = str.charAt(3);
        char c4 = str.charAt(4);
        char c6 = str.charAt(6);
        char c7 = str.charAt(7);

        if (c0 < '0' || c0 > '9') {
            return false;
        }
        if (c1 < '0' || c1 > '9') {
            return false;
        }
        if (c3 < '0' || c3 > '9') {
            return false;
        }
        if (c4 < '0' || c4 > '9') {
            return false;
        }
        if (c6 < '0' || c6 > '9') {
            return false;
        }
        if (c7 < '0' || c7 > '9') {
            return false;
        }

        int HH = (c0 - '0') * 10 + (c1 - '0');
        int mm = (c3 - '0') * 10 + (c4 - '0');
        int ss = (c6 - '0') * 10 + (c7 - '0');

        if (HH > 24 || mm > 60 || ss > 60) {
            return false;
        }

        return true;
    }
}
