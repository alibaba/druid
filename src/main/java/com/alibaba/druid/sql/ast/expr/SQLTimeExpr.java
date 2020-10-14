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
import com.alibaba.druid.sql.ast.statement.SQLCharacterDataType;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SQLTimeExpr extends SQLExprImpl implements SQLLiteralExpr, SQLValuableExpr, SQLReplaceable {
    public static final SQLDataType DATA_TYPE = new SQLDataTypeImpl("time");

    private SQLExpr literal;

    public SQLTimeExpr(){

    }


    public SQLTimeExpr(Date now, TimeZone timeZone){
        setLiteral(now, timeZone);
    }

    public void setLiteral(Date x, TimeZone timeZone) {
        if (x == null) {
            this.literal = null;
            return;
        }

        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        if (timeZone != null) {
            format.setTimeZone(timeZone);
        }
        String text = format.format(x);
        setLiteral(text);
    }

    public SQLTimeExpr(String literal) {
        this.setLiteral(literal);
    }

    public SQLExpr getLiteral() {
        return literal;
    }

    public void setLiteral(String literal) {
        setLiteral(new SQLCharExpr(literal));
    }

    public void setLiteral(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.literal = x;
    }

    @Override
    public SQLDataType computeDataType() {
        return DATA_TYPE;
    }

    public String getValue() {
        if (literal instanceof SQLCharExpr) {
            return ((SQLCharExpr) literal).getText();
        }
        return null;
    }

    @Override
    public boolean replace(SQLExpr expr, SQLExpr target) {
        if (this.literal == expr) {
            setLiteral(target);
            return true;
        }
        return false;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((literal == null) ? 0 : literal.hashCode());
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
        SQLTimeExpr other = (SQLTimeExpr) obj;
        if (literal == null) {
            if (other.literal != null) {
                return false;
            }
        } else if (!literal.equals(other.literal)) {
            return false;
        }
        return true;
    }

    public SQLTimeExpr clone() {
        SQLTimeExpr x = new SQLTimeExpr();

        if (this.literal != null) {
            x.setLiteral(literal.clone());
        }

        return x;
    }

    @Override
    public List<SQLObject> getChildren() {
        return Collections.emptyList();
    }

    public static long supportDbTypes = DbType.of(DbType.mysql, DbType.oracle, DbType.presto, DbType.postgresql);

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
