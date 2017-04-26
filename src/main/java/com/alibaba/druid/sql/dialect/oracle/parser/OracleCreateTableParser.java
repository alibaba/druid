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
package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLPartition;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionByHash;
import com.alibaba.druid.sql.ast.SQLPartitionByList;
import com.alibaba.druid.sql.ast.SQLPartitionByRange;
import com.alibaba.druid.sql.ast.SQLPartitionValue;
import com.alibaba.druid.sql.ast.SQLSubPartition;
import com.alibaba.druid.sql.ast.SQLSubPartitionBy;
import com.alibaba.druid.sql.ast.SQLSubPartitionByHash;
import com.alibaba.druid.sql.ast.SQLSubPartitionByList;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
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
                    SQLPartitionByRange partitionByRange = partitionByRange();
                    partitionClauseRest(partitionByRange);
                    stmt.setPartitioning(partitionByRange);
                    continue;
                } else if (identifierEquals("HASH")) {
                    SQLPartitionByHash partitionByHash = partitionByHash();
                    partitionClauseRest(partitionByHash);
                    stmt.setPartitioning(partitionByHash);
                    continue;
                } else if (identifierEquals("LIST")) {
                    SQLPartitionByList partitionByList = partitionByList();
                    partitionClauseRest(partitionByList);
                    stmt.setPartitioning(partitionByList);
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

    protected SQLPartitionByList partitionByList() {
        acceptIdentifier("LIST");
        SQLPartitionByList partitionByList = new SQLPartitionByList();

        accept(Token.LPAREN);
        partitionByList.setExpr(this.exprParser.expr());
        accept(Token.RPAREN);

        parsePartitionByRest(partitionByList);

        return partitionByList;
    }

    protected SQLPartitionByHash partitionByHash() {
        acceptIdentifier("HASH");
        SQLPartitionByHash partitionByHash = new SQLPartitionByHash();

        if (lexer.token() == Token.KEY) {
            lexer.nextToken();
            partitionByHash.setKey(true);
        }

        accept(Token.LPAREN);
        partitionByHash.setExpr(this.exprParser.expr());
        accept(Token.RPAREN);
        return partitionByHash;
    }

    protected SQLPartitionByRange partitionByRange() {
        acceptIdentifier("RANGE");
        accept(Token.LPAREN);
        SQLPartitionByRange clause = new SQLPartitionByRange();
        for (;;) {
            SQLName column = this.exprParser.name();
            clause.addColumn(column);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }
        accept(Token.RPAREN);

        if (lexer.token() == Token.INTERVAL) {
            lexer.nextToken();
            accept(Token.LPAREN);
            clause.setInterval(this.exprParser.expr());
            accept(Token.RPAREN);
        }

        parsePartitionByRest(clause);

        return clause;
    }

    protected void parsePartitionByRest(SQLPartitionBy clause) {
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

        if (identifierEquals("SUBPARTITION")) {
            SQLSubPartitionBy subPartitionBy = subPartitionBy();
            clause.setSubPartitionBy(subPartitionBy);
        }

        accept(Token.LPAREN);

        for (;;) {
            SQLPartition partition = parsePartition();

            clause.addPartition(partition);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        accept(Token.RPAREN);
    }

    protected SQLPartition parsePartition() {
        acceptIdentifier("PARTITION");
        SQLPartition partition = new SQLPartition();
        partition.setName(this.exprParser.name());

        SQLPartitionValue values = this.exprParser.parsePartitionValues();
        if (values != null) {
            partition.setValues(values);
        }

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (;;) {
                SQLSubPartition subPartition = parseSubPartition();

                partition.addSubPartition(subPartition);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }

            accept(Token.RPAREN);
        } else if (identifierEquals("SUBPARTITIONS")) {
            lexer.nextToken();
            SQLExpr subPartitionsCount = this.exprParser.primary();
            partition.setSubPartitionsCount(subPartitionsCount);
        }
        return partition;
    }

    protected SQLSubPartition parseSubPartition() {
        acceptIdentifier("SUBPARTITION");

        SQLSubPartition subPartition = new SQLSubPartition();
        SQLName name = this.exprParser.name();
        subPartition.setName(name);
        
        SQLPartitionValue values = this.exprParser.parsePartitionValues();
        if (values != null) {
            subPartition.setValues(values);
        }
        
        return subPartition;
    }

    protected void partitionClauseRest(SQLPartitionBy clause) {
        if (identifierEquals("PARTITIONS")) {
            lexer.nextToken();

            SQLIntegerExpr countExpr = this.exprParser.integerExpr();
            clause.setPartitionsCount(countExpr);
        }

        if (lexer.token() == Token.STORE) {
            lexer.nextToken();
            accept(Token.IN);
            accept(Token.LPAREN);
            this.exprParser.names(clause.getStoreIn(), clause);
            accept(Token.RPAREN);
        }
    }

    protected SQLSubPartitionBy subPartitionBy() {
        lexer.nextToken();
        accept(Token.BY);

        if (identifierEquals("HASH")) {
            lexer.nextToken();
            accept(Token.LPAREN);

            SQLSubPartitionByHash byHash = new SQLSubPartitionByHash();
            SQLExpr expr = this.exprParser.expr();
            byHash.setExpr(expr);
            accept(Token.RPAREN);

            return byHash;
        } else if (identifierEquals("LIST")) {
            lexer.nextToken();
            accept(Token.LPAREN);

            SQLSubPartitionByList byList = new SQLSubPartitionByList();
            SQLName column = this.exprParser.name();
            byList.setColumn(column);
            accept(Token.RPAREN);

            if (identifierEquals("SUBPARTITION")) {
                lexer.nextToken();
                acceptIdentifier("TEMPLATE");
                accept(Token.LPAREN);
                
                for (;;) {
                    SQLSubPartition subPartition = parseSubPartition();
                    subPartition.setParent(byList);
                    byList.getSubPartitionTemplate().add(subPartition);
                    
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }

            return byList;
        }

        throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
    }

}
