/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.CobarShowStatus;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlBinlogStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCommitStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlCreateUserStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDescribeStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDropTableStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDropUser;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlExecuteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlKillStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadDataInFileStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlLoadXmlStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlPrepareStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlReplicateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlResetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlRollbackStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetCharSetStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetNamesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSetTransactionIsolationLevelStatement;
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
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowMasterLogsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowStatusStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowTablesStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowWarningsStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlStartTransactionStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class MySqlStatementParser extends SQLStatementParser {

    public MySqlStatementParser(String sql) throws ParserException{
        this(new MySqlLexer(sql));
        this.lexer.nextToken();
        this.exprParser = new MySqlExprParser(lexer);
    }

    public MySqlStatementParser(Lexer lexer){
        super(lexer);
        this.exprParser = new MySqlExprParser(lexer);
    }

    public SQLCreateTableStatement parseCreateTable() throws ParserException {
        MySqlCreateTableParser parser = new MySqlCreateTableParser(lexer);
        return parser.parseCrateTable();
    }

    public SQLSelectStatement parseSelect() throws ParserException {
        return new SQLSelectStatement(new MySqlSelectParser(lexer).select());
    }

    public SQLUpdateStatement parseUpdateStatement() throws ParserException {
        MySqlUpdateStatement stmt = (MySqlUpdateStatement) super.parseUpdateStatement();

        if (lexer.token() == Token.LIMIT) {
            lexer.nextToken();

            MySqlSelectQueryBlock.Limit limit = new MySqlSelectQueryBlock.Limit();

            SQLExpr temp = this.exprParser.expr();
            if (lexer.token() == (Token.COMMA)) {
                limit.setOffset(temp);
                lexer.nextToken();
                limit.setRowCount(exprParser.expr());
            } else if (identifierEquals("OFFSET")) {
                limit.setRowCount(temp);
                lexer.nextToken();
                limit.setOffset(exprParser.expr());
            } else {
                limit.setRowCount(temp);
            }

            stmt.setLimit(limit);
        }

        return stmt;
    }

    protected MySqlUpdateStatement createUpdateStatement() {
        return new MySqlUpdateStatement();
    }

    public MySqlDeleteStatement parseDeleteStatement() throws ParserException {
        MySqlDeleteStatement deleteStatement = new MySqlDeleteStatement();

        if (lexer.token() == Token.DELETE) {
            lexer.nextToken();

            if (identifierEquals("LOW_PRIORITY")) {
                deleteStatement.setLowPriority(true);
                lexer.nextToken();
            }

            if (identifierEquals("QUICK")) {
                deleteStatement.setQuick(true);
                lexer.nextToken();
            }

            if (identifierEquals("IGNORE")) {
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
            } else {
                if (lexer.token() == Token.FROM) {
                    lexer.nextToken();
                    deleteStatement.setTableSource(createSQLSelectParser().parseTableSource());
                }
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

        if (lexer.token() == Token.LIMIT) {
            lexer.nextToken();

            MySqlSelectQueryBlock.Limit limit = new MySqlSelectQueryBlock.Limit();
            SQLExpr rowCount = exprParser.expr();
            limit.setRowCount(rowCount);

            deleteStatement.setLimit(limit);
        }

        return deleteStatement;
    }

    public SQLStatement parseCreate() throws ParserException {
        accept(Token.CREATE);

        if (lexer.token() == Token.TABLE || identifierEquals("TEMPORARY")) {
            MySqlCreateTableParser parser = new MySqlCreateTableParser(lexer);
            return parser.parseCrateTable(false);
        }

        if (lexer.token() == Token.USER) {
            return parseCreateUser();
        }

        throw new ParserException("TODO " + lexer.token());
    }

    public SQLStatement parseCreateUser() throws ParserException {
        accept(Token.USER);

        MySqlCreateUserStatement stmt = new MySqlCreateUserStatement();

        for (;;) {
            MySqlCreateUserStatement.UserSpecification userSpec = new MySqlCreateUserStatement.UserSpecification();

            SQLCharExpr expr = (SQLCharExpr) exprParser.expr();
            String user = expr.toString();
            if (lexer.token() == Token.VARIANT) {
                lexer.nextToken();
                SQLCharExpr expr2 = (SQLCharExpr) exprParser.expr();
                user += '@';
                user += expr2.toString();
            }
            userSpec.setUser(new SQLIdentifierExpr(user));

            if (lexer.token() == Token.IDENTIFIED) {
                lexer.nextToken();
                if (lexer.token() == Token.BY) {
                    lexer.nextToken();

                    if (lexer.token() == Token.PASSWORD) {
                        lexer.nextToken();
                    }

                    SQLCharExpr password = (SQLCharExpr) this.exprParser.expr();
                    userSpec.setPassword(password);
                } else if (lexer.token() == Token.WITH) {
                    lexer.nextToken();

                    SQLCharExpr text = (SQLCharExpr) this.exprParser.expr();
                    userSpec.setAuthPlugin(text);
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

    public SQLStatement parseReset() {
        acceptIdentifier("RESET");

        MySqlResetStatement stmt = new MySqlResetStatement();

        for (;;) {
            if (lexer.token() == Token.IDENTIFIER) {
                if (identifierEquals("QUERY")) {
                    lexer.nextToken();
                    acceptIdentifier("CACHE");
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

        if (identifierEquals("REPLACE")) {
            MySqlReplicateStatement stmt = parseReplicate();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("START")) {
            MySqlStartTransactionStatement stmt = parseStart();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("COMMIT")) {
            MySqlCommitStatement stmt = parseCommit();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("ROLLBACK")) {
            MySqlRollbackStatement stmt = parseRollback();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("SHOW")) {
            SQLStatement stmt = parseShow();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("BINLOG")) {
            SQLStatement stmt = parseBinlog();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("RESET")) {
            SQLStatement stmt = parseReset();
            statementList.add(stmt);
            return true;
        }

        if (identifierEquals("DESC") || identifierEquals("DESCRIBE")) {
            SQLStatement stmt = parseDescribe();
            statementList.add(stmt);
            return true;
        }

        return false;
    }

    public MySqlDescribeStatement parseDescribe() throws ParserException {
        if (identifierEquals("DESC") || identifierEquals("DESCRIBE")) {
            lexer.nextToken();
        } else {
            throw new ParserException("expect DESC, actual " + lexer.token());
        }

        MySqlDescribeStatement stmt = new MySqlDescribeStatement();
        stmt.setObject(this.exprParser.name());

        return stmt;
    }

    public SQLStatement parseShow() throws ParserException {
        acceptIdentifier("SHOW");

        if (lexer.token() == Token.FULL) {
            lexer.nextToken();
            acceptIdentifier("COLUMNS");

            MySqlShowColumnsStatement stmt = parseShowColumns();
            stmt.setFull(true);

            return stmt;
        }

        if (identifierEquals("COLUMNS")) {
            lexer.nextToken();

            MySqlShowColumnsStatement stmt = parseShowColumns();

            return stmt;
        }

        if (identifierEquals("TABLES")) {
            lexer.nextToken();

            MySqlShowTablesStatement stmt = parseShowTabless();

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
            acceptIdentifier("WARNINGS");

            MySqlShowWarningsStatement stmt = new MySqlShowWarningsStatement();
            stmt.setCount(true);

            return stmt;
        }

        if (identifierEquals("STATUS")) {
            lexer.nextToken();

            MySqlShowStatusStatement stmt = parseShowStatus();

            return stmt;
        }

        if (identifierEquals("GLOBAL")) {
            lexer.nextToken();

            if (identifierEquals("STATUS")) {
                lexer.nextToken();
                MySqlShowStatusStatement stmt = parseShowStatus();
                stmt.setGlobal(true);
                return stmt;
            }
        }

        if (identifierEquals("SESSION")) {
            lexer.nextToken();

            if (identifierEquals("STATUS")) {
                lexer.nextToken();
                MySqlShowStatusStatement stmt = parseShowStatus();
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

        if (identifierEquals("BINARY")) {
            lexer.nextToken();
            acceptIdentifier("LOGS");
            return new MySqlShowBinaryLogsStatement();
        }

        if (identifierEquals("MASTER")) {
            lexer.nextToken();
            acceptIdentifier("LOGS");
            return new MySqlShowMasterLogsStatement();
        }

        if (identifierEquals("CHARACTER")) {
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

        if (identifierEquals("BINLOG")) {
            lexer.nextToken();
            acceptIdentifier("EVENTS");
            MySqlShowBinLogEventsStatement stmt = new MySqlShowBinLogEventsStatement();

            if (lexer.token() == Token.IN) {
                lexer.nextToken();
                stmt.setIn(this.exprParser.expr());
            }

            if (lexer.token() == Token.FROM) {
                lexer.nextToken();
                stmt.setFrom(this.exprParser.expr());
            }

            if (lexer.token() == Token.LIMIT) {
                lexer.nextToken();

                MySqlSelectQueryBlock.Limit limit = new MySqlSelectQueryBlock.Limit();

                SQLExpr temp = this.exprParser.expr();
                if (lexer.token() == (Token.COMMA)) {
                    limit.setOffset(temp);
                    lexer.nextToken();
                    limit.setRowCount(exprParser.expr());
                } else if (identifierEquals("OFFSET")) {
                    limit.setRowCount(temp);
                    lexer.nextToken();
                    limit.setOffset(exprParser.expr());
                } else {
                    limit.setRowCount(temp);
                }
                stmt.setLimit(limit);
            }

            return stmt;
        }

        if (identifierEquals("CONTRIBUTORS")) {
            lexer.nextToken();
            return new MySqlShowContributorsStatement();
        }
        
        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();
            
            if (identifierEquals("DATABASE")) {
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
            
            if (identifierEquals("FUNCTION")) {
                lexer.nextToken();
                
                MySqlShowCreateFunctionStatement stmt = new MySqlShowCreateFunctionStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }
            
            if (identifierEquals("PROCEDURE")) {
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
            
            if (identifierEquals("TRIGGER")) {
                lexer.nextToken();
                
                MySqlShowCreateTriggerStatement stmt = new MySqlShowCreateTriggerStatement();
                stmt.setName(this.exprParser.name());
                return stmt;
            }
            
            throw new ParserException("TODO " + lexer.stringVal());
        }

        throw new ParserException("TODO " + lexer.stringVal());
    }

    private MySqlShowStatusStatement parseShowStatus() throws ParserException {
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

    private MySqlShowWarningsStatement parseShowWarnings() throws ParserException {
        MySqlShowWarningsStatement stmt = new MySqlShowWarningsStatement();

        if (lexer.token() == Token.LIMIT) {
            lexer.nextToken();

            MySqlSelectQueryBlock.Limit limit = new MySqlSelectQueryBlock.Limit();

            SQLExpr temp = this.exprParser.expr();
            if (lexer.token() == (Token.COMMA)) {
                limit.setOffset(temp);
                lexer.nextToken();
                limit.setRowCount(exprParser.expr());
            } else if (identifierEquals("OFFSET")) {
                limit.setRowCount(temp);
                lexer.nextToken();
                limit.setOffset(exprParser.expr());
            } else {
                limit.setRowCount(temp);
            }

            stmt.setLimit(limit);
        }

        return stmt;
    }

    private MySqlShowDatabasesStatement parseShowDatabases() throws ParserException {
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

    private MySqlShowTablesStatement parseShowTabless() throws ParserException {
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

    private MySqlShowColumnsStatement parseShowColumns() throws ParserException {
        MySqlShowColumnsStatement stmt = new MySqlShowColumnsStatement();

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            SQLName table = exprParser.name();
            stmt.setTable(table);

            if (lexer.token() == Token.FROM) {
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

    public MySqlStartTransactionStatement parseStart() throws ParserException {
        acceptIdentifier("START");
        acceptIdentifier("TRANSACTION");

        MySqlStartTransactionStatement stmt = new MySqlStartTransactionStatement();

        if (identifierEquals("WITH")) {
            lexer.nextToken();
            acceptIdentifier("CONSISTENT");
            acceptIdentifier("SNAPSHOT");
            stmt.setConsistentSnapshot(true);
        }

        if (identifierEquals("BEGIN")) {
            lexer.nextToken();
            stmt.setBegin(true);
            if (identifierEquals("WORK")) {
                lexer.nextToken();
                stmt.setWork(true);
            }
        }

        return stmt;
    }

    public MySqlRollbackStatement parseRollback() throws ParserException {
        acceptIdentifier("ROLLBACK");

        MySqlRollbackStatement stmt = new MySqlRollbackStatement();

        if (identifierEquals("WORK")) {
            lexer.nextToken();
            stmt.setWork(true);
        }

        if (lexer.token() == Token.AND) {
            lexer.nextToken();
            if (lexer.token() == Token.NOT) {
                lexer.nextToken();
                acceptIdentifier("CHAIN");
                stmt.setChain(Boolean.FALSE);
            } else {
                acceptIdentifier("CHAIN");
                stmt.setChain(Boolean.TRUE);
            }
        }

        return stmt;
    }

    public MySqlCommitStatement parseCommit() throws ParserException {
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
                acceptIdentifier("CHAIN");
                stmt.setChain(Boolean.FALSE);
            } else {
                acceptIdentifier("CHAIN");
                stmt.setChain(Boolean.TRUE);
            }
        }

        return stmt;
    }

    public MySqlReplicateStatement parseReplicate() throws ParserException {
        MySqlReplicateStatement stmt = new MySqlReplicateStatement();

        acceptIdentifier("REPLACE");

        if (identifierEquals("LOW_PRIORITY")) {
            stmt.setLowPriority(true);
            lexer.nextToken();
        }

        if (identifierEquals("DELAYED")) {
            stmt.setDelayed(true);
            lexer.nextToken();
        }

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();
        }

        SQLName tableName = exprParser.name();
        stmt.setTableName(tableName);

        if (lexer.token() == Token.VALUES || identifierEquals("VALUE")) {
            lexer.nextToken();

            for (;;) {
                accept(Token.LPAREN);
                SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
                this.exprParser.exprList(values.getValues());
                stmt.getValuesList().add(values);
                accept(Token.RPAREN);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }
        } else if (lexer.token() == Token.SELECT) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            stmt.setQuery(queryExpr);
        } else if (lexer.token() == Token.SET) {
            lexer.nextToken();
            for (;;) {
                SQLUpdateSetItem item = new SQLUpdateSetItem();
                item.setColumn(this.exprParser.name());
                accept(Token.EQ);
                item.setValue(this.exprParser.expr());

                stmt.getSetItems().add(item);

                if (lexer.token() == (Token.COMMA)) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
        }

        return stmt;
    }

    protected SQLStatement parseLoad() throws ParserException {
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

    protected MySqlLoadXmlStatement parseLoadXml() throws ParserException {
        acceptIdentifier("XML");

        MySqlLoadXmlStatement stmt = new MySqlLoadXmlStatement();

        if (identifierEquals("LOW_PRIORITY")) {
            stmt.setLowPriority(true);
            lexer.nextToken();
        }

        if (identifierEquals("CONCURRENT")) {
            stmt.setConcurrent(true);
            lexer.nextToken();
        }

        if (identifierEquals("LOCAL")) {
            stmt.setLocal(true);
            lexer.nextToken();
        }

        acceptIdentifier("INFILE");

        SQLLiteralExpr fileName = (SQLLiteralExpr) exprParser.expr();
        stmt.setFileName(fileName);

        if (identifierEquals("REPLACE")) {
            stmt.setReplicate(true);
            lexer.nextToken();
        }

        if (identifierEquals("IGNORE")) {
            stmt.setIgnore(true);
            lexer.nextToken();
        }

        accept(Token.INTO);
        accept(Token.TABLE);

        SQLName tableName = exprParser.name();
        stmt.setTableName(tableName);

        if (identifierEquals("CHARACTER")) {
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

        if (identifierEquals("IGNORE")) {
            throw new ParserException("TODO");
        }

        if (lexer.token() == Token.SET) {
            throw new ParserException("TODO");
        }

        return stmt;
    }

    protected MySqlLoadDataInFileStatement parseLoadDataInFile() throws ParserException {
        acceptIdentifier("DATA");

        MySqlLoadDataInFileStatement stmt = new MySqlLoadDataInFileStatement();

        if (identifierEquals("LOW_PRIORITY")) {
            stmt.setLowPriority(true);
            lexer.nextToken();
        }

        if (identifierEquals("CONCURRENT")) {
            stmt.setConcurrent(true);
            lexer.nextToken();
        }

        if (identifierEquals("LOCAL")) {
            stmt.setLocal(true);
            lexer.nextToken();
        }

        acceptIdentifier("INFILE");

        SQLLiteralExpr fileName = (SQLLiteralExpr) exprParser.expr();
        stmt.setFileName(fileName);

        if (identifierEquals("REPLACE")) {
            stmt.setReplicate(true);
            lexer.nextToken();
        }

        if (identifierEquals("IGNORE")) {
            stmt.setIgnore(true);
            lexer.nextToken();
        }

        accept(Token.INTO);
        accept(Token.TABLE);

        SQLName tableName = exprParser.name();
        stmt.setTableName(tableName);

        if (identifierEquals("CHARACTER")) {
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
            throw new ParserException("TODO");
        }

        if (identifierEquals("LINES")) {
            throw new ParserException("TODO");
        }

        if (identifierEquals("IGNORE")) {
            throw new ParserException("TODO");
        }

        if (lexer.token() == Token.SET) {
            throw new ParserException("TODO");
        }

        return stmt;
    }

    public MySqlPrepareStatement parsePrepare() throws ParserException {
        acceptIdentifier("PREPARE");

        SQLName name = exprParser.name();
        accept(Token.FROM);
        SQLExpr from = exprParser.expr();

        return new MySqlPrepareStatement(name, from);
    }

    public MySqlExecuteStatement parseExecute() throws ParserException {
        acceptIdentifier("EXECUTE");

        MySqlExecuteStatement stmt = new MySqlExecuteStatement();

        SQLName statementName = exprParser.name();
        stmt.setStatementName(statementName);

        if (identifierEquals("USING")) {
            lexer.nextToken();
            exprParser.exprList(stmt.getParameters());
        }

        return stmt;
    }

    public SQLInsertStatement parseInsert() {
        MySqlInsertStatement insertStatement = new MySqlInsertStatement();

        if (lexer.token() == Token.INSERT) {
            lexer.nextToken();

            if (identifierEquals("LOW_PRIORITY")) {
                insertStatement.setLowPriority(true);
                lexer.nextToken();
            }

            if (identifierEquals("DELAYED")) {
                insertStatement.setDelayed(true);
                lexer.nextToken();
            }

            if (identifierEquals("HIGH_PRIORITY")) {
                insertStatement.setHighPriority(true);
                lexer.nextToken();
            }

            if (identifierEquals("IGNORE")) {
                insertStatement.setIgnore(true);
                lexer.nextToken();
            }

            if (lexer.token() == Token.INTO) {
                lexer.nextToken();
            }

            SQLName tableName = this.exprParser.name();
            insertStatement.setTableName(tableName);

            if (lexer.token() == Token.IDENTIFIER) {
                insertStatement.setAlias(lexer.stringVal());
                lexer.nextToken();
            }

        }

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            this.exprParser.exprList(insertStatement.getColumns());
            accept(Token.RPAREN);
        }

        if (lexer.token() == (Token.VALUES)) {
            lexer.nextToken();

            for (;;) {
                accept(Token.LPAREN);
                SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
                this.exprParser.exprList(values.getValues());
                insertStatement.getValuesList().add(values);
                accept(Token.RPAREN);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }

        } else if (lexer.token() == (Token.SELECT)) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            insertStatement.setQuery(queryExpr.getSubQuery());
        }

        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            acceptIdentifier("DUPLICATE");
            accept(Token.KEY);
            accept(Token.UPDATE);

            exprParser.exprList(insertStatement.getDuplicateKeyUpdate());
        }

        return insertStatement;
    }

    public SQLStatement parseDropUser() throws ParserException {
        accept(Token.USER);

        MySqlDropUser stmt = new MySqlDropUser();
        for (;;) {
            SQLCharExpr expr = (SQLCharExpr) this.exprParser.expr();
            String user = expr.toString();
            if (lexer.token() == Token.VARIANT) {
                lexer.nextToken();
                SQLCharExpr expr2 = (SQLCharExpr) this.exprParser.expr();
                user += '@';
                user += expr2.toString();
            }
            stmt.getUsers().add(new SQLIdentifierExpr(user));
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }

        return stmt;
    }

    protected SQLDropTableStatement parseDropTable(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }
        accept(Token.TABLE);

        MySqlDropTableStatement stmt = new MySqlDropTableStatement();

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        for (;;) {
            SQLName name = this.exprParser.name();
            stmt.addTableSource(name);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        return stmt;
    }

    public SQLSelectParser createSQLSelectParser() {
        return new MySqlSelectParser(this.lexer);
    }

    public SQLStatement parseSet() throws ParserException {
        accept(Token.SET);

        Boolean global = null;
        if (identifierEquals("GLOBAL")) {
            global = Boolean.TRUE;
            lexer.nextToken();
        } else if (identifierEquals("SESSION")) {
            global = Boolean.FALSE;
            lexer.nextToken();
        }

        if (identifierEquals("TRANSACTION")) {
            lexer.nextToken();
            acceptIdentifier("ISOLATION");
            acceptIdentifier("LEVEL");

            MySqlSetTransactionIsolationLevelStatement stmt = new MySqlSetTransactionIsolationLevelStatement();
            stmt.setGlobal(global);

            if (identifierEquals("READ")) {
                lexer.nextToken();

                if (identifierEquals("UNCOMMITTED")) {
                    stmt.setLevel("READ UNCOMMITTED");
                    lexer.nextToken();
                } else if (identifierEquals("WRITE")) {
                    stmt.setLevel("READ WRITE");
                    lexer.nextToken();
                } else if (identifierEquals("ONLY")) {
                    stmt.setLevel("READ ONLY");
                    lexer.nextToken();
                } else if (identifierEquals("COMMITTED")) {
                    stmt.setLevel("READ COMMITTED");
                    lexer.nextToken();
                } else {
                    throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal());
                }
            } else if (identifierEquals("SERIALIZABLE")) {
                stmt.setLevel("SERIALIZABLE");
                lexer.nextToken();
            } else if (identifierEquals("REPEATABLE")) {
                lexer.nextToken();
                if (identifierEquals("READ")) {
                    stmt.setLevel("REPEATABLE READ");
                    lexer.nextToken();
                } else {
                    throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal());
                }
            } else {
                throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal());
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
                if (identifierEquals("COLLATE")) {
                    lexer.nextToken();

                    String collate = lexer.stringVal();
                    stmt.setCollate(collate);
                    lexer.nextToken();
                }
            }
            return stmt;
        } else if (identifierEquals("CHARACTER")) {
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
                if (identifierEquals("COLLATE")) {
                    lexer.nextToken();

                    String collate = lexer.stringVal();
                    stmt.setCollate(collate);
                    lexer.nextToken();
                }
            }
            return stmt;
        } else {
            SQLSetStatement stmt = new SQLSetStatement();

            parseAssignItems(stmt.getItems());

            if (global != null && global.booleanValue()) {
                SQLVariantRefExpr varRef = (SQLVariantRefExpr) stmt.getItems().get(0).getTarget();
                varRef.setGlobal(true);
            }

            return stmt;
        }
    }
}
