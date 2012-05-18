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
import java.util.List;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
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
import com.alibaba.druid.sql.ast.expr.SQLCurrentOfCursorExpr;
import com.alibaba.druid.sql.ast.expr.SQLDefaultExpr;
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
import com.alibaba.druid.sql.ast.statement.NotNullConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCharactorDataType;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleHint;

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
        SQLExpr expr = primary();
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
                SQLVariantRefExpr varRefExpr = new SQLVariantRefExpr(lexer.stringVal());
                lexer.nextToken();
                if (varRefExpr.getName().equals("@") && lexer.token() == Token.LITERAL_CHARS) {
                    varRefExpr.setName("@'" + lexer.stringVal() + "'");
                    lexer.nextToken();
                } else if (varRefExpr.getName().equals("@@") && lexer.token() == Token.LITERAL_CHARS) {
                    varRefExpr.setName("@@'" + lexer.stringVal() + "'");
                    lexer.nextToken();
                }
                sqlExpr = varRefExpr;
                break;
            case DEFAULT:
                sqlExpr = new SQLDefaultExpr();
                lexer.nextToken();
                break;
            case DUAL:
            case KEY:
            case DISTINCT:
            case LIMIT:
            case SCHEMA:
            case COLUMN:
            case IF:
                sqlExpr = new SQLIdentifierExpr(lexer.stringVal());
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
                    lexer.nextToken();

                    SQLExpr notTarget = expr();

                    accept(Token.RPAREN);
                    notTarget = exprRest(notTarget);

                    sqlExpr = new SQLNotExpr(notTarget);

                    return primaryRest(sqlExpr);
                } else {
                    SQLExpr restExpr = expr();
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
            case COLON:
                lexer.nextToken();
                if (lexer.token == Token.LITERAL_ALIAS) {
                    sqlExpr = new SQLVariantRefExpr(":\"" + lexer.stringVal() + "\"");
                    lexer.nextToken();
                }
                break;
            case ANY:
                lexer.nextToken();
                if (lexer.token() == Token.LPAREN) {
                    SQLAnyExpr anyExpr = new SQLAnyExpr();

                    accept(Token.LPAREN);
                    SQLSelect anySubQuery = createSelectParser().select();
                    anyExpr.setSubQuery(anySubQuery);
                    accept(Token.RPAREN);

                    anySubQuery.setParent(anyExpr);

                    sqlExpr = anyExpr;
                } else {
                    sqlExpr = new SQLIdentifierExpr("ANY");
                }
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
                throw new ParserException("ERROR. token : " + tok + " " + lexer.stringVal());
        }

        return primaryRest(sqlExpr);
    }

    protected SQLExpr parseInterval() {
        throw new ParserException("TODO");
    }

    public SQLSelectParser createSelectParser() {
        return new SQLSelectParser(lexer);
    }

    public SQLExpr primaryRest(SQLExpr expr) throws ParserException {
        if (expr == null) {
            throw new IllegalArgumentException("expr");
        }

        if (lexer.token() == Token.OF) {
            if (expr instanceof SQLIdentifierExpr) {
                String name = ((SQLIdentifierExpr) expr).getName();
                if ("CURRENT".equalsIgnoreCase(name)) {
                    lexer.nextToken();
                    SQLName cursorName = this.name();
                    return new SQLCurrentOfCursorExpr(cursorName);
                }
            }
        }

        if (lexer.token() == Token.DOT) {
            lexer.nextToken();

            if (expr instanceof SQLCharExpr) {
                String text = ((SQLCharExpr) expr).getText();
                expr = new SQLIdentifierExpr(text);
            }

            expr = dotRest(expr);
            return primaryRest(expr);
        } else {
            if (lexer.token() == Token.LPAREN) {
                return methodRest(expr, true);
            }
        }

        return expr;
    }

    protected SQLExpr methodRest(SQLExpr expr, boolean acceptLPAREN) {
        if (acceptLPAREN) {
            accept(Token.LPAREN);
        }

        if (expr instanceof SQLName || expr instanceof SQLDefaultExpr) {
            String method_name;

            SQLMethodInvokeExpr methodInvokeExpr;
            if (expr instanceof SQLPropertyExpr) {
                method_name = ((SQLPropertyExpr) expr).getName();
                methodInvokeExpr = new SQLMethodInvokeExpr(method_name);
                methodInvokeExpr.setOwner(((SQLPropertyExpr) expr).getOwner());
            } else {
                method_name = expr.toString();
                methodInvokeExpr = new SQLMethodInvokeExpr(method_name);
            }

            if (isAggreateFunction(method_name)) {
                SQLAggregateExpr aggregateExpr = parseAggregateExpr(method_name);

                return aggregateExpr;
            }

            if (lexer.token() != Token.RPAREN) {
                exprList(methodInvokeExpr.getParameters());
            }

            accept(Token.RPAREN);

            return primaryRest(methodInvokeExpr);
        }

        throw new ParserException("not support token:" + lexer.token());
    }

    protected SQLExpr dotRest(SQLExpr expr) {
        if (lexer.token() == Token.STAR) {
            lexer.nextToken();
            expr = new SQLPropertyExpr(expr, "*");
        } else {
            String name;

            if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.LITERAL_CHARS
                || lexer.token() == Token.LITERAL_ALIAS) {
                name = lexer.stringVal();
                lexer.nextToken();
            } else if (lexer.getKeywods().containsValue(lexer.token())) {
                name = lexer.stringVal();
                lexer.nextToken();
            } else {
                throw new ParserException("error : " + lexer.stringVal());
            }

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr(name);
                methodInvokeExpr.setOwner(expr);
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
        if (lexer.token() == Token.RPAREN || lexer.token() == Token.RBRACKET) {
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

    public SQLName name() throws ParserException {
        String identName;
        if (lexer.token() == Token.LITERAL_ALIAS) {
            identName = '"' + lexer.stringVal() + '"';
            lexer.nextToken();
        } else if (lexer.token() == Token.IDENTIFIER) {
            identName = lexer.stringVal();

            lexer.nextToken();
        } else if (lexer.token() == Token.LITERAL_CHARS) {
            identName = '\'' + lexer.stringVal() + '\'';
            lexer.nextToken();
        } else {
            throw new ParserException("error " + lexer.token());
        }

        SQLName name = new SQLIdentifierExpr(identName);

        name = nameRest(name);

        return name;
    }

    public SQLName nameRest(SQLName name) throws ParserException {
        if (lexer.token() == Token.DOT) {
            lexer.nextToken();

            if (lexer.token() == Token.KEY) {
                name = new SQLPropertyExpr(name, "KEY");
                lexer.nextToken();
                return name;
            }

            if (lexer.token() != Token.LITERAL_ALIAS && lexer.token() != Token.IDENTIFIER
                && (!lexer.getKeywods().containsValue(lexer.token()))) {
                throw new ParserException("error, " + lexer.token());
            }

            if (lexer.token() == Token.LITERAL_ALIAS) {
                name = new SQLPropertyExpr(name, '"' + lexer.stringVal() + '"');
            } else {
                name = new SQLPropertyExpr(name, lexer.stringVal());
            }
            lexer.nextToken();
            name = nameRest(name);
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
        return bitAndRest(expr);
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
        return bitOrRest(expr);
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
        SQLExpr expr = shift();
        return equalityRest(expr);
    }

    public SQLExpr equalityRest(SQLExpr expr) throws ParserException {
        SQLExpr rightExp;
        if (lexer.token() == Token.EQ) {
            lexer.nextToken();
            rightExp = shift();

            rightExp = equalityRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Equality, rightExp);
        } else if (lexer.token() == Token.BANGEQ) {
            lexer.nextToken();
            rightExp = shift();

            rightExp = equalityRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotEqual, rightExp);
        } else if (lexer.token() == Token.COLONEQ) {
            lexer.nextToken();
            rightExp = expr();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Assignment, rightExp);
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

    public SQLExpr and() throws ParserException {
        SQLExpr expr = relational();
        return andRest(expr);
    }

    public SQLExpr andRest(SQLExpr expr) throws ParserException {
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

    public SQLExpr or() throws ParserException {
        SQLExpr expr = and();
        return orRest(expr);
    }

    public SQLExpr orRest(SQLExpr expr) throws ParserException {

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

    public SQLExpr relational() throws ParserException {
        SQLExpr expr = equality();

        return relationalRest(expr);
    }

    public SQLExpr relationalRest(SQLExpr expr) throws ParserException {
        SQLExpr rightExp;

        if (lexer.token() == Token.LT) {
            lexer.nextToken();
            rightExp = bitOr();
            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThan, rightExp);
            // expr = relationalRest(expr);
        } else if (lexer.token() == Token.LTEQ) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrEqual, rightExp);
        } else if (lexer.token() == Token.LTEQGT) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrEqualOrGreaterThan, rightExp);
        } else if (lexer.token() == Token.GT) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.GreaterThan, rightExp);
        } else if (lexer.token() == Token.GTEQ) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.GreaterThanOrEqual, rightExp);
        } else if (lexer.token() == Token.BANGLT) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotLessThan, rightExp);
        } else if (lexer.token() == Token.BANGGT) {
            lexer.nextToken();
            rightExp = bitOr();

            rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.NotGreaterThan, rightExp);
        } else if (lexer.token() == Token.LTGT) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.LessThanOrGreater, rightExp);
        } else if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            rightExp = bitOr();

            // rightExp = relationalRest(rightExp);

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
            SQLExpr beginExpr = bitOr();
            accept(Token.AND);
            SQLExpr endExpr = bitOr();
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
            SQLExpr rightExp = equality();

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
            SQLExpr beginExpr = bitOr();
            accept(Token.AND);
            SQLExpr endExpr = bitOr();

            expr = new SQLBetweenExpr(expr, true, beginExpr, endExpr);

            return expr;
        } else {
            throw new ParserException("TODO " + lexer.token());
        }
        return expr;
    }

    public SQLDataType parseDataType() throws ParserException {

        if (lexer.token() == Token.DEFAULT || lexer.token() == Token.NOT || lexer.token() == Token.NULL) {
            return null;
        }

        SQLName typeExpr = name();
        String typeName = typeExpr.toString();

        SQLDataType dataType = new SQLDataTypeImpl(typeName);
        return parseDataTypeRest(dataType);
    }

    protected SQLDataType parseDataTypeRest(SQLDataType dataType) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            exprList(dataType.getArguments());
            accept(Token.RPAREN);

            return parseCharTypeRest(dataType);
        }

        return dataType;
    }

    protected boolean isCharType(SQLDataType dataType) {
        String dataTypeName = dataType.getName();

        return "char".equalsIgnoreCase(dataTypeName) //
               || "varchar".equalsIgnoreCase(dataTypeName)
               || "nchar".equalsIgnoreCase(dataTypeName)
               || "nvarchar".equalsIgnoreCase(dataTypeName)
        //
        ;
    }

    protected SQLDataType parseCharTypeRest(SQLDataType dataType) {
        if (!isCharType(dataType)) {
            return dataType;
        }

        SQLCharactorDataType charType = new SQLCharactorDataType(dataType.getName());
        charType.getArguments().addAll(dataType.getArguments());

        if (identifierEquals("CHARACTER")) {
            lexer.nextToken();

            accept(Token.SET);

            if (lexer.token() != Token.IDENTIFIER) {
                throw new ParserException();
            }
            charType.setCharSetName(lexer.stringVal());
            lexer.nextToken();

            if (lexer.token() == Token.IDENTIFIER) {
                if (lexer.stringVal().equalsIgnoreCase("COLLATE")) {
                    lexer.nextToken();

                    if (lexer.token() != Token.IDENTIFIER) {
                        throw new ParserException();
                    }
                    charType.setCollate(lexer.stringVal());
                    lexer.nextToken();
                }
            }
        }
        return charType;
    }

    public void accept(Token token) {
        if (lexer.token() == token) {
            lexer.nextToken();
        } else {
            throw new SQLParseException("syntax error, expect " + token + ", actual " + lexer.token() + " " + lexer.stringVal());
        }
    }

    public SQLColumnDefinition parseColumn() {
        SQLColumnDefinition column = new SQLColumnDefinition();
        column.setName(name());
        column.setDataType(parseDataType());

        return parseColumnRest(column);
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.token() == Token.DEFAULT) {
            lexer.nextToken();
            column.setDefaultExpr(bitOr());
            return parseColumnRest(column);
        }

        if (lexer.token() == Token.NOT) {
            lexer.nextToken();
            accept(Token.NULL);
            column.getConstaints().add(new NotNullConstraint());
            return parseColumnRest(column);
        }
        
        if (lexer.token() == Token.NULL) {
            lexer.nextToken();
            column.setDefaultExpr(new SQLNullExpr());
            return parseColumnRest(column);
        }

        return column;
    }

    public SQLPrimaryKey parsePrimaryKey() {
        throw new ParserException("TODO");
    }

    public SQLAssignItem parseAssignItem() {
        SQLAssignItem item = new SQLAssignItem();

        SQLExpr var = primary();

        if (var instanceof SQLIdentifierExpr) {
            var = new SQLVariantRefExpr(((SQLIdentifierExpr) var).getName());
        }
        item.setTarget(var);
        if (lexer.token() == Token.COLONEQ) {
            lexer.nextToken();
        } else {
            accept(Token.EQ);
        }
        item.setValue(expr());

        return item;
    }
    
    public void parseHints(List<SQLHint> hints) {
        if (lexer.token() == Token.HINT) {
            hints.add(new OracleHint(lexer.stringVal()));
            lexer.nextToken();
        }
    }
}
