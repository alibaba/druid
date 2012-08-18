package com.alibaba.druid.sql.visitor;

import static com.alibaba.druid.sql.visitor.SQLEvalVisitor.EVAL_VALUE;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
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
        
        if (JdbcUtils.ORACLE.equals(dbType)) {
            return new OracleEvalVisitor();
        }
        
        if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            return new PGEvalVisitor();
        }

        return new SQLEvalVisitorImpl();
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLCharExpr x) {
        x.putAttribute(EVAL_VALUE, x.getText());
        return true;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLBinaryOpExpr x) {
        x.getLeft().accept(visitor);
        x.getRight().accept(visitor);

        if (!x.getLeft().getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }

        if (!x.getRight().getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }

        if (SQLBinaryOperator.Add.equals(x.getOperator())) {
            Object value = _add(x.getLeft().getAttribute(EVAL_VALUE), x.getRight().getAttributes().get(EVAL_VALUE));
            x.putAttribute(EVAL_VALUE, value);
        }

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
            throw new DruidRuntimeException("format : " + format + ", value : " + text);
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

    public static Object _div(Object a, Object b) {
        if (a == null || b == null) {
            return null;
        }

        BigDecimal decimalA = _decimal(a);
        BigDecimal decimalB = _decimal(b);

        try {
            return decimalA.divide(decimalB);
        } catch (ArithmeticException ex) {
            return decimalA.divide(decimalB, 4, RoundingMode.CEILING);
        }
    }

    public static Object _div2(Object a, Object b) {
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

    public static boolean _gt(Object a, Object b) {
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

    public static boolean _gteq(Object a, Object b) {
        if (_eq(a, b)) {
            return true;
        }

        return _gt(a, b);
    }

    public static boolean _lt(Object a, Object b) {
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

    public static boolean _lteq(Object a, Object b) {
        if (_eq(a, b)) {
            return true;
        }

        return _lt(a, b);
    }

    public static boolean _eq(Object a, Object b) {
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

    public static Object _add(Object a, Object b) {
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

        if (a instanceof String || b instanceof String) {
            return _string(a) + _string(b);
        }

        throw new IllegalArgumentException();
    }

    public static Object _sub(Object a, Object b) {
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

    public static Object _multi(Object a, Object b) {
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
