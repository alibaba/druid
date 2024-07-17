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

    @Override
    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        DB2CreateTableStatement createTable = (DB2CreateTableStatement) stmt;
        for (; ; ) {
            if (lexer.nextIfIdentifier(FnvHash.Constants.DATA)) {
                acceptIdentifier("CAPTURE");

                if (lexer.identifierEquals(FnvHash.Constants.NONE)) {
                    lexer.nextToken();
                    createTable.setDataCaptureNone(true);
                    continue;
                }

                throw new ParserException("TODO " + lexer.info());
            } else if (lexer.nextIf(Token.IN)) {
                if (lexer.nextIf(Token.DATABASE)) {
                    SQLName database = this.exprParser.name();
                    createTable.setDatabase(database);
                } else if (lexer.identifierEquals("tablespace")) {
                    throw new ParserException("TODO " + lexer.info());
                } else {
                    SQLName tablespace = this.exprParser.name();
                    createTable.setTablespace(tablespace);
                }

                continue;
            } else if (lexer.nextIfIdentifier(FnvHash.Constants.PARTITIONING)) {
                SQLPartitionByHash partitionBy = new SQLPartitionByHash();

                accept(Token.KEY);
                accept(Token.LPAREN);
                this.exprParser.exprList(partitionBy.getColumns(), partitionBy);
                accept(Token.RPAREN);
                accept(Token.USING);
                acceptIdentifier("HASHING");
                createTable.setPartitionBy(partitionBy);
                continue;
            } else if (lexer.nextIfIdentifier(FnvHash.Constants.VALIDPROC)) {
                SQLName validproc = this.exprParser.name();
                createTable.setValidproc(validproc);
                continue;
            } else if (lexer.nextIfIdentifier(FnvHash.Constants.COMPRESS)) {
                createTable.setCompress(true);
                lexer.nextIfIdentifier(FnvHash.Constants.YES);
                continue;
            } else if (lexer.nextIf(Token.INDEX)) {
                accept(Token.IN);
                SQLName indexIn = this.exprParser.name();
                createTable.setIndexIn(indexIn);
                continue;
            }
            break;
        }
    }

    protected DB2CreateTableStatement newCreateStatement() {
        return new DB2CreateTableStatement();
    }
}
