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
package com.alibaba.druid.sql.dialect.mysql.parser;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement.TableSpaceOption;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;
import com.alibaba.druid.util.MySqlUtils;

public class MySqlCreateTableParser extends SQLCreateTableParser {

    public MySqlCreateTableParser(String sql){
        super(new MySqlExprParser(sql));
    }

    public MySqlCreateTableParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public SQLCreateTableStatement parseCreateTable() {
        return parseCreateTable(true);
    }

    @Override
    public MySqlExprParser getExprParser() {
        return (MySqlExprParser) exprParser;
    }

    public MySqlCreateTableStatement parseCreateTable(boolean acceptCreate) {
        MySqlCreateTableStatement stmt = new MySqlCreateTableStatement();
        if (acceptCreate) {
            if (lexer.hasComment() && lexer.isKeepComments()) {
                stmt.addBeforeComment(lexer.readAndResetComments());
            }
            accept(Token.CREATE);
        }

        if (lexer.identifierEquals("TEMPORARY")) {
            lexer.nextToken();
            stmt.setType(SQLCreateTableStatement.Type.GLOBAL_TEMPORARY);
        } else if (lexer.identifierEquals("SHADOW")) {
            lexer.nextToken();
            stmt.setType(SQLCreateTableStatement.Type.SHADOW);
        }

        if (lexer.identifierEquals(FnvHash.Constants.DIMENSION)) {
            lexer.nextToken();
            stmt.setDimension(true);
        }

        if (lexer.token() == Token.HINT) {
            this.exprParser.parseHints(stmt.getHints());
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            lexer.nextToken();
            stmt.setExternal(true);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IF || lexer.identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            stmt.setIfNotExiists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setLike(name);
        }

        if (lexer.token() == Token.WITH) {
            SQLSelect query = new MySqlSelectParser(this.exprParser).select();
            stmt.setSelect(query);
        } else if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();

            if (lexer.token() == Token.SELECT) {
                SQLSelect query = new MySqlSelectParser(this.exprParser).select();
                stmt.setSelect(query);
            } else {
                for (;;) {
                    SQLColumnDefinition column = null;

                    boolean global = false;
                    if (lexer.identifierEquals(FnvHash.Constants.GLOBAL)) {
                        final Lexer.SavePoint mark = lexer.mark();
                        lexer.nextToken();
                        if (lexer.token() == Token.INDEX || lexer.token() == Token.UNIQUE) {
                            global = true;
                        } else {
                            lexer.reset(mark);
                        }
                    }

                    if (lexer.token() == Token.FULLTEXT) {
                        Lexer.SavePoint mark = lexer.mark();
                        lexer.nextToken();

                        if (lexer.token() == Token.KEY) {
                            MySqlKey fulltextKey = new MySqlKey();
                            this.exprParser.parseIndex(fulltextKey.getIndexDefinition());
                            fulltextKey.setIndexType("FULLTEXT");
                            fulltextKey.setParent(stmt);
                            stmt.getTableElementList().add(fulltextKey);

                            while (lexer.token() == Token.HINT) {
                                lexer.nextToken();
                            }

                            if (lexer.token() == Token.RPAREN) {
                                break;
                            } else if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        } else if (lexer.token() == Token.INDEX) {
                            MySqlTableIndex idx = new MySqlTableIndex();
                            this.exprParser.parseIndex(idx.getIndexDefinition());
                            idx.setIndexType("FULLTEXT");
                            idx.setParent(stmt);
                            stmt.getTableElementList().add(idx);

                            if (lexer.token() == Token.RPAREN) {
                                break;
                            } else if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        } else if (lexer.token() == Token.IDENTIFIER && MySqlUtils.isBuiltinDataType(lexer.stringVal())) {
                            lexer.reset(mark);
                        } else {
                            MySqlTableIndex idx = new MySqlTableIndex();
                            this.exprParser.parseIndex(idx.getIndexDefinition());
                            idx.setIndexType("FULLTEXT");
                            idx.setParent(stmt);
                            stmt.getTableElementList().add(idx);

                            if (lexer.token() == Token.RPAREN) {
                                break;
                            } else if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        }

                    } else if (lexer.identifierEquals(FnvHash.Constants.SPATIAL)) {
                        Lexer.SavePoint mark = lexer.mark();
                        lexer.nextToken();
                        if (lexer.token() == Token.INDEX || lexer.token() == Token.KEY ||
                                lexer.token() != Token.IDENTIFIER || !MySqlUtils.isBuiltinDataType(lexer.stringVal())) {
                            MySqlTableIndex idx = new MySqlTableIndex();
                            this.exprParser.parseIndex(idx.getIndexDefinition());
                            idx.setIndexType("SPATIAL");
                            idx.setParent(stmt);
                            stmt.getTableElementList().add(idx);

                            if (lexer.token() == Token.RPAREN) {
                                break;
                            } else if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        } else {
                            lexer.reset(mark);
                        }
                    }

                    if (lexer.identifierEquals(FnvHash.Constants.ANN)) {
                        Lexer.SavePoint mark = lexer.mark();
                        lexer.nextToken();
                        if (lexer.token() == Token.INDEX || lexer.token() == Token.KEY) {
                            MySqlTableIndex idx = new MySqlTableIndex();
                            this.exprParser.parseIndex(idx.getIndexDefinition());
                            idx.setIndexType("ANN");
                            idx.setParent(stmt);
                            stmt.getTableElementList().add(idx);

                            if (lexer.token() == Token.RPAREN) {
                                break;
                            } else if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        } else {
                            lexer.reset(mark);
                        }
                    }
                    if (lexer.identifierEquals(FnvHash.Constants.CLUSTERED)) {
                        lexer.nextToken();
                        if (lexer.token() == Token.KEY) {
                            MySqlKey clsKey = new MySqlKey();
                            this.exprParser.parseIndex(clsKey.getIndexDefinition());
                            clsKey.setIndexType("CLUSTERED");
                            clsKey.setParent(stmt);
                            stmt.getTableElementList().add(clsKey);

                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        } else if (lexer.token() == Token.INDEX) {
                            MySqlTableIndex idx = new MySqlTableIndex();
                            this.exprParser.parseIndex(idx.getIndexDefinition());
                            idx.setIndexType("CLUSTERED");
                            idx.setParent(stmt);
                            stmt.getTableElementList().add(idx);

                            if (lexer.token() == Token.RPAREN) {
                                break;
                            } else if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        }
                    } else if (lexer.identifierEquals(FnvHash.Constants.CLUSTERING)) {
                        lexer.nextToken();
                        if (lexer.token() == Token.KEY) {
                            MySqlKey clsKey = new MySqlKey();
                            this.exprParser.parseIndex(clsKey.getIndexDefinition());
                            clsKey.setIndexType("CLUSTERING");
                            clsKey.setParent(stmt);
                            stmt.getTableElementList().add(clsKey);

                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        } else if(lexer.token() == Token.INDEX) {
                            MySqlTableIndex idx = new MySqlTableIndex();
                            this.exprParser.parseIndex(idx.getIndexDefinition());
                            idx.setIndexType("CLUSTERING");
                            idx.setParent(stmt);
                            stmt.getTableElementList().add(idx);

                            if (lexer.token() == Token.RPAREN) {
                                break;
                            } else if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                        }
                    } else if (lexer.token() == Token.IDENTIFIER //
                        || lexer.token() == Token.LITERAL_CHARS) {
                        column = this.exprParser.parseColumn();
                        column.setParent(stmt);
                        stmt.getTableElementList().add(column);

                        if (lexer.isKeepComments() && lexer.hasComment()) {
                            column.addAfterComment(lexer.readAndResetComments());
                        }
                    } else if (lexer.token() == Token.CONSTRAINT //
                               || lexer.token() == Token.PRIMARY //
                               || lexer.token() == Token.UNIQUE) {
                        SQLTableConstraint constraint = this.parseConstraint();
                        constraint.setParent(stmt);

                        if (constraint instanceof MySqlUnique) {
                            MySqlUnique unique = (MySqlUnique) constraint;
                            if (global) {
                                unique.setGlobal(true);
                            }
                        }

                        stmt.getTableElementList().add(constraint);
                    } else if (lexer.token() == (Token.INDEX)) {
                        MySqlTableIndex idx = new MySqlTableIndex();
                        this.exprParser.parseIndex(idx.getIndexDefinition());

                        if (global) {
                            idx.getIndexDefinition().setGlobal(true);
                        }

                        idx.setParent(stmt);
                        stmt.getTableElementList().add(idx);
                    } else if (lexer.token() == (Token.KEY)) {
                        Lexer.SavePoint savePoint = lexer.mark();
                        lexer.nextToken();

                        boolean isColumn = false;
                        if (lexer.identifierEquals(FnvHash.Constants.VARCHAR)) {
                            isColumn = true;
                        }
                        lexer.reset(savePoint);

                        if (isColumn) {
                            column = this.exprParser.parseColumn();
                            stmt.getTableElementList().add(column);
                        } else {
                            stmt.getTableElementList().add(parseConstraint());
                        }
                    } else if (lexer.token() == (Token.PRIMARY)) {
                        SQLTableConstraint pk = parseConstraint();
                        pk.setParent(stmt);
                        stmt.getTableElementList().add(pk);
                    } else if (lexer.token() == (Token.FOREIGN)) {
                        SQLForeignKeyConstraint fk = this.getExprParser().parseForeignKey();
                        fk.setParent(stmt);
                        stmt.getTableElementList().add(fk);
                    } else if (lexer.token() == Token.CHECK) {
                        SQLCheck check = this.exprParser.parseCheck();
                        stmt.getTableElementList().add(check);
                    } else if (lexer.token() == Token.LIKE) {
                        lexer.nextToken();
                        SQLTableLike tableLike = new SQLTableLike();
                        tableLike.setTable(new SQLExprTableSource(this.exprParser.name()));
                        tableLike.setParent(stmt);
                        stmt.getTableElementList().add(tableLike);

                        if (lexer.identifierEquals(FnvHash.Constants.INCLUDING)) {
                            lexer.nextToken();
                            acceptIdentifier("PROPERTIES");
                            tableLike.setIncludeProperties(true);
                        } else if (lexer.identifierEquals(FnvHash.Constants.EXCLUDING)) {
                            lexer.nextToken();
                            acceptIdentifier("PROPERTIES");
                            tableLike.setExcludeProperties(true);
                        }
                    } else {
                        column = this.exprParser.parseColumn();
                        stmt.getTableElementList().add(column);
                    }

                    if (lexer.token() == Token.HINT) {
                        lexer.nextToken();
                    }

                    if (lexer.token() != Token.COMMA) {
                        break;
                    } else {
                        lexer.nextToken();
                        if (lexer.isKeepComments() && lexer.hasComment() && column != null) {
                            column.addAfterComment(lexer.readAndResetComments());
                        }
                    }
                }
            }

            if (lexer.token() == Token.HINT) {
                lexer.nextToken();
            }

            accept(Token.RPAREN);

            if (lexer.token() == Token.HINT && lexer.stringVal().charAt(0) == '!') {
                lexer.nextToken();
            }
        }

