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
package com.alibaba.druid.sql.dialect.presto.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.presto.ast.stmt.PrestoCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

/**
 * presto 的create table解析器
 *
 * @author yanlong
 * @since 2023-03-13
 */
public class PrestoCreateTableParser extends SQLCreateTableParser {
    public PrestoCreateTableParser(String sql) {
        super(sql);
    }

    public PrestoCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLCreateTableStatement parseCreateTable() {
        List<String> comments = null;
        if (lexer.isKeepComments() && lexer.hasComment()) {
            comments = lexer.readAndResetComments();
        }

        SQLCreateTableStatement stmt = parseCreateTable(true);
        if (comments != null) {
            stmt.addBeforeComment(comments);
        }

        return stmt;
    }

    public SQLCreateTableStatement parseCreateTable(boolean acceptCreate) {
        PrestoCreateTableStatement createTable = newCreateStatement();
        createTable.setDbType(getDbType());

        if (acceptCreate) {
            if (lexer.hasComment() && lexer.isKeepComments()) {
                createTable.addBeforeComment(lexer.readAndResetComments());
            }

            accept(Token.CREATE);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IF || lexer.identifierEquals(FnvHash.Constants.IF)) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            createTable.setIfNotExiists(true);
        }

        createTable.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (; ; ) {
                Token token = lexer.token();
                if (lexer.token() == Token.LIKE) {
                    lexer.nextToken();
                    SQLTableLike tableLike = new SQLTableLike();
                    tableLike.setTable(new SQLExprTableSource(this.exprParser.name()));
                    tableLike.setParent(createTable);
                    createTable.getTableElementList().add(tableLike);

                    if (lexer.identifierEquals(FnvHash.Constants.INCLUDING)) {
                        lexer.nextToken();
                        acceptIdentifier("PROPERTIES");
                        tableLike.setIncludeProperties(true);
                    } else if (lexer.identifierEquals(FnvHash.Constants.EXCLUDING)) {
                        lexer.nextToken();
                        acceptIdentifier("PROPERTIES");
                        tableLike.setExcludeProperties(true);
                    }
                } else if (token == Token.IDENTIFIER //
                        || token == Token.LITERAL_ALIAS) {
                    SQLColumnDefinition column = this.exprParser.parseColumn(createTable);
                    column.setParent(createTable);
                    createTable.getTableElementList().add(column);
                } else {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    createTable.getTableElementList().add(column);
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();

                    if (lexer.token() == Token.RPAREN) { // compatible for sql server
                        break;
                    }
                    continue;
                }

                break;
            }

            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLExpr comment = this.exprParser.expr();
            createTable.setComment(comment);
        }

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            accept(Token.LPAREN);
            parseAssignItems(createTable.getTableOptions(), createTable, false);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.AS) {
            lexer.nextToken();
            SQLSelect select = this.createSQLSelectParser().select();
            createTable.setSelect(select);
        }

        parseCreateTableRest(createTable);

        return createTable;
    }

    protected PrestoCreateTableStatement newCreateStatement() {
        return new PrestoCreateTableStatement();
    }
}
