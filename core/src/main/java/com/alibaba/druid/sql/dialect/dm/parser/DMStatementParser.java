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
package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.dm.ast.stmt.DMAlterTableOption;
import com.alibaba.druid.sql.dialect.dm.ast.stmt.DMAlterTableOption.OptionType;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableModify;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableMoveTablespace;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraint;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleCreateTableParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleExprParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class DMStatementParser extends OracleStatementParser {
    public DMStatementParser(String sql) {
        super(new DMExprParser(sql));
    }

    public DMStatementParser(String sql, SQLParserFeature... features) {
        super(new DMExprParser(sql, features));
    }

    @Override
    public DbType getDbType() {
        return DbType.dm;
    }

    @Override
    public OracleCreateTableParser getSQLCreateTableParser() {
        return new OracleCreateTableParser((OracleExprParser) this.exprParser);
    }

    @Override
    protected SQLStatement parseAlterTable() {
        lexer.nextToken();
        SQLAlterTableStatement stmt = new SQLAlterTableStatement(getDbType());
        stmt.setName(this.exprParser.name());

        for (;;) {
            if (lexer.identifierEquals(FnvHash.Constants.ADD)) {
                lexer.nextToken();
                parseAlterTableAdd(stmt);
                continue;
            } else if (lexer.identifierEquals(FnvHash.Constants.MOVE)) {
                lexer.nextToken();
                if (lexer.token() == Token.TABLESPACE) {
                    lexer.nextToken();
                    OracleAlterTableMoveTablespace item = new OracleAlterTableMoveTablespace();
                    item.setName(this.exprParser.name());
                    stmt.addItem(item);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
            } else if (lexer.identifierEquals("RENAME")) {
                stmt.addItem(parseAlterTableRename());
            } else if (lexer.identifierEquals("MODIFY")) {
                lexer.nextToken();
                parseAlterTableModify(stmt);
                continue;
            } else if (lexer.token() == Token.TRUNCATE) {
                lexer.nextToken();
                parseAlterTableTruncate(stmt);
                continue;
            } else if (lexer.token() == Token.DROP) {
                parseAlterTableDrop(stmt);
                continue;
            } else if (lexer.token() == Token.DISABLE) {
                lexer.nextToken();
                parseAlterTableDisable(stmt);
                continue;
            } else if (lexer.token() == Token.ENABLE) {
                lexer.nextToken();
                parseAlterTableEnable(stmt);
                continue;
            } else if (lexer.token() == Token.ALTER) {
                lexer.nextToken();
                parseAlterColumn(stmt);
                continue;
            } else if (lexer.identifierEquals("PARALLEL")) {
                lexer.nextToken();
                DMAlterTableOption item = new DMAlterTableOption(OptionType.PARALLEL);
                if (lexer.token() == Token.LITERAL_INT) {
                    item.setValue(this.exprParser.integerExpr());
                }
                stmt.addItem(item);
                continue;
            } else if (lexer.identifierEquals("NOPARALLEL")) {
                lexer.nextToken();
                stmt.addItem(new DMAlterTableOption(OptionType.NOPARALLEL));
                continue;
            } else if (lexer.identifierEquals("READ")) {
                lexer.nextToken();
                if (lexer.token() == Token.ONLY || lexer.identifierEquals("ONLY")) {
                    lexer.nextToken();
                    stmt.addItem(new DMAlterTableOption(OptionType.READ_ONLY));
                } else if (lexer.identifierEquals("WRITE")) {
                    lexer.nextToken();
                    stmt.addItem(new DMAlterTableOption(OptionType.READ_WRITE));
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
                continue;
            } else if (lexer.identifierEquals("AUTO_INCREMENT")) {
                lexer.nextToken();
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                }
                DMAlterTableOption item = new DMAlterTableOption(OptionType.AUTO_INCREMENT);
                item.setValue(this.exprParser.expr());
                stmt.addItem(item);
                continue;
            }
            break;
        }
        return stmt;
    }

    private void parseAlterTableAdd(SQLAlterTableStatement stmt) {
        if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
            parseDMAlterTableAddColumn(stmt);
        } else if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            SQLAlterTableAddColumn item = parseAlterTableAddColumn();
            stmt.addItem(item);
            accept(Token.RPAREN);
        } else if (lexer.token() == Token.IF) {
            parseDMAlterTableAddColumn(stmt);
        } else if (lexer.token() == Token.CONSTRAINT
                || lexer.token() == Token.FOREIGN
                || lexer.token() == Token.PRIMARY
                || lexer.token() == Token.UNIQUE
                || lexer.token() == Token.CHECK) {
            OracleConstraint constraint = ((OracleExprParser) this.exprParser).parseConstraint();
            SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint();
            constraint.setParent(item);
            item.setParent(stmt);
            item.setConstraint(constraint);
            stmt.addItem(item);
        } else if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.LITERAL_ALIAS) {
            SQLAlterTableAddColumn item = parseAlterTableAddColumn();
            stmt.addItem(item);
        } else {
            throw new ParserException("TODO : " + lexer.info());
        }
    }

    private void parseDMAlterTableAddColumn(SQLAlterTableStatement stmt) {
        boolean ifNotExists = false;
        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            ifNotExists = true;
        }
        SQLAlterTableAddColumn item = parseAlterTableAddColumn();
        item.setIfNotExists(ifNotExists);
        stmt.addItem(item);
    }

    private void parseAlterTableModify(SQLAlterTableStatement stmt) {
        if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
            SQLName constraintName = this.exprParser.name();
            if (lexer.token() == Token.ENABLE) {
                lexer.nextToken();
                SQLAlterTableEnableConstraint item = new SQLAlterTableEnableConstraint();
                item.setConstraintName(constraintName);
                stmt.addItem(item);
            } else if (lexer.token() == Token.DISABLE) {
                lexer.nextToken();
                SQLAlterTableDisableConstraint item = new SQLAlterTableDisableConstraint();
                item.setConstraintName(constraintName);
                stmt.addItem(item);
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
        } else if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            OracleAlterTableModify item = new OracleAlterTableModify();
            for (;;) {
                SQLColumnDefinition columnDef = this.exprParser.parseColumn();
                item.addColumn(columnDef);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
            stmt.addItem(item);
        } else {
            OracleAlterTableModify item = new OracleAlterTableModify();
            SQLColumnDefinition columnDef = this.exprParser.parseColumn();
            item.addColumn(columnDef);
            stmt.addItem(item);
        }
    }

    private void parseAlterTableTruncate(SQLAlterTableStatement stmt) {
        // Handle both Token.PARTITION and identifier PARTITION
        if (lexer.token() == Token.PARTITION || lexer.identifierEquals("PARTITION")) {
            lexer.nextToken(); // consume PARTITION
            OracleAlterTableTruncatePartition item = new OracleAlterTableTruncatePartition();
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                item.setName(this.exprParser.name());
                accept(Token.RPAREN);
            } else {
                // Parse single identifier as partition name, don't use exprParser.name() as
                // it's greedy
                SQLName partitionName = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
                item.setName(partitionName);
            }
            StorageOption storageOption = parseStorageOption();
            if (storageOption != StorageOption.NONE) {
                item.putAttribute("dm.storage", storageOption.value);
            }
            stmt.addItem(item);
        } else if (lexer.identifierEquals("SUBPARTITION")) {
            lexer.nextToken();
            SQLAlterTableTruncatePartition item = new SQLAlterTableTruncatePartition();
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                item.addPartition(this.exprParser.name());
                accept(Token.RPAREN);
            } else {
                item.addPartition(this.exprParser.name());
            }
            item.putAttribute("dm.subpartition", Boolean.TRUE);
            StorageOption storageOption = parseStorageOption();
            if (storageOption != StorageOption.NONE) {
                item.putAttribute("dm.storage", storageOption.value);
            }
            stmt.addItem(item);
        } else {
            throw new ParserException("TODO : " + lexer.info());
        }
    }

    private StorageOption parseStorageOption() {
        if (lexer.token() == Token.DROP || lexer.identifierEquals("DROP")) {
            lexer.nextToken();
            if (lexer.token() == Token.STORAGE || lexer.identifierEquals("STORAGE")) {
                lexer.nextToken();
            }
            return StorageOption.DROP;
        } else if (lexer.identifierEquals("REUSE")) {
            lexer.nextToken();
            if (lexer.token() == Token.STORAGE || lexer.identifierEquals("STORAGE")) {
                lexer.nextToken();
            }
            return StorageOption.REUSE;
        }
        return StorageOption.NONE;
    }

    private void parseAlterTableDrop(SQLAlterTableStatement stmt) {
        lexer.nextToken();

        if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
            SQLAlterTableDropConstraint item = new SQLAlterTableDropConstraint();
            item.setConstraintName(this.exprParser.name());
            if (lexer.token() == Token.CASCADE) {
                lexer.nextToken();
                item.setCascade(true);
            } else if (lexer.token() == Token.RESTRICT || lexer.identifierEquals("RESTRICT")) {
                lexer.nextToken();
                item.setRestrict(true);
            }
            stmt.addItem(item);
        } else if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            if (lexer.token() == Token.IF) {
                lexer.nextToken();
                accept(Token.EXISTS);
                item.setIfExists(true);
            }
            this.exprParser.names(item.getColumns());
            // DM CASCADE / RESTRICT
            if (lexer.token() == Token.CASCADE) {
                lexer.nextToken();
                item.setCascade(true);
            } else if (lexer.token() == Token.RESTRICT || lexer.identifierEquals("RESTRICT")) {
                lexer.nextToken();
                item.setRestrict(true);
            }
            stmt.addItem(item);
        } else if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());
            stmt.addItem(item);
            accept(Token.RPAREN);
        } else if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            SQLAlterTableDropPartition item = new SQLAlterTableDropPartition();
            item.addPartition(this.exprParser.name());
            stmt.addItem(item);
        } else if (lexer.token() == Token.INDEX) {
            lexer.nextToken();
            SQLName indexName = this.exprParser.name();
            SQLAlterTableDropIndex item = new SQLAlterTableDropIndex();
            item.setIndexName(indexName);
            stmt.addItem(item);
        } else if (lexer.token() == Token.PRIMARY) {
            lexer.nextToken();
            accept(Token.KEY);
            SQLAlterTableDropPrimaryKey item = new SQLAlterTableDropPrimaryKey();
            // DM supports CASCADE / RESTRICT after DROP PRIMARY KEY
            if (lexer.token() == Token.CASCADE) {
                lexer.nextToken();
                item.putAttribute("dm.cascade", Boolean.TRUE);
            } else if (lexer.token() == Token.RESTRICT || lexer.identifierEquals("RESTRICT")) {
                lexer.nextToken();
                item.putAttribute("dm.restrict", Boolean.TRUE);
            }
            stmt.addItem(item);
        } else if (lexer.token() == Token.IDENTITY || lexer.identifierEquals("IDENTITY")) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            item.getColumns().add(new SQLIdentifierExpr("IDENTITY"));
            stmt.addItem(item);
        } else if (lexer.identifierEquals("AUTO_INCREMENT")) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            item.getColumns().add(new SQLIdentifierExpr("AUTO_INCREMENT"));
            stmt.addItem(item);
        } else if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.LITERAL_ALIAS) {
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());
            if (lexer.token() == Token.CASCADE) {
                lexer.nextToken();
                item.setCascade(true);
            } else if (lexer.token() == Token.RESTRICT || lexer.identifierEquals("RESTRICT")) {
                lexer.nextToken();
                item.setRestrict(true);
            }
            stmt.addItem(item);
        } else {
            throw new ParserException("TODO " + lexer.info());
        }
    }

    private void parseAlterTableDisable(SQLAlterTableStatement stmt) {
        if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
            SQLAlterTableDisableConstraint item = new SQLAlterTableDisableConstraint();
            item.setConstraintName(this.exprParser.name());
            stmt.addItem(item);
        } else if (lexer.token() == Token.ALL) {
            lexer.nextToken();
            if (lexer.identifierEquals("TRIGGERS")) {
                lexer.nextToken();
                stmt.addItem(new DMAlterTableOption(OptionType.DISABLE_ALL_TRIGGERS));
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
        } else {
            throw new ParserException("TODO : " + lexer.info());
        }
    }

    private void parseAlterTableEnable(SQLAlterTableStatement stmt) {
        if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
            SQLAlterTableEnableConstraint item = new SQLAlterTableEnableConstraint();
            item.setConstraintName(this.exprParser.name());
            stmt.addItem(item);
        } else if (lexer.token() == Token.ALL) {
            lexer.nextToken();
            if (lexer.identifierEquals("TRIGGERS")) {
                lexer.nextToken();
                stmt.addItem(new DMAlterTableOption(OptionType.ENABLE_ALL_TRIGGERS));
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
        } else {
            throw new ParserException("TODO : " + lexer.info());
        }
    }

    private void parseAlterColumn(SQLAlterTableStatement stmt) {
        if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
        }
        SQLName columnName = this.exprParser.name();

        if (lexer.token() == Token.SET) {
            lexer.nextToken();
            if (lexer.token() == Token.DEFAULT) {
                lexer.nextToken();
                SQLExpr defaultExpr = this.exprParser.expr();
                SQLAlterTableAlterColumn item = new SQLAlterTableAlterColumn();
                SQLColumnDefinition column = new SQLColumnDefinition();
                column.setName(columnName);
                item.setColumn(column);
                item.setSetDefault(defaultExpr);
                stmt.addItem(item);
            } else if (lexer.token() == Token.NOT) {
                lexer.nextToken();
                accept(Token.NULL);
                SQLAlterTableAlterColumn item = new SQLAlterTableAlterColumn();
                SQLColumnDefinition column = new SQLColumnDefinition();
                column.setName(columnName);
                item.setColumn(column);
                item.setSetNotNull(true);
                stmt.addItem(item);
            } else if (lexer.token() == Token.NULL) {
                lexer.nextToken();
                SQLAlterTableAlterColumn item = new SQLAlterTableAlterColumn();
                SQLColumnDefinition column = new SQLColumnDefinition();
                column.setName(columnName);
                item.setColumn(column);
                item.setSetNotNull(false);
                stmt.addItem(item);
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
        } else if (lexer.token() == Token.DROP) {
            lexer.nextToken();
            if (lexer.token() == Token.DEFAULT) {
                lexer.nextToken();
                SQLAlterTableAlterColumn item = new SQLAlterTableAlterColumn();
                SQLColumnDefinition column = new SQLColumnDefinition();
                column.setName(columnName);
                item.setColumn(column);
                item.setDropDefault(true);
                stmt.addItem(item);
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
        } else {
            throw new ParserException("TODO : " + lexer.info());
        }
    }

    private enum StorageOption {
        NONE(""),
        DROP("DROP"),
        REUSE("REUSE");

        private final String value;

        StorageOption(String value) {
            this.value = value;
        }
    }
}
