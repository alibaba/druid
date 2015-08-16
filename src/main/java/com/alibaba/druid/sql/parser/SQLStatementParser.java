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
package com.alibaba.druid.sql.parser;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddIndex;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddPartition;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAlterColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableKeys;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableLifecycle;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropColumnItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropPartition;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableKeys;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableLifecycle;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableRename;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableRenameColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableRenamePartition;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableSetComment;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableSetLifecycle;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableTouch;
import com.alibaba.druid.sql.ast.statement.SQLAlterViewRenameStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCallStatement;
import com.alibaba.druid.sql.ast.statement.SQLCheck;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCommentStatement;
import com.alibaba.druid.sql.ast.statement.SQLConstraint;
import com.alibaba.druid.sql.ast.statement.SQLCreateDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement.TriggerEvent;
import com.alibaba.druid.sql.ast.statement.SQLCreateTriggerStatement.TriggerType;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropDatabaseStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropFunctionStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropProcedureStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropSequenceStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableSpaceStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTriggerStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropUserStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLExplainStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLGrantStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLObjectType;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLReleaseSavePointStatement;
import com.alibaba.druid.sql.ast.statement.SQLRevokeStatement;
import com.alibaba.druid.sql.ast.statement.SQLRollbackStatement;
import com.alibaba.druid.sql.ast.statement.SQLSavePointStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTruncateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.ast.statement.SQLUseStatement;
import com.alibaba.druid.sql.dialect.odps.parser.OdpsSelectParser;

public class SQLStatementParser extends SQLParser {

    protected SQLExprParser exprParser;

    protected boolean parseCompleteValues = true;

    protected int parseValuesSize = 3;

    public SQLStatementParser(String sql){
        this(sql, null);
    }

    public SQLStatementParser(String sql, String dbType){
        this(new SQLExprParser(sql, dbType));
    }

    public SQLStatementParser(SQLExprParser exprParser){
        super(exprParser.getLexer(), exprParser.getDbType());
        this.exprParser = exprParser;
    }

    protected SQLStatementParser(Lexer lexer, String dbType){
        super(lexer, dbType);
    }

    public boolean isKeepComments() {
        return lexer.isKeepComments();
    }

    public void setKeepComments(boolean keepComments) {
        this.lexer.setKeepComments(keepComments);
    }

    public SQLExprParser getExprParser() {
        return exprParser;
    }

    public List<SQLStatement> parseStatementList() {
        List<SQLStatement> statementList = new ArrayList<SQLStatement>();
        parseStatementList(statementList);
        return statementList;
    }

    public void parseStatementList(List<SQLStatement> statementList) {
        parseStatementList(statementList, -1);
    }

    public void parseStatementList(List<SQLStatement> statementList, int max) {
        for (;;) {
            if (max != -1) {
                if (statementList.size() >= max) {
                    return;
                }
            }

            if (lexer.token() == Token.EOF || lexer.token() == Token.END) {
                if (lexer.isKeepComments() && lexer.hasComment() && statementList.size() > 0) {
                    SQLStatement stmt = statementList.get(statementList.size() - 1);
                    stmt.addAfterComment(lexer.readAndResetComments());
                }
                return;
            }

            if (lexer.token() == Token.SEMI) {
                int line0 = lexer.getLine();
                lexer.nextToken();
                int line1 = lexer.getLine();
                
                if (lexer.isKeepComments() && statementList.size() > 0) {
                    SQLStatement stmt = statementList.get(statementList.size() - 1);
                    if (line1 - line0 <= 1) {
                        stmt.addAfterComment(lexer.readAndResetComments());
                    }
                    stmt.getAttributes().put("format.semi", Boolean.TRUE);
                }
                continue;
            }

            if (lexer.token() == Token.SELECT) {
                statementList.add(parseSelect());
                continue;
            }

            if (lexer.token() == (Token.UPDATE)) {
                statementList.add(parseUpdateStatement());
                continue;
            }

            if (lexer.token() == (Token.CREATE)) {
                statementList.add(parseCreate());
                continue;
            }

            if (lexer.token() == (Token.INSERT)) {
                SQLStatement insertStatement = parseInsert();
                statementList.add(insertStatement);

                continue;
            }

            if (lexer.token() == (Token.DELETE)) {
                statementList.add(parseDeleteStatement());
                continue;
            }

            if (lexer.token() == (Token.EXPLAIN)) {
                statementList.add(parseExplain());
                continue;
            }

            if (lexer.token() == Token.SET) {
                statementList.add(parseSet());
                continue;
            }

            if (lexer.token() == Token.ALTER) {
                statementList.add(parseAlter());
                continue;
            }

            if (lexer.token() == Token.DROP) {
                List<String> beforeComments = null;
                if (lexer.isKeepComments() && lexer.hasComment()) {
                    beforeComments = lexer.readAndResetComments();
                }
                
                lexer.nextToken();

                if (lexer.token() == Token.TABLE || identifierEquals("TEMPORARY")) {

                    SQLDropTableStatement stmt = parseDropTable(false);
                    
                    if (beforeComments != null) {
                        stmt.addBeforeComment(beforeComments);
                    }

                    statementList.add(stmt);
                    continue;
                } else if (lexer.token() == Token.USER) {
                    SQLStatement stmt = parseDropUser();
                    if (beforeComments != null) {
                        stmt.addBeforeComment(beforeComments);
                    }
                    
                    statementList.add(stmt);
                    continue;
                } else if (lexer.token() == Token.INDEX) {
                    SQLStatement stmt = parseDropIndex();
                    if (beforeComments != null) {
                        stmt.addBeforeComment(beforeComments);
                    }
                    
                    statementList.add(stmt);
                    continue;
                } else if (lexer.token() == Token.VIEW) {
                    SQLStatement stmt = parseDropView(false);
                    
                    if (beforeComments != null) {
                        stmt.addBeforeComment(beforeComments);
                    }
                    
                    statementList.add(stmt);
                    continue;
                } else if (lexer.token() == Token.TRIGGER) {
                    SQLStatement stmt = parseDropTrigger(false);
                    
                    if (beforeComments != null) {
                        stmt.addBeforeComment(beforeComments);
                    }
                    
                    statementList.add(stmt);
                    continue;
                } else if (lexer.token() == Token.DATABASE) {
                    SQLStatement stmt = parseDropDatabase(false);
                    
                    if (beforeComments != null) {
                        stmt.addBeforeComment(beforeComments);
                    }
                    
                    statementList.add(stmt);
                    continue;
                } else if (lexer.token() == Token.FUNCTION) {
                    SQLStatement stmt = parseDropFunction(false);
                    
                    if (beforeComments != null) {
                        stmt.addBeforeComment(beforeComments);
                    }
                    
                    statementList.add(stmt);
                    continue;
                } else if (lexer.token() == Token.TABLESPACE) {
                    SQLStatement stmt = parseDropTablespace(false);
                    
                    if (beforeComments != null) {
                        stmt.addBeforeComment(beforeComments);
                    }
                    
                    statementList.add(stmt);
                    continue;
                } else if (lexer.token() == Token.PROCEDURE) {
                    SQLStatement stmt = parseDropProcedure(false);
                    
                    if (beforeComments != null) {
                        stmt.addBeforeComment(beforeComments);
                    }
                    
                    statementList.add(stmt);
                    continue;
                } else {
                    throw new ParserException("TODO " + lexer.token());
                }
            }

            if (lexer.token() == Token.TRUNCATE) {
                SQLStatement stmt = parseTruncate();
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.USE) {
                SQLStatement stmt = parseUse();
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.GRANT) {
                SQLStatement stmt = parseGrant();
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.REVOKE) {
                SQLStatement stmt = parseRevoke();
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.LBRACE || identifierEquals("CALL")) {
                SQLCallStatement stmt = parseCall();
                statementList.add(stmt);
                continue;
            }

            if (identifierEquals("RENAME")) {
                SQLStatement stmt = parseRename();
                statementList.add(stmt);
                continue;
            }

            if (identifierEquals("RELEASE")) {
                SQLStatement stmt = parseReleaseSavePoint();
                statementList.add(stmt);
                continue;
            }

            if (identifierEquals("SAVEPOINT")) {
                SQLStatement stmt = parseSavePoint();
                statementList.add(stmt);
                continue;
            }

            if (identifierEquals("ROLLBACK")) {
                SQLRollbackStatement stmt = parseRollback();

                statementList.add(stmt);
                continue;
            }

            if (identifierEquals("COMMIT")) {
                SQLStatement stmt = parseCommit();

                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.SHOW) {
                SQLStatement stmt = parseShow();

                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.LPAREN) {
                char markChar = lexer.current();
                int markBp = lexer.bp();
                lexer.nextToken();
                if (lexer.token() == Token.SELECT) {
                    lexer.reset(markBp, markChar, Token.LPAREN);
                    SQLStatement stmt = parseSelect();
                    statementList.add(stmt);
                    continue;
                }
            }

            if (parseStatementListDialect(statementList)) {
                continue;
            }

            if (lexer.token() == Token.COMMENT) {
                statementList.add(this.parseComment());
                continue;
            }

            // throw new ParserException("syntax error, " + lexer.token() + " "
            // + lexer.stringVal() + ", pos "
            // + lexer.pos());
            printError(lexer.token());
        }
    }

