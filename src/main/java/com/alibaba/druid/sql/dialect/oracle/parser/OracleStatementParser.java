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
package com.alibaba.druid.sql.dialect.oracle.parser;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLHint;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLNullExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableAddColumn;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDisableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropColumnItem;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableDropConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableEnableConstraint;
import com.alibaba.druid.sql.ast.statement.SQLAlterTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLDropSequenceStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropTriggerStatement;
import com.alibaba.druid.sql.ast.statement.SQLDropUserStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLRollbackStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleErrorLoggingClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleParameter;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterIndexStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterProcedureStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterSessionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterSynonymStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableAddConstaint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableDropPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableModify;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableMoveTablespace;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.NestedTablePartitionSpec;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.TableSpaceItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.UpdateIndexesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceAddDataFile;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTablespaceStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTriggerStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterViewStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleBlockStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleConstraint;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateDatabaseDbLinkStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateIndexStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateProcedureStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateSequenceStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDropDbLinkStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExceptionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExplainStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExprStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleFetchStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleFileSpecification;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleForStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleGotoStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleIfStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLabelStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLockTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLockTableStatement.LockMode;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLoopStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSavePointStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSetTransactionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;

public class OracleStatementParser extends SQLStatementParser {

    public OracleStatementParser(String sql){
        super(new OracleExprParser(sql));
    }

    public OracleStatementParser(Lexer lexer){
        super(new OracleExprParser(lexer));
    }

    @Override
    public OracleExprParser getExprParser() {
        return (OracleExprParser) exprParser;
    }

    public void parseHints(List<SQLHint> hints) {
        this.getExprParser().parseHints(hints);
    }

    public OracleCreateTableParser getSQLCreateTableParser() {
        return new OracleCreateTableParser(lexer);
    }

    protected void parseInsert0_hinits(SQLInsertInto insertStatement) {
        if (insertStatement instanceof OracleInsertStatement) {
            OracleInsertStatement stmt = (OracleInsertStatement) insertStatement;
            this.getExprParser().parseHints(stmt.getHints());
        } else {
            List<SQLHint> hints = new ArrayList<SQLHint>(1);
            this.getExprParser().parseHints(hints);
        }
    }

