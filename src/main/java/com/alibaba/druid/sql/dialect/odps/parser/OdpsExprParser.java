/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsUDTFSQLSelectItem;
import com.alibaba.druid.sql.parser.EOFParserException;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class OdpsExprParser extends SQLExprParser {

    public final static String[] AGGREGATE_FUNCTIONS = { "AVG", //
            "COUNT", //
            "LAG",
            "LEAD",
            "MAX", //
            "MIN", //
            "STDDEV", //
            "SUM", //
            "ROW_NUMBER"//
                                                     };

    public OdpsExprParser(Lexer lexer){
        super(lexer);

        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
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
        return new SQLCharExpr(alias);
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

        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                OdpsUDTFSQLSelectItem selectItem = new OdpsUDTFSQLSelectItem();

                selectItem.setExpr(expr);

                for (;;) {
                    String alias = lexer.stringVal();
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
            }
        }

        final String alias = as();
        
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
    
    public SQLExpr equalityRest(SQLExpr expr) {
        if (lexer.token() == Token.EQEQ) {
            SQLExpr rightExp;
            lexer.nextToken();
            try {
                rightExp = bitOr();
            } catch (EOFParserException e) {
                throw new ParserException("EOF, " + expr + "=", e);
            }
            rightExp = equalityRest(rightExp);

            expr = new SQLBinaryOpExpr(expr, SQLBinaryOperator.Equality, rightExp, getDbType());
            
            return expr;
        }
        
        return super.equalityRest(expr);
    }
}
