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

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLNumericLiteralExpr;
import com.alibaba.druid.sql.ast.statement.SQLExternalRecordFormat;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLTableElement;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleLobStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleStorageClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement.DeferredSegmentCreation;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

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

            if (lexer.identifierEquals(FnvHash.Constants.IN_MEMORY_METADATA)) {
                lexer.nextToken();
                stmt.setInMemoryMetadata(true);
                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.CURSOR_SPECIFIC_SEGMENT)) {
                lexer.nextToken();
                stmt.setCursorSpecificSegment(true);
                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.NOPARALLEL)) {
                lexer.nextToken();
                stmt.setParallel(false);
                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.PARALLEL)) {
                lexer.nextToken();
                stmt.setParallel(true);

                if (lexer.token() == Token.LITERAL_INT) {
                    stmt.setParallelValue(this.exprParser.primary());
                }
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
            } else if (lexer.identifierEquals(FnvHash.Constants.CLUSTER)) {
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
            } else if (lexer.identifierEquals(FnvHash.Constants.INCLUDING)) {
                lexer.nextToken();
                this.exprParser.names(stmt.getIncluding(), stmt);
                acceptIdentifier("OVERFLOW");
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
            } else if (lexer.token() == Token.COLUMN) {
                lexer.nextToken();
                SQLName name = this.exprParser.name();
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                }

                if (lexer.identifierEquals(FnvHash.Constants.SUBSTITUTABLE)) {
                    lexer.nextToken();
                    acceptIdentifier("AT");
                    accept(Token.ALL);
                    acceptIdentifier("LEVELS");
                }
                // skip
                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.VARRAY)) {
                lexer.nextToken();
                SQLName name = this.exprParser.name();

                accept(Token.STORE);
                accept(Token.AS);
                if (lexer.identifierEquals(FnvHash.Constants.BASICFILE)) {
                    lexer.nextToken();
                }
                this.getExprParser().parseLobStorage();
                throw new ParserException("TODO : " + lexer.info());
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
                    SQLPartitionByList partitionByList = partitionByList();
                    this.getExprParser().partitionClauseRest(partitionByList);
                    stmt.setPartitioning(partitionByList);
                    continue;
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
            } else if (lexer.identifierEquals(FnvHash.Constants.XMLTYPE)) {
                lexer.nextToken();
                if (lexer.token() == Token.COLUMN) {
                    lexer.nextToken();
                }

                OracleXmlColumnProperties xmlColumnProperties = new OracleXmlColumnProperties();
                xmlColumnProperties.setColumn(this.exprParser.name());

                if (lexer.token() == Token.STORE) {
                    lexer.nextToken();
                    accept(Token.AS);

                    OracleXmlColumnProperties.OracleXMLTypeStorage storage = new OracleXmlColumnProperties.OracleXMLTypeStorage();
                    if (lexer.identifierEquals("SECUREFILE")) {
                        storage.setSecureFile(true);
                        lexer.nextToken();
                    } else if (lexer.identifierEquals("BASICFILE")) {
                        storage.setBasicFile(true);
                        lexer.nextToken();
                    }

                    if (lexer.identifierEquals("BINARY")) {
                        lexer.nextToken();
                        acceptIdentifier("XML");
                        storage.setBinaryXml(true);
                    } else if (lexer.identifierEquals("CLOB")) {
                        lexer.nextToken();
                        storage.setClob(true);
                    }

                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();

                        OracleLobParameters lobParameters = new OracleLobParameters();

                        for_:
                        for (;;) {
                            switch (lexer.token()) {
                                case TABLESPACE: {
                                    lexer.nextToken();
                                    SQLName tableSpace = this.exprParser.name();
                                    lobParameters.setTableSpace(tableSpace);
                                }
                                    continue for_;
                                case ENABLE:
                                case DISABLE:{
                                    Boolean enable = lexer.token() == Token.ENABLE;
                                    lexer.nextToken();
                                    accept(Token.STORAGE);
                                    accept(Token.IN);
                                    accept(Token.ROW);

                                    lobParameters.setEnableStorageInRow(enable);
                                }
                                    continue for_;
                                case CHUNK:
                                    lexer.nextToken();
                                    SQLExpr chunk = this.exprParser.expr();
                                    lobParameters.setChunk(chunk);
                                    continue for_;
                                case NOCACHE:
                                    lexer.nextToken();
                                    lobParameters.setCache(false);
                                    continue for_;
                                case LOGGING:
                                    lexer.nextToken();
                                    lobParameters.setLogging(true);
                                    continue for_;
                                case NOCOMPRESS:
                                    lexer.nextToken();
                                    lobParameters.setCompress(false);
                                    continue for_;
                                case KEEP_DUPLICATES:
                                    lexer.nextToken();
                                    lobParameters.setKeepDuplicates(true);
                                    continue for_;
                                case STORAGE:
                                    OracleStorageClause storageClause = this.getExprParser().parseStorage();
                                    lobParameters.setStorage(storageClause);
                                    continue for_;
                                case IDENTIFIER:
                                    long hash = lexer.hash_lower();
                                    if (hash == FnvHash.Constants.PCTVERSION) {
                                        lobParameters.setPctVersion(this.exprParser.primary());
                                        lexer.nextToken();
                                        continue for_;
                                    }
                                    break for_;
                                default:
                                    break for_;
                            }
                        }

                        accept(Token.RPAREN);

                        storage.setLobParameters(lobParameters);
                    }
                }

                for (;;) {
                    if (lexer.identifierEquals(FnvHash.Constants.ALLOW)) {
                        lexer.nextToken();
                        if (lexer.identifierEquals("NONSCHEMA")) {
                            lexer.nextToken();
                            xmlColumnProperties.setAllowNonSchema(true);
                        } else if (lexer.identifierEquals("ANYSCHEMA")) {
                            lexer.nextToken();
                            xmlColumnProperties.setAllowAnySchema(true);
                        } else {
                            throw new ParserException("TODO : " + lexer.info());
                        }
                        continue;
                    } else if (lexer.identifierEquals(FnvHash.Constants.DISALLOW)) {
                        lexer.nextToken();
                        if (lexer.identifierEquals("NONSCHEMA")) {
                            lexer.nextToken();
                            xmlColumnProperties.setAllowNonSchema(false);
                        } else if (lexer.identifierEquals("ANYSCHEMA")) {
                            lexer.nextToken();
                            xmlColumnProperties.setAllowAnySchema(false);
                        } else {
                            throw new ParserException("TODO : " + lexer.info());
                        }
                        continue;
                    }
                    break;
                }

