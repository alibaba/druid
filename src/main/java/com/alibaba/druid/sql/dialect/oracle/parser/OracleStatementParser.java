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
package com.alibaba.druid.sql.dialect.oracle.parser;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.oracle.ast.OracleHint;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleErrorLoggingClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterProcedureStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterSessionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableDropPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.NestedTablePartitionSpec;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.TableSpaceItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.UpdateIndexesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableTruncatePartition;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleBlockStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleDeleteStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExceptionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleExplainStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleGrantStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLockTableStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLockTableStatement.LockMode;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMethodInvokeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMultiInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSetTransactionStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleStatementParser extends SQLStatementParser {

    public OracleStatementParser(String sql){
        super(new OracleLexer(sql));
        this.lexer.nextToken();
        this.exprParser = createExprParser();
    }

    public OracleStatementParser(Lexer lexer){
        super(lexer);
        this.exprParser = createExprParser();
    }

    protected OracleExprParser createExprParser() {
        return new OracleExprParser(lexer);
    }

    protected void parseInsert0_hinits(SQLInsertInto insertStatement) {
        if (insertStatement instanceof OracleInsertStatement) {
            OracleInsertStatement stmt = (OracleInsertStatement) insertStatement;
            this.createExprParser().parseHints(stmt.getHints());
        } else {
            List<OracleHint> hints = new ArrayList<OracleHint>();
            this.createExprParser().parseHints(hints);
        }
    }

    @Override
    public void parseStatementList(List<SQLStatement> statementList) throws ParserException {
        parseStatementList(statementList, -1);
    }

    public void parseStatementList(List<SQLStatement> statementList, int max) throws ParserException {
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

            if (lexer.token() == (Token.SEMI)) {
                lexer.nextToken();
                continue;
            }

            if (lexer.token() == (Token.SELECT)) {
                statementList.add(new SQLSelectStatement(new OracleSelectParser(this.lexer).select()));
                continue;
            }

            if (lexer.token() == (Token.UPDATE)) {
                statementList.add(new OracleUpdateParser(this.lexer).parseUpdate());
                continue;
            }

            if (lexer.token() == (Token.CREATE)) {
                lexer.nextToken();

                throw new ParserException("TODO");
            }

            if (lexer.token() == Token.INSERT) {
                statementList.add(parseInsert());
                continue;
            }

            if (lexer.token() == (Token.DELETE)) {
                statementList.add(parseDelete());
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
                statementList.add(new SQLSelectStatement(new OracleSelectParser(this.lexer).select()));
                continue;
            }

            if (identifierEquals("CALL")) {
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
                        SQLSetStatement stmt = new SQLSetStatement(binaryOpExpr.getLeft(), binaryOpExpr.getRight());
                        statementList.add(stmt);
                        continue;
                    }
                }
                accept(Token.COLONEQ);
                SQLExpr value = this.exprParser.expr();

                SQLSetStatement stmt = new SQLSetStatement(variant, value);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.EXCEPTION) {
                statementList.add(this.parseException());
                continue;
            }

            if (lexer.token() == Token.IDENTIFIER) {
                if (identifierEquals("EXPLAIN")) {
                    statementList.add(this.parseExplain());
                    continue;
                }

                SQLExpr expr = exprParser.expr();
                if (expr instanceof SQLMethodInvokeExpr) {
                    OracleMethodInvokeStatement stmt = new OracleMethodInvokeStatement((SQLMethodInvokeExpr) expr);
                    statementList.add(stmt);
                    continue;
                } else if (expr instanceof SQLName) {
                    SQLMethodInvokeExpr methodInvokeExpr = new SQLMethodInvokeExpr(((SQLName) expr).toString());
                    statementList.add(new OracleMethodInvokeStatement(methodInvokeExpr));
                    continue;
                } else {
                    throw new ParserException("expr : " + expr);
                }
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
            if (lexer.token() == Token.COMMENT) {
                statementList.add(this.parseComment());
                continue;
            }

            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
        }
    }

    public SQLStatement parseGrant() {
        accept(Token.GRANT);
        OracleGrantStatement stmt = new OracleGrantStatement();

        for (;;) {
            if (lexer.token() == Token.ALL) {
                lexer.nextToken();
                if (identifierEquals("PRIVILEGES")) {
                    lexer.nextToken();
                }
                stmt.getPrivileges().add("ALL");
            } else if (lexer.token() == Token.SELECT) {
                stmt.getPrivileges().add("SELECT");
                lexer.nextToken();
            } else if (lexer.token() == Token.UPDATE) {
                stmt.getPrivileges().add("UPDATE");
                lexer.nextToken();
            } else if (lexer.token() == Token.DELETE) {
                stmt.getPrivileges().add("DELETE");
                lexer.nextToken();
            } else if (lexer.token() == Token.INSERT) {
                stmt.getPrivileges().add("INSERT");
                lexer.nextToken();
            } else if (lexer.token() == Token.CREATE) {
                lexer.nextToken();

                if (lexer.token() == Token.TABLE) {
                    stmt.getPrivileges().add("CREATE TABLE");
                    lexer.nextToken();
                } else if (identifierEquals("SYNONYM")) {
                    stmt.getPrivileges().add("CREATE SYNONYM");
                    lexer.nextToken();
                } else {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                }
            }

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }

        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            stmt.setOn(this.exprParser.expr());
        }

        return stmt;
    }

    public SQLStatement parseSet() {
        accept(Token.SET);
        acceptIdentifier("TRANSACTION");
        acceptIdentifier("NAME");
        OracleSetTransactionStatement stmt = new OracleSetTransactionStatement();
        stmt.setName(this.exprParser.expr());
        return stmt;
    }

    public SQLStatement parseTruncate() {
        return super.parseTruncate();
    }

    public OracleStatement parserAlter() {
        accept(Token.ALTER);
        if (lexer.token() == Token.SESSION) {
            lexer.nextToken();

            OracleAlterSessionStatement stmt = new OracleAlterSessionStatement();
            if (lexer.token() == Token.SET) {
                lexer.nextToken();
                parseAssignItems(stmt.getItems());
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
            lexer.nextToken();
            OracleAlterTableStatement stmt = new OracleAlterTableStatement();
            stmt.setName(this.exprParser.name());

            for (;;) {
                if (identifierEquals("ADD")) {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("MOVE")) {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("RENAME")) {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("MODIFY")) {
                    throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                } else if (identifierEquals("SPLIT")) {
                    lexer.nextToken();
                    if (identifierEquals("PARTITION")) {
                        lexer.nextToken();
                        OracleAlterTableSplitPartition item = new OracleAlterTableSplitPartition();
                        item.setName(this.exprParser.name());

                        if (lexer.token() == Token.AT) {
                            lexer.nextToken();
                            accept(Token.LPAREN);
                            this.exprParser.exprList(item.getAt());
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
                                    if (identifierEquals("TABLESPACE")) {
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
                    lexer.nextToken();
                    if (identifierEquals("PARTITION")) {
                        lexer.nextToken();
                        OracleAlterTableDropPartition item = new OracleAlterTableDropPartition();
                        item.setName(this.exprParser.name());
                        stmt.getItems().add(item);
                    } else {
                        throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
                    }
                    continue;
                }

                break;
            }

            return stmt;
        }

        throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
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

    public OracleStatement parseBlock() {
        if (lexer.token() == Token.DECLARE) {
            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
        }

        accept(Token.BEGIN);
        OracleBlockStatement block = new OracleBlockStatement();

        parseStatementList(block.getStatementList());

        accept(Token.END);

        return block;
    }

    public OracleMergeStatement parseMerge() throws ParserException {
        accept(Token.MERGE);

        OracleMergeStatement stmt = new OracleMergeStatement();

        OracleExprParser exprParser = this.createExprParser();
        exprParser.parseHints(stmt.getHints());

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
                    SQLUpdateSetItem item = new SQLUpdateSetItem();
                    item.setColumn(this.exprParser.name());
                    accept(Token.EQ);
                    item.setValue(this.exprParser.expr());

                    updateClause.getItems().add(item);

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
                exprParser.exprList(insertClause.getColumns());
                accept(Token.RPAREN);
            }
            accept(Token.VALUES);
            accept(Token.LPAREN);
            exprParser.exprList(insertClause.getValues());
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
        accept(Token.INSERT);

        List<OracleHint> hints = new ArrayList<OracleHint>();

        this.createExprParser().parseHints(hints);

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

                item.setWhen(this.createExprParser().expr());
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
            List<SQLStatement> statements = new ArrayList<SQLStatement>();
            parseStatementList(statements, 1);
            item.setStatement(statements.get(0));

            stmt.getItems().add(item);

            if (lexer.token() != Token.WHEN) {
                break;
            }
        }
        return stmt;
    }

    private OracleErrorLoggingClause parseErrorLoggingClause() {
        if (lexer.token() == Token.LOG) {
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

    public SQLStatement parseStatement() {
        List<SQLStatement> list = new ArrayList<SQLStatement>();

        this.parseStatementList(list, 1);

        return list.get(0);
    }

    public OracleExplainStatement parseExplain() {
        acceptIdentifier("EXPLAIN");
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
        stmt.setForStatement(parseStatement());

        return stmt;
    }

    public OracleDeleteStatement parseDelete() throws ParserException {
        accept(Token.DELETE);

        OracleDeleteStatement deleteStatement = new OracleDeleteStatement();

        this.createExprParser().parseHints(deleteStatement.getHints());

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
}
