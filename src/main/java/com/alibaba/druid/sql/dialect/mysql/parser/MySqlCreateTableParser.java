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
package com.alibaba.druid.sql.dialect.mysql.parser;

import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLForeignKeyConstraint;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLTableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlPrimaryKey;
import com.alibaba.druid.sql.dialect.mysql.ast.MySqlUnique;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement.TableSpaceOption;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByHash;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByList;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitionByRange;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitioningClause;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPartitioningDef;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlTableIndex;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class MySqlCreateTableParser extends SQLCreateTableParser {

    public MySqlCreateTableParser(String sql){
        super(new MySqlExprParser(sql));
    }

    public MySqlCreateTableParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public SQLCreateTableStatement parseCrateTable() {
        return parseCrateTable(true);
    }

    public MySqlExprParser getExprParser() {
        return (MySqlExprParser) exprParser;
    }

    public MySqlCreateTableStatement parseCrateTable(boolean acceptCreate) {
        if (acceptCreate) {
            accept(Token.CREATE);
        }
        MySqlCreateTableStatement stmt = new MySqlCreateTableStatement();

        if (identifierEquals("TEMPORARY")) {
            lexer.nextToken();
            stmt.setType(SQLCreateTableStatement.Type.GLOBAL_TEMPORARY);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IF || identifierEquals("IF")) {
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

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                SQLName name = this.exprParser.name();
                stmt.setLike(name);
            } else {
                for (;;) {
                    if (lexer.token() == Token.IDENTIFIER //
                        || lexer.token() == Token.LITERAL_CHARS) {
                        SQLColumnDefinition column = this.exprParser.parseColumn();
                        stmt.getTableElementList().add(column);
                    } else if (lexer.token() == Token.CONSTRAINT //
                               || lexer.token() == Token.PRIMARY //
                               || lexer.token() == Token.UNIQUE) {
                        stmt.getTableElementList().add(parseConstraint());
                    } else if (lexer.token() == (Token.INDEX)) {
                        lexer.nextToken();

                        MySqlTableIndex idx = new MySqlTableIndex();

                        if (lexer.token() == Token.IDENTIFIER) {
                            if (!"USING".equalsIgnoreCase(lexer.stringVal())) {
                                idx.setName(this.exprParser.name());
                            }
                        }

                        if (identifierEquals("USING")) {
                            lexer.nextToken();
                            idx.setIndexType(lexer.stringVal());
                            lexer.nextToken();
                        }

                        accept(Token.LPAREN);
                        for (;;) {
                            idx.getColumns().add(this.exprParser.expr());
                            if (!(lexer.token() == (Token.COMMA))) {
                                break;
                            } else {
                                lexer.nextToken();
                            }
                        }
                        accept(Token.RPAREN);

                        stmt.getTableElementList().add(idx);
                    } else if (lexer.token() == (Token.KEY)) {
                        stmt.getTableElementList().add(parseConstraint());
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
                    } else {
                        SQLColumnDefinition column = this.exprParser.parseColumn();
                        stmt.getTableElementList().add(column);
                    }

                    if (!(lexer.token() == (Token.COMMA))) {
                        break;
                    } else {
                        lexer.nextToken();
                    }
                }
            }

            accept(Token.RPAREN);
        }

        for (;;) {
            if (identifierEquals("ENGINE")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("ENGINE", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("AUTO_INCREMENT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("AUTO_INCREMENT", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("AVG_ROW_LENGTH")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("AVG_ROW_LENGTH", this.exprParser.expr());
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

            if (identifierEquals("CHECKSUM")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("CHECKSUM", this.exprParser.expr());
                continue;
            }

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("COMMENT", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("CONNECTION")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("CONNECTION", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("DATA")) {
                lexer.nextToken();
                acceptIdentifier("DIRECTORY");
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("DATA DIRECTORY", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("DELAY_KEY_WRITE")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("DELAY_KEY_WRITE", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("INDEX")) {
                lexer.nextToken();
                acceptIdentifier("DIRECTORY");
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("INDEX DIRECTORY", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("INSERT_METHOD")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("INSERT_METHOD", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("KEY_BLOCK_SIZE")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("KEY_BLOCK_SIZE", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("MAX_ROWS")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("MAX_ROWS", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("MIN_ROWS")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("MIN_ROWS", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("PACK_KEYS")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("PACK_KEYS", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("PASSWORD")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("PASSWORD", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("ROW_FORMAT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                stmt.getTableOptions().put("ROW_FORMAT", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("STATS_AUTO_RECALC")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                stmt.getTableOptions().put("STATS_AUTO_RECALC", this.exprParser.expr());
                continue;
            }

            if (identifierEquals("STATS_PERSISTENT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                stmt.getTableOptions().put("STATS_PERSISTENT", this.exprParser.expr());
                continue;
            }
            
            if (identifierEquals("STATS_SAMPLE_PAGES")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                stmt.getTableOptions().put("STATS_SAMPLE_PAGES", this.exprParser.expr());
                continue;
            }
            
            if (lexer.token() == Token.UNION) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }

                accept(Token.LPAREN);
                SQLTableSource tableSrc = this.createSQLSelectParser().parseTableSource();
                stmt.getTableOptions().put("UNION", tableSrc);
                accept(Token.RPAREN);
                continue;
            }

            if (lexer.token() == Token.TABLESPACE) {
                lexer.nextToken();

                TableSpaceOption option = new TableSpaceOption();
                option.setName(this.exprParser.name());

                if (identifierEquals("STORAGE")) {
                    lexer.nextToken();
                    option.setStorage(this.exprParser.name());
                }

                stmt.getTableOptions().put("TABLESPACE", option);
                continue;
            }

            if (identifierEquals("TYPE")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.getTableOptions().put("TYPE", this.exprParser.expr());
                lexer.nextToken();
                continue;
            }

            if (identifierEquals("PARTITION")) {
                lexer.nextToken();
                accept(Token.BY);

                MySqlPartitioningClause partitionClause;

                boolean linera = false;
                if (identifierEquals("LINEAR")) {
                    lexer.nextToken();
                    linera = true;
                }

                if (lexer.token() == Token.KEY) {
                    MySqlPartitionByKey clause = new MySqlPartitionByKey();
                    lexer.nextToken();

                    if (linera) {
                        clause.setLinear(true);
                    }

                    accept(Token.LPAREN);
                    for (;;) {
                        clause.getColumns().add(this.exprParser.name());
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);

                    partitionClause = clause;

                    if (identifierEquals("PARTITIONS")) {
                        lexer.nextToken();
                        clause.setPartitionCount(this.exprParser.expr());
                    }
                } else if (identifierEquals("HASH")) {
                    lexer.nextToken();
                    MySqlPartitionByHash clause = new MySqlPartitionByHash();

                    if (linera) {
                        clause.setLinear(true);
                    }

                    accept(Token.LPAREN);
                    clause.setExpr(this.exprParser.expr());
                    accept(Token.RPAREN);
                    partitionClause = clause;

                    if (identifierEquals("PARTITIONS")) {
                        lexer.nextToken();
                        clause.setPartitionCount(this.exprParser.expr());
                    }

                } else if (identifierEquals("RANGE")) {
                    lexer.nextToken();
                    MySqlPartitionByRange clause = new MySqlPartitionByRange();

                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        clause.setExpr(this.exprParser.expr());
                        accept(Token.RPAREN);
                    } else {
                        acceptIdentifier("COLUMNS");
                        accept(Token.LPAREN);
                        for (;;) {
                            clause.getColumns().add(this.exprParser.name());
                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                            break;
                        }
                        accept(Token.RPAREN);
                    }
                    partitionClause = clause;

                    if (identifierEquals("PARTITIONS")) {
                        lexer.nextToken();
                        clause.setPartitionCount(this.exprParser.expr());
                    }
                    //

                } else if (identifierEquals("LIST")) {
                    lexer.nextToken();
                    MySqlPartitionByList clause = new MySqlPartitionByList();

                    if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        clause.setExpr(this.exprParser.expr());
                        accept(Token.RPAREN);
                    } else {
                        acceptIdentifier("COLUMNS");
                        accept(Token.LPAREN);
                        for (;;) {
                            clause.getColumns().add(this.exprParser.name());
                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }
                            break;
                        }
                        accept(Token.RPAREN);
                    }
                    partitionClause = clause;

                    if (identifierEquals("PARTITIONS")) {
                        lexer.nextToken();
                        clause.setPartitionCount(this.exprParser.expr());
                    }
                } else {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                }

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    for (;;) {
                        acceptIdentifier("PARTITION");

                        MySqlPartitioningDef partitionDef = new MySqlPartitioningDef();

                        partitionDef.setName(this.exprParser.name());

                        if (lexer.token() == Token.VALUES) {
                            lexer.nextToken();
                            if (lexer.token() == Token.IN) {
                                lexer.nextToken();
                                MySqlPartitioningDef.InValues values = new MySqlPartitioningDef.InValues();

                                accept(Token.LPAREN);
                                this.exprParser.exprList(values.getItems(), values);
                                accept(Token.RPAREN);
                                partitionDef.setValues(values);
                            } else {
                                acceptIdentifier("LESS");
                                acceptIdentifier("THAN");

                                MySqlPartitioningDef.LessThanValues values = new MySqlPartitioningDef.LessThanValues();

                                accept(Token.LPAREN);
                                this.exprParser.exprList(values.getItems(), values);
                                accept(Token.RPAREN);
                                partitionDef.setValues(values);
                            }
                        }

                        for (;;) {
                            if (identifierEquals("DATA")) {
                                lexer.nextToken();
                                acceptIdentifier("DIRECTORY");
                                if (lexer.token() == Token.EQ) {
                                    lexer.nextToken();
                                }
                                partitionDef.setDataDirectory(this.exprParser.expr());
                            } else if (lexer.token() == Token.INDEX) {
                                lexer.nextToken();
                                acceptIdentifier("DIRECTORY");
                                if (lexer.token() == Token.EQ) {
                                    lexer.nextToken();
                                }
                                partitionDef.setIndexDirectory(this.exprParser.expr());
                            } else {
                                break;
                            }
                        }

                        partitionClause.getPartitions().add(partitionDef);

                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        } else {
                            break;
                        }
                    }
                    accept(Token.RPAREN);
                }

                stmt.setPartitioning(partitionClause);
            }

            break;
        }

        if (lexer.token() == (Token.ON)) {
            throw new ParserException("TODO");
        }
        
        if (lexer.token() == (Token.AS)) {
            lexer.nextToken();
        }

        if (lexer.token() == (Token.SELECT)) {
            SQLSelect query = new MySqlSelectParser(this.exprParser).select();
            stmt.setQuery(query);
        }
        
        while (lexer.token() == (Token.HINT)) {
            this.exprParser.parseHints(stmt.getOptionHints());
        }
        return stmt;
    }

    private boolean parseTableOptionCharsetOrCollate(MySqlCreateTableStatement stmt) {
        if (identifierEquals("CHARACTER")) {
            lexer.nextToken();
            accept(Token.SET);
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            stmt.getTableOptions().put("CHARACTER SET", this.exprParser.expr());
            return true;
        }

        if (identifierEquals("CHARSET")) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            stmt.getTableOptions().put("CHARSET", this.exprParser.expr());
            return true;
        }

        if (identifierEquals("COLLATE")) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            stmt.getTableOptions().put("COLLATE", this.exprParser.expr());
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

        if (lexer.token() == (Token.KEY)) {
            lexer.nextToken();

            MySqlKey key = new MySqlKey();
            key.setHasConstaint(hasConstaint);

//            if (identifierEquals("USING")) {
//                lexer.nextToken();
//                key.setIndexType(lexer.stringVal());
//                lexer.nextToken();
//            }

            if (lexer.token() == Token.IDENTIFIER) {
                SQLName indexName = this.exprParser.name();
                if (indexName != null) {
                    key.setIndexName(indexName);
                }
            }
            
            //5.5语法 USING BTREE 放在index 名字后
            if (identifierEquals("USING")) {
                lexer.nextToken();
                key.setIndexType(lexer.stringVal());
                lexer.nextToken();
            }

            accept(Token.LPAREN);
            for (;;) {
                key.getColumns().add(this.exprParser.expr());
                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                } else {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);

            if (name != null) {
                key.setName(name);
            }

            if (identifierEquals("USING")) {
                lexer.nextToken();
                key.setIndexType(lexer.stringVal());
                lexer.nextToken();
            }

            return key;
        }

        if (lexer.token() == Token.PRIMARY) {
            MySqlPrimaryKey pk = this.getExprParser().parsePrimaryKey();
            pk.setName(name);
            pk.setHasConstaint(hasConstaint);
            return (SQLTableConstraint) pk;
        }

        if (lexer.token() == Token.UNIQUE) {
            MySqlUnique uk = this.getExprParser().parseUnique();
            uk.setName(name);
            uk.setHasConstaint(hasConstaint);
            return (SQLTableConstraint) uk;
        }

        if (lexer.token() == Token.FOREIGN) {
            MysqlForeignKey fk = this.getExprParser().parseForeignKey();
            fk.setName(name);
            fk.setHasConstraint(hasConstaint);
            return (SQLTableConstraint) fk;
        }

        throw new ParserException("TODO :" + lexer.token());
    }
}
