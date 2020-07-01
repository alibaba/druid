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

import com.alibaba.druid.sql.ast.SQLCurrentTimeExpr;
import com.alibaba.druid.sql.ast.SQLCurrentUserExpr;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLExternalRecordFormat;
import com.alibaba.druid.sql.dialect.mysql.ast.expr.MySqlOutFileExpr;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

public class HiveExprParser extends SQLExprParser {
    private final static String[] AGGREGATE_FUNCTIONS;
    private final static long[] AGGREGATE_FUNCTIONS_CODES;

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

    public HiveExprParser(String sql){
        this(new HiveLexer(sql));
        this.lexer.nextToken();
    }

    public HiveExprParser(String sql, SQLParserFeature... features){
        this(new HiveLexer(sql, features));
        this.lexer.nextToken();
    }

    public HiveExprParser(Lexer lexer){
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public SQLExpr primaryRest(SQLExpr expr) {
//        if(lexer.token() == Token.COLON) {
//            lexer.nextToken();
//            expr = dotRest(expr);
//            return expr;
//        }

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
                    String text2 = ((SQLCharExpr) expr).getText();
                    do {
                        String chars = lexer.stringVal();
                        text2 += chars;
                        lexer.nextToken();
                    } while (lexer.token() == Token.LITERAL_CHARS || lexer.token() == Token.LITERAL_ALIAS);
                    expr = new SQLCharExpr(text2);
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
            case IDENTIFIER:
                final long hash_lower = lexer.hash_lower();
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

    public SQLExternalRecordFormat parseRowFormat() {
        lexer.nextToken();
        acceptIdentifier("FORMAT");

        if (lexer.identifierEquals(FnvHash.Constants.DELIMITED)) {
            lexer.nextToken();
        }

        SQLExternalRecordFormat format = new SQLExternalRecordFormat();

        if (lexer.identifierEquals(FnvHash.Constants.FIELDS)) {
            lexer.nextToken();
            acceptIdentifier("TERMINATED");
            accept(Token.BY);

            format.setTerminatedBy(this.expr());
        } else if (lexer.identifierEquals("FIELD")) {
            throw new ParserException("syntax error, expect FIELDS, " + lexer.info());
        }

        if (lexer.token() == Token.ESCAPE || lexer.identifierEquals(FnvHash.Constants.ESCAPED)) {
            lexer.nextToken();
            accept(Token.BY);
            format.setEscapedBy(this.expr());
        }

        if (lexer.identifierEquals(FnvHash.Constants.LINES)) {
            lexer.nextToken();
            acceptIdentifier("TERMINATED");
            accept(Token.BY);

            format.setLinesTerminatedBy(this.expr());
        }

        if (lexer.identifierEquals(FnvHash.Constants.COLLECTION)) {
            lexer.nextToken();
            acceptIdentifier("ITEMS");
            acceptIdentifier("TERMINATED");
            accept(Token.BY);
            format.setCollectionItemsTerminatedBy(this.expr());
        }

        if (lexer.identifierEquals(FnvHash.Constants.MAP)) {
            lexer.nextToken();
            acceptIdentifier("KEYS");
            acceptIdentifier("TERMINATED");
            accept(Token.BY);
            format.setMapKeysTerminatedBy(this.expr());
        }

        if (lexer.identifierEquals(FnvHash.Constants.SERDE)) {
            lexer.nextToken();
            format.setSerde(this.expr());
        }

        return format;
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
        accept(Token.INTERVAL);
        SQLExpr value = expr();

        if (lexer.token() != Token.IDENTIFIER) {
            throw new ParserException("Syntax error. " + lexer.info());
        }

        String unit = lexer.stringVal();
        lexer.nextToken();

        SQLIntervalExpr intervalExpr = new SQLIntervalExpr();
        intervalExpr.setValue(value);
        SQLIntervalUnit intervalUnit = SQLIntervalUnit.valueOf(unit.toUpperCase());
        if (intervalUnit == SQLIntervalUnit.YEAR
                && lexer.token() == Token.TO) {
            lexer.nextToken();
            acceptIdentifier("MONTH");
            intervalUnit = SQLIntervalUnit.YEAR_TO_MONTH;
        }
        intervalExpr.setUnit(intervalUnit);

        return intervalExpr;
    }
}
