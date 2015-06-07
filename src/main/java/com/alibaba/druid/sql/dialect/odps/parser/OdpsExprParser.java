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
package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsUDTFSQLSelectItem;
import com.alibaba.druid.sql.parser.Lexer;
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

        return new SQLSelectItem(expr, alias);
    }
}
