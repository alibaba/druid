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

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateViewStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleStatementParser extends SQLStatementParser {

    public OracleStatementParser(String sql){
        super(sql);
    }

    public OracleStatementParser(Lexer lexer){
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
                statementList.add(new SQLSelectStatement(new OracleSelectParser(this.lexer).select()));
                continue;
            }

            if (lexer.token() == (Token.UPDATE)) {
                statementList.add(new OracleUpdateParser(this.lexer).parseUpdate());
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
                statementList.add(new OracleInsertParser(this.lexer).parseInsert());
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

            throw new ParserException("TODO");
        }
    }

    public OracleCreateTableStatement parseOracleCreateTable() throws ParserException {
        // OracleCreateTableParser parser = new OracleCreateTableParser(this.tokenList);
        // return parser.parseCrateTable();
        throw new ParserException("TODO");
    }

    public OracleCreateViewStatement parseCreateView() throws ParserException {
        OracleCreateViewStatement createView = new OracleCreateViewStatement();

        accept(Token.CREATE);
        if (lexer.token() == (Token.OR)) {
            lexer.nextToken();

            if (identifierEquals("REPLACE")) lexer.nextToken();
            else {
                throw new ParserException("syntax error");
            }

            createView.setReplace(true);
        }

        if (identifierEquals("NO")) {
            lexer.nextToken();

            if (identifierEquals("FORCE")) lexer.nextToken();
            else {
                throw new ParserException("syntax error");
            }

            createView.setForce(Boolean.FALSE);
        }

        if (identifierEquals("FORCE")) {
            lexer.nextToken();
            createView.setForce(Boolean.TRUE);
        }

        accept(Token.VIEW);

        // createView.setName(new OracleExprParser(this.lexer).name());
        //
        //
        // if (this.tokenList.lookup(1).equals(Token.OpenBraceToken)) {
        // throw new ParserException("TODO");
        // }
        //
        // if (this.tokenList.lookup(1).equals(Token.OfToken)) {
        // throw new ParserException("TODO");
        // }
        //
        // accept(Token.AsToken);
        //
        // createView.setSubQuery(new OracleSelectParser(this.tokenList).select());
        // return createView;

        throw new ParserException("TODO");
    }
}
