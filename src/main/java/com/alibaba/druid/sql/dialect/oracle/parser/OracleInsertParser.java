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

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleInsertParser extends SQLStatementParser {

    public OracleInsertParser(String sql) throws ParserException{
        super(sql);
    }

    public OracleInsertParser(Lexer lexer){
        super(lexer);
    }

    public SQLStatement parseInsert() throws ParserException {
        throw new ParserException("TODO");
    }

    public OracleInsertStatement parseOracleInsert() throws ParserException {
        accept(Token.INSERT);

        OracleInsertStatement insertStatement = new OracleInsertStatement();

        parseHints(insertStatement);

        if (lexer.token() == Token.INTO) {
            OracleInsertStatement.SigleTableInert sigleTableInsert = new OracleInsertStatement.SigleTableInert();
            sigleTableInsert.setInto(parseInto());

            if (lexer.token() == Token.VALUES) {
                OracleInsertStatement.IntoValues values = new OracleInsertStatement.IntoValues();

                lexer.nextToken();
                accept(Token.LPAREN);
                this.exprParser.exprList(values.getValues());
                accept(Token.RPAREN);

                sigleTableInsert.setSource(values);
            }

            if (identifierEquals("LOG")) {
                throw new ParserException("TODO");
            }

            insertStatement.setInsert(sigleTableInsert);
        } else {
            if (lexer.token() == Token.ALL) {
                throw new ParserException("TODO");
            }
            throw new ParserException("syntax error");
        }

        return insertStatement;
    }

    private OracleInsertStatement.Into parseInto() throws ParserException {
        accept(Token.INTO);

        OracleInsertStatement.Into into = new OracleInsertStatement.Into();
        into.setTarget(this.exprParser.expr());
        into.setAlias(as());

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            this.exprParser.exprList(into.getColumns());
            accept(Token.RPAREN);
        }

        return into;
    }

    private void parseHints(OracleInsertStatement parseInsert) throws ParserException {
        if (lexer.token() == Token.HINT) throw new ParserException("TODO");
    }
}
