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
package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLExternalRecordFormat;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsNewExpr;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsTransformExpr;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsUDTFSQLSelectItem;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;
import java.util.List;

public class OdpsExprParser extends SQLExprParser {
    public final static String[] AGGREGATE_FUNCTIONS;

    public final static long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = { "AVG", //
                "COUNT", //
                "LAG",
                "LEAD",
                "MAX", //
                "MIN", //
                "STDDEV", //
                "SUM", //
                "ROW_NUMBER",
                "WM_CONCAT",
                "COLLECT_LIST"//
        };
        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public OdpsExprParser(Lexer lexer){
        super(lexer, DbType.odps);

        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public OdpsExprParser(String sql, SQLParserFeature... features){
        this(new OdpsLexer(sql, features));
        this.lexer.nextToken();
    }
    
    public OdpsExprParser(String sql, boolean skipComments, boolean keepComments){
        this(new OdpsLexer(sql, skipComments, keepComments));
        this.lexer.nextToken();
    }
    
    protected SQLExpr parseAliasExpr(String alias) {
        String chars = alias.substring(1, alias.length() - 1);
        return new SQLCharExpr(chars);
    }
    
    final static long GSONBUILDER = FnvHash.fnv1a_64_lower("GSONBUILDER");
    
    @Override
    public SQLSelectItem parseSelectItem() {
        SQLExpr expr;
        if (lexer.token() == Token.IDENTIFIER) {
            String stringVal = lexer.stringVal();
            long hash_lower = lexer.hash_lower();

            lexer.nextTokenComma();

            if (FnvHash.Constants.DATETIME == hash_lower
                    && lexer.stringVal().charAt(0) != '`'
                    && lexer.token() == Token.LITERAL_CHARS) {
                String literal = lexer.stringVal();
                lexer.nextToken();

                SQLDateTimeExpr ts = new SQLDateTimeExpr(literal);
                expr = ts;
            } else {
                expr = new SQLIdentifierExpr(stringVal);
                if (lexer.token() != Token.COMMA) {
                    expr = this.primaryRest(expr);
                    expr = this.exprRest(expr);
                }
            }
        } else {
            expr = expr();
        }

        String alias = null;
        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                OdpsUDTFSQLSelectItem selectItem = new OdpsUDTFSQLSelectItem();

                selectItem.setExpr(expr);

                for (;;) {
                    alias = lexer.stringVal();
                    lexer.nextToken();

                    selectItem.getAliasList().add(alias);

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }

                accept(Token.RPAREN);

                return selectItem;
            } else {
                alias = alias();
            }
        } else {
            alias = as();
        }

        SQLSelectItem item = new SQLSelectItem(expr, alias);

        if (lexer.hasComment() && lexer.isKeepComments()) {
            item.addAfterComment(lexer.readAndResetComments());
        }

        return item;
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if(lexer.token() == Token.COLON) {
            lexer.nextToken();
            expr = dotRest(expr);
            return expr;
        }

        if (lexer.token() == Token.LBRACKET) {
            SQLArrayExpr array = new SQLArrayExpr();
            array.setExpr(expr);
            lexer.nextToken();
            this.exprList(array.getValues(), array);
            accept(Token.RBRACKET);
            return primaryRest(array);
        } else if ((lexer.token() == Token.LITERAL_CHARS || lexer.token() == Token.LITERAL_ALIAS) && expr instanceof SQLCharExpr) {
            SQLCharExpr charExpr = new SQLCharExpr(lexer.stringVal());
            lexer.nextTokenValue();
            SQLMethodInvokeExpr concat = new SQLMethodInvokeExpr("concat", null, expr, charExpr);

            while (lexer.token() == Token.LITERAL_CHARS || lexer.token() == Token.LITERAL_ALIAS) {
                charExpr = new SQLCharExpr(lexer.stringVal());
                lexer.nextToken();
                concat.addArgument(charExpr);
            }

            expr = concat;
        }

        if (lexer.token() == Token.LPAREN
                && expr instanceof SQLIdentifierExpr
                && ((SQLIdentifierExpr) expr).nameHashCode64() == FnvHash.Constants.TRANSFORM) {
            OdpsTransformExpr transformExpr = new OdpsTransformExpr();
            lexer.nextToken();
            this.exprList(transformExpr.getInputColumns(), transformExpr);
            accept(Token.RPAREN);

            if (lexer.identifierEquals(FnvHash.Constants.ROW)) {
                SQLExternalRecordFormat recordFormat = this.parseRowFormat();
                transformExpr.setInputRowFormat(recordFormat);
            }

            if (lexer.token() == Token.USING || lexer.identifierEquals(FnvHash.Constants.USING)) {
                lexer.nextToken();
                transformExpr.setUsing(this.expr());
            }

            if (lexer.identifierEquals(FnvHash.Constants.RESOURCES)) {
                lexer.nextToken();
                this.exprList(transformExpr.getResources(), transformExpr);
            }

            if (lexer.token() == Token.AS) {
                lexer.nextToken();
                List<SQLColumnDefinition> outputColumns = transformExpr.getOutputColumns();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    for (; ; ) {
                        SQLColumnDefinition column = this.parseColumn();
                        outputColumns.add(column);
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);
                } else {
                    SQLColumnDefinition column = new SQLColumnDefinition();
                    column.setName(this.name());
                    outputColumns.add(column);
                }
            }

            if (lexer.identifierEquals(FnvHash.Constants.ROW)) {
                SQLExternalRecordFormat recordFormat = this.parseRowFormat();
                transformExpr.setOutputRowFormat(recordFormat);
            }

            return transformExpr;
        }

        if (expr instanceof SQLIdentifierExpr
                && ((SQLIdentifierExpr) expr).nameHashCode64() == FnvHash.Constants.NEW) {
            SQLIdentifierExpr ident = (SQLIdentifierExpr) expr;

            OdpsNewExpr newExpr = new OdpsNewExpr();
            if (lexer.identifierEquals(FnvHash.Constants.GSON)
                    || lexer.identifierEquals(GSONBUILDER)
                    || lexer.identifierEquals("STRING")
            ) {
                lexer.nextToken();
                String methodName = lexer.stringVal();
                newExpr.setMethodName(methodName);
                accept(Token.LPAREN);
                this.exprList(newExpr.getArguments(), newExpr);
                accept(Token.RPAREN);
                expr = newExpr;
            } else if (lexer.identifierEquals("java") || lexer.identifierEquals("com")) {
                SQLName name = this.name();
                String strName = ident.getName() + ' ' + name.toString();
                if (lexer.token() == Token.LT) {
                    lexer.nextToken();
                    for (int i = 0; lexer.token() != Token.GT;i++) {
                        if (i != 0) {
                            strName += ", ";
                        }
                        SQLName arg = this.name();
                        strName += arg.toString();
                    }
                    lexer.nextToken();
                }
                ident.setName(strName);
            }
        }


        return super.primaryRest(expr);
    }
    

    public SQLExpr relationalRest(SQLExpr expr) {
        if (lexer.identifierEquals("REGEXP")) {
            lexer.nextToken();
            SQLExpr rightExp = bitOr();

            rightExp = relationalRest(rightExp);

            return new SQLBinaryOpExpr(expr, SQLBinaryOperator.RegExp, rightExp, dbType);
        }

        return super.relationalRest(expr);
    }

    @Override
    public OdpsSelectParser createSelectParser() {
        return new OdpsSelectParser(this);
    }
}