    public void parseStatementList(List<SQLStatement> statementList, int max) {
        for (;;) {
            if (max != -1) {
                if (statementList.size() >= max) {
                    return;
                }
            }

            if (lexer.token() == Token.EOF) {
                return;
            }
            if (lexer.token() == Token.END) {
                return;
            }
            if (lexer.token() == Token.ELSE) {
                return;
            }

            if (lexer.token() == (Token.SEMI)) {
                lexer.nextToken();
                continue;
            }

            if (lexer.token() == (Token.SELECT)) {
                SQLSelectStatement stmt = new SQLSelectStatement(new OracleSelectParser(this.exprParser).select(), JdbcConstants.ORACLE); 
                statementList.add(stmt);
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

            if (lexer.token() == Token.INSERT) {
                statementList.add(parseInsert());
                continue;
            }

            if (lexer.token() == (Token.DELETE)) {
                statementList.add(parseDeleteStatement());
                continue;
            }

            if (lexer.token() == (Token.SLASH)) {
                lexer.nextToken();
                statementList.add(new OraclePLSQLCommitStatement());
                continue;
            }

            if (lexer.token() == Token.ALTER) {
                statementList.add(parserAlter());
                continue;
            }

            if (lexer.token() == Token.WITH) {
                statementList.add(new SQLSelectStatement(new OracleSelectParser(this.exprParser).select()));
                continue;
            }

            if (lexer.token() == Token.LBRACE || identifierEquals("CALL")) {
                statementList.add(this.parseCall());
                continue;
            }

            if (lexer.token() == Token.MERGE) {
                statementList.add(this.parseMerge());
                continue;
            }

            if (lexer.token() == Token.BEGIN) {
                statementList.add(this.parseBlock());
                continue;
            }

            if (lexer.token() == Token.DECLARE) {
                statementList.add(this.parseBlock());
                continue;
            }

            if (lexer.token() == Token.LOCK) {
                statementList.add(this.parseLock());
                continue;
            }

            if (lexer.token() == Token.TRUNCATE) {
                statementList.add(this.parseTruncate());
                continue;
            }

            if (lexer.token() == Token.VARIANT) {
                SQLExpr variant = this.exprParser.primary();
                if (variant instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) variant;
                    if (binaryOpExpr.getOperator() == SQLBinaryOperator.Assignment) {
                        SQLSetStatement stmt = new SQLSetStatement(binaryOpExpr.getLeft(), binaryOpExpr.getRight(), getDbType());
                        statementList.add(stmt);
                        continue;
                    }
                }
                accept(Token.COLONEQ);
                SQLExpr value = this.exprParser.expr();

                SQLSetStatement stmt = new SQLSetStatement(variant, value, getDbType());
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.EXCEPTION) {
                statementList.add(this.parseException());
                continue;
            }

            if (identifierEquals("EXIT")) {
                lexer.nextToken();
                OracleExitStatement stmt = new OracleExitStatement();
                if (lexer.token() == Token.WHEN) {
                    lexer.nextToken();
                    stmt.setWhen(this.exprParser.expr());
                }
                statementList.add(stmt);
                continue;
            }

            if (identifierEquals("FETCH")) {
                lexer.nextToken();
                OracleFetchStatement stmt = new OracleFetchStatement();
                stmt.setCursorName(this.exprParser.name());
                accept(Token.INTO);
                for (;;) {
                    stmt.getInto().add(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }

                    break;
                }
                statementList.add(stmt);
                continue;
            }

            if (identifierEquals("ROLLBACK")) {
                SQLRollbackStatement stmt = parseRollback();

                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.EXPLAIN) {
                statementList.add(this.parseExplain());
                continue;
            }

            if (lexer.token() == Token.IDENTIFIER) {
                SQLExpr expr = exprParser.expr();
                OracleExprStatement stmt = new OracleExprStatement(expr);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.LPAREN) {
                char ch = lexer.current();
                int bp = lexer.bp();
                lexer.nextToken();

                if (lexer.token() == Token.SELECT) {
                    lexer.reset(bp, ch, Token.LPAREN);
                    statementList.add(this.parseSelect());
                    continue;
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
            }

            if (lexer.token() == Token.SET) {
                statementList.add(this.parseSet());
                continue;
            }

            if (lexer.token() == Token.GRANT) {
                statementList.add(this.parseGrant());
                continue;
            }
            
            if (lexer.token() == Token.REVOKE) {
                statementList.add(this.parseRevoke());
                continue;
            }
            
            if (lexer.token() == Token.COMMENT) {
                statementList.add(this.parseComment());
                continue;
            }
            if (lexer.token() == Token.FOR) {
                statementList.add(this.parseFor());
                continue;
            }
            if (lexer.token() == Token.LOOP) {
                statementList.add(this.parseLoop());
                continue;
            }
            if (lexer.token() == Token.IF) {
                statementList.add(this.parseIf());
                continue;
            }

            if (lexer.token() == Token.GOTO) {
                lexer.nextToken();
                SQLName label = this.exprParser.name();
                OracleGotoStatement stmt = new OracleGotoStatement(label);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.COMMIT) {
                lexer.nextToken();

                if (identifierEquals("WORK")) {
                    lexer.nextToken();
                }
                OracleCommitStatement stmt = new OracleCommitStatement();

                if (identifierEquals("WRITE")) {
                    stmt.setWrite(true);
                    lexer.nextToken();

                    for (;;) {
                        if (lexer.token() == Token.WAIT) {
                            lexer.nextToken();
                            stmt.setWait(Boolean.TRUE);
                            continue;
                        } else if (lexer.token() == Token.NOWAIT) {
                            lexer.nextToken();
                            stmt.setWait(Boolean.FALSE);
                            continue;
                        } else if (lexer.token() == Token.IMMEDIATE) {
                            lexer.nextToken();
                            stmt.setImmediate(Boolean.TRUE);
                            continue;
                        } else if (identifierEquals("BATCH")) {
                            lexer.nextToken();
                            stmt.setImmediate(Boolean.FALSE);
                            continue;
                        }

                        break;
                    }
                }

                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.SAVEPOINT) {
                lexer.nextToken();

                OracleSavePointStatement stmt = new OracleSavePointStatement();

                if (lexer.token() == Token.TO) {
                    lexer.nextToken();
                    stmt.setTo(this.exprParser.name());
                }

                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.LTLT) {
                lexer.nextToken();
                SQLName label = this.exprParser.name();
                OracleLabelStatement stmt = new OracleLabelStatement(label);
                accept(Token.GTGT);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.DROP) {
                lexer.nextToken();

                if (lexer.token() == Token.TABLE) {
                    SQLDropTableStatement stmt = parseDropTable(false);
                    statementList.add(stmt);
                    continue;
                }

                boolean isPublic = false;
                if (identifierEquals("PUBLIC")) {
                    lexer.nextToken();
                    isPublic = true;
                }

                if (lexer.token() == Token.DATABASE) {
                    lexer.nextToken();

                    if (identifierEquals("LINK")) {
                        lexer.nextToken();

                        OracleDropDbLinkStatement stmt = new OracleDropDbLinkStatement();
                        if (isPublic) {
                            stmt.setPublic(isPublic);
                        }

                        stmt.setName(this.exprParser.name());

                        statementList.add(stmt);
                        continue;
                    }
                }

                if (lexer.token() == Token.INDEX) {
                    SQLStatement stmt = parseDropIndex();
                    statementList.add(stmt);
                    continue;
                }

                if (lexer.token() == Token.VIEW) {
                    SQLStatement stmt = parseDropView(false);
                    statementList.add(stmt);
                    continue;
                }

                if (lexer.token() == Token.SEQUENCE) {
                    SQLDropSequenceStatement stmt = parseDropSequece(false);
                    statementList.add(stmt);
                    continue;
                }

                if (lexer.token() == Token.TRIGGER) {
                    SQLDropTriggerStatement stmt = parseDropTrigger(false);
                    statementList.add(stmt);
                    continue;
                }

                if (lexer.token() == Token.USER) {
                    SQLDropUserStatement stmt = parseDropUser();
                    statementList.add(stmt);
                    continue;
                }

                throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
            }

            if (lexer.token() == Token.NULL) {
                lexer.nextToken();
                OracleExprStatement stmt = new OracleExprStatement(new SQLNullExpr());
                statementList.add(stmt);
                continue;
            }

            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
        }
    }

    public SQLStatement parseIf() {
        accept(Token.IF);

        OracleIfStatement stmt = new OracleIfStatement();

        stmt.setCondition(this.exprParser.expr());

        accept(Token.THEN);

        this.parseStatementList(stmt.getStatements());

        while (lexer.token() == Token.ELSE) {
            lexer.nextToken();

            if (lexer.token() == Token.IF) {
                lexer.nextToken();

                OracleIfStatement.ElseIf elseIf = new OracleIfStatement.ElseIf();

                elseIf.setCondition(this.exprParser.expr());

                accept(Token.THEN);
                this.parseStatementList(elseIf.getStatements());

                stmt.getElseIfList().add(elseIf);
            } else {
                OracleIfStatement.Else elseItem = new OracleIfStatement.Else();
                this.parseStatementList(elseItem.getStatements());
                stmt.setElseItem(elseItem);
                break;
            }
        }

        accept(Token.END);
        accept(Token.IF);

        return stmt;
    }

    public OracleForStatement parseFor() {
        accept(Token.FOR);

        OracleForStatement stmt = new OracleForStatement();

        stmt.setIndex(this.exprParser.name());
        accept(Token.IN);
        stmt.setRange(this.exprParser.expr());
        accept(Token.LOOP);

        this.parseStatementList(stmt.getStatements());
        accept(Token.END);
        accept(Token.LOOP);
        return stmt;
    }

    public OracleLoopStatement parseLoop() {
        accept(Token.LOOP);

        OracleLoopStatement stmt = new OracleLoopStatement();

        this.parseStatementList(stmt.getStatements());
        accept(Token.END);
        accept(Token.LOOP);
        return stmt;
    }

    public SQLStatement parseSet() {
        accept(Token.SET);
        acceptIdentifier("TRANSACTION");

        OracleSetTransactionStatement stmt = new OracleSetTransactionStatement();

        if (identifierEquals("READ")) {
            lexer.nextToken();
            acceptIdentifier("ONLY");
            stmt.setReadOnly(true);
        }

        acceptIdentifier("NAME");

        stmt.setName(this.exprParser.expr());
        return stmt;
    }

    public OracleStatement parserAlter() {
        accept(Token.ALTER);
        if (lexer.token() == Token.SESSION) {
            lexer.nextToken();

            OracleAlterSessionStatement stmt = new OracleAlterSessionStatement();
            if (lexer.token() == Token.SET) {
                lexer.nextToken();
                parseAssignItems(stmt.getItems(), stmt);
            } else {
                throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
            }
            return stmt;
        } else if (lexer.token() == Token.PROCEDURE) {
            lexer.nextToken();
            OracleAlterProcedureStatement stmt = new OracleAlterProcedureStatement();
            stmt.setName(this.exprParser.name());
            if (identifierEquals("COMPILE")) {
                lexer.nextToken();
                stmt.setCompile(true);
            }

            if (identifierEquals("REUSE")) {
                lexer.nextToken();
                acceptIdentifier("SETTINGS");
                stmt.setReuseSettings(true);
            }

            return stmt;
        } else if (lexer.token() == Token.TABLE) {
            return parseAlterTable();
        } else if (lexer.token() == Token.INDEX) {
            lexer.nextToken();
            OracleAlterIndexStatement stmt = new OracleAlterIndexStatement();
            stmt.setName(this.exprParser.name());

            if (identifierEquals("RENAME")) {
                lexer.nextToken();
                accept(Token.TO);
                stmt.setRenameTo(this.exprParser.name());
            }

            for (;;) {
                if (identifierEquals("rebuild")) {
                    lexer.nextToken();

                    OracleAlterIndexStatement.Rebuild rebuild = new OracleAlterIndexStatement.Rebuild();
                    stmt.setRebuild(rebuild);
                    continue;
                } else if (identifierEquals("MONITORING")) {
                    lexer.nextToken();
                    acceptIdentifier("USAGE");
                    stmt.setMonitoringUsage(Boolean.TRUE);
                    continue;
                } else if (identifierEquals("PARALLEL")) {
                    lexer.nextToken();
                    stmt.setParallel(this.exprParser.expr());
                }
                break;
            }

            return stmt;
        } else if (lexer.token() == Token.TRIGGER) {
            lexer.nextToken();
            OracleAlterTriggerStatement stmt = new OracleAlterTriggerStatement();
            stmt.setName(this.exprParser.name());

            for (;;) {
                if (lexer.token() == Token.ENABLE) {
                    lexer.nextToken();
                    stmt.setEnable(Boolean.TRUE);
                    continue;
                } else if (lexer.token() == Token.DISABLE) {
                    lexer.nextToken();
                    stmt.setEnable(Boolean.FALSE);
                    continue;
                } else if (identifierEquals("COMPILE")) {
                    lexer.nextToken();
                    stmt.setCompile(true);
                    continue;
                }
                break;
            }

            return stmt;
        } else if (identifierEquals("SYNONYM")) {
            lexer.nextToken();
            OracleAlterSynonymStatement stmt = new OracleAlterSynonymStatement();
            stmt.setName(this.exprParser.name());

            for (;;) {
                if (lexer.token() == Token.ENABLE) {
                    lexer.nextToken();
                    stmt.setEnable(Boolean.TRUE);
                    continue;
                } else if (lexer.token() == Token.DISABLE) {
                    lexer.nextToken();
                    stmt.setEnable(Boolean.FALSE);
                    continue;
                } else if (identifierEquals("COMPILE")) {
                    lexer.nextToken();
                    stmt.setCompile(true);
                    continue;
                }
                break;
            }

            return stmt;
        } else if (lexer.token() == Token.VIEW) {
            lexer.nextToken();
            OracleAlterViewStatement stmt = new OracleAlterViewStatement();
            stmt.setName(this.exprParser.name());

            for (;;) {
                if (lexer.token() == Token.ENABLE) {
                    lexer.nextToken();
                    stmt.setEnable(Boolean.TRUE);
                    continue;
                } else if (lexer.token() == Token.DISABLE) {
                    lexer.nextToken();
                    stmt.setEnable(Boolean.FALSE);
                    continue;
                } else if (identifierEquals("COMPILE")) {
                    lexer.nextToken();
                    stmt.setCompile(true);
                    continue;
                }
                break;
            }

            return stmt;
        } else if (lexer.token() == Token.TABLESPACE) {
            lexer.nextToken();

            OracleAlterTablespaceStatement stmt = new OracleAlterTablespaceStatement();
            stmt.setName(this.exprParser.name());

            if (identifierEquals("ADD")) {
                lexer.nextToken();

                if (identifierEquals("DATAFILE")) {
                    lexer.nextToken();

                    OracleAlterTablespaceAddDataFile item = new OracleAlterTablespaceAddDataFile();

                    for (;;) {
                        OracleFileSpecification file = new OracleFileSpecification();

                        for (;;) {
                            SQLExpr fileName = this.exprParser.expr();
                            file.getFileNames().add(fileName);

                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            }

                            break;
                        }

                        if (identifierEquals("SIZE")) {
                            lexer.nextToken();
                            file.setSize(this.exprParser.expr());
                        }

                        if (identifierEquals("AUTOEXTEND")) {
                            lexer.nextToken();
                            if (identifierEquals("OFF")) {
                                lexer.nextToken();
                                file.setAutoExtendOff(true);
                            } else if (identifierEquals("ON")) {
                                lexer.nextToken();
                                file.setAutoExtendOn(this.exprParser.expr());
                            } else {
                                throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                            }
                        }

                        item.getFiles().add(file);

                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }

                        break;
                    }

                    stmt.setItem(item);
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
            } else {
                throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
            }

            return stmt;
        }

        throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
    }

