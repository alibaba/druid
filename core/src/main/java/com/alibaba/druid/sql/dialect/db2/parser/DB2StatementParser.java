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

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2CreateSchemaStatement;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2DropSchemaStatement;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2ValuesStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

import static com.alibaba.druid.sql.parser.Token.*;

public class DB2StatementParser extends SQLStatementParser {
    public DB2StatementParser(String sql) {
        super(new DB2ExprParser(sql));
    }

    public DB2StatementParser(String sql, SQLParserFeature... features) {
        super(new DB2ExprParser(sql, features));
    }

    public DB2StatementParser(Lexer lexer) {
        super(new DB2ExprParser(lexer));
    }

    public DB2SelectParser createSQLSelectParser() {
        return new DB2SelectParser(this.exprParser, selectListCache);
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.VALUES) {
            lexer.nextToken();
            DB2ValuesStatement stmt = new DB2ValuesStatement();
            stmt.setExpr(this.exprParser.expr());
            statementList.add(stmt);
            return true;
        }

        return false;
    }

    @Override
    public SQLStatement parseCreateSchema() {
        accept(Token.CREATE);
        accept(Token.SCHEMA);

        DB2CreateSchemaStatement stmt = new DB2CreateSchemaStatement();

        stmt.setSchemaName(this.exprParser.name());

        while (lexer.token() != SEMI && !lexer.isEOF()) {
            if (lexer.token() == Token.CREATE) {
                Lexer.SavePoint mark = lexer.markOut();
                lexer.nextToken();
                if (lexer.token() == Token.TABLE) {
                    lexer.reset(mark);
                    stmt.getCreateStatements().add(this.parseCreateTable());
                    continue;
                } else if (lexer.token() == VIEW) {
                    lexer.reset(mark);
                    stmt.getCreateStatements().add(this.parseCreateView());
                    continue;
                } else if (lexer.token() == INDEX) {
                    lexer.reset(mark);
                    stmt.getCreateStatements().add(this.parseCreateIndex());
                    continue;
                } else if (lexer.token() == SEQUENCE) {
                    lexer.reset(mark);
                    stmt.getCreateStatements().add(this.parseCreateSequence());
                    continue;
                } else if (lexer.token() == TRIGGER) {
                    lexer.reset(mark);
                    stmt.getCreateStatements().add(this.parseCreateTrigger());
                    continue;
                }
            }

            throw new ParserException("syntax error. " + lexer.info());
        }

        return stmt;
    }

    @Override
    protected SQLDropStatement parseDropSchema(boolean physical) {
        DB2DropSchemaStatement stmt = new DB2DropSchemaStatement();

        if (lexer.token() == Token.SCHEMA) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setSchemaName(name);

        if (lexer.token() == Token.CASCADE) {
            lexer.nextToken();
            stmt.setCascade(true);
        } else {
            stmt.setCascade(false);
        }
        if (lexer.token() == Token.RESTRICT) {
            lexer.nextToken();
            stmt.setRestrict(true);
        }

        return stmt;
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new DB2CreateTableParser(this.exprParser);
    }

    protected SQLAlterTableAlterColumn parseAlterColumn() {
        if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
        }

        SQLColumnDefinition column = this.exprParser.parseColumn();

        SQLAlterTableAlterColumn alterColumn = new SQLAlterTableAlterColumn();
        alterColumn.setColumn(column);

        if (column.getDataType() == null && column.getConstraints().isEmpty()) {
            if (lexer.token() == Token.SET) {
                lexer.nextToken();
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    accept(Token.NULL);
                    alterColumn.setSetNotNull(true);
                } else if (lexer.token() == Token.DEFAULT) {
                    lexer.nextToken();
                    SQLExpr defaultValue = this.exprParser.expr();
                    alterColumn.setSetDefault(defaultValue);
                } else if (lexer.identifierEquals(FnvHash.Constants.DATA)) {
                    lexer.nextToken();
                    acceptIdentifier("TYPE");
                    SQLDataType dataType = this.exprParser.parseDataType();
                    alterColumn.setDataType(dataType);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
            } else if (lexer.token() == Token.DROP) {
                lexer.nextToken();
                if (lexer.token() == Token.NOT) {
                    lexer.nextToken();
                    accept(Token.NULL);
                    alterColumn.setDropNotNull(true);
                } else {
                    accept(Token.DEFAULT);
                    alterColumn.setDropDefault(true);
                }
            }
        }

        return alterColumn;
    }

    @Override
    public SQLDeleteStatement parseDeleteStatement() {
        SQLDeleteStatement deleteStatement = new SQLDeleteStatement(getDbType());

        if (lexer.token() == Token.DELETE) {
            lexer.nextToken();
            if (lexer.token() == (Token.FROM)) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            SQLName tableName = exprParser.name();

            deleteStatement.setTableName(tableName);

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
                deleteStatement.setFrom(tableSource);
            }

            // try to parse alias
            deleteStatement.setAlias(tableAlias());
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            SQLExpr where = this.exprParser.expr();
            deleteStatement.setWhere(where);
        }

        return deleteStatement;
    }

    @Override
    public SQLStatement parseTruncate() {
        accept(Token.TRUNCATE);
        if (!lexer.nextIf(Token.TABLE)) {
            lexer.nextIfIdentifier("TABLE");
        }
        SQLTruncateStatement stmt = new SQLTruncateStatement(getDbType());

        SQLName name = this.exprParser.name();
        stmt.addTableSource(name);

        for (;;) {
            if (lexer.token() == Token.DROP) {
                lexer.nextToken();
                acceptIdentifier("STORAGE");
                stmt.setDropStorage(true);
                continue;
            }

            if (lexer.identifierEquals("REUSE")) {
                lexer.nextToken();
                acceptIdentifier("STORAGE");
                stmt.setReuseStorage(true);
                continue;
            }

            if (lexer.identifierEquals("IGNORE")) {
                lexer.nextToken();
                accept(Token.DELETE);
                acceptIdentifier("TRIGGERS");
                stmt.setIgnoreDeleteTriggers(true);
                continue;
            }

            if (lexer.token() == Token.RESTRICT) {
                lexer.nextToken();
                accept(Token.WHEN);
                accept(Token.DELETE);
                acceptIdentifier("TRIGGERS");
                stmt.setRestrictWhenDeleteTriggers(true);
                continue;
            }

            if (lexer.token() == Token.CONTINUE) {
                lexer.nextToken();
                accept(Token.IDENTITY);
                continue;
            }

            if (lexer.token() == Token.RESTART) {
                lexer.nextToken();
                accept(Token.IDENTITY);
                stmt.setRestartIdentity(Boolean.TRUE);
                continue;
            }

            if (lexer.identifierEquals("IMMEDIATE")) {
                lexer.nextToken();
                stmt.setImmediate(true);
                continue;
            }

            break;
        }

        return stmt;
    }
}
