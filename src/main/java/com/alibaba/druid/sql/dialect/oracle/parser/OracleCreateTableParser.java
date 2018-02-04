/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
import com.alibaba.druid.sql.ast.SQLPartition;
import com.alibaba.druid.sql.ast.SQLPartitionByHash;
import com.alibaba.druid.sql.ast.SQLPartitionByList;
import com.alibaba.druid.sql.ast.SQLPartitionByRange;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.statement.SQLExternalRecordFormat;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement.DeferredSegmentCreation;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSupplementalIdKey;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSupplementalLogGrp;
import com.alibaba.druid.sql.parser.*;

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

    public OracleCreateTableStatement parseCreateTable(boolean acceptCreate) {
        OracleCreateTableStatement stmt = (OracleCreateTableStatement) super.parseCreateTable(acceptCreate);

        if (lexer.token() == Token.OF) {
            lexer.nextToken();
            stmt.setOf(this.exprParser.name());

            if (lexer.identifierEquals("OIDINDEX")) {
                lexer.nextToken();

                OracleCreateTableStatement.OIDIndex oidIndex = new OracleCreateTableStatement.OIDIndex();

                if (lexer.token() != Token.LPAREN) {
                    oidIndex.setName(this.exprParser.name());
                }
                accept(Token.LPAREN);
                this.getExprParser().parseSegmentAttributes(oidIndex);
                accept(Token.RPAREN);

                stmt.setOidIndex(oidIndex);
            }
        }

        for (;;) {
            this.getExprParser().parseSegmentAttributes(stmt);

            if (lexer.identifierEquals("IN_MEMORY_METADATA")) {
                lexer.nextToken();
                stmt.setInMemoryMetadata(true);
                continue;
            } else if (lexer.identifierEquals("CURSOR_SPECIFIC_SEGMENT")) {
                lexer.nextToken();
                stmt.setCursorSpecificSegment(true);
                continue;
            } else if (lexer.identifierEquals("NOPARALLEL")) {
                lexer.nextToken();
                stmt.setParallel(false);
                continue;
            } else if (lexer.identifierEquals("PARALLEL")) {
                lexer.nextToken();
                stmt.setParallel(true);
                continue;
            } else if (lexer.token() == Token.CACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.TRUE);
                continue;
            } else if (lexer.token() == Token.NOCACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.FALSE);
                continue;
            } else if (lexer.token() == Token.ENABLE) {
                lexer.nextToken();
                if (lexer.token() == Token.ROW) {
                    lexer.nextToken();
                    acceptIdentifier("MOVEMENT");
                    stmt.setEnableRowMovement(Boolean.TRUE);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
                //stmt.setEnable(Boolean.TRUE);
                continue;
            } else if (lexer.token() == Token.DISABLE) {
                lexer.nextToken();
                if (lexer.token() == Token.ROW) {
                    lexer.nextToken();
                    acceptIdentifier("MOVEMENT");
                    stmt.setEnableRowMovement(Boolean.FALSE);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
                //stmt.setEnable(Boolean.FALSE);
                continue;
            } else if (lexer.token() == Token.ON) {
                lexer.nextToken();
                accept(Token.COMMIT);

                if (lexer.identifierEquals("PRESERVE")) {
                    lexer.nextToken();
                    acceptIdentifier("ROWS");
                    stmt.setOnCommitPreserveRows(true);
                } else {
                    accept(Token.DELETE);
                    acceptIdentifier("ROWS");
                    stmt.setOnCommitDeleteRows(true);
                }
                continue;
            } else if (lexer.identifierEquals("STORAGE")) {
                OracleStorageClause storage = ((OracleExprParser) this.exprParser).parseStorage();
                stmt.setStorage(storage);
                continue;
            } else if (lexer.identifierEquals("ORGANIZATION")) {
                parseOrganization(stmt);
                continue;
            } else if (lexer.identifierEquals("CLUSTER")) {
                lexer.nextToken();
                SQLName cluster = this.exprParser.name();
                stmt.setCluster(cluster);
                accept(Token.LPAREN);
                this.exprParser.names(stmt.getClusterColumns(), cluster);
                accept(Token.RPAREN);
                continue;
//            } else if (lexer.token() == Token.STORAGE) {
//                OracleStorageClause storage = ((OracleExprParser) this.exprParser).parseStorage();
//                stmt.setStorage(storage);
//                continue;
            } else if (lexer.identifierEquals("MONITORING")) {
                lexer.nextToken();
                stmt.setMonitoring(true);
                continue;
            } else if (lexer.token() == Token.LOB) {
                OracleLobStorageClause lobStorage = ((OracleExprParser) this.exprParser).parseLobStorage();
                stmt.setLobStorage(lobStorage);
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
            } else if (lexer.token() == Token.PARTITION) {
                lexer.nextToken();

                accept(Token.BY);

                if (lexer.identifierEquals("RANGE")) {
                    SQLPartitionByRange partitionByRange = this.getExprParser().partitionByRange();
                    this.getExprParser().partitionClauseRest(partitionByRange);
                    stmt.setPartitioning(partitionByRange);
                    continue;
                } else if (lexer.identifierEquals("HASH")) {
                    SQLPartitionByHash partitionByHash = this.getExprParser().partitionByHash();
                    this.getExprParser().partitionClauseRest(partitionByHash);

                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        for (;;) {
                            SQLPartition partition = this.getExprParser().parsePartition();
                            partitionByHash.addPartition(partition);
                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            } else if (lexer.token() == Token.RPAREN) {
                                lexer.nextToken();
                                break;
                            }
                            throw new ParserException("TODO : " + lexer.info());
                        }
                    }
                    stmt.setPartitioning(partitionByHash);
                    continue;
                } else if (lexer.identifierEquals("LIST")) {
                    SQLPartitionByList partitionByList = this.getExprParser().partitionByList();
                    this.getExprParser().partitionClauseRest(partitionByList);
                    stmt.setPartitioning(partitionByList);
                    continue;
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
            }
            break;
        }

        if (lexer.token() == Token.AS) {
            lexer.nextToken();

            SQLSelect select = new OracleSelectParser(exprParser).select();
            stmt.setSelect(select);
        }

        return stmt;
    }

    private void parseOrganization(OracleCreateTableStatement stmt) {
        OracleCreateTableStatement.Organization organization = new OracleCreateTableStatement.Organization();
        acceptIdentifier("ORGANIZATION");
        if (lexer.token() == Token.INDEX) {
            lexer.nextToken();
            organization.setType("INDEX");
            this.getExprParser().parseSegmentAttributes(organization);

            // index_org_table_clause http://docs.oracle.com/cd/B19306_01/server.102/b14200/statements_7002.htm#i2129638
            if (lexer.identifierEquals("PCTTHRESHOLD")) {
                lexer.nextToken();

                if (lexer.token() == Token.LITERAL_INT) {
                    int pctthreshold = ((SQLNumericLiteralExpr) this.exprParser.primary()).getNumber().intValue();
                    organization.setPctthreshold(pctthreshold);
                }
            }
        } else if (lexer.identifierEquals("HEAP")) {
            lexer.nextToken();
            organization.setType("HEAP");
            this.getExprParser().parseSegmentAttributes(organization);
        } else if (lexer.identifierEquals("EXTERNAL")) {
            lexer.nextToken();
            organization.setType("EXTERNAL");
            accept(Token.LPAREN);

            if (lexer.identifierEquals("TYPE")) {
                lexer.nextToken();
                organization.setExternalType(this.exprParser.name());
            }

            accept(Token.DEFAULT);
            acceptIdentifier("DIRECTORY");

            organization.setExternalDirectory(this.exprParser.expr());

            if (lexer.identifierEquals("ACCESS")) {
                lexer.nextToken();
                acceptIdentifier("PARAMETERS");

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();

                    SQLExternalRecordFormat recordFormat = new SQLExternalRecordFormat();

                    if (lexer.identifierEquals("RECORDS")) {
                        lexer.nextToken();


                        if (lexer.identifierEquals("DELIMITED")) {
                            lexer.nextToken();
                            accept(Token.BY);

                            if (lexer.identifierEquals("NEWLINE")) {
                                lexer.nextToken();
                                recordFormat.setDelimitedBy(new SQLIdentifierExpr("NEWLINE"));
                            } else {
                                throw new ParserException("TODO " + lexer.info());
                            }
                        } else {
                            throw new ParserException("TODO " + lexer.info());
                        }
                    }

                    if (lexer.identifierEquals("FIELDS")) {
                        lexer.nextToken();

                        if (lexer.identifierEquals("TERMINATED")) {
                            lexer.nextToken();
                            accept(Token.BY);
                            recordFormat.setTerminatedBy(this.exprParser.primary());
                        } else {
                            throw new ParserException("TODO " + lexer.info());
                        }
                    }

                    organization.setExternalDirectoryRecordFormat(recordFormat);
                    accept(Token.RPAREN);
                } else if (lexer.token() == Token.USING) {
                    lexer.nextToken();
                    acceptIdentifier("CLOB");
                    throw new ParserException("TODO " + lexer.info());
                }
            }

            acceptIdentifier("LOCATION");
            accept(Token.LPAREN);
            this.exprParser.exprList(organization.getExternalDirectoryLocation(), organization);
            accept(Token.RPAREN);

            accept(Token.RPAREN);

            if (lexer.token() == Token.REJECT) {
                lexer.nextToken();
                accept(Token.LIMIT);

                organization.setExternalRejectLimit(this.exprParser.primary());
            }
            //
        } else {
            throw new ParserException("TODO " + lexer.info());
        }
        stmt.setOrganization(organization);
    }

    protected void parseCreateTableSupplementalLogingProps(SQLCreateTableStatement stmt) {
        acceptIdentifier("SUPPLEMENTAL");
        acceptIdentifier("LOG");

        if (lexer.token() == Token.GROUP) {
            lexer.nextToken();

            OracleSupplementalLogGrp logGrp = new OracleSupplementalLogGrp();
            logGrp.setGroup(this.exprParser.name());

            accept(Token.LPAREN);
            for (;;) {
                SQLName column = this.exprParser.name();

                if (lexer.identifierEquals("NO")) {
                    lexer.nextToken();
                    acceptIdentifier("LOG");
                    column.putAttribute("NO LOG", Boolean.TRUE);
                }

                logGrp.addColumn(column);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                if (lexer.token() == Token.RPAREN) {
                    break;
                }

                throw new ParserException("TODO " + lexer.info());
            }
            accept(Token.RPAREN);

            if (lexer.identifierEquals("ALWAYS")) {
                lexer.nextToken();
                logGrp.setAlways(true);
            }

            logGrp.setParent(stmt);
            stmt.getTableElementList().add(logGrp);
        } else {
            acceptIdentifier("DATA");

            OracleSupplementalIdKey idKey = new OracleSupplementalIdKey();
            accept(Token.LPAREN);
            for (;;) {
                if (lexer.token() == Token.ALL) {
                    lexer.nextToken();
                    idKey.setAll(true);
                } else if (lexer.token() == Token.PRIMARY) {
                    lexer.nextToken();
                    accept(Token.KEY);
                    idKey.setPrimaryKey(true);
                } else if (lexer.token() == Token.UNIQUE) {
                    lexer.nextToken();

                    if (lexer.token() == Token.INDEX) {
                        lexer.nextToken();
                        idKey.setUniqueIndex(true);
                    } else {
                        idKey.setUnique(true);
                    }
                } else if (lexer.token() == Token.FOREIGN) {
                    lexer.nextToken();
                    accept(Token.KEY);
                    idKey.setForeignKey(true);
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                if (lexer.token() == Token.RPAREN) {
                    break;
                }

                throw new ParserException("TODO " + lexer.info());
            }
            accept(Token.RPAREN);
            acceptIdentifier("COLUMNS");
            idKey.setParent(stmt);
            stmt.getTableElementList().add(idKey);
        }
    }

    public OracleExprParser getExprParser() {
        return (OracleExprParser) exprParser;
    }


}