    private OracleStatement parseAlterTable() {
        lexer.nextToken();
        OracleAlterTableStatement stmt = new OracleAlterTableStatement();
        stmt.setName(this.exprParser.name());

        for (;;) {
            if (identifierEquals("ADD")) {
                lexer.nextToken();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();

                    SQLAlterTableAddColumn item = parseAlterTableAddColumn();

                    stmt.getItems().add(item);

                    accept(Token.RPAREN);
                } else if (lexer.token() == Token.CONSTRAINT) {
                    OracleConstraint constraint = ((OracleExprParser) this.exprParser).parseConstaint();
                    OracleAlterTableAddConstaint item = new OracleAlterTableAddConstaint();
                    constraint.setParent(item);
                    item.setParent(stmt);
                    item.setConstraint(constraint);
                    stmt.getItems().add(item);
                } else if (lexer.token() == Token.IDENTIFIER) {
                    SQLAlterTableAddColumn item = parseAlterTableAddColumn();
                    stmt.getItems().add(item);
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }

                continue;
            } else if (identifierEquals("MOVE")) {
                lexer.nextToken();

                if (lexer.token() == Token.TABLESPACE) {
                    lexer.nextToken();

                    OracleAlterTableMoveTablespace item = new OracleAlterTableMoveTablespace();
                    item.setName(this.exprParser.name());

                    stmt.getItems().add(item);
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
            } else if (identifierEquals("RENAME")) {
                stmt.getItems().add(parseAlterTableRename());
            } else if (identifierEquals("MODIFY")) {
                lexer.nextToken();

                OracleAlterTableModify item = new OracleAlterTableModify();
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();

                    for (;;) {
                        SQLColumnDefinition columnDef = this.exprParser.parseColumn();
                        item.getColumns().add(columnDef);
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);

                } else {
                    SQLColumnDefinition columnDef = this.exprParser.parseColumn();
                    item.getColumns().add(columnDef);
                }

                stmt.getItems().add(item);
                continue;
            } else if (identifierEquals("SPLIT")) {
                parseAlterTableSplit(stmt);
                continue;
            } else if (lexer.token() == Token.TRUNCATE) {
                lexer.nextToken();
                if (identifierEquals("PARTITION")) {
                    lexer.nextToken();
                    OracleAlterTableTruncatePartition item = new OracleAlterTableTruncatePartition();
                    item.setName(this.exprParser.name());
                    stmt.getItems().add(item);
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
                continue;
            } else if (lexer.token() == Token.DROP) {
                parseAlterDrop(stmt);
                continue;
            } else if (lexer.token() == Token.DISABLE) {
                lexer.nextToken();
                if (lexer.token() == Token.CONSTRAINT) {
                    lexer.nextToken();
                    SQLAlterTableEnableConstraint item = new SQLAlterTableEnableConstraint();
                    item.setConstraintName(this.exprParser.name());
                    stmt.getItems().add(item);
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
            } else if (lexer.token() == Token.ENABLE) {
                lexer.nextToken();
                if (lexer.token() == Token.CONSTRAINT) {
                    lexer.nextToken();
                    SQLAlterTableDisableConstraint item = new SQLAlterTableDisableConstraint();
                    item.setConstraintName(this.exprParser.name());
                    stmt.getItems().add(item);
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
            }

            break;
        }

        if (lexer.token() == Token.UPDATE) {
            lexer.nextToken();

            if (identifierEquals("GLOBAL")) {
                lexer.nextToken();
                acceptIdentifier("INDEXES");
                stmt.setUpdateGlobalIndexes(true);
            } else {
                throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
            }
        }

        return stmt;
    }

    public void parseAlterDrop(SQLAlterTableStatement stmt) {
        lexer.nextToken();
        if (lexer.token() == Token.CONSTRAINT) {
            lexer.nextToken();
            SQLAlterTableDropConstraint item = new SQLAlterTableDropConstraint();
            item.setConstraintName(this.exprParser.name());
            stmt.getItems().add(item);
        } else if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());
            stmt.getItems().add(item);
            accept(Token.RPAREN);
        } else if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());
            stmt.getItems().add(item);
        } else if (identifierEquals("PARTITION")) {
            lexer.nextToken();
            OracleAlterTableDropPartition item = new OracleAlterTableDropPartition();
            item.setName(this.exprParser.name());
            stmt.getItems().add(item);
        } else {
            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
        }
    }

    private void parseAlterTableSplit(OracleAlterTableStatement stmt) {
        lexer.nextToken();
        if (identifierEquals("PARTITION")) {
            lexer.nextToken();
            OracleAlterTableSplitPartition item = new OracleAlterTableSplitPartition();
            item.setName(this.exprParser.name());

            if (identifierEquals("AT")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                this.exprParser.exprList(item.getAt(), item);
                accept(Token.RPAREN);
            } else {
                throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
            }

            if (lexer.token() == Token.INTO) {
                lexer.nextToken();
                accept(Token.LPAREN);

                for (;;) {
                    NestedTablePartitionSpec spec = new NestedTablePartitionSpec();
                    acceptIdentifier("PARTITION");
                    spec.setPartition(this.exprParser.name());

                    for (;;) {
                        if (lexer.token() == Token.TABLESPACE) {
                            lexer.nextToken();
                            SQLName tablespace = this.exprParser.name();
                            spec.getSegmentAttributeItems().add(new TableSpaceItem(tablespace));
                            continue;
                        } else if (identifierEquals("PCTREE")) {
                            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                        } else if (identifierEquals("PCTUSED")) {
                            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                        } else if (identifierEquals("INITRANS")) {
                            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());

                        } else if (identifierEquals("STORAGE")) {
                            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());

                        } else if (identifierEquals("LOGGING")) {
                            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                        } else if (identifierEquals("NOLOGGING")) {
                            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                        } else if (identifierEquals("FILESYSTEM_LIKE_LOGGING")) {
                            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());

                        }

                        break;
                    }

                    item.getInto().add(spec);

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }

            if (lexer.token() == Token.UPDATE) {
                lexer.nextToken();
                acceptIdentifier("INDEXES");
                UpdateIndexesClause updateIndexes = new UpdateIndexesClause();
                item.setUpdateIndexes(updateIndexes);
            }
            stmt.getItems().add(item);
        } else {
            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
        }
    }

    public OracleLockTableStatement parseLock() {
        accept(Token.LOCK);
        accept(Token.TABLE);

        OracleLockTableStatement stmt = new OracleLockTableStatement();
        stmt.setTable(this.exprParser.name());

        accept(Token.IN);
        if (lexer.token() == Token.SHARE) {
            stmt.setLockMode(LockMode.SHARE);
            lexer.nextToken();
        } else if (lexer.token() == Token.EXCLUSIVE) {
            stmt.setLockMode(LockMode.EXCLUSIVE);
            lexer.nextToken();
        }
        accept(Token.MODE);

        if (lexer.token() == Token.NOWAIT) {
            lexer.nextToken();
        } else if (lexer.token() == Token.WAIT) {
            lexer.nextToken();
            stmt.setWait(exprParser.expr());
        }
        return stmt;
    }

    public OracleBlockStatement parseBlock() {
        OracleBlockStatement block = new OracleBlockStatement();

        if (lexer.token() == Token.DECLARE) {
            lexer.nextToken();

            parserParameters(block.getParameters());
            for (OracleParameter param : block.getParameters()) {
                param.setParent(block);
            }
        }

        accept(Token.BEGIN);

        parseStatementList(block.getStatementList());

        accept(Token.END);

        return block;
    }

    private void parserParameters(List<OracleParameter> parameters) {
        for (;;) {
            OracleParameter parameter = new OracleParameter();

            if (lexer.token() == Token.CURSOR) {
                lexer.nextToken();

                parameter.setName(this.exprParser.name());

                accept(Token.IS);
                SQLSelect select = this.createSQLSelectParser().select();

                SQLDataTypeImpl dataType = new SQLDataTypeImpl();
                dataType.setName("CURSOR");
                parameter.setDataType(dataType);

                parameter.setDefaultValue(new SQLQueryExpr(select));
            } else {
                parameter.setName(this.exprParser.name());
                parameter.setDataType(this.exprParser.parseDataType());

                if (lexer.token() == Token.COLONEQ) {
                    lexer.nextToken();
                    parameter.setDefaultValue(this.exprParser.expr());
                }
            }

            parameters.add(parameter);
            if (lexer.token() == Token.COMMA || lexer.token() == Token.SEMI) {
                lexer.nextToken();
            }

            if (lexer.token() != Token.BEGIN && lexer.token() != Token.RPAREN) {
                continue;
            }

            break;
        }
    }

    public OracleSelectParser createSQLSelectParser() {
        return new OracleSelectParser(this.exprParser);
    }

    public OracleMergeStatement parseMerge() {
        accept(Token.MERGE);

        OracleMergeStatement stmt = new OracleMergeStatement();

        parseHints(stmt.getHints());

        accept(Token.INTO);
        stmt.setInto(exprParser.name());

        stmt.setAlias(as());

        accept(Token.USING);

        SQLTableSource using = this.createSQLSelectParser().parseTableSource();
        stmt.setUsing(using);

        accept(Token.ON);
        stmt.setOn(exprParser.expr());

        boolean insertFlag = false;
        if (lexer.token() == Token.WHEN) {
            lexer.nextToken();
            if (lexer.token() == Token.MATCHED) {
                OracleMergeStatement.MergeUpdateClause updateClause = new OracleMergeStatement.MergeUpdateClause();
                lexer.nextToken();
                accept(Token.THEN);
                accept(Token.UPDATE);
                accept(Token.SET);

                for (;;) {
                    SQLUpdateSetItem item = this.exprParser.parseUpdateSetItem();

                    updateClause.getItems().add(item);
                    item.setParent(updateClause);

                    if (lexer.token() == (Token.COMMA)) {
                        lexer.nextToken();
                        continue;
                    }

                    break;
                }

                if (lexer.token() == Token.WHERE) {
                    lexer.nextToken();
                    updateClause.setWhere(exprParser.expr());
                }

                if (lexer.token() == Token.DELETE) {
                    lexer.nextToken();
                    accept(Token.WHERE);
                    updateClause.setWhere(exprParser.expr());
                }

                stmt.setUpdateClause(updateClause);
            } else if (lexer.token() == Token.NOT) {
                lexer.nextToken();
                insertFlag = true;
            }
        }

        if (!insertFlag) {
            if (lexer.token() == Token.WHEN) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.NOT) {
                lexer.nextToken();
                insertFlag = true;
            }
        }

        if (insertFlag) {
            OracleMergeStatement.MergeInsertClause insertClause = new OracleMergeStatement.MergeInsertClause();

            accept(Token.MATCHED);
            accept(Token.THEN);
            accept(Token.INSERT);

            if (lexer.token() == Token.LPAREN) {
                accept(Token.LPAREN);
                exprParser.exprList(insertClause.getColumns(), insertClause);
                accept(Token.RPAREN);
            }
            accept(Token.VALUES);
            accept(Token.LPAREN);
            exprParser.exprList(insertClause.getValues(), insertClause);
            accept(Token.RPAREN);

            if (lexer.token() == Token.WHERE) {
                lexer.nextToken();
                insertClause.setWhere(exprParser.expr());
            }

            stmt.setInsertClause(insertClause);
        }

        OracleErrorLoggingClause errorClause = parseErrorLoggingClause();
        stmt.setErrorLoggingClause(errorClause);

        return stmt;
    }

    public OracleStatement parseInsert() {
        if (lexer.token() == Token.LPAREN) {
            OracleInsertStatement stmt = new OracleInsertStatement();
            parseInsert0(stmt, false);

            stmt.setReturning(parseReturningClause());
            stmt.setErrorLogging(parseErrorLoggingClause());

            return stmt;
        }

        accept(Token.INSERT);

        List<SQLHint> hints = new ArrayList<SQLHint>();

        parseHints(hints);

        if (lexer.token() == Token.INTO) {
            OracleInsertStatement stmt = new OracleInsertStatement();
            stmt.setHints(hints);

            parseInsert0(stmt);

            stmt.setReturning(parseReturningClause());
            stmt.setErrorLogging(parseErrorLoggingClause());

            return stmt;
        }

        OracleMultiInsertStatement stmt = parseMultiInsert();
        stmt.setHints(hints);
        return stmt;
    }

    public OracleMultiInsertStatement parseMultiInsert() {
        OracleMultiInsertStatement stmt = new OracleMultiInsertStatement();

        if (lexer.token() == Token.ALL) {
            lexer.nextToken();
            stmt.setOption(OracleMultiInsertStatement.Option.ALL);
        } else if (lexer.token() == Token.FIRST) {
            lexer.nextToken();
            stmt.setOption(OracleMultiInsertStatement.Option.FIRST);
        }

        while (lexer.token() == Token.INTO) {
            OracleMultiInsertStatement.InsertIntoClause clause = new OracleMultiInsertStatement.InsertIntoClause();

            parseInsert0(clause);

            clause.setReturning(parseReturningClause());
            clause.setErrorLogging(parseErrorLoggingClause());

            stmt.getEntries().add(clause);
        }

        if (lexer.token() == Token.WHEN) {
            OracleMultiInsertStatement.ConditionalInsertClause clause = new OracleMultiInsertStatement.ConditionalInsertClause();

            while (lexer.token() == Token.WHEN) {
                lexer.nextToken();

                OracleMultiInsertStatement.ConditionalInsertClauseItem item = new OracleMultiInsertStatement.ConditionalInsertClauseItem();

                item.setWhen(this.exprParser.expr());
                accept(Token.THEN);
                OracleMultiInsertStatement.InsertIntoClause insertInto = new OracleMultiInsertStatement.InsertIntoClause();
                parseInsert0(insertInto);
                item.setThen(insertInto);

                clause.getItems().add(item);
            }

            if (lexer.token() == Token.ELSE) {
                lexer.nextToken();

                OracleMultiInsertStatement.InsertIntoClause insertInto = new OracleMultiInsertStatement.InsertIntoClause();
                parseInsert0(insertInto, false);
                clause.setElseItem(insertInto);
            }
            stmt.getEntries().add(clause);
        }

        SQLSelect subQuery = this.createSQLSelectParser().select();
        stmt.setSubQuery(subQuery);

        return stmt;
    }

    private OracleExceptionStatement parseException() {
        accept(Token.EXCEPTION);
        OracleExceptionStatement stmt = new OracleExceptionStatement();

        for (;;) {
            accept(Token.WHEN);
            OracleExceptionStatement.Item item = new OracleExceptionStatement.Item();
            item.setWhen(this.exprParser.expr());
            accept(Token.THEN);
            parseStatementList(item.getStatements());

            stmt.getItems().add(item);

            if (lexer.token() != Token.WHEN) {
                break;
            }
        }
        return stmt;
    }

    private OracleErrorLoggingClause parseErrorLoggingClause() {
        if (identifierEquals("LOG")) {
            OracleErrorLoggingClause errorClause = new OracleErrorLoggingClause();

            lexer.nextToken();
            accept(Token.ERRORS);
            if (lexer.token() == Token.INTO) {
                lexer.nextToken();
                errorClause.setInto(exprParser.name());
            }

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                errorClause.setSimpleExpression(exprParser.expr());
                accept(Token.RPAREN);
            }

            if (lexer.token() == Token.REJECT) {
                lexer.nextToken();
                accept(Token.LIMIT);
                errorClause.setLimit(exprParser.expr());
            }

            return errorClause;
        }
        return null;
    }

    public OracleReturningClause parseReturningClause() {
        OracleReturningClause clause = null;

        if (lexer.token() == Token.RETURNING) {
            lexer.nextToken();
            clause = new OracleReturningClause();

            for (;;) {
                SQLExpr item = exprParser.expr();
                clause.getItems().add(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.INTO);
            for (;;) {
                SQLExpr item = exprParser.expr();
                clause.getValues().add(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
        }
        return clause;
    }

    public OracleExplainStatement parseExplain() {
        accept(Token.EXPLAIN);
        acceptIdentifier("PLAN");
        OracleExplainStatement stmt = new OracleExplainStatement();

        if (lexer.token() == Token.SET) {
            lexer.nextToken();
            acceptIdentifier("STATEMENT_ID");
            accept(Token.EQ);
            stmt.setStatementId((SQLCharExpr) this.exprParser.primary());
        }

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();
            stmt.setInto(this.exprParser.name());
        }

        accept(Token.FOR);
        stmt.setStatement(parseStatement());

        return stmt;
    }

    public OracleDeleteStatement parseDeleteStatement() {
        OracleDeleteStatement deleteStatement = new OracleDeleteStatement();

        if (lexer.token() == Token.DELETE) {
            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            parseHints(deleteStatement.getHints());

            if (lexer.token() == (Token.FROM)) {
                lexer.nextToken();
            }

            if (identifierEquals("ONLY")) {
                lexer.nextToken();
                accept(Token.LPAREN);

                SQLName tableName = exprParser.name();
                deleteStatement.setTableName(tableName);

                accept(Token.RPAREN);
            } else {
                SQLName tableName = exprParser.name();
                deleteStatement.setTableName(tableName);
            }

            deleteStatement.setAlias(as());
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            deleteStatement.setWhere(this.exprParser.expr());
        }

        if (lexer.token() == Token.RETURNING) {
            OracleReturningClause clause = this.parseReturningClause();
            deleteStatement.setReturning(clause);
        }
        if (identifierEquals("RETURN") || identifierEquals("RETURNING")) {
            throw new ParserException("TODO");
        }

        if (identifierEquals("LOG")) {
            throw new ParserException("TODO");
        }

        return deleteStatement;
    }

    public SQLStatement parseCreateDbLink() {
        accept(Token.CREATE);

        OracleCreateDatabaseDbLinkStatement dbLink = new OracleCreateDatabaseDbLinkStatement();

        if (identifierEquals("SHARED")) {
            dbLink.setShared(true);
            lexer.nextToken();
        }

        if (identifierEquals("PUBLIC")) {
            dbLink.setPublic(true);
            lexer.nextToken();
        }

        accept(Token.DATABASE);
        acceptIdentifier("LINK");

        dbLink.setName(this.exprParser.name());

        if (lexer.token() == Token.CONNECT) {
            lexer.nextToken();
            accept(Token.TO);

            dbLink.setUser(this.exprParser.name());

            if (lexer.token() == Token.IDENTIFIED) {
                lexer.nextToken();
                accept(Token.BY);
                dbLink.setPassword(lexer.stringVal());
                accept(Token.IDENTIFIER);
            }
        }

        if (identifierEquals("AUTHENTICATED")) {
            lexer.nextToken();
            accept(Token.BY);
            dbLink.setAuthenticatedUser(this.exprParser.name());

            accept(Token.IDENTIFIED);
            accept(Token.BY);
            dbLink.setPassword(lexer.stringVal());
            accept(Token.IDENTIFIER);
        }

        if (lexer.token() == Token.USING) {
            lexer.nextToken();
            dbLink.setUsing(this.exprParser.expr());
        }

        return dbLink;
    }

    public OracleCreateIndexStatement parseCreateIndex(boolean acceptCreate) {
        if (acceptCreate) {
            accept(Token.CREATE);
        }

        OracleCreateIndexStatement stmt = new OracleCreateIndexStatement();
        if (lexer.token() == Token.UNIQUE) {
            stmt.setType("UNIQUE");
            lexer.nextToken();
        } else if (identifierEquals("BITMAP")) {
            stmt.setType("BITMAP");
            lexer.nextToken();
        }

        accept(Token.INDEX);

        stmt.setName(this.exprParser.name());

        accept(Token.ON);

        stmt.setTable(this.exprParser.name());

        accept(Token.LPAREN);

        for (;;) {
            SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
            stmt.getItems().add(item);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);

        for (;;) {
            if (lexer.token() == Token.TABLESPACE) {
                lexer.nextToken();
                stmt.setTablespace(this.exprParser.name());
                continue;
            } else if (lexer.token() == Token.PCTFREE) {
                lexer.nextToken();
                stmt.setPtcfree(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.INITRANS) {
                lexer.nextToken();
                stmt.setInitrans(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.MAXTRANS) {
                lexer.nextToken();
                stmt.setMaxtrans(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.COMPUTE) {
                lexer.nextToken();
                acceptIdentifier("STATISTICS");
                stmt.setComputeStatistics(true);
                continue;
            } else if (lexer.token() == Token.ENABLE) {
                lexer.nextToken();
                stmt.setEnable(true);
                continue;
            } else if (lexer.token() == Token.DISABLE) {
                lexer.nextToken();
                stmt.setEnable(false);
                continue;
            } else if (identifierEquals("ONLINE")) {
                lexer.nextToken();
                stmt.setOnline(true);
                continue;
            } else if (identifierEquals("NOPARALLEL")) {
                lexer.nextToken();
                stmt.setNoParallel(true);
                continue;
            } else if (identifierEquals("PARALLEL")) {
                lexer.nextToken();
                stmt.setParallel(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.INDEX) {
                lexer.nextToken();
                acceptIdentifier("ONLY");
                acceptIdentifier("TOPLEVEL");
                stmt.setIndexOnlyTopLevel(true);
                continue;
            } else {
                break;
            }
        }
        return stmt;
    }

    public OracleCreateSequenceStatement parseCreateSequence(boolean acceptCreate) {
        if (acceptCreate) {
            accept(Token.CREATE);
        }

        accept(Token.SEQUENCE);

        OracleCreateSequenceStatement stmt = new OracleCreateSequenceStatement();
        stmt.setName(this.exprParser.name());

        for (;;) {
            if (lexer.token() == Token.START) {
                lexer.nextToken();
                accept(Token.WITH);
                stmt.setStartWith(this.exprParser.expr());
                continue;
            } else if (identifierEquals("INCREMENT")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setIncrementBy(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.CACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.TRUE);
                continue;
            } else if (lexer.token() == Token.NOCACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.FALSE);
                continue;
            } else if (identifierEquals("CYCLE")) {
                lexer.nextToken();
                stmt.setCycle(Boolean.TRUE);
                continue;
            } else if (identifierEquals("NOCYCLE")) {
                lexer.nextToken();
                stmt.setCycle(Boolean.FALSE);
                continue;
            } else if (identifierEquals("MINVALUE")) {
                lexer.nextToken();
                stmt.setMinValue(this.exprParser.expr());
                continue;
            } else if (identifierEquals("MAXVALUE")) {
                lexer.nextToken();
                stmt.setMaxValue(this.exprParser.expr());
                continue;
            } else if (identifierEquals("NOMAXVALUE")) {
                lexer.nextToken();
                stmt.setNoMaxValue(true);
                continue;
            } else if (identifierEquals("NOMINVALUE")) {
                lexer.nextToken();
                stmt.setNoMinValue(true);
                continue;
            }
            break;
        }

        return stmt;
    }

    public OracleCreateProcedureStatement parseCreateProcedure() {
        OracleCreateProcedureStatement stmt = new OracleCreateProcedureStatement();
        accept(Token.CREATE);
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            stmt.setOrReplace(true);
        }

        accept(Token.PROCEDURE);

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            parserParameters(stmt.getParameters());
            accept(Token.RPAREN);
        }

        accept(Token.AS);

        OracleBlockStatement block = this.parseBlock();

        stmt.setBlock(block);

        return stmt;
    }

    public SQLUpdateStatement parseUpdateStatement() {
        return new OracleUpdateParser(this.lexer).parseUpdateStatement();
    }
}
