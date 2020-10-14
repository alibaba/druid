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
package com.alibaba.druid.sql.dialect.db2.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLPartitionByHash;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2CreateTableStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class DB2CreateTableParser extends SQLCreateTableParser {
    public DB2CreateTableParser(String sql) {
        super(new DB2ExprParser(sql));
    }

    public DB2CreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLCreateTableStatement parseCreateTable(boolean acceptCreate) {
        DB2CreateTableStatement createTable = newCreateStatement();

        if (acceptCreate) {
            if (lexer.hasComment() && lexer.isKeepComments()) {
                createTable.addBeforeComment(lexer.readAndResetComments());
            }

            accept(Token.CREATE);
        }

        if (lexer.identifierEquals("GLOBAL")) {
            lexer.nextToken();

            if (lexer.identifierEquals("TEMPORARY")) {
                lexer.nextToken();
                createTable.setType(SQLCreateTableStatement.Type.GLOBAL_TEMPORARY);
            } else {
                throw new ParserException("syntax error " + lexer.info());
            }
        } else if (lexer.token() == Token.IDENTIFIER && lexer.stringVal().equalsIgnoreCase("LOCAL")) {
            lexer.nextToken();
            if (lexer.token() == Token.IDENTIFIER && lexer.stringVal().equalsIgnoreCase("TEMPORAY")) {
                lexer.nextToken();
                createTable.setType(SQLCreateTableStatement.Type.LOCAL_TEMPORARY);
            } else {
                throw new ParserException("syntax error. " + lexer.info());
            }
        }

        accept(Token.TABLE);

        createTable.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (; ; ) {
                Token token = lexer.token();
                if (token == Token.IDENTIFIER //
                        || token == Token.LITERAL_ALIAS) {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    createTable.getTableElementList().add(column);
                } else if (token == Token.PRIMARY //
                        || token == Token.UNIQUE //
                        || token == Token.CHECK //
                        || token == Token.CONSTRAINT
                        || token == Token.FOREIGN) {
                    SQLConstraint constraint = this.exprParser.parseConstaint();
                    constraint.setParent(createTable);
                    createTable.getTableElementList().add((SQLTableElement) constraint);
                } else if (token == Token.TABLESPACE) {
                    throw new ParserException("TODO "  + lexer.info());
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

            if (lexer.identifierEquals("INHERITS")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                SQLName inherits = this.exprParser.name();
                createTable.setInherits(new SQLExprTableSource(inherits));
                accept(Token.RPAREN);
            }
        }

        if (lexer.token() == Token.AS) {
            lexer.nextToken();
            SQLSelect select = this.createSQLSelectParser().select();
            createTable.setSelect(select);
        }

        for (;;) {
            if (lexer.identifierEquals(FnvHash.Constants.DATA)) {
                lexer.nextToken();
                acceptIdentifier("CAPTURE");

                if (lexer.identifierEquals(FnvHash.Constants.NONE)) {
                    lexer.nextToken();
                    createTable.setDataCaptureNone(true);
                    continue;
                }

                throw new ParserException("TODO "  + lexer.info());
            } else if (lexer.token() == Token.IN) {
                lexer.nextToken();

                if (lexer.token() == Token.DATABASE) {
                    lexer.nextToken();
                    SQLName database = this.exprParser.name();
                    createTable.setDatabase(database);
                } else if (lexer.identifierEquals("tablespace")) {
                    throw new ParserException("TODO "  + lexer.info());
                } else {
                    SQLName tablespace = this.exprParser.name();
                    createTable.setTablespace(tablespace);
                }

                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.PARTITIONING)) {
                SQLPartitionByHash partitionBy = new SQLPartitionByHash();

                lexer.nextToken();
                accept(Token.KEY);
                accept(Token.LPAREN);
                this.exprParser.exprList(partitionBy.getColumns(), partitionBy);
                accept(Token.RPAREN);
                accept(Token.USING);
                acceptIdentifier("HASHING");
                createTable.setPartitioning(partitionBy);
                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.VALIDPROC)) {
                lexer.nextToken();
                SQLName validproc = this.exprParser.name();
                createTable.setValidproc(validproc);
                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.COMPRESS)) {
                lexer.nextToken();
                createTable.setCompress(true);

                if (lexer.identifierEquals(FnvHash.Constants.YES)) {
                    lexer.nextToken();
                }
                continue;
            } else if (lexer.token() == Token.INDEX) {
                lexer.nextToken();
                accept(Token.IN);
                SQLName indexIn = this.exprParser.name();
                createTable.setIndexIn(indexIn);
                continue;
            }
            break;
        }

        return createTable;
    }

    protected DB2CreateTableStatement newCreateStatement() {
        return new DB2CreateTableStatement();
    }
}
