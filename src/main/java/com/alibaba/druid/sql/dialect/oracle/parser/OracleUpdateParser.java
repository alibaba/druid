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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleUpdateParser extends SQLStatementParser {

    public OracleUpdateParser(String sql) throws ParserException{
        super(new OracleExprParser(sql));
    }

    public OracleUpdateParser(Lexer lexer){
        super(new OracleExprParser(lexer));
    }

    public OracleUpdateStatement parseUpdateStatement() throws ParserException {
        OracleUpdateStatement update = new OracleUpdateStatement();
        
        if (lexer.token() == Token.UPDATE) {
            lexer.nextToken();

            parseHints(update);

            if (identifierEquals("ONLY")) {
                update.setOnly(true);
            }

            SQLTableSource tableSource = this.exprParser.createSelectParser().parseTableSource();
            update.setTableSource(tableSource);

            if ((update.getAlias() == null) || (update.getAlias().length() == 0)) {
                update.setAlias(as());
            }
        }

        parseSet(update);

        parseWhere(update);

        parseReturn(update);

        parseErrorLoging(update);

        return update;
    }

    private void parseErrorLoging(OracleUpdateStatement update) throws ParserException {
        if (identifierEquals("LOG")) {
            throw new ParserException("TODO");
        }
    }

    private void parseReturn(OracleUpdateStatement update) throws ParserException {
        if (identifierEquals("RETURN") || lexer.token() == Token.RETURNING) {
            lexer.nextToken();

            for (;;) {
                SQLExpr item = this.exprParser.expr();
                update.getReturning().add(item);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }

            accept(Token.INTO);

            for (;;) {
                SQLExpr item = this.exprParser.expr();
                update.getReturningInto().add(item);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
        }
    }

    private void parseHints(OracleUpdateStatement update) throws ParserException {
        if (lexer.token() == Token.HINT) {
            throw new ParserException("TODO");
        }
    }

    private void parseWhere(OracleUpdateStatement update) throws ParserException {
        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            update.setWhere(this.exprParser.expr());
        }
    }

    private void parseSet(OracleUpdateStatement update) throws ParserException {
        accept(Token.SET);

        for (;;) {
            SQLUpdateSetItem item = new SQLUpdateSetItem();

            if (lexer.token() == (Token.LPAREN)) {
                lexer.nextToken();
                SQLListExpr list = new SQLListExpr();
                this.exprParser.exprList(list.getItems());
                accept(Token.RPAREN);
                item.setColumn(list);
            } else {
                item.setColumn(this.exprParser.primary());
            }
            accept(Token.EQ);
            item.setValue(this.exprParser.expr());
            update.getItems().add(item);

            if (lexer.token() != Token.COMMA) {
                break;
            }

            lexer.nextToken();
        }
    }
}