    public SQLRollbackStatement parseRollback() {
        lexer.nextToken();

        if (identifierEquals("WORK")) {
            lexer.nextToken();
        }

        SQLRollbackStatement stmt = new SQLRollbackStatement(getDbType());

        if (lexer.token() == Token.TO) {
            lexer.nextToken();

            if (identifierEquals("SAVEPOINT")) {
                lexer.nextToken();
            }

            stmt.setTo(this.exprParser.name());
        }
        return stmt;
    }

    public SQLStatement parseCommit() {
        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
    }

    public SQLStatement parseShow() {
        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
    }

    public SQLUseStatement parseUse() {
        accept(Token.USE);
        SQLUseStatement stmt = new SQLUseStatement(getDbType());
        stmt.setDatabase(this.exprParser.name());
        return stmt;
    }

    public SQLGrantStatement parseGrant() {
        accept(Token.GRANT);
        SQLGrantStatement stmt = new SQLGrantStatement(getDbType());

        parsePrivileages(stmt.getPrivileges(), stmt);

        if (lexer.token() == Token.ON) {
            lexer.nextToken();

            if (lexer.token() == Token.PROCEDURE) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.PROCEDURE);
            } else if (lexer.token() == Token.FUNCTION) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.FUNCTION);
            } else if (lexer.token() == Token.TABLE) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.TABLE);
            } else if (lexer.token() == Token.USER) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.USER);
            } else if (lexer.token() == Token.DATABASE) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.DATABASE);
            }

            if (stmt.getObjectType() != null && lexer.token() == Token.COLONCOLON) {
                lexer.nextToken(); // sql server
            }

            SQLExpr expr = this.exprParser.expr();
            if (stmt.getObjectType() == SQLObjectType.TABLE || stmt.getObjectType() == null) {
                stmt.setOn(new SQLExprTableSource(expr));
            } else {
                stmt.setOn(expr);
            }
        }

        if (lexer.token() == Token.TO) {
            lexer.nextToken();
            stmt.setTo(this.exprParser.expr());
        }

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();

            for (;;) {
                if (identifierEquals("MAX_QUERIES_PER_HOUR")) {
                    lexer.nextToken();
                    stmt.setMaxQueriesPerHour(this.exprParser.primary());
                    continue;
                }

                if (identifierEquals("MAX_UPDATES_PER_HOUR")) {
                    lexer.nextToken();
                    stmt.setMaxUpdatesPerHour(this.exprParser.primary());
                    continue;
                }

                if (identifierEquals("MAX_CONNECTIONS_PER_HOUR")) {
                    lexer.nextToken();
                    stmt.setMaxConnectionsPerHour(this.exprParser.primary());
                    continue;
                }

                if (identifierEquals("MAX_USER_CONNECTIONS")) {
                    lexer.nextToken();
                    stmt.setMaxUserConnections(this.exprParser.primary());
                    continue;
                }

                break;
            }
        }

        if (identifierEquals("ADMIN")) {
            lexer.nextToken();
            acceptIdentifier("OPTION");
            stmt.setAdminOption(true);
        }

        if (lexer.token() == Token.IDENTIFIED) {
            lexer.nextToken();
            accept(Token.BY);
            stmt.setIdentifiedBy(this.exprParser.expr());
        }

        return stmt;
    }

    protected void parsePrivileages(List<SQLExpr> privileges, SQLObject parent) {
        for (;;) {
            String privilege = null;
            if (lexer.token() == Token.ALL) {
                lexer.nextToken();
                if (identifierEquals("PRIVILEGES")) {
                    privilege = "ALL PRIVILEGES";
                } else {
                    privilege = "ALL";
                }
            } else if (lexer.token() == Token.SELECT) {
                privilege = "SELECT";
                lexer.nextToken();
            } else if (lexer.token() == Token.UPDATE) {
                privilege = "UPDATE";
                lexer.nextToken();
            } else if (lexer.token() == Token.DELETE) {
                privilege = "DELETE";
                lexer.nextToken();
            } else if (lexer.token() == Token.INSERT) {
                privilege = "INSERT";
                lexer.nextToken();
            } else if (lexer.token() == Token.INDEX) {
                lexer.nextToken();
                privilege = "INDEX";
            } else if (lexer.token() == Token.TRIGGER) {
                lexer.nextToken();
                privilege = "TRIGGER";
            } else if (lexer.token() == Token.REFERENCES) {
                privilege = "REFERENCES";
                lexer.nextToken();
            } else if (lexer.token() == Token.CREATE) {
                lexer.nextToken();

                if (lexer.token() == Token.TABLE) {
                    privilege = "CREATE TABLE";
                    lexer.nextToken();
                } else if (lexer.token() == Token.SESSION) {
                    privilege = "CREATE SESSION";
                    lexer.nextToken();
                } else if (lexer.token() == Token.TABLESPACE) {
                    privilege = "CREATE TABLESPACE";
                    lexer.nextToken();
                } else if (lexer.token() == Token.USER) {
                    privilege = "CREATE USER";
                    lexer.nextToken();
                } else if (lexer.token() == Token.VIEW) {
                    privilege = "CREATE VIEW";
                    lexer.nextToken();
                } else if (lexer.token() == Token.ANY) {
                    lexer.nextToken();

                    if (lexer.token() == Token.TABLE) {
                        lexer.nextToken();
                        privilege = "CREATE ANY TABLE";
                    } else if (identifierEquals("MATERIALIZED")) {
                        lexer.nextToken();
                        accept(Token.VIEW);
                        privilege = "CREATE ANY MATERIALIZED VIEW";
                    } else {
                        throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                    }
                } else if (identifierEquals("SYNONYM")) {
                    privilege = "CREATE SYNONYM";
                    lexer.nextToken();
                } else if (identifierEquals("ROUTINE")) {
                    privilege = "CREATE ROUTINE";
                    lexer.nextToken();
                } else if (identifierEquals("TEMPORARY")) {
                    lexer.nextToken();
                    accept(Token.TABLE);
                    privilege = "CREATE TEMPORARY TABLE";
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
            } else if (lexer.token() == Token.ALTER) {
                lexer.nextToken();
                if (lexer.token() == Token.TABLE) {
                    privilege = "ALTER TABLE";
                    lexer.nextToken();
                } else if (lexer.token() == Token.SESSION) {
                    privilege = "ALTER SESSION";
                    lexer.nextToken();
                } else if (lexer.token() == Token.ANY) {
                    lexer.nextToken();

                    if (lexer.token() == Token.TABLE) {
                        lexer.nextToken();
                        privilege = "ALTER ANY TABLE";
                    } else if (identifierEquals("MATERIALIZED")) {
                        lexer.nextToken();
                        accept(Token.VIEW);
                        privilege = "ALTER ANY MATERIALIZED VIEW";
                    } else {
                        throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                    }
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
            } else if (lexer.token() == Token.DROP) {
                lexer.nextToken();
                if (lexer.token() == Token.DROP) {
                    privilege = "DROP TABLE";
                    lexer.nextToken();
                } else if (lexer.token() == Token.SESSION) {
                    privilege = "DROP SESSION";
                    lexer.nextToken();
                } else if (lexer.token() == Token.ANY) {
                    lexer.nextToken();

                    if (lexer.token() == Token.TABLE) {
                        lexer.nextToken();
                        privilege = "DROP ANY TABLE";
                    } else if (identifierEquals("MATERIALIZED")) {
                        lexer.nextToken();
                        accept(Token.VIEW);
                        privilege = "DROP ANY MATERIALIZED VIEW";
                    } else {
                        throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                    }
                } else {
                    privilege = "DROP";
                }
            } else if (identifierEquals("USAGE")) {
                privilege = "USAGE";
                lexer.nextToken();
            } else if (identifierEquals("EXECUTE")) {
                privilege = "EXECUTE";
                lexer.nextToken();
            } else if (identifierEquals("PROXY")) {
                privilege = "PROXY";
                lexer.nextToken();
            } else if (identifierEquals("QUERY")) {
                lexer.nextToken();
                acceptIdentifier("REWRITE");
                privilege = "QUERY REWRITE";
            } else if (identifierEquals("GLOBAL")) {
                lexer.nextToken();
                acceptIdentifier("QUERY");
                acceptIdentifier("REWRITE");
                privilege = "GLOBAL QUERY REWRITE";
            } else if (identifierEquals("INHERIT")) {
                lexer.nextToken();
                acceptIdentifier("PRIVILEGES");
                privilege = "INHERIT PRIVILEGES";
            } else if (identifierEquals("EVENT")) {
                lexer.nextToken();
                privilege = "EVENT";
            } else if (identifierEquals("FILE")) {
                lexer.nextToken();
                privilege = "FILE";
            } else if (lexer.token() == Token.GRANT) {
                lexer.nextToken();
                acceptIdentifier("OPTION");
                privilege = "GRANT OPTION";
            } else if (lexer.token() == Token.LOCK) {
                lexer.nextToken();
                acceptIdentifier("TABLES");
                privilege = "LOCK TABLES";
            } else if (identifierEquals("PROCESS")) {
                lexer.nextToken();
                privilege = "PROCESS";
            } else if (identifierEquals("RELOAD")) {
                lexer.nextToken();
                privilege = "RELOAD";
            } else if (identifierEquals("REPLICATION")) {
                lexer.nextToken();
                if (identifierEquals("SLAVE")) {
                    lexer.nextToken();
                    privilege = "REPLICATION SLAVE";
                } else {
                    acceptIdentifier("CLIENT");
                    privilege = "REPLICATION CLIENT";
                }
            } else if (lexer.token() == Token.SHOW) {
                lexer.nextToken();

                if (lexer.token() == Token.VIEW) {
                    lexer.nextToken();
                    privilege = "SHOW VIEW";
                } else {
                    acceptIdentifier("DATABASES");
                    privilege = "SHOW DATABASES";
                }
            } else if (identifierEquals("SHUTDOWN")) {
                lexer.nextToken();
                privilege = "SHUTDOWN";
            } else if (identifierEquals("SUPER")) {
                lexer.nextToken();
                privilege = "SUPER";

            } else if (identifierEquals("CONTROL")) { // sqlserver
                lexer.nextToken();
                privilege = "CONTROL";
            } else if (identifierEquals("IMPERSONATE")) { // sqlserver
                lexer.nextToken();
                privilege = "IMPERSONATE";
            }

            if (privilege != null) {
                SQLExpr expr = new SQLIdentifierExpr(privilege);

                if (lexer.token() == Token.LPAREN) {
                    expr = this.exprParser.primaryRest(expr);
                }

                expr.setParent(parent);
                privileges.add(expr);
            }

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
    }

    public SQLRevokeStatement parseRevoke() {
        accept(Token.REVOKE);

        SQLRevokeStatement stmt = new SQLRevokeStatement(getDbType());

        parsePrivileages(stmt.getPrivileges(), stmt);

        if (lexer.token() == Token.ON) {
            lexer.nextToken();

            if (lexer.token() == Token.PROCEDURE) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.PROCEDURE);
            } else if (lexer.token() == Token.FUNCTION) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.FUNCTION);
            } else if (lexer.token() == Token.TABLE) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.TABLE);
            } else if (lexer.token() == Token.USER) {
                lexer.nextToken();
                stmt.setObjectType(SQLObjectType.USER);
            }

            SQLExpr expr = this.exprParser.expr();
            if (stmt.getObjectType() == SQLObjectType.TABLE || stmt.getObjectType() == null) {
                stmt.setOn(new SQLExprTableSource(expr));
            } else {
                stmt.setOn(expr);
            }
        }

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            stmt.setFrom(this.exprParser.expr());
        }

        return stmt;
    }

    public SQLStatement parseSavePoint() {
        acceptIdentifier("SAVEPOINT");
        SQLSavePointStatement stmt = new SQLSavePointStatement(getDbType());
        stmt.setName(this.exprParser.name());
        return stmt;
    }

    public SQLStatement parseReleaseSavePoint() {
        acceptIdentifier("RELEASE");
        acceptIdentifier("SAVEPOINT");
        SQLReleaseSavePointStatement stmt = new SQLReleaseSavePointStatement(getDbType());
        stmt.setName(this.exprParser.name());
        return stmt;
    }

    public SQLStatement parseAlter() {
        accept(Token.ALTER);

        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();

            SQLAlterTableStatement stmt = new SQLAlterTableStatement();
            stmt.setName(this.exprParser.name());

            for (;;) {
                if (lexer.token() == Token.DROP) {
                    parseAlterDrop(stmt);
                } else if (identifierEquals("ADD")) {
                    lexer.nextToken();

                    boolean ifNotExists = false;

                    if (lexer.token() == Token.IF) {
                        lexer.nextToken();
                        accept(Token.NOT);
                        accept(Token.EXISTS);
                        ifNotExists = true;
                    }

                    if (lexer.token() == Token.PRIMARY) {
                        SQLPrimaryKey primaryKey = this.exprParser.parsePrimaryKey();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(primaryKey);
                        stmt.getItems().add(item);
                    } else if (lexer.token() == Token.IDENTIFIER) {
                        SQLAlterTableAddColumn item = parseAlterTableAddColumn();
                        stmt.getItems().add(item);
                    } else if (lexer.token() == Token.COLUMN) {
                        lexer.nextToken();
                        SQLAlterTableAddColumn item = parseAlterTableAddColumn();
                        stmt.getItems().add(item);
                    } else if (lexer.token() == Token.CHECK) {
                        SQLCheck check = this.exprParser.parseCheck();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(check);
                        stmt.getItems().add(item);
                    } else if (lexer.token() == Token.CONSTRAINT) {
                        SQLConstraint constraint = this.exprParser.parseConstaint();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(constraint);
                        stmt.getItems().add(item);
                    } else if (lexer.token() == Token.FOREIGN) {
                        SQLConstraint constraint = this.exprParser.parseForeignKey();
                        SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint(constraint);
                        stmt.getItems().add(item);
                    } else if (lexer.token() == Token.PARTITION) {
                        lexer.nextToken();
                        SQLAlterTableAddPartition addPartition = new SQLAlterTableAddPartition();

                        addPartition.setIfNotExists(ifNotExists);

                        accept(Token.LPAREN);

                        parseAssignItems(addPartition.getPartition(), addPartition);

                        accept(Token.RPAREN);

                        stmt.getItems().add(addPartition);
                    } else {
                        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                    }
                } else if (lexer.token() == Token.DISABLE) {
                    lexer.nextToken();

                    if (lexer.token() == Token.CONSTRAINT) {
                        lexer.nextToken();
                        SQLAlterTableDisableConstraint item = new SQLAlterTableDisableConstraint();
                        item.setConstraintName(this.exprParser.name());
                        stmt.getItems().add(item);
                    } else if (identifierEquals("LIFECYCLE")) {
                        lexer.nextToken();
                        SQLAlterTableDisableLifecycle item = new SQLAlterTableDisableLifecycle();
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
                    } else if (identifierEquals("LIFECYCLE")) {
                        lexer.nextToken();
                        SQLAlterTableEnableLifecycle item = new SQLAlterTableEnableLifecycle();
                        stmt.getItems().add(item);
                    } else {
                        acceptIdentifier("KEYS");
                        SQLAlterTableEnableKeys item = new SQLAlterTableEnableKeys();
                        stmt.getItems().add(item);
                    }
                } else if (lexer.token() == Token.ALTER) {
                    lexer.nextToken();
                    if (lexer.token() == Token.COLUMN) {
                        SQLAlterTableAlterColumn alterColumn = parseAlterColumn();
                        stmt.getItems().add(alterColumn);
                    } else if (lexer.token() == Token.LITERAL_ALIAS) {
                        SQLAlterTableAlterColumn alterColumn = parseAlterColumn();
                        stmt.getItems().add(alterColumn);
                    } else {
                        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                    }
                } else if (lexer.token() == Token.WITH) {
                    lexer.nextToken();
                    acceptIdentifier("NOCHECK");
                    acceptIdentifier("ADD");
                    SQLConstraint check = this.exprParser.parseConstaint();

                    SQLAlterTableAddConstraint addCheck = new SQLAlterTableAddConstraint();
                    addCheck.setWithNoCheck(true);
                    addCheck.setConstraint(check);
                    stmt.getItems().add(addCheck);
                } else if (identifierEquals("RENAME")) {
                    stmt.getItems().add(parseAlterTableRename());
                } else if (lexer.token() == Token.SET) {
                    lexer.nextToken();
                    
                    if (lexer.token() == Token.COMMENT) {
                        lexer.nextToken();
                        SQLAlterTableSetComment setComment = new SQLAlterTableSetComment();
                        setComment.setComment(this.exprParser.primary());
                        stmt.getItems().add(setComment);
                    } else if (identifierEquals("LIFECYCLE")) {
                        lexer.nextToken();
                        SQLAlterTableSetLifecycle setLifecycle = new SQLAlterTableSetLifecycle();
                        setLifecycle.setLifecycle(this.exprParser.primary());
                        stmt.getItems().add(setLifecycle);
                    } else {
                        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
                    }
                } else if (lexer.token() == Token.PARTITION) {
                    lexer.nextToken();

                    SQLAlterTableRenamePartition renamePartition = new SQLAlterTableRenamePartition();

                    accept(Token.LPAREN);

                    parseAssignItems(renamePartition.getPartition(), renamePartition);

                    accept(Token.RPAREN);
                    
                    if (lexer.token() == Token.ENABLE) {
                        lexer.nextToken();
                        if(identifierEquals("LIFECYCLE")) {
                            lexer.nextToken();
                        }
                        
                        SQLAlterTableEnableLifecycle enableLifeCycle = new SQLAlterTableEnableLifecycle();
                        for (SQLAssignItem condition : renamePartition.getPartition()) {
                            enableLifeCycle.getPartition().add(condition);
                            condition.setParent(enableLifeCycle);
                        }
                        stmt.getItems().add(enableLifeCycle);
                        
                        continue;
                    }
                    
                    if (lexer.token() == Token.DISABLE) {
                        lexer.nextToken();
                        if(identifierEquals("LIFECYCLE")) {
                            lexer.nextToken();
                        }
                        
                        SQLAlterTableDisableLifecycle disableLifeCycle = new SQLAlterTableDisableLifecycle();
                        for (SQLAssignItem condition : renamePartition.getPartition()) {
                            disableLifeCycle.getPartition().add(condition);
                            condition.setParent(disableLifeCycle);
                        }
                        stmt.getItems().add(disableLifeCycle);
                        
                        continue;
                    }
                    
                    acceptIdentifier("RENAME");
                    accept(Token.TO);
                    accept(Token.PARTITION);
                    
                    accept(Token.LPAREN);

                    parseAssignItems(renamePartition.getTo(), renamePartition);

                    accept(Token.RPAREN);

                    stmt.getItems().add(renamePartition);
                } else if(identifierEquals("TOUCH")) {
                    lexer.nextToken();
                    SQLAlterTableTouch item = new SQLAlterTableTouch();
                    
                    if (lexer.token() == Token.PARTITION) {
                        lexer.nextToken();
    
                        accept(Token.LPAREN);
                        parseAssignItems(item.getPartition(), item);
                        accept(Token.RPAREN);
                    }
                    
                    stmt.getItems().add(item);
                } else {
                    break;
                }
            }

            return stmt;
        } else if (lexer.token() == Token.VIEW) {
            lexer.nextToken();
            SQLName viewName = this.exprParser.name();

            if (identifierEquals("RENAME")) {
                lexer.nextToken();
                accept(Token.TO);

                SQLAlterViewRenameStatement stmt = new SQLAlterViewRenameStatement();
                stmt.setName(viewName);

                SQLName newName = this.exprParser.name();

                stmt.setTo(newName);

                return stmt;
            }
            throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
        }
        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
    }

    protected SQLAlterTableItem parseAlterTableRename() {
        acceptIdentifier("RENAME");

        if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
            SQLAlterTableRenameColumn renameColumn = new SQLAlterTableRenameColumn();
            renameColumn.setColumn(this.exprParser.name());
            accept(Token.TO);
            renameColumn.setTo(this.exprParser.name());
            return renameColumn;
        }

        if (lexer.token() == Token.TO) {
            lexer.nextToken();
            SQLAlterTableRename item = new SQLAlterTableRename();
            item.setTo(this.exprParser.name());
            return item;
        }

        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
    }

    protected SQLAlterTableAlterColumn parseAlterColumn() {
        lexer.nextToken();
        SQLColumnDefinition column = this.exprParser.parseColumn();

        SQLAlterTableAlterColumn alterColumn = new SQLAlterTableAlterColumn();
        alterColumn.setColumn(column);
        return alterColumn;
    }

    public void parseAlterDrop(SQLAlterTableStatement stmt) {
        lexer.nextToken();

        boolean ifNotExists = false;

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            ifNotExists = true;
        }

        if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
            SQLAlterTableDropConstraint item = new SQLAlterTableDropConstraint();
            item.setConstraintName(this.exprParser.name());
            stmt.getItems().add(item);
        } else if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());

            if (lexer.token == Token.CASCADE) {
                item.setCascade(true);
                lexer.nextToken();
            }

            stmt.getItems().add(item);
        } else if (lexer.token() == Token.LITERAL_ALIAS) {
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());

            if (lexer.token == Token.CASCADE) {
                item.setCascade(true);
                lexer.nextToken();
            }

            stmt.getItems().add(item);
        } else if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            SQLAlterTableDropPartition dropPartition = new SQLAlterTableDropPartition();

            dropPartition.setIfNotExists(ifNotExists);

            accept(Token.LPAREN);

            parseAssignItems(dropPartition.getPartition(), dropPartition);

            accept(Token.RPAREN);
            
            if (identifierEquals("PURGE")) {
                lexer.nextToken();
                dropPartition.setPurge(true);
            }

            stmt.getItems().add(dropPartition);
        } else {
            throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
        }
    }

    public SQLStatement parseRename() {
        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
    }

    protected SQLDropTableStatement parseDropTable(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        SQLDropTableStatement stmt = new SQLDropTableStatement(getDbType());

        if (identifierEquals("TEMPORARY")) {
            lexer.nextToken();
            stmt.setTemporary(true);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        for (;;) {
            SQLName name = this.exprParser.name();
            stmt.getTableSources().add(new SQLExprTableSource(name));
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }

        for (;;) {
            if (identifierEquals("RESTRICT")) {
                lexer.nextToken();
                stmt.setRestrict(true);
                continue;
            }

            if (identifierEquals("CASCADE")) {
                lexer.nextToken();
                stmt.setCascade(true);

                if (identifierEquals("CONSTRAINTS")) { // for oracle
                    lexer.nextToken();
                }

                continue;
            }

            if (lexer.token() == Token.PURGE) {
                lexer.nextToken();
                stmt.setPurge(true);
                continue;
            }

            break;
        }

        return stmt;
    }

    protected SQLDropSequenceStatement parseDropSequece(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        lexer.nextToken();

        SQLName name = this.exprParser.name();

        SQLDropSequenceStatement stmt = new SQLDropSequenceStatement(getDbType());
        stmt.setName(name);
        return stmt;
    }

    protected SQLDropTriggerStatement parseDropTrigger(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        lexer.nextToken();

        SQLName name = this.exprParser.name();

        SQLDropTriggerStatement stmt = new SQLDropTriggerStatement(getDbType());
        stmt.setName(name);
        return stmt;
    }

    protected SQLDropViewStatement parseDropView(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        SQLDropViewStatement stmt = new SQLDropViewStatement(getDbType());

        accept(Token.VIEW);

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        for (;;) {
            SQLName name = this.exprParser.name();
            stmt.getTableSources().add(new SQLExprTableSource(name));
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }

        if (identifierEquals("RESTRICT")) {
            lexer.nextToken();
            stmt.setRestrict(true);
        } else if (identifierEquals("CASCADE")) {
            lexer.nextToken();

            if (identifierEquals("CONSTRAINTS")) { // for oracle
                lexer.nextToken();
            }

            stmt.setCascade(true);
        }

        return stmt;
    }

    protected SQLDropDatabaseStatement parseDropDatabase(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        SQLDropDatabaseStatement stmt = new SQLDropDatabaseStatement(getDbType());

        accept(Token.DATABASE);

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setDatabase(name);

        return stmt;
    }

    protected SQLDropFunctionStatement parseDropFunction(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        SQLDropFunctionStatement stmt = new SQLDropFunctionStatement(getDbType());

        accept(Token.FUNCTION);

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    protected SQLDropTableSpaceStatement parseDropTablespace(boolean acceptDrop) {
        SQLDropTableSpaceStatement stmt = new SQLDropTableSpaceStatement(getDbType());
        
        if (lexer.isKeepComments() && lexer.hasComment()) {
            stmt.addBeforeComment(lexer.readAndResetComments());
        }
        
        if (acceptDrop) {
            accept(Token.DROP);
        }
        
        accept(Token.TABLESPACE);

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    protected SQLDropProcedureStatement parseDropProcedure(boolean acceptDrop) {
        if (acceptDrop) {
            accept(Token.DROP);
        }

        SQLDropProcedureStatement stmt = new SQLDropProcedureStatement(getDbType());

        accept(Token.PROCEDURE);

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.EXISTS);
            stmt.setIfExists(true);
        }

        SQLName name = this.exprParser.name();
        stmt.setName(name);

        return stmt;
    }

    public SQLStatement parseTruncate() {
        accept(Token.TRUNCATE);
        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();
        }
        SQLTruncateStatement stmt = new SQLTruncateStatement(getDbType());

        if (lexer.token() == Token.ONLY) {
            lexer.nextToken();
            stmt.setOnly(true);
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

        for (;;) {
            if (lexer.token() == Token.PURGE) {
                lexer.nextToken();

                if (identifierEquals("SNAPSHOT")) {
                    lexer.nextToken();
                    acceptIdentifier("LOG");
                    stmt.setPurgeSnapshotLog(true);
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
                continue;
            }

            if (lexer.token() == Token.RESTART) {
                lexer.nextToken();
                accept(Token.IDENTITY);
                stmt.setRestartIdentity(Boolean.TRUE);
                continue;
            } else if (lexer.token() == Token.SHARE) {
                lexer.nextToken();
                accept(Token.IDENTITY);
                stmt.setRestartIdentity(Boolean.FALSE);
                continue;
            }

            if (lexer.token() == Token.CASCADE) {
                lexer.nextToken();
                stmt.setCascade(Boolean.TRUE);
                continue;
            } else if (lexer.token() == Token.RESTRICT) {
                lexer.nextToken();
                stmt.setCascade(Boolean.FALSE);
                continue;
            }

            break;
        }

        return stmt;
    }

    public SQLStatement parseInsert() {
        SQLInsertStatement insertStatement = new SQLInsertStatement();

        if (lexer.token() == Token.INSERT) {
            accept(Token.INSERT);
        }

        parseInsert0(insertStatement);
        return insertStatement;
    }

    protected void parseInsert0(SQLInsertInto insertStatement) {
        parseInsert0(insertStatement, true);
    }

    protected void parseInsert0_hinits(SQLInsertInto insertStatement) {

    }

    protected void parseInsert0(SQLInsertInto insertStatement, boolean acceptSubQuery) {
        if (lexer.token() == Token.INTO) {
            lexer.nextToken();

            SQLName tableName = this.exprParser.name();
            insertStatement.setTableName(tableName);

            if (lexer.token() == Token.LITERAL_ALIAS) {
                insertStatement.setAlias(as());
            }

            parseInsert0_hinits(insertStatement);

            if (lexer.token() == Token.IDENTIFIER) {
                insertStatement.setAlias(lexer.stringVal());
                lexer.nextToken();
            }
        }

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            this.exprParser.exprList(insertStatement.getColumns(), insertStatement);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.VALUES) {
            lexer.nextToken();
            accept(Token.LPAREN);
            SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
            this.exprParser.exprList(values.getValues(), values);
            insertStatement.setValues(values);
            accept(Token.RPAREN);
        } else if (acceptSubQuery && (lexer.token() == Token.SELECT || lexer.token() == Token.LPAREN)) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            insertStatement.setQuery(queryExpr.getSubQuery());
        }
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        return false;
    }

    public SQLDropUserStatement parseDropUser() {
        accept(Token.USER);

        SQLDropUserStatement stmt = new SQLDropUserStatement(getDbType());
        for (;;) {
            SQLExpr expr = this.exprParser.expr();
            stmt.getUsers().add(expr);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }

        return stmt;
    }

    public SQLStatement parseDropIndex() {
        accept(Token.INDEX);
        SQLDropIndexStatement stmt = new SQLDropIndexStatement(getDbType());
        stmt.setIndexName(this.exprParser.name());

        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            stmt.setTableName(this.exprParser.name());
        }
        return stmt;
    }

    public SQLCallStatement parseCall() {

        boolean brace = false;
        if (lexer.token() == Token.LBRACE) {
            lexer.nextToken();
            brace = true;
        }

        SQLCallStatement stmt = new SQLCallStatement(getDbType());

        if (lexer.token() == Token.QUES) {
            lexer.nextToken();
            accept(Token.EQ);
            stmt.setOutParameter(new SQLVariantRefExpr("?"));
        }

        acceptIdentifier("CALL");

        stmt.setProcedureName(exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            exprParser.exprList(stmt.getParameters(), stmt);
            accept(Token.RPAREN);
        }

        if (brace) {
            accept(Token.RBRACE);
            stmt.setBrace(true);
        }

        return stmt;
    }

    public SQLStatement parseSet() {
        accept(Token.SET);
        SQLSetStatement stmt = new SQLSetStatement(getDbType());

        parseAssignItems(stmt.getItems(), stmt);

        return stmt;
    }

    public void parseAssignItems(List<SQLAssignItem> items, SQLObject parent) {
        for (;;) {
            SQLAssignItem item = exprParser.parseAssignItem();
            item.setParent(parent);
            items.add(item);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            } else {
                break;
            }
        }
    }

    public SQLStatement parseCreate() {
        char markChar = lexer.current();
        int markBp = lexer.bp();

        accept(Token.CREATE);

        Token token = lexer.token();

        if (token == Token.TABLE || identifierEquals("GLOBAL")) {
            SQLCreateTableParser createTableParser = getSQLCreateTableParser();
            return createTableParser.parseCrateTable(false);
        } else if (token == Token.INDEX //
                   || token == Token.UNIQUE //
                   || identifierEquals("NONCLUSTERED") // sql server
        ) {
            return parseCreateIndex(false);
        } else if (lexer.token() == Token.SEQUENCE) {
            return parseCreateSequence(false);
        } else if (token == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            if (lexer.token() == Token.PROCEDURE) {
                lexer.reset(markBp, markChar, Token.CREATE);
                return parseCreateProcedure();
            }

            if (lexer.token() == Token.VIEW) {
                lexer.reset(markBp, markChar, Token.CREATE);
                return parseCreateView();
            }

            // lexer.reset(mark_bp, mark_ch, Token.CREATE);
            throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
        } else if (token == Token.DATABASE) {
            lexer.nextToken();
            if (identifierEquals("LINK")) {
                lexer.reset(markBp, markChar, Token.CREATE);
                return parseCreateDbLink();
            }

            lexer.reset(markBp, markChar, Token.CREATE);
            return parseCreateDatabase();
        } else if (identifierEquals("PUBLIC") || identifierEquals("SHARE")) {
            lexer.reset(markBp, markChar, Token.CREATE);
            return parseCreateDbLink();
        } else if (token == Token.VIEW) {
            return parseCreateView();
        } else if (token == Token.TRIGGER) {
            return parseCreateTrigger();
        }

        throw new ParserException("TODO " + lexer.token());
    }

    public SQLStatement parseCreateDbLink() {
        throw new ParserException("TODO " + lexer.token());
    }

    public SQLStatement parseCreateTrigger() {
        accept(Token.TRIGGER);

        SQLCreateTriggerStatement stmt = new SQLCreateTriggerStatement(getDbType());
        stmt.setName(this.exprParser.name());

        if (identifierEquals("BEFORE")) {
            stmt.setTriggerType(TriggerType.BEFORE);
            lexer.nextToken();
        } else if (identifierEquals("AFTER")) {
            stmt.setTriggerType(TriggerType.AFTER);
            lexer.nextToken();
        } else if (identifierEquals("INSTEAD")) {
            lexer.nextToken();
            accept(Token.OF);
            stmt.setTriggerType(TriggerType.INSTEAD_OF);
        }

        for (;;) {
            if (lexer.token() == Token.INSERT) {
                lexer.nextToken();
                stmt.getTriggerEvents().add(TriggerEvent.INSERT);
                continue;
            }

            if (lexer.token() == Token.UPDATE) {
                lexer.nextToken();
                stmt.getTriggerEvents().add(TriggerEvent.UPDATE);
                continue;
            }

            if (lexer.token() == Token.DELETE) {
                lexer.nextToken();
                stmt.getTriggerEvents().add(TriggerEvent.DELETE);
                continue;
            }
            break;
        }

        accept(Token.ON);
        stmt.setOn(this.exprParser.name());

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();
            acceptIdentifier("EACH");
            accept(Token.ROW);
            stmt.setForEachRow(true);
        }

        List<SQLStatement> body = this.parseStatementList();
        if (body == null || body.isEmpty()) {
            throw new ParserException("syntax error");
        }
        stmt.setBody(body.get(0));
        return stmt;
    }

    public SQLStatement parseBlock() {
        throw new ParserException("TODO " + lexer.token());
    }

    public SQLStatement parseCreateDatabase() {
        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();
        }

        accept(Token.DATABASE);

        SQLCreateDatabaseStatement stmt = new SQLCreateDatabaseStatement(getDbType());
        stmt.setName(this.exprParser.name());
        return stmt;
    }

    public SQLStatement parseCreateProcedure() {
        throw new ParserException("TODO " + lexer.token());
    }

    public SQLStatement parseCreateSequence(boolean acceptCreate) {
        throw new ParserException("TODO " + lexer.token());
    }

    public SQLStatement parseCreateIndex(boolean acceptCreate) {
        if (acceptCreate) {
            accept(Token.CREATE);
        }

        SQLCreateIndexStatement stmt = new SQLCreateIndexStatement(getDbType());
        if (lexer.token() == Token.UNIQUE) {
            lexer.nextToken();
            if (identifierEquals("CLUSTERED")) {
                lexer.nextToken();
                stmt.setType("UNIQUE CLUSTERED");
            } else {
                stmt.setType("UNIQUE");
            }
        } else if (identifierEquals("FULLTEXT")) {
            stmt.setType("FULLTEXT");
            lexer.nextToken();
        } else if (identifierEquals("NONCLUSTERED")) {
            stmt.setType("NONCLUSTERED");
            lexer.nextToken();
        }

        accept(Token.INDEX);

        stmt.setName(this.exprParser.name());

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

        return stmt;
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new SQLCreateTableParser(this.exprParser);
    }

    public SQLSelectStatement parseSelect() {
        SQLSelectParser selectParser = createSQLSelectParser();
        SQLSelect select = selectParser.select();
        return new SQLSelectStatement(select);
    }

    public SQLSelectParser createSQLSelectParser() {
        return new OdpsSelectParser(this.exprParser);
    }

    public SQLUpdateStatement parseUpdateStatement() {
        SQLUpdateStatement udpateStatement = createUpdateStatement();

        if (lexer.token() == Token.UPDATE) {
            lexer.nextToken();

            SQLTableSource tableSource = this.exprParser.createSelectParser().parseTableSource();
            udpateStatement.setTableSource(tableSource);
        }

        parseUpdateSet(udpateStatement);

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            udpateStatement.setWhere(this.exprParser.expr());
        }

        return udpateStatement;
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

    protected SQLUpdateStatement createUpdateStatement() {
        return new SQLUpdateStatement(getDbType());
    }

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
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            SQLExpr where = this.exprParser.expr();
            deleteStatement.setWhere(where);
        }

        return deleteStatement;
    }

    public SQLCreateTableStatement parseCreateTable() {
        // SQLCreateTableParser parser = new SQLCreateTableParser(this.lexer);
        // return parser.parseCrateTable();
        throw new ParserException("TODO");
    }

    public SQLCreateViewStatement parseCreateView() {
        SQLCreateViewStatement createView = new SQLCreateViewStatement(getDbType());

        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            createView.setOrReplace(true);
        }

        this.accept(Token.VIEW);

        if (lexer.token() == Token.IF || identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            createView.setIfNotExists(true);
        }

        createView.setName(exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (;;) {
                SQLCreateViewStatement.Column column = new SQLCreateViewStatement.Column();
                SQLExpr expr = this.exprParser.expr();
                column.setExpr(expr);

                if (lexer.token() == Token.COMMENT) {
                    lexer.nextToken();
                    column.setComment((SQLCharExpr) exprParser.primary());
                }

                column.setParent(createView);
                createView.getColumns().add(column);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }

            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLCharExpr comment = (SQLCharExpr) exprParser.primary();
            createView.setComment(comment);
        }

        this.accept(Token.AS);

        createView.setSubQuery(new SQLSelectParser(this.exprParser).select());
        return createView;
    }

    public SQLCommentStatement parseComment() {
        accept(Token.COMMENT);
        SQLCommentStatement stmt = new SQLCommentStatement();

        accept(Token.ON);

        if (lexer.token() == Token.TABLE) {
            stmt.setType(SQLCommentStatement.Type.TABLE);
            lexer.nextToken();
        } else if (lexer.token() == Token.COLUMN) {
            stmt.setType(SQLCommentStatement.Type.COLUMN);
            lexer.nextToken();
        }

        stmt.setOn(this.exprParser.name());

        accept(Token.IS);
        stmt.setComment(this.exprParser.expr());

        return stmt;
    }

    protected SQLAlterTableAddColumn parseAlterTableAddColumn() {
        SQLAlterTableAddColumn item = new SQLAlterTableAddColumn();

        for (;;) {
            SQLColumnDefinition columnDef = this.exprParser.parseColumn();
            item.getColumns().add(columnDef);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                if (identifierEquals("ADD")) {
                    break;
                }
                continue;
            }
            break;
        }
        return item;
    }

    public SQLStatement parseStatement() {
        List<SQLStatement> list = new ArrayList<SQLStatement>();

        this.parseStatementList(list, 1);

        return list.get(0);
    }

    public SQLExplainStatement parseExplain() {
        accept(Token.EXPLAIN);
        if (identifierEquals("PLAN")) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();
        }

        SQLExplainStatement explain = new SQLExplainStatement(getDbType());

        if (lexer.token == Token.HINT) {
            explain.setHints(this.exprParser.parseHints());
        }

        explain.setStatement(parseStatement());

        return explain;
    }

    protected SQLAlterTableAddIndex parseAlterTableAddIndex() {
        SQLAlterTableAddIndex item = new SQLAlterTableAddIndex();

        if (lexer.token() == Token.UNIQUE) {
            item.setUnique(true);
            lexer.nextToken();
            if (lexer.token() == Token.INDEX) {
                item.setKeyOrIndex(Token.INDEX.name);
                lexer.nextToken();
            } else if (lexer.token() == Token.KEY) {
                item.setKeyOrIndex(Token.KEY.name);
                lexer.nextToken();
            }
        } else {
            if (lexer.token() == Token.INDEX) {
                item.setKeyOrIndex(Token.INDEX.name);
                accept(Token.INDEX);
            } else if (lexer.token() == Token.KEY) {
                item.setKeyOrIndex(Token.KEY.name);
                accept(Token.KEY);
            }
        }

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
        } else {
            item.setName(this.exprParser.name());
            accept(Token.LPAREN);
        }

        for (;;) {
            SQLSelectOrderByItem column = this.exprParser.parseSelectOrderByItem();
            item.getItems().add(column);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);
        return item;
    }

    public boolean isParseCompleteValues() {
        return parseCompleteValues;
    }

    public void setParseCompleteValues(boolean parseCompleteValues) {
        this.parseCompleteValues = parseCompleteValues;
    }

    public int getParseValuesSize() {
        return parseValuesSize;
    }

    public void setParseValuesSize(int parseValuesSize) {
        this.parseValuesSize = parseValuesSize;
    }
}
