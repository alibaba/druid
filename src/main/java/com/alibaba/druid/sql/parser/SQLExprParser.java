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
package com.alibaba.druid.sql.parser;

import java.math.BigInteger;
import java.util.Collection;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllColumnExpr;
import com.alibaba.druid.sql.ast.expr.SQLAllExpr;
import com.alibaba.druid.sql.ast.expr.SQLAnyExpr;
import com.alibaba.druid.sql.ast.expr.SQLBetweenExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCaseExpr;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLExistsExpr;
import com.alibaba.druid.sql.ast.expr.SQLHexExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLInListExpr;
import com.alibaba.druid.sql.ast.expr.SQLInSubQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNotExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLSomeExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLCharactorDataType;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;

public class SQLExprParser extends SQLParser {

    public SQLExprParser(String sql) throws ParserException{
        super(sql);
    }

    public SQLExprParser(Lexer lexer){
        super(lexer);
    }

    public SQLExpr expr() throws ParserException {
        if (lexer.token() == Token.STAR) {
            lexer.nextToken();

            return new SQLAllColumnExpr();
        }

        SQLExpr expr = primary();

        if (lexer.token() == Token.COMMA) {
            return expr;
        }

        return exprRest(expr);
    }

    public SQLExpr exprRest(SQLExpr expr) throws ParserException {
        expr = bitXorRest(expr);
        expr = multiplicativeRest(expr);
        expr = additiveRest(expr);
        expr = shiftRest(expr);
        expr = bitAndRest(expr);
        expr = bitOrRest(expr);
        expr = inRest(expr);
        expr = relationalRest(expr);
        expr = equalityRest(expr);
        expr = andRest(expr);
        expr = orRest(expr);

        return expr;
    }

    public final SQLExpr bitXor() throws ParserException {
        SQLExpr expr = primary();
        return bitXorRest(expr);
    }

    public SQLExpr bitXorRest(SQLExpr expr) throws ParserException {
        if (lexer.token() == Token.CARET) {
            lexer.nextToken();
            SQLExpr rightExp = primary();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BitwiseXor, rightExp);
            expr = bitXorRest(expr);
        }

