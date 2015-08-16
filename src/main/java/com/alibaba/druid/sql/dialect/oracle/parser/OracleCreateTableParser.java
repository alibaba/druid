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
package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OraclePartitionByRangeClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleRangeValuesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement.DeferredSegmentCreation;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelect;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleCreateTableParser extends SQLCreateTableParser {

    public OracleCreateTableParser(Lexer lexer){
        super(new OracleExprParser(lexer));
    }

    public OracleCreateTableParser(String sql){
        super(new OracleExprParser(sql));
    }

    protected OracleCreateTableStatement newCreateStatement() {
        return new OracleCreateTableStatement();
    }

    public OracleCreateTableStatement parseCrateTable(boolean acceptCreate) {
        OracleCreateTableStatement stmt = (OracleCreateTableStatement) super.parseCrateTable(acceptCreate);

        for (;;) {
            if (lexer.token() == Token.TABLESPACE) {
                lexer.nextToken();
                stmt.setTablespace(this.exprParser.name());
                continue;
            } else if (identifierEquals("IN_MEMORY_METADATA")) {
                lexer.nextToken();
                stmt.setInMemoryMetadata(true);
                continue;
            } else if (identifierEquals("CURSOR_SPECIFIC_SEGMENT")) {
                lexer.nextToken();
                stmt.setCursorSpecificSegment(true);
                continue;
            } else if (identifierEquals("NOPARALLEL")) {
                lexer.nextToken();
                stmt.setParallel(false);
                continue;
            } else if (lexer.token() == Token.LOGGING) {
                lexer.nextToken();
                stmt.setLogging(Boolean.TRUE);
                continue;
            } else if (lexer.token() == Token.CACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.TRUE);
                continue;
            } else if (lexer.token() == Token.NOCACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.FALSE);
                continue;
            } else if (lexer.token() == Token.NOCOMPRESS) {
                lexer.nextToken();
                stmt.setCompress(Boolean.FALSE);
                continue;
            } else if (lexer.token() == Token.ON) {
                lexer.nextToken();
                accept(Token.COMMIT);
                stmt.setOnCommit(true);
                continue;
            } else if (identifierEquals("PRESERVE")) {
                lexer.nextToken();
                acceptIdentifier("ROWS");
                stmt.setPreserveRows(true);
                continue;
            } else if (identifierEquals("STORAGE")) {
                OracleStorageClause storage = ((OracleExprParser) this.exprParser).parseStorage();
                stmt.setStorage(storage);
                continue;
            } else if (identifierEquals("organization")) {
                lexer.nextToken();
                accept(Token.INDEX);
                stmt.setOrganizationIndex(true);
                continue;
            } else if (lexer.token() == Token.PCTFREE) {
                lexer.nextToken();
                stmt.setPtcfree(this.exprParser.expr());
                continue;
            } else if (identifierEquals("PCTUSED")) {
                lexer.nextToken();
                stmt.setPctused(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.STORAGE) {
                OracleStorageClause storage = ((OracleExprParser) this.exprParser).parseStorage();
                stmt.setStorage(storage);
                continue;
            } else if (lexer.token() == Token.LOB) {
                OracleLobStorageClause lobStorage = ((OracleExprParser) this.exprParser).parseLobStorage();
                stmt.setLobStorage(lobStorage);
                continue;
            } else if (lexer.token() == Token.INITRANS) {
                lexer.nextToken();
                stmt.setInitrans(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.MAXTRANS) {
                lexer.nextToken();
                stmt.setMaxtrans(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.SEGMENT) {
                lexer.nextToken();
                accept(Token.CREATION);
                if (lexer.token() == Token.IMMEDIATE) {
                    lexer.nextToken();
                    stmt.setDeferredSegmentCreation(DeferredSegmentCreation.IMMEDIATE);
                } else {
                    accept(Token.DEFERRED);
                    stmt.setDeferredSegmentCreation(DeferredSegmentCreation.DEFERRED);
                }
                continue;
            } else if (identifierEquals("PARTITION")) {
                lexer.nextToken();
                accept(Token.BY);

                if (identifierEquals("RANGE")) {
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    OraclePartitionByRangeClause clause = new OraclePartitionByRangeClause();
                    for (;;) {
                        SQLName column = this.exprParser.name();
                        clause.getColumns().add(column);

                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }

                        break;
                    }
                    accept(Token.RPAREN);

                    if (identifierEquals("INTERVAL")) {
                        lexer.nextToken();
                        clause.setInterval(this.exprParser.expr());
                    }

                    if (lexer.token() == Token.STORE) {
                        lexer.nextToken();
                        accept(Token.IN);
                        accept(Token.LPAREN);
                        for (;;) {
                            SQLName tablespace = this.exprParser.name();
                            clause.getStoreIn().add(tablespace);

                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }

                            break;
                        }
                        accept(Token.RPAREN);
                    }

                    accept(Token.LPAREN);

                    for (;;) {
                        acceptIdentifier("PARTITION");
                        OracleRangeValuesClause range = new OracleRangeValuesClause();
                        range.setName(this.exprParser.name());

                        accept(Token.VALUES);
                        acceptIdentifier("LESS");
                        acceptIdentifier("THAN");

                        accept(Token.LPAREN);
                        for (;;) {
                            SQLExpr rangeValue = this.exprParser.expr();
                            range.getValues().add(rangeValue);

                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }

                            break;
                        }
                        accept(Token.RPAREN);

                        clause.getRanges().add(range);

                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }

                        break;
                    }

                    accept(Token.RPAREN);

                    stmt.setPartitioning(clause);
                    continue;
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
            }
            break;
        }

        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            OracleSelect select = new OracleSelectParser(exprParser).select();
            stmt.setSelect(select);
        }

        return stmt;
    }

}