        for (;;) {
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
            }

            if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                SQLExpr expr = null;
                if (lexer.token() == Token.MERGE) {
                    expr = new SQLIdentifierExpr(lexer.stringVal());
                    lexer.nextToken();
                } else {
                    expr = this.exprParser.expr();
                }
                stmt.setEngine(expr);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.BLOCK_SIZE)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                SQLExpr expr = null;
                if (lexer.token() == Token.MERGE) {
                    expr = new SQLIdentifierExpr(lexer.stringVal());
                    lexer.nextToken();
                } else {
                    expr = this.exprParser.integerExpr();
                }
                stmt.addOption("BLOCK_SIZE", expr);
                continue;
            }

            if (lexer.identifierEquals("BLOCK_FORMAT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                SQLExpr expr = this.exprParser.primary();
                stmt.addOption("BLOCK_FORMAT", expr);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.REPLICA_NUM)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                SQLExpr expr = this.exprParser.integerExpr();
                stmt.addOption("REPLICA_NUM", expr);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.TABLET_SIZE)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                SQLExpr expr = this.exprParser.integerExpr();
                stmt.addOption("TABLET_SIZE", expr);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.PCTFREE)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                SQLExpr expr = this.exprParser.integerExpr();
                stmt.addOption("PCTFREE", expr);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.USE_BLOOM_FILTER)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                SQLExpr expr = this.exprParser.primary();
                stmt.addOption("USE_BLOOM_FILTER", expr);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.AUTO_INCREMENT)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("AUTO_INCREMENT", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("AVG_ROW_LENGTH")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("AVG_ROW_LENGTH", this.exprParser.expr());
                continue;
            }

            if (lexer.token() == Token.DEFAULT) {
                lexer.nextToken();
                parseTableOptionCharsetOrCollate(stmt);
                continue;
            }

            if (parseTableOptionCharsetOrCollate(stmt)) {
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.CHECKSUM)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("CHECKSUM", this.exprParser.expr());
                continue;
            }

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.setComment(this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.CONNECTION)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("CONNECTION", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.DATA)) {
                lexer.nextToken();
                acceptIdentifier("DIRECTORY");
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("DATA DIRECTORY", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("DELAY_KEY_WRITE")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("DELAY_KEY_WRITE", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("FULLTEXT_DICT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("FULLTEXT_DICT", this.exprParser.charExpr());
                continue;
            }

            if (lexer.token() == Token.INDEX) {
                lexer.nextToken();
                acceptIdentifier("DIRECTORY");
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("INDEX DIRECTORY", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("INSERT_METHOD")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("INSERT_METHOD", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("KEY_BLOCK_SIZE")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("KEY_BLOCK_SIZE", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.MAX_ROWS)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("MAX_ROWS", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.MIN_ROWS)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("MIN_ROWS", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.PACK_KEYS)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("PACK_KEYS", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.PASSWORD)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("PASSWORD", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("ROW_FORMAT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("ROW_FORMAT", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("STATS_AUTO_RECALC")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                stmt.addOption("STATS_AUTO_RECALC", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("STATS_PERSISTENT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                stmt.addOption("STATS_PERSISTENT", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals("STATS_SAMPLE_PAGES")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                stmt.addOption("STATS_SAMPLE_PAGES", this.exprParser.expr());
                continue;
            }

            if (lexer.token() == Token.UNION) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }


                accept(Token.LPAREN);
                SQLListExpr list = new SQLListExpr();
                this.exprParser.exprList(list.getItems(), list);
                stmt.addOption("UNION", list);
                accept(Token.RPAREN);
                continue;
            }

            if (lexer.token() == Token.TABLESPACE) {
                lexer.nextToken();

                TableSpaceOption option = new TableSpaceOption();
                option.setName(this.exprParser.name());

                if (lexer.identifierEquals("STORAGE")) {
                    lexer.nextToken();
                    option.setStorage(this.exprParser.name());
                }

                stmt.addOption("TABLESPACE", option);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.TABLEGROUP)) {
                lexer.nextToken();

                SQLName tableGroup = this.exprParser.name();
                stmt.setTableGroup(tableGroup);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.TYPE)) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.addOption("TYPE", this.exprParser.expr());
                continue;
            }


            if (lexer.identifierEquals("INDEX_ALL")) {
                lexer.nextToken();
                accept(Token.EQ);
                if (lexer.token() == Token.LITERAL_CHARS) {
                    if ("Y".equalsIgnoreCase(lexer.stringVal())) {
                        lexer.nextToken();
                        stmt.addOption("INDEX_ALL", new SQLCharExpr("Y"));
                    } else if ("N".equalsIgnoreCase(lexer.stringVal())) {
                        lexer.nextToken();
                        stmt.addOption("INDEX_ALL", new SQLCharExpr("N"));
                    } else {
                        throw new ParserException("INDEX_ALL accept parameter ['Y' or 'N'] only.");
                    }
                }
                continue;
            }

            if (lexer.identifierEquals("RT_INDEX_ALL")) {
                lexer.nextToken();
                accept(Token.EQ);
                if (lexer.token() == Token.LITERAL_CHARS) {
                    if ("Y".equalsIgnoreCase(lexer.stringVal())) {
                        lexer.nextToken();
                        stmt.addOption("RT_INDEX_ALL", new SQLCharExpr("Y"));
                    } else if ("N".equalsIgnoreCase(lexer.stringVal())) {
                        lexer.nextToken();
                        stmt.addOption("RT_INDEX_ALL", new SQLCharExpr("N"));
                    } else {
                        throw new ParserException("RT_INDEX_ALL accepts parameter ['Y' or 'N'] only.");
                    }
                }

                continue;
            }
            if (lexer.identifierEquals(FnvHash.Constants.ARCHIVE)) {
                lexer.nextToken();
                accept(Token.BY);
                acceptIdentifier("OSS");
                stmt.setArchiveBy(new SQLIdentifierExpr("OSS"));
                continue;
            }

            if (lexer.identifierEquals("STORAGE_TYPE")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.addOption("STORAGE_TYPE", this.exprParser.charExpr());
                continue;
            }

            if (lexer.identifierEquals("STORAGE_POLICY")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.addOption("STORAGE_POLICY", this.exprParser.charExpr());
                continue;
            }

            if (lexer.identifierEquals("HOT_PARTITION_COUNT")) {
                lexer.nextToken();
                accept(Token.EQ);
                try {
                    stmt.addOption("HOT_PARTITION_COUNT", this.exprParser.integerExpr());
                } catch (Exception e) {
                    throw new ParserException("only integer number is supported for hot_partition_count");
                }
                continue;
            }

            if (lexer.identifierEquals("TABLE_PROPERTIES")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.addOption("TABLE_PROPERTIES",exprParser.charExpr());
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.ENCRYPTION)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("ENCRYPTION", this.exprParser.expr());
                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.COMPRESSION)) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.addOption("COMPRESSION", this.exprParser.expr());
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.CLUSTERED)) {
                lexer.nextToken();
                accept(Token.BY);
                accept(Token.LPAREN);
                for (; ; ) {
                    SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
                    stmt.addClusteredByItem(item);
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
                continue;
            }

            if (lexer.token() == Token.PARTITION) {
                SQLPartitionBy partitionClause = parsePartitionBy();
                stmt.setPartitioning(partitionClause);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.BROADCAST)) {
                lexer.nextToken();
                stmt.setBroadCast(true);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.DISTRIBUTE) || lexer.identifierEquals(FnvHash.Constants.DISTRIBUTED)) {
                lexer.nextToken();
                accept(Token.BY);
                if (lexer.identifierEquals(FnvHash.Constants.HASH)) {
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    for (; ; ) {
                        SQLName name = this.exprParser.name();
                        stmt.getDistributeBy().add(name);
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);
                    stmt.setDistributeByType(new SQLIdentifierExpr("HASH"));
                } else if (lexer.identifierEquals(FnvHash.Constants.BROADCAST)) {
                    lexer.nextToken();
                    stmt.setDistributeByType(new SQLIdentifierExpr("BROADCAST"));
                }
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.DBPARTITION)) {
                lexer.nextToken();
                accept(Token.BY);
                SQLExpr dbPartitoinBy = this.exprParser.primary();
                stmt.setDbPartitionBy(dbPartitoinBy);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.DBPARTITIONS)) {
                lexer.nextToken();
                SQLExpr dbPartitoins = this.exprParser.primary();
                stmt.setDbPartitions(dbPartitoins);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.TBPARTITION)) {
                lexer.nextToken();
                accept(Token.BY);
                SQLExpr expr = this.exprParser.expr();
                if (lexer.identifierEquals(FnvHash.Constants.STARTWITH)) {
                    lexer.nextToken();
                    SQLExpr start = this.exprParser.primary();
                    acceptIdentifier("ENDWITH");
                    SQLExpr end = this.exprParser.primary();
                    expr = new SQLBetweenExpr(expr, start, end);
                }
                stmt.setTablePartitionBy(expr);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.TBPARTITIONS)) {
                lexer.nextToken();
                SQLExpr tbPartitions = this.exprParser.primary();
                stmt.setTablePartitions(tbPartitions);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.EXTPARTITION)) {
                lexer.nextToken();
                accept(Token.LPAREN);

                MySqlExtPartition partitionDef = new MySqlExtPartition();

                for (;;) {
                    MySqlExtPartition.Item item = new MySqlExtPartition.Item();

                    if (lexer.identifierEquals(FnvHash.Constants.DBPARTITION)) {
                        lexer.nextToken();
                        SQLName name = this.exprParser.name();
                        item.setDbPartition(name);
                        accept(Token.BY);
                        SQLExpr value = this.exprParser.primary();
                        item.setDbPartitionBy(value);
                    }

                    if (lexer.identifierEquals(FnvHash.Constants.TBPARTITION)) {
                        lexer.nextToken();
                        SQLName name = this.exprParser.name();
                        item.setTbPartition(name);
                        accept(Token.BY);
                        SQLExpr value = this.exprParser.primary();
                        item.setTbPartitionBy(value);
                    }

                    item.setParent(partitionDef);
                    partitionDef.getItems().add(item);

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    } else {
                        break;
                    }
                }
                accept(Token.RPAREN);
                stmt.setExPartition(partitionDef);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.OPTIONS)) {
                lexer.nextToken();
                accept(Token.LPAREN);

                stmt.putAttribute("ads.options", Boolean.TRUE);
                for (;;) {
                    String name = lexer.stringVal();
                    lexer.nextToken();
                    accept(Token.EQ);
                    SQLExpr value = this.exprParser.primary();
                    stmt.addOption(name, value);
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }

                accept(Token.RPAREN);
                continue;
            }

            if (lexer.identifierEquals(FnvHash.Constants.STORED)) {
                lexer.nextToken();
                accept(Token.BY);
                SQLName name = this.exprParser.name();
                stmt.setStoredBy(name);
            }

            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                accept(Token.LPAREN);

                for (;;) {
                    String name = lexer.stringVal();
                    lexer.nextToken();
                    accept(Token.EQ);
                    SQLName value = this.exprParser.name();
                    stmt.getWith().put(name, value);
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }

                accept(Token.RPAREN);
                continue;
            }

            if (lexer.token() == (Token.HINT)) {
                this.exprParser.parseHints(stmt.getOptionHints());
                continue;
            }

            break;
        }

        if (lexer.token() == (Token.ON)) {
            throw new ParserException("TODO. " + lexer.info());
        }

        if (lexer.token() == Token.REPLACE) {
            lexer.nextToken();
            stmt.setReplace(true);
        } else if (lexer.identifierEquals("IGNORE")) {
            lexer.nextToken();
            stmt.setIgnore(true);
        }

        if (lexer.token() == (Token.AS)) {
            lexer.nextToken();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                SQLSelect query = new MySqlSelectParser(this.exprParser).select();
                stmt.setSelect(query);
                accept(Token.RPAREN);
            }
        }

        SQLCommentHint hint = null;
        if (lexer.token() == Token.HINT) {
            hint = this.exprParser.parseHint();
        }

        if (lexer.token() == (Token.SELECT)) {
            SQLSelect query = new MySqlSelectParser(this.exprParser).select();
            if (hint != null) {
                query.setHeadHint(hint);
            }
            stmt.setSelect(query);

            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                if (lexer.identifierEquals(FnvHash.Constants.NO)) {
                    lexer.nextToken();
                    acceptIdentifier("DATA");
                    stmt.setWithData(false);
                } else {
                    acceptIdentifier("DATA");
                    stmt.setWithData(true);
                }
            }
        }

        while (lexer.token() == (Token.HINT)) {
            this.exprParser.parseHints(stmt.getOptionHints());
        }
        return stmt;
    }

    public SQLPartitionBy parsePartitionBy() {
        lexer.nextToken();
        accept(Token.BY);

        SQLPartitionBy partitionClause;

        boolean linera = false;
        if (lexer.identifierEquals(FnvHash.Constants.LINEAR)) {
            lexer.nextToken();
            linera = true;
        }

        if (lexer.token() == Token.KEY) {
            MySqlPartitionByKey clause = new MySqlPartitionByKey();
            lexer.nextToken();

            if (linera) {
                clause.setLinear(true);
            }

            if (lexer.identifierEquals(FnvHash.Constants.ALGORITHM)) {
                lexer.nextToken();
                accept(Token.EQ);
                clause.setAlgorithm(lexer.integerValue().shortValue());
                lexer.nextToken();
            }

            accept(Token.LPAREN);
            if (lexer.token() != Token.RPAREN) {
                for (;;) {
                    clause.addColumn(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
            }
            accept(Token.RPAREN);

            partitionClause = clause;

            partitionClauseRest(clause);
        } else if (lexer.identifierEquals("HASH") || lexer.identifierEquals("UNI_HASH")) {
            SQLPartitionByHash clause = new SQLPartitionByHash();

            if (lexer.identifierEquals("UNI_HASH")) {
                clause.setUnique(true);
            }

            lexer.nextToken();

            if (linera) {
                clause.setLinear(true);
            }

            if (lexer.token() == Token.KEY) {
                lexer.nextToken();
                clause.setKey(true);
            }

            accept(Token.LPAREN);
            this.exprParser.exprList(clause.getColumns(), clause);
            accept(Token.RPAREN);
            partitionClause = clause;

            partitionClauseRest(clause);

        } else if (lexer.identifierEquals("RANGE")) {
            SQLPartitionByRange clause = partitionByRange();
            partitionClause = clause;

            partitionClauseRest(clause);

        } else if (lexer.identifierEquals("VALUE")) {
            SQLPartitionByValue clause = partitionByValue();
            partitionClause = clause;

            partitionClauseRest(clause);

        } else if (lexer.identifierEquals("LIST")) {
            lexer.nextToken();
            SQLPartitionByList clause = new SQLPartitionByList();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                clause.addColumn(this.exprParser.expr());
                accept(Token.RPAREN);
            } else {
                acceptIdentifier("COLUMNS");
                accept(Token.LPAREN);
                for (; ; ) {
                    clause.addColumn(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }
            partitionClause = clause;

            partitionClauseRest(clause);
        } else if (lexer.token() == Token.IDENTIFIER) {
            SQLPartitionByRange clause = partitionByRange();
            partitionClause = clause;

            partitionClauseRest(clause);
        } else {
            throw new ParserException("TODO. " + lexer.info());
        }

        if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
            lexer.nextToken();
            partitionClause.setLifecycle((SQLIntegerExpr) exprParser.expr());
        }

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            for (;;) {
                SQLPartition partitionDef = this.getExprParser().parsePartition();

                partitionClause.addPartition(partitionDef);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }
            accept(Token.RPAREN);
        }
        return partitionClause;
    }

    protected SQLPartitionByRange partitionByRange1() {

        acceptIdentifier("RANGE");

        SQLPartitionByRange clause = new SQLPartitionByRange();

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            clause.addColumn(this.exprParser.expr());
            accept(Token.RPAREN);
        } else {
            acceptIdentifier("COLUMNS");
            accept(Token.LPAREN);
            for (;;) {
                clause.addColumn(this.exprParser.name());
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
        }
        return clause;
    }

    protected SQLPartitionByValue partitionByValue() {
        SQLPartitionByValue clause = new SQLPartitionByValue();
        if (lexer.identifierEquals(FnvHash.Constants.VALUE)) {
            lexer.nextToken();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                clause.addColumn(this.exprParser.expr());
                accept(Token.RPAREN);
            }
        }
        return clause;
    }

    protected SQLPartitionByRange partitionByRange() {
        SQLPartitionByRange clause = new SQLPartitionByRange();
        if (lexer.identifierEquals(FnvHash.Constants.RANGE)) {
            lexer.nextToken();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                clause.addColumn(this.exprParser.expr());
                accept(Token.RPAREN);
            } else {
                acceptIdentifier("COLUMNS");
                accept(Token.LPAREN);
                for (;;) {
                    clause.addColumn(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }
        } else {
            SQLExpr expr = this.exprParser.expr();
            if (lexer.identifierEquals(FnvHash.Constants.STARTWITH)) {
                lexer.nextToken();
                SQLExpr start = this.exprParser.primary();
                acceptIdentifier("ENDWITH");
                SQLExpr end = this.exprParser.primary();
                expr = new SQLBetweenExpr(expr, start, end);
            }
            clause.setInterval(expr);
        }

        return clause;
    }

    protected void partitionClauseRest(SQLPartitionBy clause) {
        if (lexer.identifierEquals(FnvHash.Constants.PARTITIONS)
                || lexer.identifierEquals(FnvHash.Constants.TBPARTITIONS)
                || lexer.identifierEquals(FnvHash.Constants.DBPARTITIONS)) {
            lexer.nextToken();
            SQLIntegerExpr countExpr = this.exprParser.integerExpr();
            clause.setPartitionsCount(countExpr);
        }

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();

            if (lexer.identifierEquals("NUM")) {
                lexer.nextToken();
            }

            clause.setPartitionsCount(this.exprParser.expr());

            clause.putAttribute("ads.partition", Boolean.TRUE);
        }

        if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
            lexer.nextToken();
            clause.setLifecycle((SQLIntegerExpr) exprParser.expr());
        }

        if (lexer.identifierEquals(FnvHash.Constants.SUBPARTITION)) {
            lexer.nextToken();
            accept(Token.BY);

            SQLSubPartitionBy subPartitionByClause = null;

            boolean linear = false;
            if (lexer.identifierEquals("LINEAR")) {
                lexer.nextToken();
                linear = true;
            }

            if (lexer.token() == Token.KEY) {
                MySqlSubPartitionByKey subPartitionKey = new MySqlSubPartitionByKey();
                lexer.nextToken();

                if (linear) {
                    clause.setLinear(true);
                }

                if (lexer.identifierEquals(FnvHash.Constants.ALGORITHM)) {
                    lexer.nextToken();
                    accept(Token.EQ);
                    subPartitionKey.setAlgorithm(lexer.integerValue().shortValue());
                    lexer.nextToken();
                }

                accept(Token.LPAREN);
                for (;;) {
                    subPartitionKey.addColumn(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);

                subPartitionByClause = subPartitionKey;

            } else if (lexer.identifierEquals("VALUE")) {
                MySqlSubPartitionByValue subPartitionByValue = new MySqlSubPartitionByValue();
                lexer.nextToken();
                accept(Token.LPAREN);
                for (; ; ) {
                    subPartitionByValue.addColumn(this.exprParser.expr());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);

                subPartitionByClause = subPartitionByValue;

            } else if (lexer.identifierEquals("HASH")) {
                lexer.nextToken();
                SQLSubPartitionByHash subPartitionHash = new SQLSubPartitionByHash();

                if (linear) {
                    clause.setLinear(true);
                }

                if (lexer.token() == Token.KEY) {
                    lexer.nextToken();
                    subPartitionHash.setKey(true);
                }

                accept(Token.LPAREN);
                subPartitionHash.setExpr(this.exprParser.expr());
                accept(Token.RPAREN);
                subPartitionByClause = subPartitionHash;

            } else if (lexer.identifierEquals("LIST")) {
                lexer.nextToken();
                MySqlSubPartitionByList subPartitionList = new MySqlSubPartitionByList();

                //for ads
                if (lexer.token() == Token.KEY) {
                    lexer.nextToken();
                    accept(Token.LPAREN);

                    for(;;) {
                        SQLExpr expr = this.exprParser.expr();

                        if (expr instanceof SQLIdentifierExpr
                                && (lexer.identifierEquals("bigint") || lexer.identifierEquals("long"))) {
                            String dataType = lexer.stringVal();
                            lexer.nextToken();

                            SQLColumnDefinition column = this.exprParser.createColumnDefinition();
                            column.setName((SQLIdentifierExpr) expr);
                            column.setDataType(new SQLDataTypeImpl(dataType));
                            subPartitionList.addColumn(column);

                            subPartitionList.putAttribute("ads.subPartitionList", Boolean.TRUE);
                        }

                        subPartitionList.addKey(expr);
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    subPartitionList.putAttribute("ads.subPartitionList", Boolean.TRUE);
                    accept(Token.RPAREN);
                } else if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();

                    SQLExpr expr;
                    if (lexer.token() == Token.LITERAL_ALIAS) {
                        expr = new SQLIdentifierExpr(lexer.stringVal());
                        lexer.nextToken();
                    } else {
                        expr = this.exprParser.expr();
                    }

                    if (expr instanceof SQLIdentifierExpr
                            && (lexer.identifierEquals("bigint") || lexer.identifierEquals("long"))) {
                        String dataType = lexer.stringVal();
                        lexer.nextToken();

                        SQLColumnDefinition column = this.exprParser.createColumnDefinition();
                        column.setName((SQLIdentifierExpr) expr);
                        column.setDataType(new SQLDataTypeImpl(dataType));
                        subPartitionList.addColumn(column);

                        subPartitionList.putAttribute("ads.subPartitionList", Boolean.TRUE);
                    } else {
                        subPartitionList.addKey(expr);
                    }
                    accept(Token.RPAREN);
                } else {
                    acceptIdentifier("COLUMNS");
                    accept(Token.LPAREN);
                    for (;;) {
                        subPartitionList.addColumn(this.exprParser.parseColumn());
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);
                }
                subPartitionByClause = subPartitionList;
            } else if (lexer.identifierEquals(FnvHash.Constants.RANGE)) {
                lexer.nextToken();
                SQLSubPartitionByRange range = new SQLSubPartitionByRange();

                accept(Token.LPAREN);
                this.exprParser.exprList(range.getColumns(), range);
                accept(Token.RPAREN);
                subPartitionByClause = range;
            }

            if (lexer.identifierEquals(FnvHash.Constants.SUBPARTITION)) {
                lexer.nextToken();
                acceptIdentifier("OPTIONS");
                this.exprParser.parseAssignItem(subPartitionByClause.getOptions(), subPartitionByClause);
            }

            if (lexer.identifierEquals(FnvHash.Constants.SUBPARTITIONS)) {
                lexer.nextToken();
                Number intValue = lexer.integerValue();
                SQLNumberExpr numExpr = new SQLNumberExpr(intValue);
                subPartitionByClause.setSubPartitionsCount(numExpr);
                lexer.nextToken();
            } else if (lexer.identifierEquals(FnvHash.Constants.PARTITIONS)) { // ADB
                lexer.nextToken();
                subPartitionByClause.setSubPartitionsCount((SQLIntegerExpr)exprParser.expr());
                subPartitionByClause.getAttributes().put("adb.partitons", true);
            }

            if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
                lexer.nextToken();
                subPartitionByClause.setLifecycle((SQLIntegerExpr) exprParser.expr());
            }

            if (subPartitionByClause != null) {
                subPartitionByClause.setLinear(linear);

                clause.setSubPartitionBy(subPartitionByClause);
            }
        }
    }

    private boolean parseTableOptionCharsetOrCollate(MySqlCreateTableStatement stmt) {
        if (lexer.identifierEquals("CHARACTER")) {
            lexer.nextToken();
            accept(Token.SET);
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr charset;
            if (lexer.token() == Token.IDENTIFIER) {
                charset = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
            } else if (lexer.token() == Token.LITERAL_CHARS) {
                charset = new SQLCharExpr(lexer.stringVal());
                lexer.nextToken();
            } else {
                charset = this.exprParser.primary();
            }
            stmt.addOption("CHARACTER SET", charset);
            return true;
        }

        if (lexer.identifierEquals("CHARSET")) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr charset;
            if (lexer.token() == Token.IDENTIFIER) {
                charset = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
            } else if (lexer.token() == Token.LITERAL_CHARS) {
                charset = new SQLCharExpr(lexer.stringVal());
                lexer.nextToken();
            } else {
                charset = this.exprParser.primary();
            }
            stmt.addOption("CHARSET", charset);
            return true;
        }

        if (lexer.identifierEquals("COLLATE")) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            stmt.addOption("COLLATE", this.exprParser.expr());
            return true;
        }

        return false;
    }

    protected SQLTableConstraint parseConstraint() {
        SQLName name = null;
        boolean hasConstaint = false;
        if (lexer.token() == (Token.CONSTRAINT)) {
            hasConstaint = true;
            lexer.nextToken();
        }

        if (lexer.token() == Token.IDENTIFIER) {
            name = this.exprParser.name();
        }

        SQLTableConstraint constraint = null;

        if (lexer.token() == (Token.KEY)) {
            MySqlKey key = new MySqlKey();
            this.exprParser.parseIndex(key.getIndexDefinition());
            key.setHasConstraint(hasConstaint);

            if (name != null) {
                key.setName(name);
            }

            constraint = key;
        } else if (lexer.token() == Token.PRIMARY) {
            MySqlPrimaryKey pk = this.getExprParser().parsePrimaryKey();
            if (name != null) {
                pk.setName(name);
            }
            pk.setHasConstraint(hasConstaint);
            constraint = pk;
        } else if (lexer.token() == Token.UNIQUE) {
            MySqlUnique uk = this.getExprParser().parseUnique();
            // should not use CONSTRAINT [symbol] for index name if index_name already specified
            if (name != null && uk.getName() == null) {
                uk.setName(name);
            }

            uk.setHasConstraint(hasConstaint);

            constraint = uk;
        } else if (lexer.token() == Token.FOREIGN) {
            MysqlForeignKey fk = this.getExprParser().parseForeignKey();
            fk.setName(name);
            fk.setHasConstraint(hasConstaint);
            constraint = fk;
        } else if (lexer.token() == Token.CHECK) {
            lexer.nextToken();
            SQLCheck check = new SQLCheck();
            check.setName(name);
            SQLExpr expr = this.exprParser.primary();
            check.setExpr(expr);
            constraint = check;

            boolean enforce = true;
            if (Token.NOT.equals(lexer.token())) {
                enforce = false;
                lexer.nextToken();
            }
            if (lexer.stringVal().equalsIgnoreCase("ENFORCED")) {
                check.setEnforced(enforce);
                lexer.nextToken();
            }
            if (lexer.token() == Token.HINT) {
                String hintText = lexer.stringVal();
                if (hintText != null) {
                    hintText = hintText.trim();
                }

                if (hintText.startsWith("!")) {
                    if (hintText.endsWith("NOT ENFORCED")) {
                        check.setEnforced(false);
                    } else if (hintText.endsWith(" ENFORCED")) {
                        check.setEnforced(true);
                    }
                    lexer.nextToken();
                }
            }
        }

        if (constraint != null) {
            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                SQLExpr comment = this.exprParser.primary();
                constraint.setComment(comment);
            }

            return constraint;
        }

        throw new ParserException("TODO. " + lexer.info());
    }
}