        return expr;
    }

    public final SQLExpr multiplicative() throws ParserException {
        SQLExpr expr = bitXor();
        return multiplicativeRest(expr);
    }

    public SQLExpr multiplicativeRest(SQLExpr expr) throws ParserException {
        if (lexer.token() == Token.STAR) {
            lexer.nextToken();
            SQLExpr rightExp = bitXor();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Multiply, rightExp);
            expr = multiplicativeRest(expr);
        } else if (lexer.token() == Token.SLASH) {
            lexer.nextToken();
            SQLExpr rightExp = bitXor();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Divide, rightExp);
            expr = multiplicativeRest(expr);
        } else if (lexer.token() == Token.PERCENT) {
            lexer.nextToken();
            SQLExpr rightExp = bitXor();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Modulus, rightExp);
            expr = multiplicativeRest(expr);
        }
        return expr;
    }

    public SQLExpr primary() throws ParserException {
        SQLExpr sqlExpr = null;

        final Token tok = lexer.token();

        switch (tok) {
            case LPAREN:
                lexer.nextToken();
                sqlExpr = expr();
                if (lexer.token() == Token.COMMA) {
                    SQLListExpr listExpr = new SQLListExpr();
                    listExpr.getItems().add(sqlExpr);
                    do {
                        lexer.nextToken();
                        listExpr.getItems().add(expr());
                    } while (lexer.token() == Token.COMMA);

                    sqlExpr = listExpr;
                }
                accept(Token.RPAREN);
                break;
            case INSERT:
                lexer.nextToken();
                if (lexer.token() != Token.LPAREN) {
                    throw new ParserException("syntax error");
                }
                sqlExpr = new SQLIdentifierExpr("INSERT");
                break;
            case IDENTIFIER:
                sqlExpr = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
                break;
            case NEW:
                throw new ParserException("TODO");
            case LITERAL_INT:
                sqlExpr = new SQLIntegerExpr(lexer.integerValue());
                lexer.nextToken();
                break;
            case LITERAL_FLOAT:
                sqlExpr = new SQLNumberExpr(lexer.decimalValue());
                lexer.nextToken();
                break;
            case LITERAL_CHARS:
                sqlExpr = new SQLCharExpr(lexer.stringVal());
                lexer.nextToken();
                break;
            case LITERAL_NCHARS:
                sqlExpr = new SQLNCharExpr(lexer.stringVal());
                lexer.nextToken();
                break;
            case VARIANT:
                sqlExpr = new SQLVariantRefExpr(lexer.stringVal());
                lexer.nextToken();
                break;
            case CASE:
                SQLCaseExpr caseExpr = new SQLCaseExpr();
                lexer.nextToken();
                if (lexer.token() != Token.WHEN) {
                    caseExpr.setValueExpr(expr());
                }

                accept(Token.WHEN);
                SQLExpr testExpr = expr();
                accept(Token.THEN);
                SQLExpr valueExpr = expr();
                SQLCaseExpr.Item caseItem = new SQLCaseExpr.Item(testExpr, valueExpr);
                caseExpr.getItems().add(caseItem);

                while (lexer.token() == Token.WHEN) {
                    lexer.nextToken();
                    testExpr = expr();
                    accept(Token.THEN);
                    valueExpr = expr();
                    caseItem = new SQLCaseExpr.Item(testExpr, valueExpr);
                    caseExpr.getItems().add(caseItem);
                }

                if (lexer.token() == Token.ELSE) {
                    lexer.nextToken();
                    caseExpr.setElseExpr(expr());
                }

                accept(Token.END);

                sqlExpr = caseExpr;
                break;
            case EXISTS:
                lexer.nextToken();
                accept(Token.LPAREN);
                sqlExpr = new SQLExistsExpr(createSelectParser().select());
                accept(Token.RPAREN);
                break;
            case NOT:
                lexer.nextToken();
                if (lexer.token() == Token.EXISTS) {
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    sqlExpr = new SQLExistsExpr(createSelectParser().select(), true);
                    accept(Token.RPAREN);
                } else if (lexer.token() == Token.LPAREN) {
                    lexer.token();

                    sqlExpr = new SQLNotExpr(expr());

                    accept(Token.RPAREN);
                    return primaryRest(sqlExpr);
                } else {
                    SQLExpr restExpr = primary();
                    sqlExpr = new SQLNotExpr(restExpr);
                }
                break;
            case SELECT:
                SQLQueryExpr queryExpr = new SQLQueryExpr(createSelectParser().select());
                sqlExpr = queryExpr;
                break;
            case CAST:
                lexer.nextToken();
                accept(Token.LPAREN);
                SQLCastExpr cast = new SQLCastExpr();
                cast.setExpr(expr());
                accept(Token.AS);
                cast.setDataType(parseDataType());
                accept(Token.RPAREN);

                sqlExpr = cast;
                break;
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
                    default:
                        throw new ParserException("TODO");
                }
                break;
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
                    default:
                        throw new ParserException("TODO");
                }
                break;
            case TILDE:
                lexer.nextToken();
                SQLExpr unaryValueExpr = expr();
                SQLUnaryExpr unary = new SQLUnaryExpr(SQLUnaryOperator.Compl, unaryValueExpr);
                sqlExpr = unary;
                break;
            case QUES:
                lexer.nextToken();
                sqlExpr = new SQLVariantRefExpr("?");
                break;
            case LEFT:
                sqlExpr = new SQLIdentifierExpr("LEFT");
                lexer.nextToken();
                break;
            case RIGHT:
                sqlExpr = new SQLIdentifierExpr("RIGHT");
                lexer.nextToken();
                break;
            case LOCK:
                sqlExpr = new SQLIdentifierExpr("LOCK");
                lexer.nextToken();
                break;
            case NULL:
                sqlExpr = new SQLNullExpr();
                lexer.nextToken();
                break;
            case BANG:
                lexer.nextToken();
                SQLExpr bangExpr = expr();
                sqlExpr = new SQLUnaryExpr(SQLUnaryOperator.Not, bangExpr);
                break;
            case LITERAL_HEX:
                String hex = lexer.hexString();
                sqlExpr = new SQLHexExpr(hex);
                lexer.nextToken();
                break;
            case INTERVAL:
                sqlExpr = parseInterval();
                break;
            case DEFAULT:
                lexer.nextToken();
                sqlExpr = new SQLIdentifierExpr("DEFAULT");
                break;
            case ANY:
                lexer.nextToken();
                SQLAnyExpr anyExpr = new SQLAnyExpr();

                accept(Token.LPAREN);
                SQLSelect anySubQuery = createSelectParser().select();
                anyExpr.setSubQuery(anySubQuery);
                accept(Token.RPAREN);

                anySubQuery.setParent(anyExpr);

                sqlExpr = anyExpr;
                break;
            case SOME:
                lexer.nextToken();
                SQLSomeExpr someExpr = new SQLSomeExpr();

                accept(Token.LPAREN);
                SQLSelect someSubQuery = createSelectParser().select();
                someExpr.setSubQuery(someSubQuery);
                accept(Token.RPAREN);

                someSubQuery.setParent(someExpr);

                sqlExpr = someExpr;
                break;
            case KEY:
                lexer.nextToken();
                sqlExpr = new SQLIdentifierExpr("KEY");
                break;
            case DISTINCT:
                lexer.nextToken();
                sqlExpr = new SQLIdentifierExpr("DISTINCT");
                break;
            case ALL:
                lexer.nextToken();
                SQLAllExpr allExpr = new SQLAllExpr();

                accept(Token.LPAREN);
                SQLSelect allSubQuery = createSelectParser().select();
                allExpr.setSubQuery(allSubQuery);
                accept(Token.RPAREN);

                allSubQuery.setParent(allExpr);

                sqlExpr = allExpr;
                break;
            default:
                throw new ParserException("ERROR. token : " + tok);
        }

        return primaryRest(sqlExpr);
    }

    protected SQLExpr parseInterval() {
        throw new ParserException("TODO");
    }

    protected SQLSelectParser createSelectParser() {
        return new SQLSelectParser(lexer);
    }

    public SQLExpr primaryRest(SQLExpr expr) throws ParserException {
        if (expr == null) {
            throw new IllegalArgumentException("expr");
        }

        if (lexer.token() == Token.DOT) {
            lexer.nextToken();
            expr = dotRest(expr);
        } else if (lexer.token() == Token.COLONEQ) {
            lexer.nextToken();
            SQLExpr rightExp = primary();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Assignment, rightExp);
        } else {
            if (lexer.token() == Token.LPAREN) {
                if (expr instanceof SQLIdentifierExpr) {
                    SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                    String method_name = identExpr.getName();
                    lexer.nextToken();

                    if (isAggreateFunction(method_name)) {
                        SQLAggregateExpr aggregateExpr = parseAggregateExpr(method_name);

                        return aggregateExpr;
                    }

                    SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr(method_name);
                    if (lexer.token() != Token.RPAREN) {
                        exprList(methodInvokeExpr.getParameters());
                    }

                    accept(Token.RPAREN);

                    return primaryRest(methodInvokeExpr);
                }

                throw new ParserException("not support token:");
            }
        }

        return expr;
    }

    protected SQLExpr dotRest(SQLExpr expr) {
        if (lexer.token() == Token.STAR) {
            lexer.nextToken();
            expr = new SQLPropertyExpr(expr, "*");
        } else {
            String name;

            if (lexer.token() == Token.KEY) {
                name = "KEY";
                lexer.nextToken();
            } else if (lexer.token() != Token.IDENTIFIER) {
                throw new ParserException("error : " + lexer.stringVal());
            } else {
                name = lexer.stringVal();
                lexer.nextToken();
            }

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr(name);
                if (lexer.token() == Token.RPAREN) {
                    lexer.nextToken();
                } else {
                    if (lexer.token() == Token.PLUS) {
                        methodInvokeExpr.getParameters().add(new SQLIdentifierExpr("+"));
                        lexer.nextToken();
                    } else {
                        exprList(methodInvokeExpr.getParameters());
                    }
                    accept(Token.RPAREN);
                }
                expr = methodInvokeExpr;
            } else {
                expr = new SQLPropertyExpr(expr, name);
            }
        }

        expr = primaryRest(expr);
        return expr;
    }

    public final SQLExpr groupComparisionRest(SQLExpr expr) throws ParserException {
        return expr;
    }

    public final void names(Collection<SQLName> exprCol) throws ParserException {
        if (lexer.token() == Token.RBRACE) {
            return;
        }

        if (lexer.token() == Token.EOF) {
            return;
        }

        exprCol.add(name());

        while (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            exprCol.add(name());
        }
    }

    public final void exprList(Collection<SQLExpr> exprCol) throws ParserException {
        if (lexer.token() == Token.RPAREN) {
            return;
        }

        if (lexer.token() == Token.EOF) {
            return;
        }

        SQLExpr expr = expr();
        exprCol.add(expr);

        while (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            expr = expr();
            exprCol.add(expr);
        }
    }

    public final SQLName name() throws ParserException {
        String identName;
        if (lexer.token() == Token.LITERAL_ALIAS) {
            identName = '"' + lexer.stringVal() + '"';
            lexer.nextToken();
        } else if (lexer.token() == Token.IDENTIFIER) {
            identName = lexer.stringVal();

            lexer.nextToken();
        } else {
            throw new ParserException("error");
        }

        SQLName name = new SQLIdentifierExpr(identName);

        if (lexer.token() == Token.DOT) {
            lexer.nextToken();

            if (lexer.token() != Token.IDENTIFIER) {
                throw new ParserException("error");
            }

            name = new SQLPropertyExpr(name, lexer.stringVal());
            lexer.nextToken();
        }

        return name;
    }

    public boolean isAggreateFunction(String word) {
        String[] _aggregateFunctions = { "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM" };

        for (int i = 0; i < _aggregateFunctions.length; ++i) {
            if (_aggregateFunctions[i].compareToIgnoreCase(word) == 0) {
                return true;
            }
        }

        return false;
    }

    protected SQLAggregateExpr parseAggregateExpr(String method_name) throws ParserException {
        SQLAggregateExpr aggregateExpr;
        if (lexer.token() == Token.ALL) {
            aggregateExpr = new SQLAggregateExpr(method_name, 1);
            lexer.nextToken();
        } else if (lexer.token() == Token.DISTINCT) {
            aggregateExpr = new SQLAggregateExpr(method_name, 0);
            lexer.nextToken();
        } else {
            aggregateExpr = new SQLAggregateExpr(method_name, 1);
        }
        exprList(aggregateExpr.getArguments());

        accept(Token.RPAREN);

        return aggregateExpr;
    }

    public SQLOrderBy parseOrderBy() throws ParserException {
        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = new SQLOrderBy();

            lexer.nextToken();

            accept(Token.BY);

            orderBy.getItems().add(parseSelectOrderByItem());

            while (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                orderBy.getItems().add(parseSelectOrderByItem());
            }

            return orderBy;
        }

        return null;
    }

    public SQLSelectOrderByItem parseSelectOrderByItem() throws ParserException {
        SQLSelectOrderByItem item = new SQLSelectOrderByItem();

        item.setExpr(expr());

        if (lexer.token() == Token.ASC) {
            lexer.nextToken();
            item.setType(SQLOrderingSpecification.ASC);
        } else if (lexer.token() == Token.DESC) {
            lexer.nextToken();
            item.setType(SQLOrderingSpecification.DESC);
        }

        return item;
    }

    public final SQLExpr bitAnd() throws ParserException {
        SQLExpr expr = shift();
        return orRest(expr);
    }

    public final SQLExpr bitAndRest(SQLExpr expr) throws ParserException {
        while (lexer.token() == Token.AMP) {
            lexer.nextToken();
            SQLExpr rightExp = shift();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BitwiseAnd, rightExp);
        }
        return expr;
    }

    public final SQLExpr bitOr() throws ParserException {
        SQLExpr expr = bitAnd();
        return orRest(expr);
    }

    public final SQLExpr bitOrRest(SQLExpr expr) throws ParserException {
        if (lexer.token() == Token.BAR) {
            lexer.nextToken();
            SQLExpr rightExp = bitAnd();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BitwiseOr, rightExp);
            expr = bitAndRest(expr);
        } else if (lexer.token() == Token.TILDE) {
            lexer.nextToken();
            SQLExpr rightExp = bitAnd();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.InvertBits, rightExp);
            expr = bitAndRest(expr);
        }
        return expr;
    }

    public final SQLExpr equality() throws ParserException {
        SQLExpr expr = additive();
        return equalityRest(expr);
    }

    public final SQLExpr equalityRest(SQLExpr expr) throws ParserException {
        SQLExpr rightExp;
        if (lexer.token() == Token.EQ) {
            lexer.nextToken();
            rightExp = additive();

            rightExp = equalityRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Equality, rightExp);
        } else if (lexer.token() == Token.BANGEQ) {
            lexer.nextToken();
            rightExp = additive();

            rightExp = equalityRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotEqual, rightExp);
        }

        return expr;
    }

    public final SQLExpr inRest(SQLExpr expr) throws ParserException {
        if (lexer.token() == Token.IN) {
            lexer.nextToken();
            accept(Token.LPAREN);

            SQLInListExpr inListExpr = new SQLInListExpr(expr);
            exprList(inListExpr.getTargetList());
            expr = inListExpr;

            accept(Token.RPAREN);
            expr = inListExpr;

            if (inListExpr.getTargetList().size() == 1) {
                SQLExpr targetExpr = inListExpr.getTargetList().get(0);
                if (targetExpr instanceof SQLQueryExpr) {
                    SQLInSubQueryExpr inSubQueryExpr = new SQLInSubQueryExpr();
                    inSubQueryExpr.setExpr(inListExpr.getExpr());
                    inSubQueryExpr.setSubQuery(((SQLQueryExpr) targetExpr).getSubQuery());
                    expr = inSubQueryExpr;
                }
            }
        }

        expr = andRest(expr);
        expr = orRest(expr);
        return expr;
    }

    public final SQLExpr additive() throws ParserException {
        SQLExpr expr = multiplicative();
        return additiveRest(expr);
    }

    public SQLExpr additiveRest(SQLExpr expr) throws ParserException {
        if (lexer.token() == Token.PLUS) {
            lexer.nextToken();
            SQLExpr rightExp = multiplicative();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Add, rightExp);
            expr = additiveRest(expr);
        } else if (lexer.token() == Token.BARBAR) {
            lexer.nextToken();
            SQLExpr rightExp = multiplicative();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Concat, rightExp);
            expr = additiveRest(expr);
        } else if (lexer.token() == Token.SUB) {
            lexer.nextToken();
            SQLExpr rightExp = multiplicative();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Subtract, rightExp);
            expr = additiveRest(expr);
        }

        return expr;
    }

    public final SQLExpr shift() throws ParserException {
        SQLExpr expr = additive();
        return shiftRest(expr);
    }

    public SQLExpr shiftRest(SQLExpr expr) throws ParserException {
        if (lexer.token() == Token.LTLT) {
            lexer.nextToken();
            SQLExpr rightExp = additive();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LeftShift, rightExp);
            expr = shiftRest(expr);
        } else if (lexer.token() == Token.GTGT) {
            lexer.nextToken();
            SQLExpr rightExp = additive();

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.RightShift, rightExp);
            expr = shiftRest(expr);
        }

        return expr;
    }

    public final SQLExpr and() throws ParserException {
        SQLExpr expr = relational();
        return andRest(expr);
    }

    public final SQLExpr andRest(SQLExpr expr) throws ParserException {
        for (;;) {
            if (lexer.token() == Token.AND || lexer.token() == Token.AMPAMP) {
                lexer.nextToken();
                SQLExpr rightExp = relational();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BooleanAnd, rightExp);
            } else {
                break;
            }
        }

        return expr;
    }

    public final SQLExpr or() throws ParserException {
        SQLExpr expr = and();
        return orRest(expr);
    }

    public final SQLExpr orRest(SQLExpr expr) throws ParserException {

        for (;;) {
            if (lexer.token() == Token.OR) {
                lexer.nextToken();
                SQLExpr rightExp = and();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BooleanOr, rightExp);
            } else if (lexer.token() == Token.XOR) {
                lexer.nextToken();
                SQLExpr rightExp = and();

                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.BooleanXor, rightExp);
            } else {
                break;
            }
        }

        return expr;
    }

    public final SQLExpr relational() throws ParserException {
        SQLExpr expr = equality();

        return relationalRest(expr);
    }

    public SQLExpr relationalRest(SQLExpr expr) throws ParserException {
        SQLExpr rightExp;

        if (lexer.token() == Token.LT) {
            lexer.nextToken();
            rightExp = equality();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThan, rightExp);
        } else if (lexer.token() == Token.LTEQ) {
            lexer.nextToken();
            rightExp = shift();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrEqual, rightExp);
        } else if (lexer.token() == Token.LTEQGT) {
            lexer.nextToken();
            rightExp = shift();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrEqualOrGreaterThan, rightExp);
        } else if (lexer.token() == Token.GT) {
            lexer.nextToken();
            rightExp = shift();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.GreaterThan, rightExp);
        } else if (lexer.token() == Token.GTEQ) {
            lexer.nextToken();
            rightExp = shift();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.GreaterThanOrEqual, rightExp);
        } else if (lexer.token() == Token.BANGLT) {
            lexer.nextToken();
            rightExp = shift();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotLessThan, rightExp);
        } else if (lexer.token() == Token.BANGGT) {
            lexer.nextToken();
            rightExp = shift();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotGreaterThan, rightExp);
        } else if (lexer.token() == Token.LTGT) {
            lexer.nextToken();
            rightExp = shift();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrGreater, rightExp);
        } else if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            rightExp = shift();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Like, rightExp);

            if (lexer.token() == Token.ESCAPE) {
                lexer.nextToken();
                rightExp = expr();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Escape, rightExp);
            }
        } else if (lexer.token() == (Token.NOT)) {
            lexer.nextToken();
            expr = notRationalRest(expr);
        } else if (lexer.token() == (Token.BETWEEN)) {
            lexer.nextToken();
            SQLExpr beginExpr = primary();
            accept(Token.AND);
            SQLExpr endExpr = primary();
            expr = new SQLBetweenExpr(expr, beginExpr, endExpr);
        } else if (lexer.token() == (Token.IS)) {
            lexer.nextToken();

            if (lexer.token() == (Token.NOT)) {
                lexer.nextToken();
                SQLExpr rightExpr = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.IsNot, rightExpr);
            } else {
                SQLExpr rightExpr = primary();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Is, rightExpr);
            }
        } else if (lexer.token() == Token.IN) {
            expr = inRest(expr);
        }

        return expr;
    }

    public SQLExpr notRationalRest(SQLExpr expr) {
        if (lexer.token() == (Token.LIKE)) {
            lexer.nextToken();
            SQLExpr rightExp = shift();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotLike, rightExp);

            if (lexer.token() == Token.ESCAPE) {
                lexer.nextToken();
                rightExp = expr();
                expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Escape, rightExp);
            }
        } else if (lexer.token() == Token.IN) {
            lexer.nextToken();
            accept(Token.LPAREN);

            SQLInListExpr inListExpr = new SQLInListExpr(expr, true);
            exprList(inListExpr.getTargetList());
            expr = inListExpr;

            accept(Token.RPAREN);

            if (inListExpr.getTargetList().size() == 1) {
                SQLExpr targetExpr = inListExpr.getTargetList().get(0);
                if (targetExpr instanceof SQLQueryExpr) {
                    SQLInSubQueryExpr inSubQueryExpr = new SQLInSubQueryExpr();
                    inSubQueryExpr.setNot(true);
                    inSubQueryExpr.setExpr(inListExpr.getExpr());
                    inSubQueryExpr.setSubQuery(((SQLQueryExpr) targetExpr).getSubQuery());
                    expr = inSubQueryExpr;
                }
            }

            expr = relationalRest(expr);
            return expr;
        } else if (lexer.token() == (Token.BETWEEN)) {
            lexer.nextToken();
            SQLExpr beginExpr = primary();
            accept(Token.AND);
            SQLExpr endExpr = primary();

            expr = new SQLBetweenExpr(expr, true, beginExpr, endExpr);

            return expr;
        } else {
            throw new ParserException("TODO");
        }
        return expr;
    }

    public SQLDataType parseDataType() throws ParserException {
        if (lexer.token() != Token.IDENTIFIER) {
            throw new ParserException();
        }

        String typeName = lexer.stringVal();

        lexer.nextToken();
        SQLDataType dataType = new SQLDataTypeImpl(typeName);
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            exprList(dataType.getArguments());
            accept(Token.RPAREN);
        }
        return dataType;
    }

    protected SQLDataType parseCharType() throws ParserException {
        if (lexer.token() != Token.IDENTIFIER) {
            throw new ParserException();
        }

        String typeName = lexer.stringVal();

        lexer.nextToken();
        SQLCharactorDataType dataType = new SQLCharactorDataType(typeName);
        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            exprList(dataType.getArguments());
            accept(Token.RPAREN);
        }

        if (lexer.token() != Token.IDENTIFIER) {
            throw new ParserException();
        }

        if (lexer.stringVal().equalsIgnoreCase("CHARACTER")) {
            if (lexer.token() != Token.IDENTIFIER) {
                throw new ParserException();
            }

            if (!lexer.stringVal().equalsIgnoreCase("SET")) {
                throw new ParserException();
            }

            if (lexer.token() != Token.IDENTIFIER) {
                throw new ParserException();
            }
            dataType.setCharSetName(lexer.stringVal());
            lexer.nextToken();

            if (lexer.token() == Token.IDENTIFIER) {
                if (lexer.stringVal().equalsIgnoreCase("COLLATE")) {
                    lexer.nextToken();

                    if (lexer.token() != Token.IDENTIFIER) {
                        throw new ParserException();
                    }
                    dataType.setCollate(lexer.stringVal());
                    lexer.nextToken();
                }
            }
        }
        return dataType;
    }

    public void accept(Token token) {
        if (lexer.token() == token) {
            lexer.nextToken();
        } else {
            throw new SQLParseException("syntax error, expect " + token + ", actual " + lexer.token());
        }
    }
}
