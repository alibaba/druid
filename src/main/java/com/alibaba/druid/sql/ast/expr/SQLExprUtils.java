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
import com.alibaba.druid.FastsqlException;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.util.FnvHash;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class SQLExprUtils {

    public static boolean equals(SQLExpr a, SQLExpr b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        Class<?> clazz_a = a.getClass();
        Class<?> clazz_b = b.getClass();

        if (clazz_a == SQLPropertyExpr.class && clazz_b == SQLIdentifierExpr.class) {
            return ((SQLPropertyExpr) a).equals((SQLIdentifierExpr) b);
        }

        if (clazz_a != clazz_b) {
            return false;
        }

        if (clazz_a == SQLIdentifierExpr.class) {
            SQLIdentifierExpr x_a = (SQLIdentifierExpr) a;
            SQLIdentifierExpr x_b = (SQLIdentifierExpr) b;
            return x_a.hashCode() == x_b.hashCode();
        }

        if (clazz_a == SQLBinaryOpExpr.class) {
            SQLBinaryOpExpr x_a = (SQLBinaryOpExpr) a;
            SQLBinaryOpExpr x_b = (SQLBinaryOpExpr) b;

            return x_a.equals(x_b);
        }

        return a.equals(b);
    }

    public static boolean isLiteralExpr(SQLExpr expr) {
        if (expr instanceof SQLLiteralExpr) {
            return true;
        }

        if (expr instanceof SQLBinaryOpExpr) {
            SQLBinaryOpExpr binary = (SQLBinaryOpExpr) expr;
            return isLiteralExpr(binary.left) && isLiteralExpr(binary.right);
        }

        return false;
    }

    public static SQLExpr fromJavaObject(Object o) {
        return fromJavaObject(o, null);
    }

    public static SQLExpr fromJavaObject(Object o, TimeZone timeZone) {
        if (o == null) {
            return new SQLNullExpr();
        }

        if (o instanceof String) {
            return new SQLCharExpr((String) o);
        }

        if (o instanceof BigDecimal) {
            return new SQLDecimalExpr((BigDecimal) o);
        }

        if (o instanceof Byte || o instanceof Short || o instanceof Integer || o instanceof Long || o instanceof BigInteger) {
            return new SQLIntegerExpr((Number) o);
        }

        if (o instanceof Number) {
            return new SQLNumberExpr((Number) o);
        }

        if (o instanceof Date) {
            return new SQLTimestampExpr((Date) o, timeZone);
        }

        throw new ParserException("not support class : " + o.getClass());
    }

    public static SQLInListExpr conditionIn(String column, List<Object> values, TimeZone timeZone) {
        SQLInListExpr in = new SQLInListExpr();
        in.setExpr(
                SQLUtils.toSQLExpr(column));

        for (Object value : values) {
            in.addTarget(
                    fromJavaObject(value, timeZone));
        }
        return in;
    }

    public static String quote(String str, DbType dbType, char quote) {
        SQLExpr expr;
        if (quote == '`') {
            expr = new SQLIdentifierExpr(str);
        } else if (quote == '"') {
            if (dbType == DbType.oracle || dbType == DbType.presto) {
                expr = new SQLIdentifierExpr(str);
            }
            else {
                expr = new SQLCharExpr(str);
            }
        } else if (quote == '\'') {
            expr = new SQLCharExpr(str);
        } else {
            throw new FastsqlException("quote not support");
        }

        return SQLUtils.toSQLString(expr, dbType);
    }

    public static SQLDataType createDataTypeFromJdbc(DbType dbType, int jdbType, Integer precision, Integer scale) {
        SQLDataType dataType = null;

        switch (jdbType) {
            case Types.INTEGER:
                if (dbType == null) {
                    return new SQLDataTypeImpl("integer");
                }

                switch (dbType) {
                    case mysql:
                        return new SQLDataTypeImpl("int");
                    default:
                        break;
                }
                break;
            case Types.VARCHAR:
                switch (dbType) {
                    case mysql:
                        return new SQLDataTypeImpl("varchar");
                    default:
                        break;
                }
                break;
            default:
                break;
        }

        if (dataType != null) {
            if (dbType != null) {
                dataType.setDbType(dbType);
            }

            return dataType;
        }
        throw new FastsqlException("type " + jdbType + " not support");
    }
}
