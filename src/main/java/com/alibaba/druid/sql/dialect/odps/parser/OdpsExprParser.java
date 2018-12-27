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
package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsUDTFSQLSelectItem;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.JdbcConstants;

import java.util.Arrays;

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
                "WM_CONCAT"//
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
        super(lexer, JdbcConstants.ODPS);

        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public OdpsExprParser(String sql){
        this(new OdpsLexer(sql));
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
    
    @Override
    public SQLSelectItem parseSelectItem() {
        SQLExpr expr;
        if (lexer.token() == Token.IDENTIFIER) {
            expr = new SQLIdentifierExpr(lexer.stringVal());
            lexer.nextTokenComma();

            if (lexer.token() != Token.COMMA) {
                expr = this.primaryRest(expr);
                expr = this.exprRest(expr);
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

    public SQLDataType parseDataType() {
        if (lexer.identifierEquals(FnvHash.Constants.ARRAY)) {
            lexer.nextToken();
            accept(Token.LT);
            SQLDataType itemType = parseDataType();
            accept(Token.GT);

            return new SQLArrayDataType(itemType, dbType);
        }

        if (lexer.identifierEquals(FnvHash.Constants.MAP)) {
            lexer.nextToken();
            accept(Token.LT);

            SQLDataType keyType = parseDataType();
            accept(Token.COMMA);
            SQLDataType valueType = parseDataType();
            accept(Token.GT);

            return new SQLMapDataType(keyType, valueType, dbType);
        }

        if (lexer.identifierEquals(FnvHash.Constants.STRUCT)) {
            lexer.nextToken();

            SQLStructDataType struct = new SQLStructDataType(dbType);
            accept(Token.LT);
            for (;;) {
                SQLName name = this.name();
                accept(Token.COLON);
                SQLDataType dataType = this.parseDataType();
                struct.addField(name, dataType);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.GT);
            return struct;
//            throw new ParserException("TODO : " + lexer.info());
        }


        return super.parseDataType();
    }


    @Override
    public OdpsSelectParser createSelectParser() {
        return new OdpsSelectParser(this);
    }
}
