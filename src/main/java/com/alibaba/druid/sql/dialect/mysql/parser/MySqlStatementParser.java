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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLCommentHint;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLNCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddIndex;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableKeys;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropColumnItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropForeignKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropIndex;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableKeys;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement.ValuesClause;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.MysqlForeignKey;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableAddColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableChangeColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableCharacter;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableDiscardTablespace;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableImportTablespace;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableModifyColumn;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableOption;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAlterUserStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlAnalyzeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlBinlogStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlBlockStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCommitStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateIndexStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDescribeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlHelpStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlHintStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadXmlStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLockTableStatement.LockType;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlOptimizeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPrepareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRenameTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlReplaceStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlResetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRollbackStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock.Limit;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetCharSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetNamesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetPasswordStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetTransactionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowAuthorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowBinLogEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowBinaryLogsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCharacterSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCollationStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowColumnsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowContributorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateDatabaseStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateEventStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateFunctionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateProcedureStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateTriggerStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowCreateViewStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowDatabasesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEngineStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEnginesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowErrorsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionCodeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowFunctionStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowGrantsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowIndexesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowKeysStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterLogsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowOpenTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowPluginsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowPrivilegesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureCodeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcedureStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProcessListStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProfileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowProfilesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowRelayLogEventsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveHostsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowSlaveStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTableStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTriggersStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowVariantsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowWarningsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStartTransactionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUnlockTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;

public class MySqlStatementParser extends SQLStatementParser {

    private static final String AUTO_INCREMENT = "AUTO_INCREMENT";
    private static final String COLLATE2       = "COLLATE";
    private static final String CHAIN          = "CHAIN";
    private static final String ENGINES        = "ENGINES";
    private static final String ENGINE         = "ENGINE";
    private static final String BINLOG         = "BINLOG";
    private static final String EVENTS         = "EVENTS";
    private static final String CHARACTER      = "CHARACTER";
    private static final String SESSION        = "SESSION";
    private static final String GLOBAL         = "GLOBAL";
    private static final String VARIABLES      = "VARIABLES";
    private static final String ERRORS         = "ERRORS";
    private static final String STATUS         = "STATUS";
    private static final String IGNORE         = "IGNORE";
    private static final String RESET          = "RESET";
    private static final String DESCRIBE       = "DESCRIBE";
    private static final String WRITE          = "WRITE";
    private static final String READ           = "READ";
    private static final String LOCAL          = "LOCAL";
    private static final String TABLES         = "TABLES";
    private static final String TEMPORARY      = "TEMPORARY";
    private static final String SPATIAL        = "SPATIAL";
    private static final String FULLTEXT       = "FULLTEXT";
    private static final String DELAYED        = "DELAYED";
    private static final String LOW_PRIORITY   = "LOW_PRIORITY";

    public MySqlStatementParser(String sql){
        super(new MySqlExprParser(sql));
    }

    public MySqlStatementParser(Lexer lexer){
        super(new MySqlExprParser(lexer));
    }

    public SQLCreateTableStatement parseCreateTable() {
        MySqlCreateTableParser parser = new MySqlCreateTableParser(this.exprParser);
        return parser.parseCrateTable();
    }

    public SQLSelectStatement parseSelect() {
        MySqlSelectParser selectParser = new MySqlSelectParser(this.exprParser);
        return new SQLSelectStatement(selectParser.select(), JdbcConstants.MYSQL);
    }

    public SQLUpdateStatement parseUpdateStatement() {
        MySqlUpdateStatement stmt = createUpdateStatement();

        if (lexer.token() == Token.UPDATE) {
            lexer.nextToken();

            if (identifierEquals(LOW_PRIORITY)) {
                lexer.nextToken();
                stmt.setLowPriority(true);
            }

            if (identifierEquals(IGNORE)) {
                lexer.nextToken();
                stmt.setIgnore(true);
            }

            SQLTableSource tableSource = this.exprParser.createSelectParser().parseTableSource();
            stmt.setTableSource(tableSource);
        }

        parseUpdateSet(stmt);

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            stmt.setWhere(this.exprParser.expr());
        }

        stmt.setOrderBy(this.exprParser.parseOrderBy());

        stmt.setLimit(parseLimit());

