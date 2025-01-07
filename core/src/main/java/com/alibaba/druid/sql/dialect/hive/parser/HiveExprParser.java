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
package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLCurrentTimeExpr;
import com.alibaba.druid.sql.ast.SQLCurrentUserExpr;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

public class HiveExprParser extends SQLExprParser {
    private static final String[] AGGREGATE_FUNCTIONS;
    private static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {"AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM", "ROW_NUMBER",
                "ROWNUMBER"};

        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public HiveExprParser(String sql) {
        this(new HiveLexer(sql));
        this.lexer.nextToken();
    }

    public HiveExprParser(String sql, SQLParserFeature... features) {
        this(new HiveLexer(sql, features));
        this.lexer.nextToken();
    }

    public HiveExprParser(Lexer lexer) {
        this(lexer, DbType.hive);
    }

    public HiveExprParser(Lexer lexer, DbType dbType) {
        super(lexer, dbType);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    @Override
    protected SQLExpr primaryCommon(SQLExpr sqlExpr) {
        sqlExpr = new SQLIdentifierExpr(lexer.stringVal());
        lexer.nextToken();
        return sqlExpr;
    }
    @Override
    protected String doRestSpecific(SQLExpr expr) {
        String name = null;
        if ((lexer.token() == Token.LITERAL_INT || lexer.token() == Token.LITERAL_FLOAT)) {
            name = lexer.numberString();
            lexer.nextToken();
        }
        return name;
    }

    @Override
    protected SQLExpr relationalRestEqeq(SQLExpr expr) {
        Lexer.SavePoint mark = lexer.mark();
        lexer.nextToken();
        SQLExpr rightExp;
        try {
            if (lexer.token() == Token.SEMI) {
                lexer.reset(mark);
                return expr;
            }
            rightExp = bitOr();
        } catch (EOFParserException e) {
            throw new ParserException("EOF, " + expr + "=", e);
        }

        if (lexer.token() == Token.COLONEQ) {
            lexer.nextToken();
            SQLExpr colonExpr = expr();
            rightExp = new SQLBinaryOpExpr(rightExp, SQLBinaryOperator.Assignment, colonExpr, dbType);
        }
        return new SQLBinaryOpExpr(expr, SQLBinaryOperator.Equality, rightExp, dbType);
    }

    @Override
    protected SQLExpr parseAssignItemOnColon(SQLExpr sqlExpr) {
        if (lexer.token() == Token.COLON) {
            lexer.nextToken();
            String str = sqlExpr.toString() + ':';
            str += lexer.numberString();
            lexer.nextToken();
            sqlExpr = new SQLIdentifierExpr(str);
        }
        return sqlExpr;
    }

    @Override
    protected SQLExpr parseSelectItemRest(String ident, long hash_lower) {
        SQLExpr expr = null;
        if (lexer.identifierEquals(FnvHash.Constants.COLLATE)
                && lexer.stringVal().charAt(0) != '`'
        ) {
            lexer.nextToken();
            String collate = lexer.stringVal();
            lexer.nextToken();

            SQLBinaryOpExpr binaryExpr = new SQLBinaryOpExpr(
                    new SQLIdentifierExpr(ident),
                    SQLBinaryOperator.COLLATE,
                    new SQLIdentifierExpr(collate), dbType
            );
            expr = binaryExpr;

        } else if (FnvHash.Constants.TIMESTAMP == hash_lower
                && lexer.stringVal().charAt(0) != '`'
                && lexer.token() == Token.LITERAL_CHARS) {
            String literal = lexer.stringVal();
            lexer.nextToken();

            SQLTimestampExpr ts = new SQLTimestampExpr(literal);
            expr = ts;

            if (lexer.identifierEquals(FnvHash.Constants.AT)) {
                Lexer.SavePoint mark = lexer.mark();
                lexer.nextToken();

                String timeZone = null;
                if (lexer.identifierEquals(FnvHash.Constants.TIME)) {
                    lexer.nextToken();
                    if (lexer.identifierEquals(FnvHash.Constants.ZONE)) {
                        lexer.nextToken();
                        timeZone = lexer.stringVal();
                        lexer.nextToken();
                    }
                }
                if (timeZone == null) {
                    lexer.reset(mark);
                } else {
                    ts.setTimeZone(timeZone);
                }
            }
        } else if (FnvHash.Constants.DATETIME == hash_lower
                && lexer.stringVal().charAt(0) != '`'
                && lexer.token() == Token.LITERAL_CHARS) {
            String literal = lexer.stringVal();
            lexer.nextToken();

            SQLDateTimeExpr ts = new SQLDateTimeExpr(literal);
            expr = ts;
        } else if (FnvHash.Constants.CURRENT_DATE == hash_lower
                && ident.charAt(0) != '`'
                && lexer.token() != Token.LPAREN) {
            expr = new SQLCurrentTimeExpr(SQLCurrentTimeExpr.Type.CURRENT_DATE);

        } else if (FnvHash.Constants.CURRENT_TIMESTAMP == hash_lower
                && ident.charAt(0) != '`'
                && lexer.token() != Token.LPAREN) {
            expr = new SQLCurrentTimeExpr(SQLCurrentTimeExpr.Type.CURRENT_TIMESTAMP);

        } else if (FnvHash.Constants.CURRENT_TIME == hash_lower
                && ident.charAt(0) != '`'
                && lexer.token() != Token.LPAREN) {
            expr = new SQLCurrentTimeExpr(SQLCurrentTimeExpr.Type.CURRENT_TIME);

        } else if (FnvHash.Constants.CURDATE == hash_lower
                && ident.charAt(0) != '`'
                && lexer.token() != Token.LPAREN) {
            expr = new SQLCurrentTimeExpr(SQLCurrentTimeExpr.Type.CURDATE);

        } else if (FnvHash.Constants.LOCALTIME == hash_lower
                && ident.charAt(0) != '`'
                && lexer.token() != Token.LPAREN) {
            expr = new SQLCurrentTimeExpr(SQLCurrentTimeExpr.Type.LOCALTIME);

        } else if (FnvHash.Constants.LOCALTIMESTAMP == hash_lower
                && ident.charAt(0) != '`'
                && lexer.token() != Token.LPAREN) {
            expr = new SQLCurrentTimeExpr(SQLCurrentTimeExpr.Type.LOCALTIMESTAMP);
        }
        return expr;
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if (lexer.token() == Token.COLON) {
            lexer.nextToken();
            expr = dotRest(expr);
            if (expr instanceof SQLPropertyExpr) {
                SQLPropertyExpr spe = (SQLPropertyExpr) expr;
                spe.setSplitString(":");
            }
            return expr;
        }

        switch (lexer.token()) {
            case LBRACKET:
                SQLArrayExpr array = new SQLArrayExpr();
                array.setExpr(expr);
                lexer.nextToken();
                this.exprList(array.getValues(), array);
                accept(Token.RBRACKET);
                return primaryRest(array);
            case LITERAL_CHARS:
                if (expr instanceof SQLCharExpr) {
                    StringBuilder text2 = new StringBuilder(((SQLCharExpr) expr).getText());
                    do {
                        String chars = lexer.stringVal();
                        text2.append(chars);
                        lexer.nextToken();
                    } while (lexer.token() == Token.LITERAL_CHARS || lexer.token() == Token.LITERAL_ALIAS);
                    expr = new SQLCharExpr(text2.toString());
                }
                break;
            case IDENTIFIER:
                if (lexer.identifierEquals(FnvHash.Constants.BD) && expr instanceof SQLNumericLiteralExpr) {
                    lexer.nextToken();
                    Number num = ((SQLNumericLiteralExpr) expr).getNumber();
                    expr = new SQLDecimalExpr(num.toString());
                }
                break;
            default:
                break;
        }

        return super.primaryRest(expr);
    }

    public SQLExpr primary() {
        final Token tok = lexer.token();
        switch (tok) {
            case WITH: {
                return primaryRest(
                        new SQLQueryExpr(
                                createSelectParser()
                                        .select()));
            }
            case IDENTIFIER:
                final long hash_lower = lexer.hashLCase();
                if (hash_lower == FnvHash.Constants.OUTLINE) {
                    lexer.nextToken();
                    SQLExpr file = primary();
                    SQLExpr expr = new MySqlOutFileExpr(file);

                    return primaryRest(expr);
                }

                SQLCurrentTimeExpr currentTimeExpr = null;
                if (hash_lower == FnvHash.Constants.CURRENT_TIMESTAMP) {
                    currentTimeExpr = new SQLCurrentTimeExpr(SQLCurrentTimeExpr.Type.CURRENT_TIMESTAMP);
                } else if (hash_lower == FnvHash.Constants.CURRENT_DATE) {
                    currentTimeExpr = new SQLCurrentTimeExpr(SQLCurrentTimeExpr.Type.CURRENT_DATE);
                } else if (hash_lower == FnvHash.Constants.CURRENT_USER && isEnabled(SQLParserFeature.EnableCurrentUserExpr)) {
                    lexer.nextToken();
                    return primaryRest(new SQLCurrentUserExpr());
                }

                if (currentTimeExpr != null) {
                    String methodName = lexer.stringVal();
                    lexer.nextToken();

                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        if (lexer.token() == Token.LPAREN) {
                            lexer.nextToken();
                        } else {
                            return primaryRest(
                                    methodRest(new SQLIdentifierExpr(methodName), false)
                            );
                        }
                    }

                    return primaryRest(currentTimeExpr);
                }
            default:
                break;
        }

        return super.primary();
    }

