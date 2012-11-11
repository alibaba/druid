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
package com.alibaba.druid.sql.visitor;

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlEvalVisitorImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleEvalVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGEvalVisitor;
import com.alibaba.druid.util.JdbcUtils;

public class SQLEvalVisitorUtils {

    public static Object evalExpr(String dbType, String expr, Object... parameters) {
        SQLExpr sqlExpr = SQLUtils.toSQLExpr(expr, dbType);
        return eval(dbType, sqlExpr, parameters);
    }

    public static Object evalExpr(String dbType, String expr, List<Object> parameters) {
        SQLExpr sqlExpr = SQLUtils.toSQLExpr(expr);
        return eval(dbType, sqlExpr, parameters);
    }

    public static Object eval(String dbType, SQLObject sqlObject, Object... parameters) {
        return eval(dbType, sqlObject, Arrays.asList(parameters));
    }

    public static Object getValue(SQLObject sqlObject) {
        if (sqlObject instanceof SQLNumericLiteralExpr) {
            return ((SQLNumericLiteralExpr) sqlObject).getNumber();
        }

        return sqlObject.getAttributes().get(EVAL_VALUE);
    }

    public static Object eval(String dbType, SQLObject sqlObject, List<Object> parameters) {
        return eval(dbType, sqlObject, parameters, true);
    }

    public static Object eval(String dbType, SQLObject sqlObject, List<Object> parameters, boolean throwError) {
        SQLEvalVisitor visitor = createEvalVisitor(dbType);
        visitor.setParameters(parameters);
        sqlObject.accept(visitor);

        Object value = getValue(sqlObject);
        if (value == null) {
            if (throwError && !sqlObject.getAttributes().containsKey(EVAL_VALUE)) {
                throw new DruidRuntimeException("eval error : " + SQLUtils.toSQLString(sqlObject, dbType));
            }
        }

        return value;
    }