        return stmt;
    }

    protected MySqlUpdateStatement createUpdateStatement() {
        return new MySqlUpdateStatement();
    }

    public MySqlDeleteStatement parseDeleteStatement() {
        MySqlDeleteStatement deleteStatement = new MySqlDeleteStatement();

        if (lexer.token() == Token.DELETE) {
            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            if (identifierEquals(LOW_PRIORITY)) {
                deleteStatement.setLowPriority(true);
                lexer.nextToken();
            }

            if (identifierEquals("QUICK")) {
                deleteStatement.setQuick(true);
                lexer.nextToken();
            }

            if (identifierEquals(IGNORE)) {
                deleteStatement.setIgnore(true);
                lexer.nextToken();
            }

            if (lexer.token() == Token.IDENTIFIER) {
                deleteStatement.setTableSource(createSQLSelectParser().parseTableSource());

                if (lexer.token() == Token.FROM) {
                    lexer.nextToken();
                    SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
                    deleteStatement.setFrom(tableSource);
                }
            } else if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                deleteStatement.setTableSource(createSQLSelectParser().parseTableSource());
            } else {
                throw new ParserException("syntax error");
            }

            if (identifierEquals("USING")) {
                lexer.nextToken();

                SQLTableSource tableSource = createSQLSelectParser().parseTableSource();
                deleteStatement.setUsing(tableSource);
            }
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            SQLExpr where = this.exprParser.expr();
            deleteStatement.setWhere(where);
        }

        if (lexer.token() == (Token.ORDER)) {
            SQLOrderBy orderBy = exprParser.parseOrderBy();
            deleteStatement.setOrderBy(orderBy);
        }

        deleteStatement.setLimit(parseLimit());

        return deleteStatement;
    }

    public SQLStatement parseCreate() {
        char markChar = lexer.current();
        int markBp = lexer.bp();

        accept(Token.CREATE);

        boolean replace = false;
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            replace = true;
        }

        List<SQLCommentHint> hints = this.exprParser.parseHints();

        if (lexer.token() == Token.TABLE || identifierEquals(TEMPORARY)) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }
            MySqlCreateTableParser parser = new MySqlCreateTableParser(this.exprParser);
            MySqlCreateTableStatement stmt = parser.parseCrateTable(false);
            stmt.setHints(hints);
            return stmt;
        }

        if (lexer.token() == Token.DATABASE) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }
            return parseCreateDatabase();
        }

        if (lexer.token() == Token.UNIQUE || lexer.token() == Token.INDEX || identifierEquals(FULLTEXT)
            || identifierEquals(SPATIAL)) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }
            return parseCreateIndex(false);
        }

        if (lexer.token() == Token.USER) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }
            return parseCreateUser();
        }

        if (lexer.token() == Token.VIEW) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }

            return parseCreateView();
        }

        if (lexer.token() == Token.TRIGGER) {
            if (replace) {
                lexer.reset(markBp, markChar, Token.CREATE);
            }
            return parseCreateTrigger();
        }

        throw new ParserException("TODO " + lexer.token());
    }

    public SQLStatement parseCreateIndex(boolean acceptCreate) {
        if (acceptCreate) {
            accept(Token.CREATE);
        }

        MySqlCreateIndexStatement stmt = new MySqlCreateIndexStatement();

        if (lexer.token() == Token.UNIQUE) {
            stmt.setType("UNIQUE");
            lexer.nextToken();
        } else if (identifierEquals(FULLTEXT)) {
            stmt.setType(FULLTEXT);
            lexer.nextToken();
        } else if (identifierEquals(SPATIAL)) {
            stmt.setType(SPATIAL);
            lexer.nextToken();
        }

        accept(Token.INDEX);

        stmt.setName(this.exprParser.name());

        parseCreateIndexUsing(stmt);

        accept(Token.ON);

        stmt.setTable(this.exprParser.name());

        accept(Token.LPAREN);

        for (;;) {
            SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
            item.setParent(stmt);
            stmt.getItems().add(item);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);

        parseCreateIndexUsing(stmt);

        return stmt;
    }

    private void parseCreateIndexUsing(MySqlCreateIndexStatement stmt) {
        if (identifierEquals("USING")) {
            lexer.nextToken();

            if (identifierEquals("BTREE")) {
                stmt.setUsing("BTREE");
                lexer.nextToken();
            } else if (identifierEquals("HASH")) {
                stmt.setUsing("HASH");
                lexer.nextToken();
            } else {
                throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
            }
        }
    }

    public SQLStatement parseCreateUser() {
        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();
        }

        accept(Token.USER);

        MySqlCreateUserStatement stmt = new MySqlCreateUserStatement();

        for (;;) {
            MySqlCreateUserStatement.UserSpecification userSpec = new MySqlCreateUserStatement.UserSpecification();

            SQLExpr expr = exprParser.primary();
            userSpec.setUser(expr);

            if (lexer.token() == Token.IDENTIFIED) {
                lexer.nextToken();
                if (lexer.token() == Token.BY) {
                    lexer.nextToken();

                    if (identifierEquals("PASSWORD")) {
                        lexer.nextToken();
                        userSpec.setPasswordHash(true);
                    }

                    SQLCharExpr password = (SQLCharExpr) this.exprParser.expr();
                    userSpec.setPassword(password);
                } else if (lexer.token() == Token.WITH) {
                    lexer.nextToken();
                    userSpec.setAuthPlugin(this.exprParser.expr());
                }
            }

            stmt.getUsers().add(userSpec);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return stmt;
    }

    public SQLStatement parseKill() {
        accept(Token.KILL);

        MySqlKillStatement stmt = new MySqlKillStatement();

        if (identifierEquals("CONNECTION")) {
            stmt.setType(MySqlKillStatement.Type.CONNECTION);
            lexer.nextToken();
        } else if (identifierEquals("QUERY")) {
            stmt.setType(MySqlKillStatement.Type.QUERY);
            lexer.nextToken();
        } else {
            throw new ParserException("not support kill type " + lexer.token());
        }

        SQLExpr threadId = this.exprParser.expr();
        stmt.setThreadId(threadId);

        return stmt;
    }

    public SQLStatement parseBinlog() {
        acceptIdentifier("binlog");

        MySqlBinlogStatement stmt = new MySqlBinlogStatement();

        SQLExpr expr = this.exprParser.expr();
        stmt.setExpr(expr);

        return stmt;
    }

    public MySqlAnalyzeStatement parseAnalyze() {
        accept(Token.ANALYZE);
        accept(Token.TABLE);

        MySqlAnalyzeStatement stmt = new MySqlAnalyzeStatement();
        List<SQLName> names = new ArrayList<SQLName>();
        this.exprParser.names(names, stmt);

        for (SQLName name : names) {
            stmt.getTableSources().add(new SQLExprTableSource(name));
        }
        return stmt;
    }

    public MySqlOptimizeStatement parseOptimize() {
        accept(Token.OPTIMIZE);
        accept(Token.TABLE);

        MySqlOptimizeStatement stmt = new MySqlOptimizeStatement();
        List<SQLName> names = new ArrayList<SQLName>();
        this.exprParser.names(names, stmt);

        for (SQLName name : names) {
            stmt.getTableSources().add(new SQLExprTableSource(name));
        }
        return stmt;
    }

    public SQLStatement parseReset() {
        acceptIdentifier(RESET);

        MySqlResetStatement stmt = new MySqlResetStatement();

        for (;;) {
            if (lexer.token() == Token.IDENTIFIER) {
                if (identifierEquals("QUERY")) {
                    lexer.nextToken();
                    accept(Token.CACHE);
                    stmt.getOptions().add("QUERY CACHE");
                } else {
                    stmt.getOptions().add(lexer.stringVal());
                    lexer.nextToken();
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
            }
            break;
        }

        return stmt;
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.KILL) {
            SQLStatement stmt = parseKill();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("PREPARE")) {
            MySqlPrepareStatement stmt = parsePrepare();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("EXECUTE")) {
            MySqlExecuteStatement stmt = parseExecute();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("LOAD")) {
            SQLStatement stmt = parseLoad();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.REPLACE) {
            MySqlReplaceStatement stmt = parseReplicate();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("START")) {
            MySqlStartTransactionStatement stmt = parseStart();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.SHOW) {
            SQLStatement stmt = parseShow();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals(BINLOG)) {
            SQLStatement stmt = parseBinlog();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals(RESET)) {
            SQLStatement stmt = parseReset();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.ANALYZE) {
            SQLStatement stmt = parseAnalyze();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.OPTIMIZE) {
            SQLStatement stmt = parseOptimize();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("HELP")) {
            lexer.nextToken();
            MySqlHelpStatement stmt = new MySqlHelpStatement();
            stmt.setContent(this.exprParser.primary());
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.DESC || identifierEquals(DESCRIBE)) {
            SQLStatement stmt = parseDescribe();
            statementList.add(stmt);
            return true;
        }

        if (lexer.token() == Token.LOCK) {
            lexer.nextToken();
            acceptIdentifier(TABLES);

            MySqlLockTableStatement stmt = new MySqlLockTableStatement();
            stmt.setTableSource(this.exprParser.name());

            if (identifierEquals(READ)) {
                lexer.nextToken();
                if (identifierEquals(LOCAL)) {
                    lexer.nextToken();
                    stmt.setLockType(LockType.READ_LOCAL);
                } else {
                    stmt.setLockType(LockType.READ);
                }
            } else if (identifierEquals(WRITE)) {
                stmt.setLockType(LockType.WRITE);
            } else if (identifierEquals(LOW_PRIORITY)) {
                lexer.nextToken();
                acceptIdentifier(WRITE);
                stmt.setLockType(LockType.LOW_PRIORITY_WRITE);
            }

            if (lexer.token() == Token.HINT) {
                stmt.setHints(this.exprParser.parseHints());
            }
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("UNLOCK")) {
            lexer.nextToken();
            acceptIdentifier(TABLES);
            statementList.add(new MySqlUnlockTablesStatement());
            return true;
        }

        if (lexer.token() == Token.HINT) {
            statementList.add(this.parseHint());
            return true;
        }

        if (lexer.token() == Token.BEGIN) {
            statementList.add(this.parseBlock());
            return true;
        }

        return false;
    }

    public MySqlBlockStatement parseBlock() {
        MySqlBlockStatement block = new MySqlBlockStatement();

        accept(Token.BEGIN);

        parseStatementList(block.getStatementList());

        accept(Token.END);

        return block;
    }

    public MySqlDescribeStatement parseDescribe() {
        if (lexer.token() == Token.DESC || identifierEquals(DESCRIBE)) {
            lexer.nextToken();
        } else {
            throw new ParserException("expect DESC, actual " + lexer.token());
        }

        MySqlDescribeStatement stmt = new MySqlDescribeStatement();
        stmt.setObject(this.exprParser.name());
        if (lexer.token() == Token.IDENTIFIER) {
            stmt.setColName(this.exprParser.name());
        }
        return stmt;
    }

    public SQLStatement parseShow() {
        accept(Token.SHOW);

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
        }

        boolean full = false;
        if (lexer.token() == Token.FULL) {
            lexer.nextToken();
            full = true;
        }

        if (identifierEquals("PROCESSLIST")) {
            lexer.nextToken();
            MySqlShowProcessListStatement stmt = new MySqlShowProcessListStatement();
            stmt.setFull(full);
            return stmt;
        }

        if (identifierEquals("COLUMNS") || identifierEquals("FIELDS")) {
            lexer.nextToken();

            MySqlShowColumnsStatement stmt = parseShowColumns();
            stmt.setFull(full);

            return stmt;
        }

        if (identifierEquals("COLUMNS")) {
            lexer.nextToken();

            MySqlShowColumnsStatement stmt = parseShowColumns();

            return stmt;
        }

        if (identifierEquals(TABLES)) {
            lexer.nextToken();

            MySqlShowTablesStatement stmt = parseShowTabless();
            stmt.setFull(full);

            return stmt;
        }

        if (identifierEquals("DATABASES")) {
            lexer.nextToken();

            MySqlShowDatabasesStatement stmt = parseShowDatabases();

            return stmt;
        }

        if (identifierEquals("WARNINGS")) {
            lexer.nextToken();

            MySqlShowWarningsStatement stmt = parseShowWarnings();

            return stmt;
        }

        if (identifierEquals("COUNT")) {
            lexer.nextToken();
            accept(Token.LPAREN);
            accept(Token.STAR);
            accept(Token.RPAREN);

            if (identifierEquals(ERRORS)) {
                lexer.nextToken();

                MySqlShowErrorsStatement stmt = new MySqlShowErrorsStatement();
                stmt.setCount(true);

                return stmt;
            } else {
                acceptIdentifier("WARNINGS");

                MySqlShowWarningsStatement stmt = new MySqlShowWarningsStatement();
                stmt.setCount(true);

                return stmt;
            }
        }

        if (identifierEquals(ERRORS)) {
            lexer.nextToken();

            MySqlShowErrorsStatement stmt = new MySqlShowErrorsStatement();
            stmt.setLimit(parseLimit());

            return stmt;
        }

        if (identifierEquals(STATUS)) {
            lexer.nextToken();

            MySqlShowStatusStatement stmt = parseShowStatus();

            return stmt;
        }

        if (identifierEquals(VARIABLES)) {
            lexer.nextToken();

            MySqlShowVariantsStatement stmt = parseShowVariants();

            return stmt;
        }

        if (identifierEquals(GLOBAL)) {
            lexer.nextToken();

            if (identifierEquals(STATUS)) {
                lexer.nextToken();
                MySqlShowStatusStatement stmt = parseShowStatus();
                stmt.setGlobal(true);
                return stmt;
            }

            if (identifierEquals(VARIABLES)) {
                lexer.nextToken();
                MySqlShowVariantsStatement stmt = parseShowVariants();
                stmt.setGlobal(true);
                return stmt;
            }
        }

        if (identifierEquals(SESSION)) {
            lexer.nextToken();

            if (identifierEquals(STATUS)) {
                lexer.nextToken();
                MySqlShowStatusStatement stmt = parseShowStatus();
                stmt.setSession(true);
                return stmt;
            }

            if (identifierEquals(VARIABLES)) {
                lexer.nextToken();
                MySqlShowVariantsStatement stmt = parseShowVariants();
                stmt.setSession(true);
                return stmt;
            }
        }

        if (identifierEquals("COBAR_STATUS")) {
            lexer.nextToken();
            return new CobarShowStatus();
        }

        if (identifierEquals("AUTHORS")) {
            lexer.nextToken();
            return new MySqlShowAuthorsStatement();
        }

        if (lexer.token() == Token.BINARY) {
            lexer.nextToken();
            acceptIdentifier("LOGS");
            return new MySqlShowBinaryLogsStatement();
        }

        if (identifierEquals("MASTER")) {
            lexer.nextToken();
            if (identifierEquals("LOGS")) {
                lexer.nextToken();
                return new MySqlShowMasterLogsStatement();
            }
            acceptIdentifier(STATUS);
            return new MySqlShowMasterStatusStatement();
        }

        if (identifierEquals(CHARACTER)) {
            lexer.nextToken();
            accept(Token.SET);
            MySqlShowCharacterSetStatement stmt = new MySqlShowCharacterSetStatement();

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setPattern(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }

            return stmt;
        }

        if (identifierEquals("COLLATION")) {
            lexer.nextToken();
            MySqlShowCollationStatement stmt = new MySqlShowCollationStatement();

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setPattern(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }

            return stmt;
        }

        if (identifierEquals(BINLOG)) {
            lexer.nextToken();
            acceptIdentifier(EVENTS);
            MySqlShowBinLogEventsStatement stmt = new MySqlShowBinLogEventsStatement();

            if (lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setIn(this.exprParser.expr());
            }

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                stmt.setFrom(this.exprParser.expr());
            }

            stmt.setLimit(parseLimit());

            return stmt;
        }

        if (identifierEquals("CONTRIBUTORS")) {
            lexer.nextToken();
            return new MySqlShowContributorsStatement();
        }

        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();

            if (lexer.token() == Token.DATABASE) {
                lexer.nextToken();

                MySqlShowCreateDatabaseStatement stmt = new MySqlShowCreateDatabaseStatement();
                stmt.setDatabase(this.exprParser.name());
                return stmt;
            }

            if (identifierEquals("EVENT")) {
                lexer.nextToken();

                MySqlShowCreateEventStatement stmt = new MySqlShowCreateEventStatement();
                stmt.setEventName(this.exprParser.name());
                return stmt;
            }

            if (lexer.token() == Token.FUNCTION) {
                lexer.nextToken();

                MySqlShowCreateFunctionStatement stmt = new MySqlShowCreateFunctionStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            if (lexer.token() == Token.PROCEDURE) {
                lexer.nextToken();

                MySqlShowCreateProcedureStatement stmt = new MySqlShowCreateProcedureStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            if (lexer.token() == Token.TABLE) {
                lexer.nextToken();

                MySqlShowCreateTableStatement stmt = new MySqlShowCreateTableStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            if (lexer.token() == Token.VIEW) {
                lexer.nextToken();

                MySqlShowCreateViewStatement stmt = new MySqlShowCreateViewStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            if (lexer.token() == Token.TRIGGER) {
                lexer.nextToken();

                MySqlShowCreateTriggerStatement stmt = new MySqlShowCreateTriggerStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            throw new ParserException("TODO " + lexer.stringVal());
        }

        if (identifierEquals(ENGINE)) {
            lexer.nextToken();
            MySqlShowEngineStatement stmt = new MySqlShowEngineStatement();
            stmt.setName(this.exprParser.name());
            stmt.setOption(MySqlShowEngineStatement.Option.valueOf(lexer.stringVal().toUpperCase()));
            lexer.nextToken();
            return stmt;
        }

        if (identifierEquals("STORAGE")) {
            lexer.nextToken();
            acceptIdentifier(ENGINES);
            MySqlShowEnginesStatement stmt = new MySqlShowEnginesStatement();
            stmt.setStorage(true);
            return stmt;
        }

        if (identifierEquals(ENGINES)) {
            lexer.nextToken();
            MySqlShowEnginesStatement stmt = new MySqlShowEnginesStatement();
            return stmt;
        }

        if (identifierEquals(EVENTS)) {
            lexer.nextToken();
            MySqlShowEventsStatement stmt = new MySqlShowEventsStatement();

            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setSchema(this.exprParser.name());
            }

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }
            return stmt;
        }

        if (lexer.token() == Token.FUNCTION) {
            lexer.nextToken();

            if (identifierEquals("CODE")) {
                lexer.nextToken();
                MySqlShowFunctionCodeStatement stmt = new MySqlShowFunctionCodeStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            acceptIdentifier(STATUS);
            MySqlShowFunctionStatusStatement stmt = new MySqlShowFunctionStatusStatement();

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }
            return stmt;
        }

        // MySqlShowFunctionStatusStatement

        if (identifierEquals(ENGINE)) {
            lexer.nextToken();
            MySqlShowEngineStatement stmt = new MySqlShowEngineStatement();
            stmt.setName(this.exprParser.name());
            stmt.setOption(MySqlShowEngineStatement.Option.valueOf(lexer.stringVal().toUpperCase()));
            lexer.nextToken();
            return stmt;
        }

        if (identifierEquals("STORAGE")) {
            lexer.nextToken();
            acceptIdentifier(ENGINES);
            MySqlShowEnginesStatement stmt = new MySqlShowEnginesStatement();
            stmt.setStorage(true);
            return stmt;
        }

        if (identifierEquals(ENGINES)) {
            lexer.nextToken();
            MySqlShowEnginesStatement stmt = new MySqlShowEnginesStatement();
            return stmt;
        }

        if (identifierEquals("GRANTS")) {
            lexer.nextToken();
            MySqlShowGrantsStatement stmt = new MySqlShowGrantsStatement();

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                stmt.setUser(this.exprParser.expr());
            }

            return stmt;
        }

        if (lexer.token() == Token.INDEX || identifierEquals("INDEXES")) {
            lexer.nextToken();
            MySqlShowIndexesStatement stmt = new MySqlShowIndexesStatement();

            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                SQLName table = exprParser.name();
                stmt.setTable(table);

                if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                    lexer.nextToken();
                    SQLName database = exprParser.name();
                    stmt.setDatabase(database);
                }
            }

            if (lexer.token() == Token.HINT) {
                stmt.setHints(this.exprParser.parseHints());
            }

            return stmt;
        }

        if (identifierEquals("KEYS")) {
            lexer.nextToken();
            MySqlShowKeysStatement stmt = new MySqlShowKeysStatement();

            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                SQLName table = exprParser.name();
                stmt.setTable(table);

                if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                    lexer.nextToken();
                    SQLName database = exprParser.name();
                    stmt.setDatabase(database);
                }
            }

            return stmt;
        }

        if (identifierEquals("OPEN")) {
            lexer.nextToken();
            acceptIdentifier(TABLES);
            MySqlShowOpenTablesStatement stmt = new MySqlShowOpenTablesStatement();

            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setDatabase(this.exprParser.name());
            }

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }
            return stmt;
        }

        if (identifierEquals("PLUGINS")) {
            lexer.nextToken();
            MySqlShowPluginsStatement stmt = new MySqlShowPluginsStatement();
            return stmt;
        }

        if (identifierEquals("PRIVILEGES")) {
            lexer.nextToken();
            MySqlShowPrivilegesStatement stmt = new MySqlShowPrivilegesStatement();
            return stmt;
        }

        if (lexer.token() == Token.PROCEDURE) {
            lexer.nextToken();

            if (identifierEquals("CODE")) {
                lexer.nextToken();
                MySqlShowProcedureCodeStatement stmt = new MySqlShowProcedureCodeStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }

            acceptIdentifier(STATUS);
            MySqlShowProcedureStatusStatement stmt = new MySqlShowProcedureStatusStatement();

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }
            return stmt;
        }

        if (identifierEquals("PROCESSLIST")) {
            lexer.nextToken();
            MySqlShowProcessListStatement stmt = new MySqlShowProcessListStatement();
            return stmt;
        }

        if (identifierEquals("PROFILES")) {
            lexer.nextToken();
            MySqlShowProfilesStatement stmt = new MySqlShowProfilesStatement();
            return stmt;
        }

        if (identifierEquals("PROFILE")) {
            lexer.nextToken();
            MySqlShowProfileStatement stmt = new MySqlShowProfileStatement();

            for (;;) {
                if (lexer.token() == Token.ALL) {
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.ALL);
                    lexer.nextToken();
                } else if (identifierEquals("BLOCK")) {
                    lexer.nextToken();
                    acceptIdentifier("IO");
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.BLOCK_IO);
                } else if (identifierEquals("CONTEXT")) {
                    lexer.nextToken();
                    acceptIdentifier("SWITCHES");
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.CONTEXT_SWITCHES);
                } else if (identifierEquals("CPU")) {
                    lexer.nextToken();
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.CPU);
                } else if (identifierEquals("IPC")) {
                    lexer.nextToken();
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.IPC);
                } else if (identifierEquals("MEMORY")) {
                    lexer.nextToken();
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.MEMORY);
                } else if (identifierEquals("PAGE")) {
                    lexer.nextToken();
                    acceptIdentifier("FAULTS");
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.PAGE_FAULTS);
                } else if (identifierEquals("SOURCE")) {
                    lexer.nextToken();
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.SOURCE);
                } else if (identifierEquals("SWAPS")) {
                    lexer.nextToken();
                    stmt.getTypes().add(MySqlShowProfileStatement.Type.SWAPS);
                } else {
                    break;
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                acceptIdentifier("QUERY");
                stmt.setForQuery(this.exprParser.primary());
            }

            stmt.setLimit(this.parseLimit());

            return stmt;
        }

        if (identifierEquals("RELAYLOG")) {
            lexer.nextToken();
            acceptIdentifier(EVENTS);
            MySqlShowRelayLogEventsStatement stmt = new MySqlShowRelayLogEventsStatement();

            if (lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setLogName(this.exprParser.primary());
            }

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                stmt.setFrom(this.exprParser.primary());
            }

            stmt.setLimit(this.parseLimit());

            return stmt;
        }

        if (identifierEquals("RELAYLOG")) {
            lexer.nextToken();
            acceptIdentifier(EVENTS);
            MySqlShowRelayLogEventsStatement stmt = new MySqlShowRelayLogEventsStatement();

            if (lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setLogName(this.exprParser.primary());
            }

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                stmt.setFrom(this.exprParser.primary());
            }

            stmt.setLimit(this.parseLimit());

            return stmt;
        }

        if (identifierEquals("SLAVE")) {
            lexer.nextToken();
            if (identifierEquals(STATUS)) {
                lexer.nextToken();
                return new MySqlShowSlaveStatusStatement();
            } else {
                acceptIdentifier("HOSTS");
                MySqlShowSlaveHostsStatement stmt = new MySqlShowSlaveHostsStatement();
                return stmt;
            }
        }

        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();
            acceptIdentifier(STATUS);
            MySqlShowTableStatusStatement stmt = new MySqlShowTableStatusStatement();
            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setDatabase(this.exprParser.name());
            }

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                stmt.setLike(this.exprParser.expr());
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                stmt.setWhere(this.exprParser.expr());
            }

            return stmt;
        }

        if (identifierEquals("TRIGGERS")) {
            lexer.nextToken();
            MySqlShowTriggersStatement stmt = new MySqlShowTriggersStatement();

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                SQLName database = exprParser.name();
                stmt.setDatabase(database);
            }

            if (lexer.token() == Token.LIKE) {
                lexer.nextToken();
                SQLExpr like = exprParser.expr();
                stmt.setLike(like);
            }

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                SQLExpr where = exprParser.expr();
                stmt.setWhere(where);
            }

            return stmt;
        }

        // MySqlShowSlaveHostsStatement
        throw new ParserException("TODO " + lexer.stringVal());
    }

    private MySqlShowStatusStatement parseShowStatus() {
        MySqlShowStatusStatement stmt = new MySqlShowStatusStatement();

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    private MySqlShowVariantsStatement parseShowVariants() {
        MySqlShowVariantsStatement stmt = new MySqlShowVariantsStatement();

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    private MySqlShowWarningsStatement parseShowWarnings() {
        MySqlShowWarningsStatement stmt = new MySqlShowWarningsStatement();

        stmt.setLimit(parseLimit());

        return stmt;
    }

    private MySqlShowDatabasesStatement parseShowDatabases() {
        MySqlShowDatabasesStatement stmt = new MySqlShowDatabasesStatement();

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    private MySqlShowTablesStatement parseShowTabless() {
        MySqlShowTablesStatement stmt = new MySqlShowTablesStatement();

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            SQLName database = exprParser.name();
            stmt.setDatabase(database);
        }

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    private MySqlShowColumnsStatement parseShowColumns() {
        MySqlShowColumnsStatement stmt = new MySqlShowColumnsStatement();

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            SQLName table = exprParser.name();
            stmt.setTable(table);

            if (lexer.token() == Token.FROM || lexer.token() == Token.IN) {
                lexer.nextToken();
                SQLName database = exprParser.name();
                stmt.setDatabase(database);
            }
        }

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLExpr like = exprParser.expr();
            stmt.setLike(like);
        }

        if (lexer.token() == Token.WHERE) {
            lexer.nextToken();
            SQLExpr where = exprParser.expr();
            stmt.setWhere(where);
        }

        return stmt;
    }

    public MySqlStartTransactionStatement parseStart() {
        acceptIdentifier("START");
        acceptIdentifier("TRANSACTION");

        MySqlStartTransactionStatement stmt = new MySqlStartTransactionStatement();

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            acceptIdentifier("CONSISTENT");
            acceptIdentifier("SNAPSHOT");
            stmt.setConsistentSnapshot(true);
        }

        if (lexer.token() == Token.BEGIN) {
            lexer.nextToken();
            stmt.setBegin(true);
            if (identifierEquals("WORK")) {
                lexer.nextToken();
                stmt.setWork(true);
            }
        }

        if (lexer.token() == Token.HINT) {
            stmt.setHints(this.exprParser.parseHints());
        }

        return stmt;
    }

    @Override
    public MySqlRollbackStatement parseRollback() {
        acceptIdentifier("ROLLBACK");

        MySqlRollbackStatement stmt = new MySqlRollbackStatement();

        if (identifierEquals("WORK")) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.AND) {
            lexer.nextToken();
            if (lexer.token() == Token.NOT) {
                lexer.nextToken();
                acceptIdentifier(CHAIN);
                stmt.setChain(Boolean.FALSE);
            } else {
                acceptIdentifier(CHAIN);
                stmt.setChain(Boolean.TRUE);
            }
        }

        if (lexer.token() == Token.TO) {
            lexer.nextToken();

            if (identifierEquals("SAVEPOINT")) {
                lexer.nextToken();
            }

            stmt.setTo(this.exprParser.name());
        }

        return stmt;
    }

    public MySqlCommitStatement parseCommit() {
        acceptIdentifier("COMMIT");

        MySqlCommitStatement stmt = new MySqlCommitStatement();

        if (identifierEquals("WORK")) {
            lexer.nextToken();
            stmt.setWork(true);
        }

        if (lexer.token() == Token.AND) {
            lexer.nextToken();
            if (lexer.token() == Token.NOT) {
                lexer.nextToken();
                acceptIdentifier(CHAIN);
                stmt.setChain(Boolean.FALSE);
            } else {
                acceptIdentifier(CHAIN);
                stmt.setChain(Boolean.TRUE);
            }
        }

        return stmt;
    }

    public MySqlReplaceStatement parseReplicate() {
        MySqlReplaceStatement stmt = new MySqlReplaceStatement();

        accept(Token.REPLACE);

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
        }

        if (identifierEquals(LOW_PRIORITY)) {
            stmt.setLowPriority(true);
            lexer.nextToken();
        }

        if (identifierEquals(DELAYED)) {
            stmt.setDelayed(true);
            lexer.nextToken();
        }

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();
        }

        SQLName tableName = exprParser.name();
        stmt.setTableName(tableName);

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            if (lexer.token() == Token.SELECT) {
                SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
                stmt.setQuery(queryExpr);
            } else {
                this.exprParser.exprList(stmt.getColumns(), stmt);
            }
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.VALUES || identifierEquals("VALUE")) {
            lexer.nextToken();

            parseValueClause(stmt.getValuesList(), 0);
        } else if (lexer.token() == Token.SELECT) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            stmt.setQuery(queryExpr);
        } else if (lexer.token() == Token.SET) {
            lexer.nextToken();

            SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
            stmt.getValuesList().add(values);
            for (;;) {
                stmt.getColumns().add(this.exprParser.name());
                if (lexer.token() == Token.COLONEQ) {
                    lexer.nextToken();
                } else {
                    accept(Token.EQ);
                }
                values.getValues().add(this.exprParser.expr());

                if (lexer.token() == (Token.COMMA)) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
        } else if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            stmt.setQuery(queryExpr);
            accept(Token.RPAREN);
        }

        return stmt;
    }

    protected SQLStatement parseLoad() {
        acceptIdentifier("LOAD");

        if (identifierEquals("DATA")) {
            SQLStatement stmt = parseLoadDataInFile();
            return stmt;
        }

        if (identifierEquals("XML")) {
            SQLStatement stmt = parseLoadXml();
            return stmt;
        }

        throw new ParserException("TODO");
    }

    protected MySqlLoadXmlStatement parseLoadXml() {
        acceptIdentifier("XML");

        MySqlLoadXmlStatement stmt = new MySqlLoadXmlStatement();

        if (identifierEquals(LOW_PRIORITY)) {
            stmt.setLowPriority(true);
            lexer.nextToken();
        }

        if (identifierEquals("CONCURRENT")) {
            stmt.setConcurrent(true);
            lexer.nextToken();
        }

        if (identifierEquals(LOCAL)) {
            stmt.setLocal(true);
            lexer.nextToken();
        }

        acceptIdentifier("INFILE");

        SQLLiteralExpr fileName = (SQLLiteralExpr) exprParser.expr();
        stmt.setFileName(fileName);

        if (lexer.token() == Token.REPLACE) {
            stmt.setReplicate(true);
            lexer.nextToken();
        }

        if (identifierEquals(IGNORE)) {
            stmt.setIgnore(true);
            lexer.nextToken();
        }

        accept(Token.INTO);
        accept(Token.TABLE);

        SQLName tableName = exprParser.name();
        stmt.setTableName(tableName);

        if (identifierEquals(CHARACTER)) {
            lexer.nextToken();
            accept(Token.SET);

            if (lexer.token() != Token.LITERAL_CHARS) {
                throw new ParserException("syntax error, illegal charset");
            }

            String charset = lexer.stringVal();
            lexer.nextToken();
            stmt.setCharset(charset);
        }

        if (identifierEquals("ROWS")) {
            lexer.nextToken();
            accept(Token.IDENTIFIED);
            accept(Token.BY);
            SQLExpr rowsIdentifiedBy = exprParser.expr();
            stmt.setRowsIdentifiedBy(rowsIdentifiedBy);
        }

        if (identifierEquals(IGNORE)) {
            throw new ParserException("TODO");
        }

        if (lexer.token() == Token.SET) {
            throw new ParserException("TODO");
        }

        return stmt;
    }

    protected MySqlLoadDataInFileStatement parseLoadDataInFile() {

        acceptIdentifier("DATA");

        MySqlLoadDataInFileStatement stmt = new MySqlLoadDataInFileStatement();

        if (identifierEquals(LOW_PRIORITY)) {
            stmt.setLowPriority(true);
            lexer.nextToken();
        }

        if (identifierEquals("CONCURRENT")) {
            stmt.setConcurrent(true);
            lexer.nextToken();
        }

        if (identifierEquals(LOCAL)) {
            stmt.setLocal(true);
            lexer.nextToken();
        }

        acceptIdentifier("INFILE");

        SQLLiteralExpr fileName = (SQLLiteralExpr) exprParser.expr();
        stmt.setFileName(fileName);

        if (lexer.token() == Token.REPLACE) {
            stmt.setReplicate(true);
            lexer.nextToken();
        }

        if (identifierEquals(IGNORE)) {
            stmt.setIgnore(true);
            lexer.nextToken();
        }

        accept(Token.INTO);
        accept(Token.TABLE);

        SQLName tableName = exprParser.name();
        stmt.setTableName(tableName);

        if (identifierEquals(CHARACTER)) {
            lexer.nextToken();
            accept(Token.SET);

            if (lexer.token() != Token.LITERAL_CHARS) {
                throw new ParserException("syntax error, illegal charset");
            }

            String charset = lexer.stringVal();
            lexer.nextToken();
            stmt.setCharset(charset);
        }

        if (identifierEquals("FIELDS") || identifierEquals("COLUMNS")) {
            lexer.nextToken();
            if (identifierEquals("TERMINATED")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setColumnsTerminatedBy(new SQLCharExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            if (identifierEquals("OPTIONALLY")) {
                stmt.setColumnsEnclosedOptionally(true);
                lexer.nextToken();
            }

            if (identifierEquals("ENCLOSED")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setColumnsEnclosedBy(new SQLCharExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            if (identifierEquals("ESCAPED")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setColumnsEscaped(new SQLCharExpr(lexer.stringVal()));
                lexer.nextToken();
            }
        }

        if (identifierEquals("LINES")) {
            lexer.nextToken();
            if (identifierEquals("STARTING")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setLinesStartingBy(new SQLCharExpr(lexer.stringVal()));
                lexer.nextToken();
            }

            if (identifierEquals("TERMINATED")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setLinesTerminatedBy(new SQLCharExpr(lexer.stringVal()));
                lexer.nextToken();
            }
        }

        if (identifierEquals(IGNORE)) {
            lexer.nextToken();
            stmt.setIgnoreLinesNumber(this.exprParser.expr());
            acceptIdentifier("LINES");
        }

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            this.exprParser.exprList(stmt.getColumns(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.SET) {
            lexer.nextToken();
            this.exprParser.exprList(stmt.getSetList(), stmt);
        }

        return stmt;

    }

    public MySqlPrepareStatement parsePrepare() {
        acceptIdentifier("PREPARE");

        SQLName name = exprParser.name();
        accept(Token.FROM);
        SQLExpr from = exprParser.expr();

        return new MySqlPrepareStatement(name, from);
    }

    public MySqlExecuteStatement parseExecute() {
        acceptIdentifier("EXECUTE");

        MySqlExecuteStatement stmt = new MySqlExecuteStatement();

        SQLName statementName = exprParser.name();
        stmt.setStatementName(statementName);

        if (identifierEquals("USING")) {
            lexer.nextToken();
            exprParser.exprList(stmt.getParameters(), stmt);
        }

        return stmt;
    }

    public SQLInsertStatement parseInsert() {
        MySqlInsertStatement insertStatement = new MySqlInsertStatement();

        if (lexer.token() == Token.INSERT) {
            lexer.nextToken();

            if (identifierEquals(LOW_PRIORITY)) {
                insertStatement.setLowPriority(true);
                lexer.nextToken();
            }

            if (identifierEquals(DELAYED)) {
                insertStatement.setDelayed(true);
                lexer.nextToken();
            }

            if (identifierEquals("HIGH_PRIORITY")) {
                insertStatement.setHighPriority(true);
                lexer.nextToken();
            }

            if (identifierEquals(IGNORE)) {
                insertStatement.setIgnore(true);
                lexer.nextToken();
            }

            if (lexer.token() == Token.INTO) {
                lexer.nextToken();
            }

            SQLName tableName = this.exprParser.name();
            insertStatement.setTableName(tableName);

            if (lexer.token() == Token.IDENTIFIER && !identifierEquals("VALUE")) {
                insertStatement.setAlias(lexer.stringVal());
                lexer.nextToken();
            }

        }

        int columnSize = 0;
        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            if (lexer.token() == (Token.SELECT)) {
                SQLSelect select = this.exprParser.createSelectParser().select();
                select.setParent(insertStatement);
                insertStatement.setQuery(select);
            } else {
                this.exprParser.exprList(insertStatement.getColumns(), insertStatement);
                columnSize = insertStatement.getColumns().size();
            }
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.VALUES || identifierEquals("VALUE")) {
            lexer.nextTokenLParen();
            parseValueClause(insertStatement.getValuesList(), columnSize);
        } else if (lexer.token() == Token.SET) {
            lexer.nextToken();

            SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
            insertStatement.getValuesList().add(values);

            for (;;) {
                SQLName name = this.exprParser.name();
                insertStatement.getColumns().add(name);
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                } else {
                    accept(Token.COLONEQ);
                }
                values.getValues().add(this.exprParser.expr());

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }

        } else if (lexer.token() == (Token.SELECT)) {
            SQLSelect select = this.exprParser.createSelectParser().select();
            select.setParent(insertStatement);
            insertStatement.setQuery(select);
        } else if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            SQLSelect select = this.exprParser.createSelectParser().select();
            select.setParent(insertStatement);
            insertStatement.setQuery(select);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            acceptIdentifier("DUPLICATE");
            accept(Token.KEY);
            accept(Token.UPDATE);

            exprParser.exprList(insertStatement.getDuplicateKeyUpdate(), insertStatement);
        }

        return insertStatement;
    }

    private void parseValueClause(List<ValuesClause> valueClauseList, int columnSize) {
        for (;;) {
            if (lexer.token() != Token.LPAREN) {
                throw new ParserException("syntax error, expect ')'");
            }
            lexer.nextTokenValue();

            if (lexer.token() != Token.RPAREN) {
                List<SQLExpr> valueExprList;
                if (columnSize > 0) {
                    valueExprList = new ArrayList<SQLExpr>(columnSize);
                } else {
                    valueExprList = new ArrayList<SQLExpr>();
                }

                for (;;) {
                    SQLExpr expr;
                    if (lexer.token() == Token.LITERAL_INT) {
                        expr = new SQLIntegerExpr(lexer.integerValue());
                        lexer.nextTokenComma();
                    } else if (lexer.token() == Token.LITERAL_CHARS) {
                        expr = new SQLCharExpr(lexer.stringVal());
                        lexer.nextTokenComma();
                    } else if (lexer.token() == Token.LITERAL_NCHARS) {
                        expr = new SQLNCharExpr(lexer.stringVal());
                        lexer.nextTokenComma();
                    } else {
                        expr = exprParser.expr();
                    }

                    if (lexer.token() == Token.COMMA) {
                        valueExprList.add(expr);
                        lexer.nextTokenValue();
                        continue;
                    } else if (lexer.token() == Token.RPAREN) {
                        valueExprList.add(expr);
                        break;
                    } else {
                        expr = this.exprParser.primaryRest(expr);
                        if (lexer.token() != Token.COMMA && lexer.token() != Token.RPAREN) {
                            expr = this.exprParser.exprRest(expr);
                        }

                        valueExprList.add(expr);
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        } else {
                            break;
                        }
                    }
                }

                SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause(valueExprList);
                valueClauseList.add(values);
            } else {
                SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause(new ArrayList<SQLExpr>(0));
                valueClauseList.add(values);
            }

            if (lexer.token() != Token.RPAREN) {
                throw new ParserException("syntax error");
            }

            if (!parseCompleteValues && valueClauseList.size() >= parseValuesSize) {
                lexer.skipToEOF();
                break;
            }

            lexer.nextTokenComma();
            if (lexer.token() == Token.COMMA) {
                lexer.nextTokenLParen();
                continue;
            } else {
                break;
            }
        }
    }

    public SQLSelectParser createSQLSelectParser() {
        return new MySqlSelectParser(this.exprParser);
    }

    public SQLStatement parseSet() {
        accept(Token.SET);

        if (identifierEquals("PASSWORD")) {
            lexer.nextToken();
            MySqlSetPasswordStatement stmt = new MySqlSetPasswordStatement();

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();
                stmt.setUser(this.exprParser.name());
            }

            accept(Token.EQ);

            stmt.setPassword(this.exprParser.expr());

            return stmt;
        }

        Boolean global = null;
        if (identifierEquals(GLOBAL)) {
            global = Boolean.TRUE;
            lexer.nextToken();
        } else if (identifierEquals(SESSION)) {
            global = Boolean.FALSE;
            lexer.nextToken();
        }

        if (identifierEquals("TRANSACTION")) {
            MySqlSetTransactionStatement stmt = new MySqlSetTransactionStatement();
            stmt.setGlobal(global);

            lexer.nextToken();
            if (identifierEquals("ISOLATION")) {
                lexer.nextToken();
                acceptIdentifier("LEVEL");

                if (identifierEquals(READ)) {
                    lexer.nextToken();

                    if (identifierEquals("UNCOMMITTED")) {
                        stmt.setIsolationLevel("READ UNCOMMITTED");
                        lexer.nextToken();
                    } else if (identifierEquals(WRITE)) {
                        stmt.setIsolationLevel("READ WRITE");
                        lexer.nextToken();
                    } else if (identifierEquals("ONLY")) {
                        stmt.setIsolationLevel("READ ONLY");
                        lexer.nextToken();
                    } else if (identifierEquals("COMMITTED")) {
                        stmt.setIsolationLevel("READ COMMITTED");
                        lexer.nextToken();
                    } else {
                        throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal());
                    }
                } else if (identifierEquals("SERIALIZABLE")) {
                    stmt.setIsolationLevel("SERIALIZABLE");
                    lexer.nextToken();
                } else if (identifierEquals("REPEATABLE")) {
                    lexer.nextToken();
                    if (identifierEquals(READ)) {
                        stmt.setIsolationLevel("REPEATABLE READ");
                        lexer.nextToken();
                    } else {
                        throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal());
                    }
                } else {
                    throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal());
                }
            } else if (identifierEquals(READ)) {
                lexer.nextToken();
                if (identifierEquals("ONLY")) {
                    stmt.setAccessModel("ONLY");
                    lexer.nextToken();
                } else if (identifierEquals("WRITE")) {
                    stmt.setAccessModel("WRITE");
                    lexer.nextToken();
                } else {
                    throw new ParserException("UNKOWN ACCESS MODEL : " + lexer.stringVal());
                }
            }

            return stmt;
        } else if (identifierEquals("NAMES")) {
            lexer.nextToken();

            MySqlSetNamesStatement stmt = new MySqlSetNamesStatement();
            if (lexer.token() == Token.DEFAULT) {
                lexer.nextToken();
                stmt.setDefault(true);
            } else {
                String charSet = lexer.stringVal();
                stmt.setCharSet(charSet);
                lexer.nextToken();
                if (identifierEquals(COLLATE2)) {
                    lexer.nextToken();

                    String collate = lexer.stringVal();
                    stmt.setCollate(collate);
                    lexer.nextToken();
                }
            }
            return stmt;
        } else if (identifierEquals(CHARACTER)) {
            lexer.nextToken();

            accept(Token.SET);

            MySqlSetCharSetStatement stmt = new MySqlSetCharSetStatement();
            if (lexer.token() == Token.DEFAULT) {
                lexer.nextToken();
                stmt.setDefault(true);
            } else {
                String charSet = lexer.stringVal();
                stmt.setCharSet(charSet);
                lexer.nextToken();
                if (identifierEquals(COLLATE2)) {
                    lexer.nextToken();

                    String collate = lexer.stringVal();
                    stmt.setCollate(collate);
                    lexer.nextToken();
                }
            }
            return stmt;
        } else {
            SQLSetStatement stmt = new SQLSetStatement(getDbType());

            parseAssignItems(stmt.getItems(), stmt);

            if (global != null && global.booleanValue()) {
                SQLVariantRefExpr varRef = (SQLVariantRefExpr) stmt.getItems().get(0).getTarget();
                varRef.setGlobal(true);
            }

            if (lexer.token() == Token.HINT) {
                stmt.setHints(this.exprParser.parseHints());
            }

            return stmt;
        }
    }

    public Limit parseLimit() {
        return ((MySqlExprParser) this.exprParser).parseLimit();
    }

    public SQLStatement parseAlter() {
        accept(Token.ALTER);

        if (lexer.token() == Token.USER) {
            return parseAlterUser();
        }

        boolean ignore = false;

        if (identifierEquals(IGNORE)) {
            ignore = true;
            lexer.nextToken();
        }

        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();

            MySqlAlterTableStatement stmt = new MySqlAlterTableStatement();
            stmt.setIgnore(ignore);
            stmt.setName(this.exprParser.name());

            for (;;) {
                if (lexer.token() == Token.DROP) {
                    parseAlterDrop(stmt);
                } else if (identifierEquals("ADD")) {
                    lexer.nextToken();

                    if (lexer.token() == Token.COLUMN) {
                        lexer.nextToken();
                        parseAlterTableAddColumn(stmt);
                    } else if (lexer.token() == Token.INDEX) {
                        SQLAlterTableAddIndex item = parseAlterTableAddIndex();
                        item.setParent(stmt);
                        stmt.getItems().add(item);
                    } else if (lexer.token() == Token.UNIQUE) {
                        SQLAlterTableAddIndex item = parseAlterTableAddIndex();
                        item.setParent(stmt);
                        stmt.getItems().add(item);
                    } else if (lexer.token() == Token.PRIMARY) {
                        SQLPrimaryKey primaryKey = this.exprParser.parsePrimaryKey();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(primaryKey);
                        stmt.getItems().add(item);
                    } else if (lexer.token() == Token.KEY) {
                        // throw new ParserException("TODO " + lexer.token() +
                        // " " + lexer.stringVal());
                        SQLAlterTableAddIndex item = parseAlterTableAddIndex();
                        item.setParent(stmt);
                        stmt.getItems().add(item);
                    } else if (lexer.token() == Token.CONSTRAINT) {
                        lexer.nextToken();
                        SQLName constraintName = this.exprParser.name();

                        if (lexer.token() == Token.PRIMARY) {
                            SQLPrimaryKey primaryKey = ((MySqlExprParser) this.exprParser).parsePrimaryKey();

                            primaryKey.setName(constraintName);

                            SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(primaryKey);
                            item.setParent(stmt);

                            stmt.getItems().add(item);
                        } else if (lexer.token() == Token.FOREIGN) {
                            MysqlForeignKey fk = this.getExprParser().parseForeignKey();
                            fk.setName(constraintName);
                            fk.setHasConstraint(true);

                            SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(fk);

                            item.setParent(stmt);

                            stmt.getItems().add(item);
                        } else {
                            throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                        }
                    } else if (identifierEquals(FULLTEXT)) {
                        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                    } else if (identifierEquals(SPATIAL)) {
                        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                    } else {
                        parseAlterTableAddColumn(stmt);
                    }
                } else if (lexer.token() == Token.ALTER) {
                    lexer.nextToken();
                    if (lexer.token() == Token.COLUMN) {
                        lexer.nextToken();
                    }

                    SQLAlterTableAlterColumn alterColumn = new SQLAlterTableAlterColumn();
                    SQLColumnDefinition columnDef = this.exprParser.parseColumn();
                    alterColumn.setColumn(columnDef);

                    stmt.getItems().add(alterColumn);
                } else if (identifierEquals("CHANGE")) {
                    lexer.nextToken();
                    if (lexer.token() == Token.COLUMN) {
                        lexer.nextToken();
                    }
                    MySqlAlterTableChangeColumn item = new MySqlAlterTableChangeColumn();
                    item.setColumnName(this.exprParser.name());
                    item.setNewColumnDefinition(this.exprParser.parseColumn());
                    if (identifierEquals("AFTER")) {
                        lexer.nextToken();
                        item.setAfterColumn(this.exprParser.name());
                    } else if (identifierEquals("FIRST")) {
                        lexer.nextToken();
                        if (lexer.token() == Token.IDENTIFIER) {
                            item.setFirstColumn(this.exprParser.name());
                        } else {
                            item.setFirst(true);
                        }
                    }
                    stmt.getItems().add(item);
                } else if (identifierEquals("MODIFY")) {
                    lexer.nextToken();
                    if (lexer.token() == Token.COLUMN) {
                        lexer.nextToken();
                    }
                    MySqlAlterTableModifyColumn item = new MySqlAlterTableModifyColumn();
                    item.setNewColumnDefinition(this.exprParser.parseColumn());
                    if (identifierEquals("AFTER")) {
                        lexer.nextToken();
                        item.setAfterColumn(this.exprParser.name());
                    } else if (identifierEquals("FIRST")) {
                        lexer.nextToken();
                        if (lexer.token() == Token.IDENTIFIER) {
                            item.setFirstColumn(this.exprParser.name());
                        } else {
                            item.setFirst(true);
                        }
                    }
                    stmt.getItems().add(item);
                } else if (lexer.token() == Token.DISABLE) {
                    lexer.nextToken();

                    if (lexer.token() == Token.CONSTRAINT) {
                        lexer.nextToken();
                        SQLAlterTableDisableConstraint item = new SQLAlterTableDisableConstraint();
                        item.setConstraintName(this.exprParser.name());
                        stmt.getItems().add(item);
                    } else {
                        acceptIdentifier("KEYS");
                        SQLAlterTableDisableKeys item = new SQLAlterTableDisableKeys();
                        stmt.getItems().add(item);
                    }
                } else if (lexer.token() == Token.ENABLE) {
                    lexer.nextToken();
                    if (lexer.token() == Token.CONSTRAINT) {
                        lexer.nextToken();
                        SQLAlterTableEnableConstraint item = new SQLAlterTableEnableConstraint();
                        item.setConstraintName(this.exprParser.name());
                        stmt.getItems().add(item);
                    } else {
                        acceptIdentifier("KEYS");
                        SQLAlterTableEnableKeys item = new SQLAlterTableEnableKeys();
                        stmt.getItems().add(item);
                    }
                } else if (identifierEquals("RENAME")) {
                    lexer.nextToken();
                    if (lexer.token() == Token.TO || lexer.token() == Token.AS) {
                        lexer.nextToken();
                    }
                    MySqlRenameTableStatement renameStmt = new MySqlRenameTableStatement();
                    MySqlRenameTableStatement.Item item = new MySqlRenameTableStatement.Item();
                    item.setName(stmt.getTableSource().getExpr());
                    item.setTo(this.exprParser.name());
                    renameStmt.getItems().add(item);

                    return renameStmt;
                } else if (lexer.token() == Token.ORDER) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("CONVERT")) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (lexer.token() == Token.DEFAULT) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("DISCARD")) {
                    lexer.nextToken();
                    accept(Token.TABLESPACE);
                    MySqlAlterTableDiscardTablespace item = new MySqlAlterTableDiscardTablespace();
                    stmt.getItems().add(item);
                } else if (identifierEquals("IMPORT")) {
                    lexer.nextToken();
                    accept(Token.TABLESPACE);
                    MySqlAlterTableImportTablespace item = new MySqlAlterTableImportTablespace();
                    stmt.getItems().add(item);
                } else if (identifierEquals("FORCE")) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("TRUNCATE")) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("COALESCE")) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("REORGANIZE")) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("EXCHANGE")) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("ANALYZE")) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("CHECK")) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (lexer.token() == Token.OPTIMIZE) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("REBUILD")) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("REPAIR")) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("REMOVE")) {
                    throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("ALGORITHM")) {
                    lexer.nextToken();
                    accept(Token.EQ);
                    stmt.getItems().add(new MySqlAlterTableOption("ALGORITHM", lexer.stringVal()));
                    lexer.nextToken();
                } else if (identifierEquals(ENGINE)) {
                    lexer.nextToken();
                    accept(Token.EQ);
                    stmt.getItems().add(new MySqlAlterTableOption(ENGINE, lexer.stringVal()));
                    lexer.nextToken();
                } else if (identifierEquals(AUTO_INCREMENT)) {
                    lexer.nextToken();
                    accept(Token.EQ);
                    stmt.getItems().add(new MySqlAlterTableOption(AUTO_INCREMENT, lexer.integerValue()));
                    lexer.nextToken();
                } else if (identifierEquals(COLLATE2)) {
                    lexer.nextToken();
                    accept(Token.EQ);
                    stmt.getItems().add(new MySqlAlterTableOption(COLLATE2, lexer.stringVal()));
                    lexer.nextToken();
                } else if (identifierEquals("PACK_KEYS")) {
                    lexer.nextToken();
                    accept(Token.EQ);
                    if (identifierEquals("PACK")) {
                        lexer.nextToken();
                        accept(Token.ALL);
                        stmt.getItems().add(new MySqlAlterTableOption("PACK_KEYS", "PACK ALL"));
                    } else {
                        stmt.getItems().add(new MySqlAlterTableOption("PACK_KEYS", lexer.stringVal()));
                        lexer.nextToken();
                    }
                } else if (identifierEquals(CHARACTER)) {
                    lexer.nextToken();
                    accept(Token.SET);
                    accept(Token.EQ);
                    MySqlAlterTableCharacter item = new MySqlAlterTableCharacter();
                    item.setCharacterSet(this.exprParser.primary());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        acceptIdentifier(COLLATE2);
                        accept(Token.EQ);
                        item.setCollate(this.exprParser.primary());
                    }
                    stmt.getItems().add(item);
                } else if (lexer.token() == Token.COMMENT) {
                    lexer.nextToken();
                    if (lexer.token() == Token.EQ) {
                        accept(Token.EQ);
                    }
                    stmt.getItems().add(new MySqlAlterTableOption("COMMENT", '\'' + lexer.stringVal() + '\''));
                    lexer.nextToken();
                } else {
                    break;
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }

            return stmt;
        }
        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
    }

    protected void parseAlterTableAddColumn(MySqlAlterTableStatement stmt) {
        boolean parenFlag = false;
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            parenFlag = true;
        }
        
        MySqlAlterTableAddColumn item = new MySqlAlterTableAddColumn();
        for (;;) {
            
            SQLColumnDefinition columnDef = this.exprParser.parseColumn();
            item.getColumns().add(columnDef);
            if (identifierEquals("AFTER")) {
                lexer.nextToken();
                item.setAfterColumn(this.exprParser.name());
            } else if (identifierEquals("FIRST")) {
                lexer.nextToken();
                if (lexer.token() == Token.IDENTIFIER) {
                    item.setFirstColumn(this.exprParser.name());
                } else {
                    item.setFirst(true);
                }
            }
            
            if (parenFlag && lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            
            break;
        }
        
        stmt.getItems().add(item);
        
        if (parenFlag) {
            accept(Token.RPAREN);
        }
    }

    public void parseAlterDrop(SQLAlterTableStatement stmt) {
        lexer.nextToken();
        if (lexer.token() == Token.INDEX) {
            lexer.nextToken();
            SQLName indexName = this.exprParser.name();
            SQLAlterTableDropIndex item = new SQLAlterTableDropIndex();
            item.setIndexName(indexName);
            stmt.getItems().add(item);
        } else if (lexer.token() == Token.FOREIGN) {
            lexer.nextToken();
            accept(Token.KEY);
            SQLName indexName = this.exprParser.name();
            SQLAlterTableDropForeignKey item = new SQLAlterTableDropForeignKey();
            item.setIndexName(indexName);
            stmt.getItems().add(item);
        } else if (lexer.token() == Token.PRIMARY) {
            lexer.nextToken();
            accept(Token.KEY);
            SQLAlterTableDropPrimaryKey item = new SQLAlterTableDropPrimaryKey();
            stmt.getItems().add(item);
        } else if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
            SQLAlterTableDropConstraint item = new SQLAlterTableDropConstraint();
            item.setConstraintName(this.exprParser.name());
            stmt.getItems().add(item);
        } else if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();

            SQLName name = exprParser.name();
            name.setParent(item);
            item.getColumns().add(name);

            while (lexer.token() == Token.COMMA) {
                lexer.mark();
                lexer.nextToken();
                if (identifierEquals("CHANGE")) {
                    lexer.reset();
                    break;
                }

                if (lexer.token() == Token.IDENTIFIER) {
                    name = exprParser.name();
                    name.setParent(item);
                } else {
                    lexer.reset();
                    break;
                }
            }

            stmt.getItems().add(item);
        } else if (lexer.token() == Token.IDENTIFIER) {
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());
            stmt.getItems().add(item);
        } else {
            super.parseAlterDrop(stmt);
        }
    }

    public SQLStatement parseRename() {
        MySqlRenameTableStatement stmt = new MySqlRenameTableStatement();

        acceptIdentifier("RENAME");

        accept(Token.TABLE);

        for (;;) {
            MySqlRenameTableStatement.Item item = new MySqlRenameTableStatement.Item();
            item.setName(this.exprParser.name());
            accept(Token.TO);
            item.setTo(this.exprParser.name());

            stmt.getItems().add(item);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return stmt;
    }

    public SQLStatement parseCreateDatabase() {
        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();
        }

        accept(Token.DATABASE);

        SQLCreateDatabaseStatement stmt = new SQLCreateDatabaseStatement(JdbcConstants.MYSQL);
        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.DEFAULT) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.HINT) {
            stmt.setHints(this.exprParser.parseHints());
        }

        return stmt;
    }

    protected void parseUpdateSet(SQLUpdateStatement update) {
        accept(Token.SET);

        for (;;) {
            SQLUpdateSetItem item = this.exprParser.parseUpdateSetItem();
            update.addItem(item);

            if (lexer.token() != Token.COMMA) {
                break;
            }

            lexer.nextToken();
        }
    }

    public MySqlAlterUserStatement parseAlterUser() {
        accept(Token.USER);

        MySqlAlterUserStatement stmt = new MySqlAlterUserStatement();
        for (;;) {
            SQLExpr user = this.exprParser.expr();
            acceptIdentifier("PASSWORD");
            acceptIdentifier("EXPIRE");
            stmt.getUsers().add(user);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }
        return stmt;
    }

    public MySqlExprParser getExprParser() {
        return (MySqlExprParser) exprParser;
    }

    public MySqlHintStatement parseHint() {
        // accept(Token.HINT);
        MySqlHintStatement stmt = new MySqlHintStatement();
        stmt.setHints(this.exprParser.parseHints());

        return stmt;
    }
}
