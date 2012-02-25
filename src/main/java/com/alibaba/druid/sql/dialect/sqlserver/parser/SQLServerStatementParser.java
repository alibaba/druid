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
package com.alibaba.druid.sql.dialect.sqlserver.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleDeleteParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleSelectParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class SQLServerStatementParser extends SQLStatementParser {

    public SQLServerStatementParser(String sql){
        super(new SQLServerLexer(sql));
        this.lexer.nextToken();
    }

    public SQLServerStatementParser(Lexer lexer){
        super(lexer);
    }

    @Override
    public void parseStatementList(List<SQLStatement> statementList) throws ParserException {
        for (;;) {
            if (lexer.token() == Token.EOF) {
                return;
            }

            if (lexer.token() == (Token.SEMI)) {
                lexer.nextToken();
                continue;
            }

            if (lexer.token() == (Token.SELECT)) {
                statementList.add(new SQLSelectStatement(new SQLServerSelectParser(this.lexer).select()));
                continue;
            }

            if (lexer.token() == (Token.UPDATE)) {
                statementList.add(parseUpdateStatement());
                continue;
            }

            if (lexer.token() == (Token.CREATE)) {
                lexer.nextToken();

                // if ((this.tokenList.lookup(1).equals(Token.ViewToken)) ||
                // (this.tokenList.lookup(3).equals(Token.ViewToken))
                // || (this.tokenList.lookup(4).equals(Token.ViewToken)) ||
                // (this.tokenList.lookup(5).equals(Token.ViewToken))) {
                // statementList.add(parseCreateView());
                // }
                //
                // if ((this.tokenList.lookup(1).equals(Token.TableToken)) ||
                // (this.tokenList.lookup(3).equals(Token.TableToken))) {
                // statementList.add(parseOracleCreateTable());
                // }

                throw new ParserException("TODO");
            }

            if (lexer.token() == Token.INSERT) {
                statementList.add(parseInsert());
                continue;
            }

            if (lexer.token() == (Token.DELETE)) {
                statementList.add(new OracleDeleteParser(this.lexer).parseDelete());
                continue;
            }

            if (lexer.token() == (Token.SLASH)) {
                lexer.nextToken();
                statementList.add(new OraclePLSQLCommitStatement());
                continue;
            }

            if (lexer.token() == Token.ALTER) {
                throw new ParserException("TODO");
            }

            if (lexer.token() == Token.WITH) {
                statementList.add(new SQLSelectStatement(new OracleSelectParser(this.lexer).select()));
                continue;
            }

            if (identifierEquals("CALL")) {
                statementList.add(this.parseCall());
                continue;
            }

            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
        }
    }

}