    protected SQLExpr parseAliasExpr(String alias) {
        String chars = alias.substring(1, alias.length() - 1);

        StringBuilder buf = null;

        for (int i = 0; i < chars.length(); ++i) {
            char ch = chars.charAt(i);
            if (ch == '\\' && i < chars.length() - 1) {
                char next = chars.charAt(i + 1);
                if (next == '\\') {
                    if (buf == null) {
                        buf = new StringBuilder();
                        buf.append(chars.substring(0, i));
                    }
                    buf.append('\\');
                    ++i;
                } else if (next == '\"') {
                    if (buf == null) {
                        buf = new StringBuilder();
                        buf.append(chars.substring(0, i));
                    }
                    buf.append('"');
                    ++i;
                } else {
                    if (buf != null) {
                        buf.append(ch);
                    }
                }
            } else {
                if (buf != null) {
                    buf.append(ch);
                }
            }
        }

        if (buf != null) {
            chars = buf.toString();
        }
        return new SQLCharExpr(chars);
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        if (lexer.identifierEquals(FnvHash.Constants.MAPPED)) {
            lexer.nextToken();
            accept(Token.BY);
            this.parseAssignItem(column.getMappedBy(), column);
        }

        if (lexer.identifierEquals(FnvHash.Constants.COLPROPERTIES)) {
            lexer.nextToken();
            this.parseAssignItem(column.getColProperties(), column);
        }

        return super.parseColumnRest(column);
    }

