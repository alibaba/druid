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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlEvalVisitorImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleEvalVisitor;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGEvalVisitor;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.spi.WallVisitorUtils;
import com.alibaba.druid.wall.spi.WallVisitorUtils.WallConditionContext;

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
        if ("concat".equalsIgnoreCase(x.getMethodName())) {
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
        } else if ("now".equalsIgnoreCase(x.getMethodName())) {
            x.getAttributes().put(EVAL_VALUE, new Date());
        } else if ("ascii".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() == 0) {
                return false;
            }
            SQLExpr param = x.getParameters().get(0);
            param.accept(visitor);

            Object paramValue = param.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            String strValue = paramValue.toString();
            if (strValue.length() == 0) {
                return false;
            }

            int ascii = strValue.charAt(0);
            x.getAttributes().put(EVAL_VALUE, ascii);
        } else if ("instr".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 2) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            SQLExpr param1 = x.getParameters().get(1);
            param0.accept(visitor);
            param1.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            Object param1Value = param1.getAttributes().get(EVAL_VALUE);
            if (param0Value == null || param1Value == null) {
                return false;
            }

            String strValue0 = param0Value.toString();
            String strValue1 = param1Value.toString();

            int result = strValue0.indexOf(strValue1) + 1;

            x.putAttribute(EVAL_VALUE, result);
        } else if ("left".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 2) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            SQLExpr param1 = x.getParameters().get(1);
            param0.accept(visitor);
            param1.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            Object param1Value = param1.getAttributes().get(EVAL_VALUE);
            if (param0Value == null || param1Value == null) {
                return false;
            }

            String strValue = param0Value.toString();
            int intValue = castToInteger(param1Value);

            String result = strValue.substring(0, intValue);

            x.putAttribute(EVAL_VALUE, result);
        } else if ("right".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 2) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            SQLExpr param1 = x.getParameters().get(1);
            param0.accept(visitor);
            param1.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            Object param1Value = param1.getAttributes().get(EVAL_VALUE);
            if (param0Value == null || param1Value == null) {
                return false;
            }

            String strValue = param0Value.toString();
            int intValue = castToInteger(param1Value);

            String result = strValue.substring(strValue.length() - intValue, strValue.length());

            x.putAttribute(EVAL_VALUE, result);
        } else if ("reverse".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            if (param0Value == null) {
                return false;
            }

            String strValue = param0Value.toString();

            StringBuilder buf = new StringBuilder();
            for (int i = strValue.length() - 1; i >= 0; --i) {
                buf.append(strValue.charAt(i));
            }
            String result = buf.toString();

            x.putAttribute(EVAL_VALUE, result);
        } else if ("trim".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            if (param0Value == null) {
                return false;
            }

            String strValue = param0Value.toString();
            String result = strValue.trim();

            x.putAttribute(EVAL_VALUE, result);
        } else if ("length".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            if (param0Value == null) {
                return false;
            }

            String strValue = param0Value.toString();

            int result = strValue.length();

            x.putAttribute(EVAL_VALUE, result);
        } else if ("ucase".equalsIgnoreCase(x.getMethodName()) || "upper".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            if (param0Value == null) {
                return false;
            }

            String strValue = param0Value.toString();

            String result = strValue.toUpperCase();

            x.putAttribute(EVAL_VALUE, result);
        } else if ("lcase".equalsIgnoreCase(x.getMethodName()) || "lower".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            if (param0Value == null) {
                return false;
            }

            String strValue = param0Value.toString();

            String result = strValue.toLowerCase();

            x.putAttribute(EVAL_VALUE, result);

        } else if ("mod".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 2) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            SQLExpr param1 = x.getParameters().get(1);
            param0.accept(visitor);
            param1.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            Object param1Value = param1.getAttributes().get(EVAL_VALUE);
            if (param0Value == null || param1Value == null) {
                return false;
            }

            int intValue0 = castToInteger(param0Value);
            int intValue1 = castToInteger(param1Value);

            int result = intValue0 % intValue1;

            x.putAttribute(EVAL_VALUE, result);
        } else if ("abs".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            Object result;
            if (paramValue instanceof Integer) {
                result = Math.abs(((Integer) paramValue).intValue());
            } else if (paramValue instanceof Long) {
                result = Math.abs(((Long) paramValue).longValue());
            } else {
                result = castToDecimal(paramValue).abs();
            }

            x.putAttribute(EVAL_VALUE, result);
        } else if ("acos".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.acos(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("asin".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.asin(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("atan".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            double result = Math.atan(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("atan2".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 2) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            SQLExpr param1 = x.getParameters().get(1);
            param0.accept(visitor);
            param1.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            Object param1Value = param1.getAttributes().get(EVAL_VALUE);
            if (param0Value == null || param1Value == null) {
                return false;
            }

            double doubleValue0 = castToDouble(param0Value);
            double doubleValue1 = castToDouble(param1Value);
            double result = Math.atan2(doubleValue0, doubleValue1);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("ceil".equalsIgnoreCase(x.getMethodName()) || "ceiling".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            int result = (int) Math.ceil(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("cos".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            int result = (int) Math.cos(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("sin".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            int result = (int) Math.sin(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("log".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            int result = (int) Math.log(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("log10".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            int result = (int) Math.log10(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("tan".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            int result = (int) Math.tan(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("sqrt".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 1) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            param0.accept(visitor);

            Object paramValue = param0.getAttributes().get(EVAL_VALUE);
            if (paramValue == null) {
                return false;
            }

            double doubleValue = castToDouble(paramValue);
            int result = (int) Math.sqrt(doubleValue);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("power".equalsIgnoreCase(x.getMethodName())) {
            if (x.getParameters().size() != 2) {
                return false;
            }

            SQLExpr param0 = x.getParameters().get(0);
            SQLExpr param1 = x.getParameters().get(1);
            param0.accept(visitor);
            param1.accept(visitor);

            Object param0Value = param0.getAttributes().get(EVAL_VALUE);
            Object param1Value = param1.getAttributes().get(EVAL_VALUE);
            if (param0Value == null || param1Value == null) {
                return false;
            }

            double doubleValue0 = castToDouble(param0Value);
            double doubleValue1 = castToDouble(param1Value);
            double result = Math.pow(doubleValue0, doubleValue1);

            if (Double.isNaN(result)) {
                x.putAttribute(EVAL_VALUE, null);
            } else {
                x.putAttribute(EVAL_VALUE, result);
            }
        } else if ("pi".equalsIgnoreCase(x.getMethodName())) {
            x.putAttribute(EVAL_VALUE, Math.PI);
        } else if ("rand".equalsIgnoreCase(x.getMethodName())) {
            x.putAttribute(EVAL_VALUE, Math.random());
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
        Object value;
        if (x.getValueExpr() != null) {
            x.getValueExpr().accept(visitor);

            if (!x.getValueExpr().getAttributes().containsKey(EVAL_VALUE)) {
                return false;
            }

            value = x.getValueExpr().getAttribute(EVAL_VALUE);
        } else {
            value = null;
        }

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

    public static boolean visit(SQLEvalVisitor visitor, SQLQueryExpr x) {
        if (x.getSubQuery().getQuery() instanceof SQLSelectQueryBlock) {
            SQLSelectQueryBlock queryBlock = (SQLSelectQueryBlock) x.getSubQuery().getQuery();

            boolean nullFrom = false;
            if (queryBlock.getFrom() == null) {
                nullFrom = true;
            } else if (queryBlock.getFrom() instanceof SQLExprTableSource) {
                SQLExpr expr = ((SQLExprTableSource) queryBlock.getFrom()).getExpr();
                if (expr instanceof SQLIdentifierExpr) {
                    if ("dual".equalsIgnoreCase(((SQLIdentifierExpr) expr).getName())) {
                        nullFrom = true;
                    }
                }
            }

            if (nullFrom) {
                List<Object> row = new ArrayList<Object>(queryBlock.getSelectList().size());
                for (int i = 0; i < queryBlock.getSelectList().size(); ++i) {
                    SQLSelectItem item = queryBlock.getSelectList().get(i);
                    item.getExpr().accept(visitor);
                    Object cell = item.getExpr().getAttribute(EVAL_VALUE);
                    row.add(cell);
                }
                List<List<Object>> rows = new ArrayList<List<Object>>(1);
                rows.add(row);

                Object result = rows;
                queryBlock.putAttribute(EVAL_VALUE, result);
                x.getSubQuery().putAttribute(EVAL_VALUE, result);
                x.putAttribute(EVAL_VALUE, result);

                return false;
            }
        }

        return false;
    }

    public static boolean visit(SQLEvalVisitor visitor, SQLBinaryOpExpr x) {
        SQLExpr left = x.getLeft();
        SQLExpr right = x.getRight();

        // final WallConditionContext old = wallConditionContextLocal.get();

        left.accept(visitor);
        right.accept(visitor);

        if (x.getOperator() == SQLBinaryOperator.BooleanOr) {
            final WallConditionContext wallContext = WallVisitorUtils.getWallConditionContext();
            if (wallContext != null) {
                if (left.getAttribute(EVAL_VALUE) == Boolean.TRUE || right.getAttribute(EVAL_VALUE) == Boolean.TRUE) {
                    wallContext.setPartAllowTrue(true);
                }
            }
        }

        if (!left.getAttributes().containsKey(EVAL_VALUE)) {
            return false;
        }

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
            case Equality:
                value = eq(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            case IsNot:
                value = !eq(left.getAttribute(EVAL_VALUE), right.getAttributes().get(EVAL_VALUE));
                x.putAttribute(EVAL_VALUE, value);
                break;
            case RegExp:
            case RLike: {
                String pattern = castToString(right.getAttributes().get(EVAL_VALUE));
                String input = castToString(left.getAttributes().get(EVAL_VALUE));
                boolean matchResult = Pattern.matches(pattern, input);
                x.putAttribute(EVAL_VALUE, matchResult);
            }
                break;
            case NotRegExp:
            case NotRLike: {
                String pattern = castToString(right.getAttributes().get(EVAL_VALUE));
                String input = castToString(left.getAttributes().get(EVAL_VALUE));
                boolean matchResult = !Pattern.matches(pattern, input);
                x.putAttribute(EVAL_VALUE, matchResult);
            }
                break;
            case Like: {
                String pattern = castToString(right.getAttributes().get(EVAL_VALUE));
                String input = castToString(left.getAttributes().get(EVAL_VALUE));
                boolean matchResult = like(input, pattern);
                x.putAttribute(EVAL_VALUE, matchResult);
            }
                break;
            case NotLike: {
                String pattern = castToString(right.getAttributes().get(EVAL_VALUE));
                String input = castToString(left.getAttributes().get(EVAL_VALUE));
                boolean matchResult = !like(input, pattern);
                x.putAttribute(EVAL_VALUE, matchResult);
            }
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

    public static Boolean castToBoolean(Object val) {
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

    public static String castToString(Object val) {
        Object value = val;

        if (value == null) {
            return null;
        }

        return value.toString();
    }

    public static Byte castToByte(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Byte) {
            return (Byte) val;
        }

        return ((Number) val).byteValue();
    }

    public static Short castToShort(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Short) {
            return (Short) val;
        }

        return ((Number) val).shortValue();
    }

    public static Integer castToInteger(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Integer) {
            return (Integer) val;
        }

        return ((Number) val).intValue();
    }

    public static Long castToLong(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Long) {
            return (Long) val;
        }

        return ((Number) val).longValue();
    }

    public static Float castToFloat(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Float) {
            return (Float) val;
        }

        return ((Number) val).floatValue();
    }

    public static Double castToDouble(Object val) {
        if (val == null) {
            return null;
        }

        if (val instanceof Double) {
            return (Double) val;
        }

        return ((Number) val).doubleValue();
    }

    public static BigInteger castToBigInteger(Object val) {
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

    public static Date castToDate(Object val) {
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
            return castToDate((String) val);
        }

        throw new DruidRuntimeException("can cast to date");
    }

    public static Date castToDate(String text) {
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

    public static BigDecimal castToDecimal(Object val) {
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

    public static Object sum(Object a, Object b) {
        if (a == null) {
            return b;
        }

        if (b == null) {
            return a;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return castToDecimal(a).add(castToDecimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return castToBigInteger(a).add(castToBigInteger(b));
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) + castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) + castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) + castToShort(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) + castToByte(b);
        }

        throw new IllegalArgumentException();
    }

    public static Object div(Object a, Object b) {
        if (a == null || b == null) {
            return null;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return castToDecimal(a).divide(castToDecimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return castToBigInteger(a).divide(castToBigInteger(b));
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) / castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) / castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) / castToShort(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) / castToByte(b);
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
            return castToDecimal(a).compareTo(castToDecimal(b)) > 0;
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return castToBigInteger(a).compareTo(castToBigInteger(b)) > 0;
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) > castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) > castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) > castToShort(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) > castToByte(b);
        }

        if (a instanceof Date || b instanceof Date) {
            Date d1 = castToDate(a);
            Date d2 = castToDate(b);

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
            return castToDecimal(a).compareTo(castToDecimal(b)) < 0;
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return castToBigInteger(a).compareTo(castToBigInteger(b)) < 0;
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) < castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) < castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) < castToShort(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) < castToByte(b);
        }

        if (a instanceof Date || b instanceof Date) {
            Date d1 = castToDate(a);
            Date d2 = castToDate(b);

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
            return castToDecimal(a).compareTo(castToDecimal(b)) == 0;
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return castToBigInteger(a).compareTo(castToBigInteger(b)) == 0;
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) == castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) == castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) == castToShort(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) == castToByte(b);
        }

        if (a instanceof Date || b instanceof Date) {
            Date d1 = castToDate(a);
            Date d2 = castToDate(b);

            if (d1 == d2) {
                return true;
            }

            if (d1 == null || d2 == null) {
                return false;
            }

            return d1.equals(d2);
        }

        if (a instanceof String || b instanceof String) {
            return castToString(a).equals(castToString(b));
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
            return castToDecimal(a).add(castToDecimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return castToBigInteger(a).add(castToBigInteger(b));
        }

        if (a instanceof Double || b instanceof Double) {
            return castToDouble(a) + castToDouble(b);
        }

        if (a instanceof Float || b instanceof Float) {
            return castToFloat(a) + castToFloat(b);
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) + castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) + castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) + castToShort(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) + castToByte(b);
        }

        if (a instanceof String || b instanceof String) {
            return castToString(a) + castToString(b);
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
            return castToDecimal(a).subtract(castToDecimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return castToBigInteger(a).subtract(castToBigInteger(b));
        }

        if (a instanceof Double || b instanceof Double) {
            return castToDouble(a) - castToDouble(b);
        }

        if (a instanceof Float || b instanceof Float) {
            return castToFloat(a) - castToFloat(b);
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) - castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) - castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) - castToShort(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) - castToByte(b);
        }

        throw new IllegalArgumentException();
    }

    public static Object multi(Object a, Object b) {
        if (a == null || b == null) {
            return null;
        }

        if (a instanceof BigDecimal || b instanceof BigDecimal) {
            return castToDecimal(a).multiply(castToDecimal(b));
        }

        if (a instanceof BigInteger || b instanceof BigInteger) {
            return castToBigInteger(a).multiply(castToBigInteger(b));
        }

        if (a instanceof Long || b instanceof Long) {
            return castToLong(a) * castToLong(b);
        }

        if (a instanceof Integer || b instanceof Integer) {
            return castToInteger(a) * castToInteger(b);
        }

        if (a instanceof Short || b instanceof Short) {
            return castToShort(a) * castToShort(b);
        }

        if (a instanceof Byte || b instanceof Byte) {
            return castToByte(a) * castToByte(b);
        }

        throw new IllegalArgumentException();
    }

    public static boolean like(String input, String pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern is null");
        }

        StringBuilder regexprBuilder = new StringBuilder(pattern.length() + 4);

        final int STAT_NOTSET = 0;
        final int STAT_RANGE = 1;
        final int STAT_LITERAL = 2;

        int stat = STAT_NOTSET;

        int blockStart = -1;
        for (int i = 0; i < pattern.length(); ++i) {
            char ch = pattern.charAt(i);

            if (stat == STAT_LITERAL //
                && (ch == '%' || ch == '_' || ch == '[')) {
                String block = pattern.substring(blockStart, i);
                regexprBuilder.append("\\Q");
                regexprBuilder.append(block);
                regexprBuilder.append("\\E");
                blockStart = -1;
                stat = STAT_NOTSET;
            }

            if (ch == '%') {
                regexprBuilder.append(".*");
            } else if (ch == '_') {
                regexprBuilder.append('.');
            } else if (ch == '[') {
                if (stat == STAT_RANGE) {
                    throw new IllegalArgumentException("illegal pattern : " + pattern);
                }
                stat = STAT_RANGE;
                blockStart = i;
            } else if (ch == ']') {
                if (stat != STAT_RANGE) {
                    throw new IllegalArgumentException("illegal pattern : " + pattern);
                }
                String block = pattern.substring(blockStart, i + 1);
                regexprBuilder.append(block);

                blockStart = -1;
            } else {
                if (stat == STAT_NOTSET) {
                    stat = STAT_LITERAL;
                    blockStart = i;
                }

                if (stat == STAT_LITERAL && i == pattern.length() - 1) {
                    String block = pattern.substring(blockStart, i + 1);
                    regexprBuilder.append("\\Q");
                    regexprBuilder.append(block);
                    regexprBuilder.append("\\E");
                }
            }
        }
        if ("%".equals(pattern) || "%%".equals(pattern)) {
            return true;
        }

        String regexpr = regexprBuilder.toString();
        return Pattern.matches(regexpr, input);
    }

}
