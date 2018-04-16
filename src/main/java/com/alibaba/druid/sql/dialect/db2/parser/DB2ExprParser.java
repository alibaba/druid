/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.db2.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;

import java.util.Arrays;

public class DB2ExprParser extends SQLExprParser {
    public final static String[] AGGREGATE_FUNCTIONS;

    public final static long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = { "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM", "ROW_NUMBER",
                "ROWNUMBER" };
        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public DB2ExprParser(String sql){
        this(new DB2Lexer(sql));
        this.lexer.nextToken();
        this.dbType = JdbcConstants.DB2;
    }

    public DB2ExprParser(String sql, SQLParserFeature... features){
        this(new DB2Lexer(sql, features));
        this.lexer.nextToken();
    }

    public DB2ExprParser(Lexer lexer){
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
        this.dbType = JdbcConstants.DB2;
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if (lexer.identifierEquals(FnvHash.Constants.VALUE)) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                if (identExpr.hashCode64() == FnvHash.Constants.NEXT) {
                    lexer.nextToken();
                    accept(Token.FOR);
                    SQLName seqName = this.name();
                    SQLSequenceExpr seqExpr = new SQLSequenceExpr(seqName, SQLSequenceExpr.Function.NextVal);
                    return seqExpr;
                } else if (identExpr.hashCode64() == FnvHash.Constants.PREVIOUS) {
                    lexer.nextToken();
                    accept(Token.FOR);
                    SQLName seqName = this.name();
                    SQLSequenceExpr seqExpr = new SQLSequenceExpr(seqName, SQLSequenceExpr.Function.PrevVal);
                    return seqExpr;
                }
            }
        } else if (lexer.identifierEquals(FnvHash.Constants.DATE)) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                if (identExpr.hashCode64() == FnvHash.Constants.CURRENT) {
                    lexer.nextToken();

                    expr = new SQLIdentifierExpr("CURRENT DATE");
                }
            }
        } else if (lexer.identifierEquals(FnvHash.Constants.DAY) && expr instanceof SQLIntegerExpr) {
            lexer.nextToken();
            expr = new SQLIntervalExpr(expr, SQLIntervalUnit.DAY);
        } else if (lexer.identifierEquals(FnvHash.Constants.TIMESTAMP)) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                if (identExpr.hashCode64() == FnvHash.Constants.CURRENT) {
                    lexer.nextToken();

                    expr = new SQLIdentifierExpr("CURRENT TIMESTAMP");
                }
            }
        } else if (lexer.identifierEquals(FnvHash.Constants.TIME)) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                if (identExpr.hashCode64() == FnvHash.Constants.CURRENT) {
                    lexer.nextToken();

                    expr = new SQLIdentifierExpr("CURRENT TIME");
                }
            }
        } else if (lexer.token() == Token.SCHEMA) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                if (identExpr.hashCode64() == FnvHash.Constants.CURRENT) {
                    lexer.nextToken();

                    expr = new SQLIdentifierExpr("CURRENT SCHEMA");
                }
            }
        } else if (lexer.identifierEquals(FnvHash.Constants.PATH)) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                if (identExpr.hashCode64() == FnvHash.Constants.CURRENT) {
                    lexer.nextToken();

                    expr = new SQLIdentifierExpr("CURRENT PATH");
                }
            }
        } else if (lexer.identifierEquals(FnvHash.Constants.MONTHS)) {
            SQLIntervalExpr intervalExpr = new SQLIntervalExpr(expr, SQLIntervalUnit.MONTH);
            lexer.nextToken();
            expr = intervalExpr;
        } else if (lexer.identifierEquals(FnvHash.Constants.YEARS)) {
            SQLIntervalExpr intervalExpr = new SQLIntervalExpr(expr, SQLIntervalUnit.YEAR);
            lexer.nextToken();
            expr = intervalExpr;
        } else if (lexer.identifierEquals(FnvHash.Constants.DAYS)) {
            SQLIntervalExpr intervalExpr = new SQLIntervalExpr(expr, SQLIntervalUnit.DAY);
            lexer.nextToken();
            expr = intervalExpr;
        } else if (lexer.identifierEquals(FnvHash.Constants.HOURS)) {
            SQLIntervalExpr intervalExpr = new SQLIntervalExpr(expr, SQLIntervalUnit.HOUR);
            lexer.nextToken();
            expr = intervalExpr;
        } else if (lexer.identifierEquals(FnvHash.Constants.MINUTES)) {
            SQLIntervalExpr intervalExpr = new SQLIntervalExpr(expr, SQLIntervalUnit.MINUTE);
            lexer.nextToken();
            expr = intervalExpr;
        } else if (lexer.identifierEquals(FnvHash.Constants.SECONDS)) {
            SQLIntervalExpr intervalExpr = new SQLIntervalExpr(expr, SQLIntervalUnit.SECOND);
            lexer.nextToken();
            expr = intervalExpr;
        }

        return super.primaryRest(expr);
    }

    protected SQLExpr dotRest(SQLExpr expr) {
        if (lexer.identifierEquals(FnvHash.Constants.NEXTVAL)) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                SQLSequenceExpr seqExpr = new SQLSequenceExpr(identExpr, SQLSequenceExpr.Function.NextVal);
                lexer.nextToken();
                return seqExpr;
            }
        } else if (lexer.identifierEquals(FnvHash.Constants.PREVVAL)) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                SQLSequenceExpr seqExpr = new SQLSequenceExpr(identExpr, SQLSequenceExpr.Function.PrevVal);
                lexer.nextToken();
                return seqExpr;
            }
        } else if (lexer.identifierEquals(FnvHash.Constants.CURRVAL)) {
            if (expr instanceof SQLIdentifierExpr) {
                SQLIdentifierExpr identExpr = (SQLIdentifierExpr) expr;
                SQLSequenceExpr seqExpr = new SQLSequenceExpr(identExpr, SQLSequenceExpr.Function.CurrVal);
                lexer.nextToken();
                return seqExpr;
            }
        }

        return super.dotRest(expr);
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        column = super.parseColumnRest(column);

        if (lexer.identifierEquals(FnvHash.Constants.GENERATED)) {
            lexer.nextToken();
            if (lexer.identifierEquals(FnvHash.Constants.ALWAYS)) {
                lexer.nextToken();
            } else {
                throw new ParserException("TODO " + lexer.info());
            }

            accept(Token.AS);

            if (lexer.token() == Token.IDENTITY) {
                SQLColumnDefinition.Identity identity = parseIdentity();
                column.setIdentity(identity);
            } else {
                SQLExpr expr = this.expr();

                column.setGeneratedAlawsAs(expr);
            }

            parseColumnRest(column);
        }

        return column;
    }

    private SQLColumnDefinition.Identity parseIdentity() {
        SQLColumnDefinition.Identity identity = new SQLColumnDefinition.Identity();

        accept(Token.IDENTITY);
        if (lexer.token() == Token.LPAREN) {
            accept(Token.LPAREN);

            if (lexer.identifierEquals(FnvHash.Constants.START)) {
                lexer.nextToken();
                accept(Token.WITH);
                if (lexer.token() == Token.LITERAL_INT) {
                    identity.setSeed((Integer) lexer.integerValue());
                    lexer.nextToken();
                } else {
                    throw new ParserException("TODO " + lexer.info());
                }

                accept(Token.COMMA);
            }

            if (lexer.identifierEquals(FnvHash.Constants.INCREMENT)) {
                lexer.nextToken();
                accept(Token.BY);
                if (lexer.token() == Token.LITERAL_INT) {
                    identity.setIncrement((Integer) lexer.integerValue());
                    lexer.nextToken();
                } else {
                    throw new ParserException("TODO " + lexer.info());
                }
            }

            accept(Token.RPAREN);
        }

        return identity;
    }
}