    public static SQLEvalVisitor createEvalVisitor(String dbType) {
        if (JdbcUtils.MYSQL.equals(dbType)) {
            return new MySqlEvalVisitorImpl();
        }

        if (JdbcUtils.H2.equals(dbType)) {
            return new MySqlEvalVisitorImpl();
        }

        if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            return new OracleEvalVisitor();
        }

        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return new PGEvalVisitor();
        }

        return new SQLEvalVisitorImpl();
    }
    
    public static boolean visit(SQLEvalVisitor visitor, SQLMethodInvokeExpr x) {
        if ("concat".equals(x.getMethodName())) {
            StringBuilder buf = new StringBuilder();
            
            for (SQLExpr item : x.getParameters()) {
                item.accept(visitor);
                
                Object itemValue = item.getAttributes().get(EVAL_VALUE);
                if (itemValue == null) {
                    continue;
                }
                buf.append(itemValue.toString());
            }
            
            x.getAttributes().put(EVAL_VALUE, buf.toString());
        } else if ("now".equals(x.getMethodName())) {
            x.getAttributes().put(EVAL_VALUE, new Date());
        }
        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLCharExpr x) {
        x.putAttribute(EVAL_VALUE, x.getText());
        return true;
    }
    
    public static boolean visit(SQLEvalVisitor visitor, SQLBetweenExpr x) {
        x.getTestExpr().accept(visitor);
        
        if (!x.getTestExpr().getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }

        Object value = x.getTestExpr().getAttribute(EVAL_VALUE);
        
        x.getBeginExpr().accept(visitor);
        if (!x.getBeginExpr().getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }

        Object begin = x.getBeginExpr().getAttribute(EVAL_VALUE);
        
        if (lt(value, begin)) {
            x.getAttributes().put(EVAL_VALUE, x.isNot() ? true : false);
            return false;
        }
        
        x.getEndExpr().accept(visitor);
        if (!x.getEndExpr().getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }

        Object end = x.getEndExpr().getAttribute(EVAL_VALUE);
        
        if (gt(value, end)) {
            x.getAttributes().put(EVAL_VALUE, x.isNot() ? true : false);
            return false;
        }

        x.getAttributes().put(EVAL_VALUE, x.isNot() ? false : true);
        return false;
    }
    
    public static boolean visit(SQLEvalVisitor visitor, SQLNullExpr x) {
        x.getAttributes().put(EVAL_VALUE, null);
        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLCaseExpr x) {
        x.getValueExpr().accept(visitor);

        if (!x.getValueExpr().getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }

        Object value = x.getValueExpr().getAttribute(EVAL_VALUE);

        for (SQLCaseExpr.Item item : x.getItems()) {
            item.getConditionExpr().accept(visitor);

            if (!item.getConditionExpr().getAttributes().containsKey(EVAL_VALUE)) {
                return false;
            }

            Object conditionValue = item.getConditionExpr().getAttribute(EVAL_VALUE);

            if (eq(value, conditionValue)) {
                item.getValueExpr().accept(visitor);

                if (item.getValueExpr().getAttributes().containsKey(EVAL_VALUE)) {
                    x.getAttributes().put(EVAL_VALUE, item.getValueExpr().getAttribute(EVAL_VALUE));
                }

                return false;
            }
        }

        if (x.getElseExpr() != null) {
            x.getElseExpr().accept(visitor);

            if (x.getElseExpr().getAttributes().containsKey(EVAL_VALUE)) {
                x.getAttributes().put(EVAL_VALUE, x.getElseExpr().getAttribute(EVAL_VALUE));
            }
        }

        return false;
    }
    
    public static boolean visit(SQLEvalVisitor visitor, SQLInListExpr x) {
        SQLExpr valueExpr = x.getExpr();
        valueExpr.accept(visitor);
        if (!valueExpr.getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }
        Object value = valueExpr.getAttribute(EVAL_VALUE);
        
        for (SQLExpr item : x.getTargetList()) {
            item.accept(visitor);
            if (!item.getAttributes().containsKey(EVAL_VALUE)) {
                return false;
            }
            Object itemValue = item.getAttribute(EVAL_VALUE);
            if (eq(value, itemValue)) {
                x.getAttributes().put(EVAL_VALUE, x.isNot() ? false : true);
                return false;
            }
        }
        
        x.getAttributes().put(EVAL_VALUE, x.isNot() ? true : false);
        return false;
    }
    
    public static boolean visit(SQLEvalVisitor visitor, SQLBinaryOpExpr x) {
        SQLExpr left = x.getLeft();
        SQLExpr right = x.getRight();
        
        left.accept(visitor);
        if (!left.getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }
        
        right.accept(visitor);
        if (!right.getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }

        Object value = null;
        switch (x.getOperator()) {
            case Add:
                value = add(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            case Subtract:
                value = sub(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            case Multiply:
                value = multi(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            case Divide:
                value = div(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            case GreaterThan:
                value = gt(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            case GreaterThanOrEqual:
                value = gteq(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            case LessThan:
                value = lt(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            case LessThanOrEqual:
                value = lteq(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            case Is:
                value = eq(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            case IsNot:
                value = !eq(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            default:
                break;
        }

        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLNumericLiteralExpr x) {
        x.getAttributes().put(EVAL_VALUE, x.getNumber());
        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLVariantRefExpr x) {
        if (!"?".equals(x.getName())) {
            return false;
        }

        Map<String, Object> attributes = x.getAttributes();

        int varIndex = x.getIndex();

        if (varIndex != -1 && visitor.getParameters().size() > varIndex) {
            boolean containsValue = attributes.containsKey(EVAL_VALUE);
            if (!containsValue) {
                Object value = visitor.getParameters().get(varIndex);
                attributes.put(EVAL_VALUE, value);
            }
        }

        return false;
    }

    public static Boolean _bool(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Boolean) {
            return (Boolean) val;
        }

        if (val instanceof Number) {
            return ((Number) val).intValue() == 1;
        }

        throw new IllegalArgumentException();
    }

    public static String _string(Object val) {
        Object value = val;

        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public static Byte _byte(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Byte) {
            return (Byte) val;
        }

        return ((Number) val).byteValue();
    }

    public static Short _short(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Short) {
            return (Short) val;
        }

        return ((Number) val).shortValue();
    }

    public static Integer _int(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Integer) {
            return (Integer) val;
        }

        return ((Number) val).intValue();
    }

    public static Long _long(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Long) {
            return (Long) val;
        }

        return ((Number) val).longValue();
    }

    public static Float _float(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Float) {
            return (Float) val;
        }

        return ((Number) val).floatValue();
    }

    public static Double _double(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Double) {
            return (Double) val;
        }

        return ((Number) val).doubleValue();
    }

    public static BigInteger _bigInt(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof BigInteger) {
            return (BigInteger) val;
        }

        if (val instanceof String) {
            return new BigInteger((String) val);
        }

        return BigInteger.valueOf(((Number) val).longValue());
    }

    public static Date _date(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Date) {
            return (Date) val;
        }

        if (val instanceof Number) {
            return new Date(((Number) val).longValue());
        }

        if (val instanceof String) {
            return _date((String) val);
        }

        throw new DruidRuntimeException("can cast to date");
    }

    public static Date _date(String text) {
        if (text == null || text.length() == 0) {
            return null;
        }

        String format;

        if (text.length() == "yyyy-MM-dd".length()) {
            format = "yyyy-MM-dd";
        } else {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        try {
            return new SimpleDateFormat(format).parse(text);
        } catch (ParseException e) {
            throw new DruidRuntimeException("format : " + format + ", value : " + text, e);
        }
    }

    public static BigDecimal _decimal(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof BigDecimal) {
            return (BigDecimal) val;
        }

        if (val instanceof String) {
            return new BigDecimal((String) val);
        }

        if (val instanceof Float) {
            return new BigDecimal((Float) val);
        }

        if (val instanceof Double) {
            return new BigDecimal((Double) val);
        }

        return BigDecimal.valueOf(((Number) val).longValue());
    }

    public static Object _sum(Object a, Object b) {
        if (a == null) {
            return b;
        }

        if (b == null) {
            return a;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return _decimal(a).add(_decimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return _bigInt(a).add(_bigInt(b));
        }

        if (a instanceof Long || b instanceof Long) {
            return _long(a) + _long(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return _int(a) + _int(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return _short(a) + _short(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return _byte(a) + _byte(b);
        }

        throw new IllegalArgumentException();
    }

    public static Object div(Object a, Object b) {
        if (a == null || b == null) {
            return null;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return _decimal(a).divide(_decimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return _bigInt(a).divide(_bigInt(b));
        }

        if (a instanceof Long || b instanceof Long) {
            return _long(a) / _long(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return _int(a) / _int(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return _short(a) / _short(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return _byte(a) / _byte(b);
        }

        throw new IllegalArgumentException();
    }

    public static boolean gt(Object a, Object b) {
        if (a == null) {
            return false;
        }

        if (b == null) {
            return true;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return _decimal(a).compareTo(_decimal(b)) > 0;
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return _bigInt(a).compareTo(_bigInt(b)) > 0;
        }

        if (a instanceof Long || b instanceof Long) {
            return _long(a) > _long(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return _int(a) > _int(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return _short(a) > _short(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return _byte(a) > _byte(b);
        }

        if (a instanceof Date || b instanceof Date) {
            Date d1 = _date(a);
            Date d2 = _date(b);

            if (d1 == d2) {
                return false;
            }

            if (d1 == null) {
                return false;
            }

            if (d2 == null) {
                return true;
            }

            return d1.compareTo(d2) > 0;
        }

        throw new IllegalArgumentException();
    }

    public static boolean gteq(Object a, Object b) {
        if (eq(a, b)) {
            return true;
        }

        return gt(a, b);
    }

    public static boolean lt(Object a, Object b) {
        if (a == null) {
            return true;
        }

        if (b == null) {
            return false;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return _decimal(a).compareTo(_decimal(b)) < 0;
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return _bigInt(a).compareTo(_bigInt(b)) < 0;
        }

        if (a instanceof Long || b instanceof Long) {
            return _long(a) < _long(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return _int(a) < _int(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return _short(a) < _short(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return _byte(a) < _byte(b);
        }

        if (a instanceof Date || b instanceof Date) {
            Date d1 = _date(a);
            Date d2 = _date(b);

            if (d1 == d2) {
                return false;
            }

            if (d1 == null) {
                return true;
            }

            if (d2 == null) {
                return false;
            }

            return d1.compareTo(d2) < 0;
        }

        throw new IllegalArgumentException();
    }

    public static boolean lteq(Object a, Object b) {
        if (eq(a, b)) {
            return true;
        }

        return lt(a, b);
    }

    public static boolean eq(Object a, Object b) {
        if (a == b) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        if (a.equals(b)) {
            return true;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return _decimal(a).compareTo(_decimal(b)) == 0;
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return _bigInt(a).compareTo(_bigInt(b)) == 0;
        }

        if (a instanceof Long || b instanceof Long) {
            return _long(a) == _long(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return _int(a) == _int(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return _short(a) == _short(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return _byte(a) == _byte(b);
        }

        if (a instanceof Date || b instanceof Date) {
            Date d1 = _date(a);
            Date d2 = _date(b);

            if (d1 == d2) {
                return true;
            }

            if (d1 == null || d2 == null) {
                return false;
            }

            return d1.equals(d2);
        }

        throw new IllegalArgumentException();
    }

    public static Object add(Object a, Object b) {
        if (a == null) {
            return b;
        }

        if (b == null) {
            return a;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return _decimal(a).add(_decimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return _bigInt(a).add(_bigInt(b));
        }

        if (a instanceof Double || b instanceof Double) {
            return _double(a) + _double(b);
        }

        if (a instanceof Float || b instanceof Float) {
            return _float(a) + _float(b);
        }

        if (a instanceof Long || b instanceof Long) {
            return _long(a) + _long(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return _int(a) + _int(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return _short(a) + _short(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return _byte(a) + _byte(b);
        }

        if (a instanceof String || b instanceof String) {
            return _string(a) + _string(b);
        }

        throw new IllegalArgumentException();
    }

    public static Object sub(Object a, Object b) {
        if (a == null) {
            return null;
        }

        if (b == null) {
            return a;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return _decimal(a).subtract(_decimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return _bigInt(a).subtract(_bigInt(b));
        }

        if (a instanceof Double || b instanceof Double) {
            return _double(a) - _double(b);
        }

        if (a instanceof Float || b instanceof Float) {
            return _float(a) - _float(b);
        }

        if (a instanceof Long || b instanceof Long) {
            return _long(a) - _long(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return _int(a) - _int(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return _short(a) - _short(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return _byte(a) - _byte(b);
        }

        throw new IllegalArgumentException();
    }

    public static Object multi(Object a, Object b) {
        if (a == null || b == null) {
            return null;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return _decimal(a).multiply(_decimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return _bigInt(a).multiply(_bigInt(b));
        }

        if (a instanceof Long || b instanceof Long) {
            return _long(a) * _long(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return _int(a) * _int(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return _short(a) * _short(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return _byte(a) * _byte(b);
        }

        throw new IllegalArgumentException();
    }
}
