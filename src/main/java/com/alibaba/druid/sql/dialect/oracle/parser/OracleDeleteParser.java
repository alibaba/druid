/*
 * Copyright 2011 Alibaba Group.
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

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleDeleteParser extends SQLStatementParser {

    public OracleDeleteParser(String sql){
        super(sql);
    }

    public OracleDeleteParser(Lexer lexer){
        super(lexer);
    }

    public OracleDeleteStatement parseDelete() throws ParserException {
        accept(Token.DELETE);

        OracleDeleteStatement deleteStatement = new OracleDeleteStatement();

        parseHints(deleteStatement);

        if (lexer.token() == (Token.FROM)) {
            lexer.nextToken();
        }

        if (identifierEquals("ONLY")) {
            lexer.nextToken();
            accept(Token.LPAREN);

            SQLName tableName = exprParser.name();
            deleteStatement.setTableName(tableName);

            accept(Token.RPAREN);
        } else {
            SQLName tableName = exprParser.name();
            deleteStatement.setTableName(tableName);
        }

        deleteStatement.setAlias(as());

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            deleteStatement.setWhere(this.exprParser.expr());
        }

        if (identifierEquals("RETURN") || identifierEquals("RETURNING")) {
            throw new ParserException("TODO");
        }

        if (identifierEquals("LOG")) {
            throw new ParserException("TODO");
        }

        return deleteStatement;
    }

    private void parseHints(OracleDeleteStatement parseInsert) throws ParserException {
        if (lexer.token() == Token.HINT) throw new ParserException("TODO");
    }
}