    protected SQLExpr parseInterval() {
        String str = lexer.stringVal();
        accept(Token.INTERVAL);
        if (lexer.token() == Token.AS || lexer.token() == Token.RPAREN) {
            return new SQLIdentifierExpr(str);
        }

        SQLExpr value = expr();

        if (value instanceof SQLIntervalExpr) {
            return value;
        }

        if (lexer.token() != Token.IDENTIFIER) {
            throw new ParserException("Syntax error. " + lexer.info());
        }

        String unit = lexer.stringVal();
        lexer.nextToken();

        SQLIntervalExpr intervalExpr = new SQLIntervalExpr();
        intervalExpr.setValue(value);
        SQLIntervalUnit intervalUnit = SQLIntervalUnit.of(unit.toUpperCase());
        if (intervalUnit == SQLIntervalUnit.YEAR
                && lexer.token() == Token.TO) {
            lexer.nextToken();
            acceptIdentifier("MONTH");
            intervalUnit = SQLIntervalUnit.YEAR_TO_MONTH;
        }
        if (intervalUnit == SQLIntervalUnit.YEAR && lexer.nextIf(Token.TO)) {
            acceptIdentifier(FnvHash.Constants.MONTH);
            intervalUnit = SQLIntervalUnit.YEAR_TO_MONTH;
        } else if (intervalUnit == SQLIntervalUnit.DAY && lexer.nextIf(Token.TO)) {
            acceptIdentifier(FnvHash.Constants.SECOND);
            intervalUnit = SQLIntervalUnit.DAY_HOUR;
        } else if (intervalUnit == SQLIntervalUnit.HOUR && lexer.nextIf(Token.TO)) {
            acceptIdentifier(FnvHash.Constants.SECOND);
            intervalUnit = SQLIntervalUnit.HOUR_SECOND;
        }
        intervalExpr.setUnit(intervalUnit);

        return intervalExpr;
    }

    protected SQLExpr primaryIdentifierRest(long hash_lower, String ident) {
        if (ident.length() > 3 && ident.charAt(0) == '`' && ident.charAt(ident.length() - 1) == '`' && ident.indexOf('.') != -1) {
            return topPropertyExpr(ident);
        }
        return super.primaryIdentifierRest(hash_lower, ident);
    }
}