//                throw new ParserException("TODO : " + lexer.info());

                stmt.setXmlTypeColumnProperties(xmlColumnProperties);
                continue;
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
            if (lexer.identifierEquals(FnvHash.Constants.PCTTHRESHOLD)) {
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

                            if (lexer.identifierEquals(FnvHash.Constants.NOLOGFILE)) {
                                lexer.nextToken();
                                recordFormat.setLogfile(false);
                            }

                            if (lexer.identifierEquals(FnvHash.Constants.NOBADFILE)) {
                                lexer.nextToken();
                                recordFormat.setBadfile(false);
                            }
                        } else {
                            throw new ParserException("TODO " + lexer.info());
                        }
                    }

                    if (lexer.identifierEquals(FnvHash.Constants.FIELDS)) {
                        lexer.nextToken();

                        if (lexer.identifierEquals(FnvHash.Constants.TERMINATED)) {
                            lexer.nextToken();
                            accept(Token.BY);
                            recordFormat.setTerminatedBy(this.exprParser.primary());
                        } else {
                            throw new ParserException("TODO " + lexer.info());
                        }

                        if (lexer.identifierEquals(FnvHash.Constants.LTRIM)) {
                            lexer.nextToken();
                            recordFormat.setLtrim(true);
                        }
                    }

                    if (lexer.identifierEquals(FnvHash.Constants.MISSING)) {
                        lexer.nextToken();
                        acceptIdentifier("FIELD");
                        accept(Token.VALUES);
                        acceptIdentifier("ARE");
                        accept(Token.NULL);
                        recordFormat.setMissingFieldValuesAreNull(true);
                    }

                    if (lexer.token() == Token.REJECT) {
                        lexer.nextToken();
                        acceptIdentifier("ROWS");
                        accept(Token.WITH);
                        accept(Token.ALL);
                        accept(Token.NULL);
                        acceptIdentifier("FIELDS");
                        recordFormat.setRejectRowsWithAllNullFields(true);
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

    protected SQLPartitionByList partitionByList() {
        acceptIdentifier("LIST");
        SQLPartitionByList partitionByList = new SQLPartitionByList();

        accept(Token.LPAREN);
        partitionByList.addColumn(this.exprParser.expr());
        accept(Token.RPAREN);

        this.getExprParser().parsePartitionByRest(partitionByList);

        return partitionByList;
    }

    protected SQLTableElement parseCreateTableSupplementalLogingProps() {
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

            return logGrp;
        } else if (lexer.identifierEquals(FnvHash.Constants.DATA)){
            lexer.nextToken();

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

            return idKey;
        }

        throw new ParserException("TODO " + lexer.info());
    }

    @Override
    public OracleExprParser getExprParser() {
        return (OracleExprParser) exprParser;
    }
}
