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
package com.alibaba.druid.sql.dialect.oracle.parser;

import java.math.BigInteger;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleOrderBy;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAggregateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalytic;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleAnalyticWindowing;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleBinaryDoubleExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleBinaryFloatExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleCursorExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDateExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDateTimeUnit;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDbLinkExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleExtractExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalType;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIsSetExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleOuterExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleTimestampExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelect;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleExprParser extends SQLExprParser {



    public boolean                allowStringAdditive = false;

    /**
     * @formatter:off
     */
    private static final String[] _aggregateFunctions = { "AVG", "CORR", "COVAR_POP", "COVAR_SAMP", "COUNT", "CUME_DIST", "DENSE_RANK", "FIRST", "FIRST_VALUE", "LAG", "LAST", "LAST_VALUE", "LEAD", "MAX", "MIN", "NTILE", "PERCENT_RANK", "PERCENTILE_CONT", "PERCENTILE_DISC", "RANK", "RATIO_TO_REPORT", "REGR_SLOPE", "REGR_INTERCEPT", "REGR_COUNT", "REGR_R2", "REGR_AVGX",
            "REGR_AVGY", "REGR_SXX", "REGR_SYY", "REGR_SXY", "ROW_NUMBER", "STDDEV", "STDDEV_POP", "STDDEV_SAMP", "SUM", "VAR_POP", "VAR_SAMP", "VARIANCE" };

    public OracleExprParser(Lexer lexer){
        super(lexer);
    }

    public OracleExprParser(String text){
        super(new OracleLexer(text));
        this.lexer.nextToken();
    }

    public boolean isAggreateFunction(String word) {
        for (int i = 0; i < _aggregateFunctions.length; ++i) {
            if (_aggregateFunctions[i].compareToIgnoreCase(word) == 0) {
                return true;
            }
        }

        return false;
    }

    public SQLExpr primary() throws ParserException {
        final Token tok = lexer.token();

        SQLExpr sqlExpr = null;
        switch (tok) {
            case COLON:
                lexer.nextToken();
                if (lexer.token() == Token.LITERAL_INT) {
                    String name = ":" + lexer.numberString();
                    lexer.nextToken();
                    return new SQLVariantRefExpr(name);
                } else if (lexer.token() == Token.IDENTIFIER) {
                    String name = lexer.stringVal();
                    if (name.startsWith("B")) {
                        lexer.nextToken();
                        return new SQLVariantRefExpr(":" + name);
                    }
                    throw new ParserException("syntax error : " + lexer.token());
                } else {
                    throw new ParserException("syntax error : " + lexer.token());
                }
            case LITERAL_ALIAS:
                String alias = '"' + lexer.stringVal() + '"';
                lexer.nextToken();
                return primaryRest(new SQLIdentifierExpr(alias));
            case EXTRACT:
                lexer.nextToken();
                OracleExtractExpr extract = new OracleExtractExpr();

                accept(Token.LPAREN);

                extract.setUnit(OracleDateTimeUnit.valueOf(lexer.stringVal()));
                lexer.nextToken();

                accept(Token.FROM);

                extract.setFrom(expr());

                accept(Token.RPAREN);

                return primaryRest(extract);
            case TIMESTAMP:
                return primaryRest(parseTimestamp());
            case DATE:
                return primaryRest(parseDate());
            case BINARY_FLOAT:
                OracleBinaryFloatExpr floatExpr = new OracleBinaryFloatExpr();
                floatExpr.setValue(Float.parseFloat(lexer.numberString()));
                lexer.nextToken();
                return primaryRest(floatExpr);
            case BINARY_DOUBLE:
                OracleBinaryDoubleExpr doubleExpr = new OracleBinaryDoubleExpr();
                doubleExpr.setValue(Double.parseDouble(lexer.numberString()));

                lexer.nextToken();
                return primaryRest(doubleExpr);
            case TABLE:
                lexer.nextToken();
                return primaryRest(new SQLIdentifierExpr("TABLE"));
            case PLUS:
                lexer.nextToken();
                switch (lexer.token()) {
                    case LITERAL_INT:
                        sqlExpr = new SQLIntegerExpr(lexer.integerValue());
                        lexer.nextToken();
                        break;
                    case LITERAL_FLOAT:
                        sqlExpr = new SQLNumberExpr(lexer.decimalValue());
                        lexer.nextToken();
                        break;
                    case BINARY_FLOAT:
                        sqlExpr = new OracleBinaryFloatExpr(Float.parseFloat(lexer.numberString()));
                        lexer.nextToken();
                        break;
                    case BINARY_DOUBLE:
                        sqlExpr = new OracleBinaryDoubleExpr(Double.parseDouble(lexer.numberString()));
                        lexer.nextToken();
                        break;
                    default:
                        throw new ParserException("TODO");
                }
                return primaryRest(sqlExpr);
            case SUB:
                lexer.nextToken();
                switch (lexer.token()) {
                    case LITERAL_INT:
                        Number integerValue = lexer.integerValue();
                        if (integerValue instanceof Integer) {
                            int intVal = ((Integer) integerValue).intValue();
                            if (intVal == Integer.MIN_VALUE) {
                                integerValue = Long.valueOf(((long) intVal) * -1);
                            } else {
                                integerValue = Integer.valueOf(intVal * -1);
                            }
                        } else if (integerValue instanceof Long) {
                            long longVal = ((Long) integerValue).longValue();
                            if (longVal == 2147483648L) {
                                integerValue = Integer.valueOf((int) (((long) longVal) * -1));
                            } else {
                                integerValue = Long.valueOf(longVal * -1);
                            }
                        } else {
                            integerValue = ((BigInteger) integerValue).negate();
                        }
                        sqlExpr = new SQLIntegerExpr(integerValue);
                        lexer.nextToken();
                        break;
                    case LITERAL_FLOAT:
                        sqlExpr = new SQLNumberExpr(lexer.decimalValue().negate());
                        lexer.nextToken();
                        break;
                    case BINARY_FLOAT:
                        sqlExpr = new OracleBinaryFloatExpr(Float.parseFloat(lexer.numberString()) * -1);
                        lexer.nextToken();
                        break;
                    case BINARY_DOUBLE:
                        sqlExpr = new OracleBinaryDoubleExpr(Double.parseDouble(lexer.numberString()) * -1);
                        lexer.nextToken();
                        break;
                    default:
                        throw new ParserException("TODO");
                }
                return primaryRest(sqlExpr);
                
           case CURSOR:
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    
                    OracleSelect select = createSelectParser().select();
                    OracleCursorExpr cursorExpr = new OracleCursorExpr(select);
                    
                    accept(Token.RPAREN);
                    
                    sqlExpr = cursorExpr;
                    return  primaryRest(sqlExpr);
            default:
                return super.primary();
        }
    }

    public SQLExpr primaryRest(SQLExpr expr) throws ParserException {
        if (lexer.token() == Token.MONKEYS_AT) {
            lexer.nextToken();

            OracleDbLinkExpr dblink = new OracleDbLinkExpr();
            dblink.setExpr(expr);

            String link = lexer.stringVal();
            accept(Token.IDENTIFIER);
            dblink.setDbLink(link);

            expr = dblink;
        }
        
        if (lexer.token() == Token.DAY || lexer.token() == Token.YEAR) {
            lexer.mark();
            
            String name = lexer.token().name;
            lexer.nextToken();
            
            if (lexer.token() == Token.COMMA) {
                lexer.reset();
                return expr;
            }
            
            OracleIntervalExpr interval = new OracleIntervalExpr();
            interval.setValue(expr);
            OracleIntervalType type = OracleIntervalType.valueOf(name);
            interval.setType(type);
            
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                if (lexer.token() != Token.LITERAL_INT) {
                    throw new ParserException("syntax error");
                }
                interval.setPrecision(lexer.integerValue().intValue());
                lexer.nextToken();
                accept(Token.RPAREN);
            }
            
            accept(Token.TO);
            if (lexer.token() == Token.SECOND) {
                lexer.nextToken();
                interval.setToType(OracleIntervalType.SECOND);
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    if (lexer.token() != Token.LITERAL_INT) {
                        throw new ParserException("syntax error");
                    }
                    interval.setFactionalSecondsPrecision(lexer.integerValue().intValue());
                    lexer.nextToken();
                    accept(Token.RPAREN);
                }
            } else {
                interval.setToType(OracleIntervalType.MONTH);
                lexer.nextToken();
            }
            
            expr = interval;
        }
        
        SQLExpr restExpr = super.primaryRest(expr);
        
        if (restExpr != expr && restExpr instanceof SQLMethodInvokeExpr) {
            SQLMethodInvokeExpr methodInvoke = (SQLMethodInvokeExpr) restExpr;
            if (methodInvoke.getParameters().size() == 1) {
                SQLExpr paramExpr = methodInvoke.getParameters().get(0);
                if (paramExpr instanceof SQLIdentifierExpr && "+".equals(((SQLIdentifierExpr) paramExpr).getName())) {
                    OracleOuterExpr outerExpr = new OracleOuterExpr();
                    if (methodInvoke.getOwner() == null) {
                        outerExpr.setExpr(new SQLIdentifierExpr(methodInvoke.getMethodName()));
                    } else {
                        outerExpr.setExpr(new SQLPropertyExpr(methodInvoke.getOwner(), methodInvoke.getMethodName()));
                    }
                    return outerExpr;
                }
            }
        }
        
        return restExpr;
    }

    protected SQLExpr dotRest(SQLExpr expr) {
        if (lexer.token() == Token.LITERAL_ALIAS) {
            String name = '"' + lexer.stringVal() + '"';
            lexer.nextToken();
            expr = new SQLPropertyExpr(expr, name);
            expr = primaryRest(expr);

            return expr;
        }

        return super.dotRest(expr);
    }

    public OracleDateExpr parseDate() {
        accept(Token.DATE);

        OracleDateExpr timestamp = new OracleDateExpr();

        String literal = lexer.stringVal();
        timestamp.setLiteral(literal);
        accept(Token.LITERAL_CHARS);

        return timestamp;
    }

    public OracleTimestampExpr parseTimestamp() {
        accept(Token.TIMESTAMP);

        OracleTimestampExpr timestamp = new OracleTimestampExpr();

        String literal = lexer.stringVal();
        timestamp.setLiteral(literal);
        accept(Token.LITERAL_CHARS);

        if (identifierEquals("AT")) {
            lexer.nextToken();
            acceptIdentifier("TIME");
            acceptIdentifier("ZONE");

            String timezone = lexer.stringVal();
            timestamp.setTimeZone(timezone);
            accept(Token.LITERAL_CHARS);
        }

        return timestamp;
    }

    @Override
    public OracleOrderBy parseOrderBy() {
        if (lexer.token() == (Token.ORDER)) {
            OracleOrderBy orderBy = new OracleOrderBy();

            lexer.nextToken();

            if (identifierEquals("SIBLINGS")) {
                lexer.nextToken();
                orderBy.setSibings(true);
            }

            accept(Token.BY);

            orderBy.getItems().add(parseSelectOrderByItem());

            while (lexer.token() == (Token.COMMA)) {
                lexer.nextToken();
                orderBy.getItems().add(parseSelectOrderByItem());
            }

            return orderBy;
        }

        return null;
    }

    protected OracleAggregateExpr parseAggregateExpr(String method_name) {
        OracleAggregateExpr aggregateExpr;
        if (lexer.token() == (Token.ALL)) {
            aggregateExpr = new OracleAggregateExpr(method_name, 1);
            lexer.nextToken();
        } else if (lexer.token() == (Token.DISTINCT)) {
            aggregateExpr = new OracleAggregateExpr(method_name, 0);
            lexer.nextToken();
        } else {
            aggregateExpr = new OracleAggregateExpr(method_name, 1);
        }
        exprList(aggregateExpr.getArguments());

        if (lexer.stringVal().equalsIgnoreCase("IGNORE")) {
            lexer.nextToken();
            identifierEquals("NULLS");
            aggregateExpr.setIgnoreNulls(true);
        }

        accept(Token.RPAREN);

        if (identifierEquals("OVER")) {
            OracleAnalytic over = new OracleAnalytic();

            lexer.nextToken();
            accept(Token.LPAREN);

            if (identifierEquals("PARTITION")) {
                lexer.nextToken();
                accept(Token.BY);

                if (lexer.token() == (Token.LPAREN)) {
                    lexer.nextToken();
                    exprList(over.getPartitionBy());
                    accept(Token.RPAREN);
                } else {
                    exprList(over.getPartitionBy());
                }
            }

            over.setOrderBy(parseOrderBy());
            if (over.getOrderBy() != null) {
                OracleAnalyticWindowing windowing = null;
                if (lexer.stringVal().equalsIgnoreCase("ROWS")) {
                    lexer.nextToken();
                    windowing = new OracleAnalyticWindowing();
                    windowing.setType(OracleAnalyticWindowing.Type.ROWS);
                } else if (lexer.stringVal().equalsIgnoreCase("RANGE")) {
                    lexer.nextToken();
                    windowing = new OracleAnalyticWindowing();
                    windowing.setType(OracleAnalyticWindowing.Type.RANGE);
                }

                if (windowing != null) {
                    if (lexer.stringVal().equalsIgnoreCase("CURRENT")) {
                        lexer.nextToken();
                        if (lexer.stringVal().equalsIgnoreCase("ROW")) {
                            lexer.nextToken();
                            windowing.setExpr(new SQLIdentifierExpr("CURRENT ROW"));
                            over.setWindowing(windowing);
                        }
                        throw new ParserException("syntax error");
                    }
                    if (lexer.stringVal().equalsIgnoreCase("UNBOUNDED")) {
                        lexer.nextToken();
                        if (lexer.stringVal().equalsIgnoreCase("PRECEDING")) {
                            lexer.nextToken();
                            windowing.setExpr(new SQLIdentifierExpr("UNBOUNDED PRECEDING"));
                        } else {
                            throw new ParserException("syntax error");
                        }
                    }

                    over.setWindowing(windowing);
                }
            }

            accept(Token.RPAREN);

            aggregateExpr.setOver(over);
        }
        return aggregateExpr;
    }

    @SuppressWarnings("unused")
    private OracleIntervalType parseIntervalType() {
        String currentTokenUpperValue = lexer.stringVal();
        lexer.nextToken();

        if (currentTokenUpperValue.equals("YEAR")) return OracleIntervalType.YEAR;
        if (currentTokenUpperValue.equals("MONTH")) return OracleIntervalType.MONTH;
        if (currentTokenUpperValue.equals("HOUR")) return OracleIntervalType.HOUR;
        if (currentTokenUpperValue.equals("MINUTE")) return OracleIntervalType.MINUTE;
        if (currentTokenUpperValue.equals("SECOND")) {
            return OracleIntervalType.SECOND;
        }
        throw new ParserException("syntax error");
    }

    @Override
    public OracleSelectParser createSelectParser() {
        return new OracleSelectParser(this.lexer);
    }

    @Override
    public OracleOrderByItem parseSelectOrderByItem() {
        OracleOrderByItem item = new OracleOrderByItem();

        item.setExpr(expr());

        if (lexer.token() == (Token.ASC)) {
            lexer.nextToken();
            item.setType(SQLOrderingSpecification.ASC);
        } else if (lexer.token() == (Token.DESC)) {
            lexer.nextToken();
            item.setType(SQLOrderingSpecification.DESC);
        }

        if (identifierEquals("NULLS")) {
            lexer.nextToken();
            if (identifierEquals("FIRST")) {
                lexer.nextToken();
                item.setNullsOrderType(OracleOrderByItem.NullsOrderType.NullsFirst);
            } else if (identifierEquals("LAST")) {
                lexer.nextToken();
                item.setNullsOrderType(OracleOrderByItem.NullsOrderType.NullsLast);
            } else {
                throw new ParserException("TODO " + lexer.token());
            }
        }

        return item;
    }

    protected SQLExpr parseInterval() {
        accept(Token.INTERVAL);

        OracleIntervalExpr interval = new OracleIntervalExpr();
        if (lexer.token() != Token.LITERAL_CHARS) {
            throw new ParserException("syntax error : " + lexer.token());
        }
        interval.setValue(new SQLCharExpr(lexer.stringVal()));
        lexer.nextToken();

        
        OracleIntervalType type = OracleIntervalType.valueOf(lexer.token().name);
        interval.setType(type);
        lexer.nextToken();
        
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            if (lexer.token() != Token.LITERAL_INT) {
                throw new ParserException("syntax error");
            }
            interval.setPrecision(lexer.integerValue().intValue());
            lexer.nextToken();
            
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                if (lexer.token() != Token.LITERAL_INT) {
                    throw new ParserException("syntax error");
                }
                interval.setFactionalSecondsPrecision(lexer.integerValue().intValue());
                lexer.nextToken();
            }
            accept(Token.RPAREN);
        }
        
        if (lexer.token() == Token.TO) {
            accept(Token.TO);
            if (lexer.token() == Token.SECOND) {
                lexer.nextToken();
                interval.setToType(OracleIntervalType.SECOND);
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    if (lexer.token() != Token.LITERAL_INT) {
                        throw new ParserException("syntax error");
                    }
                    interval.setToFactionalSecondsPrecision(lexer.integerValue().intValue());
                    lexer.nextToken();
                    accept(Token.RPAREN);
                }
            } else {
                interval.setToType(OracleIntervalType.MONTH);
                lexer.nextToken();
            }
        }
        
        return interval;    
    }
    
    public SQLExpr relationalRest(SQLExpr expr) throws ParserException {
        if (lexer.token() == Token.IS) {
            lexer.nextToken();
            
            if (lexer.token() == Token.NOT) {
                lexer.nextToken();
                SQLExpr rightExpr = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.IsNot, rightExpr);
            } else if (identifierEquals("A")) {
                lexer.nextToken();
                accept(Token.SET);
                expr = new OracleIsSetExpr(expr);
            } else {
                SQLExpr rightExpr = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Is, rightExpr);
            }
            
            return expr;
        }
        return super.relationalRest(expr);
    }
}
