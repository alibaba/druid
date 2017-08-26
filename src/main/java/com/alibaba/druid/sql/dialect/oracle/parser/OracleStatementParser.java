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

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.ast.statement.SQLWhileStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.*;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.NestedTablePartitionSpec;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.TableSpaceItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleAlterTableSplitPartition.UpdateIndexesClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleLockTableStatement.LockMode;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.JdbcConstants;

public class OracleStatementParser extends SQLStatementParser {

    public OracleStatementParser(String sql){
        super(new OracleExprParser(sql));
    }

    public OracleStatementParser(String sql, SQLParserFeature... features){
        super(new OracleExprParser(sql, features));
    }

    public OracleStatementParser(Lexer lexer){
        super(new OracleExprParser(lexer));
    }

    @Override
    public OracleExprParser getExprParser() {
        return (OracleExprParser) exprParser;
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

    public void parseStatementList(List<SQLStatement> statementList, int max, SQLObject parent) {
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
                if(statementList.size() > 0) {
                    SQLStatement lastStmt = statementList.get(statementList.size() - 1);
                    lastStmt.setAfterSemi(true);
                }
                continue;
            }

            if (lexer.token() == (Token.SELECT)) {
                SQLStatement stmt = new SQLSelectStatement(new OracleSelectParser(this.exprParser).select(), JdbcConstants.ORACLE);
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == (Token.UPDATE)) {
                SQLStatement stmt = parseUpdateStatement();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == (Token.CREATE)) {
                SQLStatement stmt = parseCreate();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.INSERT) {
                SQLStatement stmt = parseInsert();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == (Token.DELETE)) {
                SQLStatement stmt = parseDeleteStatement();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == (Token.SLASH)) {
                lexer.nextToken();

                SQLStatement stmt = new OraclePLSQLCommitStatement();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.ALTER) {
                SQLStatement stmt = parserAlter();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.WITH) {
                SQLSelectStatement stmt = new SQLSelectStatement(this.createSQLSelectParser().select(), dbType);
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.LBRACE || lexer.identifierEquals("CALL")) {
                SQLStatement stmt = parseCall();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.MERGE) {
                SQLStatement stmt = parseMerge();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.BEGIN
                    || lexer.token() == Token.DECLARE) {
                SQLStatement stmt = parseBlock();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.LOCK) {
                SQLStatement stmt = parseLock();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.TRUNCATE) {
                SQLStatement stmt = parseTruncate();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.VARIANT) {
                SQLExpr variant = this.exprParser.primary();
                if (variant instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) variant;
                    if (binaryOpExpr.getOperator() == SQLBinaryOperator.Assignment) {
                        SQLSetStatement stmt = new SQLSetStatement(binaryOpExpr.getLeft(), binaryOpExpr.getRight(), getDbType());
                        stmt.setParent(parent);
                        statementList.add(stmt);
                        continue;
                    }
                }
                accept(Token.COLONEQ);
                SQLExpr value = this.exprParser.expr();

                SQLSetStatement stmt = new SQLSetStatement(variant, value, getDbType());
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.EXCEPTION) {
                OracleExceptionStatement stmt = this.parseException();
                stmt.setParent(parent);
                if (parent instanceof SQLBlockStatement) {
                    ((SQLBlockStatement) parent).setException(stmt);
                } else {
                    statementList.add(stmt);
                }
                continue;
            }

            if (lexer.identifierEquals("EXIT")) {
                lexer.nextToken();
                OracleExitStatement stmt = parseExit();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.CONTINUE) {
                lexer.nextToken();
                OracleContinueStatement stmt = new OracleContinueStatement();

                if (lexer.token() == Token.IDENTIFIER) {
                    String label = lexer.stringVal();
                    lexer.nextToken();
                    stmt.setLabel(label);
                }
                if (lexer.token() == Token.WHEN) {
                    lexer.nextToken();
                    stmt.setWhen(this.exprParser.expr());
                }
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.FETCH || lexer.identifierEquals("FETCH")) {
                SQLStatement stmt = parseFetch();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.identifierEquals("ROLLBACK")) {
                SQLRollbackStatement stmt = parseRollback();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.EXPLAIN) {
                OracleExplainStatement stmt = this.parseExplain();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.IDENTIFIER) {
                String strVal = lexer.stringVal();
                if (strVal.equalsIgnoreCase("RAISE")) {
                    SQLStatement stmt = this.parseRaise();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }

                if (strVal.equalsIgnoreCase("FORALL")) {
                    SQLStatement stmt = this.parseFor();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }

                if (strVal.equalsIgnoreCase("RENAME")) {
                    SQLStatement stmt = this.parseRename();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }

                if (strVal.equalsIgnoreCase("EXECUTE")) {
                    SQLStatement stmt = this.parseExecute();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }

                SQLExpr expr = exprParser.expr();

                if (expr instanceof SQLBinaryOpExpr) {
                    SQLBinaryOpExpr binaryOpExpr = (SQLBinaryOpExpr) expr;
                    if (binaryOpExpr.getOperator() == SQLBinaryOperator.Assignment) {
                        SQLSetStatement stmt = new SQLSetStatement();
                        stmt.setDbType(JdbcConstants.ORACLE);
                        stmt.setParent(parent);

                        SQLAssignItem assignItem = new SQLAssignItem(binaryOpExpr.getLeft(), binaryOpExpr.getRight());
                        assignItem.setParent(stmt);
                        stmt.getItems().add(assignItem);

                        statementList.add(stmt);

                        continue;
                    }
                }

                OracleExprStatement stmt = new OracleExprStatement(expr);
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.LPAREN) {
                char ch = lexer.current();
                int bp = lexer.bp();
                lexer.nextToken();

                if (lexer.token() == Token.SELECT) {
                    lexer.reset(bp, ch, Token.LPAREN);

                    SQLStatement stmt = parseSelect();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
            }

            if (lexer.token() == Token.SET) {
                SQLStatement stmt = parseSet();
                stmt.setParent(parent);
                statementList.add(stmt);
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
                OracleForStatement forStatement = this.parseFor();
                forStatement.setParent(parent);
                if (lexer.token() == Token.IDENTIFIER) {
                    String strVal = lexer.stringVal();
                    int stmtListSize = statementList.size();
                    if (stmtListSize > 0) {
                        SQLStatement lastStmt = statementList.get(stmtListSize - 1);
                        if (lastStmt instanceof OracleLabelStatement) {
                            if (((OracleLabelStatement) lastStmt).getLabel().getSimpleName().equalsIgnoreCase(strVal)) {
                                SQLName endLabbel = this.exprParser.name();
                                forStatement.setEndLabel(endLabbel);
                            }
                        }
                    }
                }
                statementList.add(forStatement);
                continue;
            }
            if (lexer.token() == Token.LOOP) {
                SQLStatement stmt = parseLoop();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }
            if (lexer.token() == Token.IF) {
                SQLStatement stmt = parseIf();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.GOTO) {
                lexer.nextToken();
                SQLName label = this.exprParser.name();
                OracleGotoStatement stmt = new OracleGotoStatement(label);
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.COMMIT) {
                lexer.nextToken();

                if (lexer.identifierEquals("WORK")) {
                    lexer.nextToken();
                }
                SQLCommitStatement stmt = new SQLCommitStatement();
                stmt.setParent(parent);

                if (lexer.identifierEquals("WRITE")) {
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
                        } else if (lexer.identifierEquals("BATCH")) {
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

                SQLSavePointStatement stmt = new SQLSavePointStatement();
                stmt.setDbType(dbType);
                stmt.setParent(parent);

                if (lexer.token() == Token.TO) {
                    lexer.nextToken();
                    stmt.setName(this.exprParser.name());
                } else if (lexer.token() != Token.SEMI) {
                    stmt.setName(this.exprParser.name());
                }
                accept(Token.SEMI);
                stmt.setAfterSemi(true);

                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.LTLT) {
                lexer.nextToken();
                SQLName label = this.exprParser.name();
                OracleLabelStatement stmt = new OracleLabelStatement(label);
                accept(Token.GTGT);
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.DROP) {
                lexer.nextToken();

                if (lexer.token() == Token.TABLE) {
                    SQLDropTableStatement stmt = parseDropTable(false);
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }

                boolean isPublic = false;
                if (lexer.identifierEquals("PUBLIC")) {
                    lexer.nextToken();
                    isPublic = true;
                }

                if (lexer.token() == Token.DATABASE) {
                    lexer.nextToken();

                    if (lexer.identifierEquals("LINK")) {
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
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }

                if (lexer.token() == Token.VIEW) {
                    SQLStatement stmt = parseDropView(false);
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }

                if (lexer.token() == Token.SEQUENCE) {
                    SQLDropSequenceStatement stmt = parseDropSequece(false);
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }

                if (lexer.token() == Token.TRIGGER) {
                    SQLDropTriggerStatement stmt = parseDropTrigger(false);
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }

                if (lexer.token() == Token.USER) {
                    SQLDropUserStatement stmt = parseDropUser();
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }
                
                if (lexer.token() == Token.PROCEDURE) {
                    SQLDropProcedureStatement stmt = parseDropProcedure(false);
                    stmt.setParent(parent);
                    statementList.add(stmt);
                    continue;
                }

                throw new ParserException("TODO : " + lexer.info());
            }

            if (lexer.token() == Token.NULL) {
                lexer.nextToken();
                OracleExprStatement stmt = new OracleExprStatement(new SQLNullExpr());
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }
            
            if (lexer.token() == Token.OPEN) {
                SQLStatement stmt = this.parseOpen();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.CLOSE) {
                SQLStatement stmt = this.parseClose();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.CASE) {
                SQLStatement stmt = this.parseCase();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.PROCEDURE) {
                SQLStatement stmt = this.parseCreateProcedure();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.ELSIF
                    && parent instanceof SQLIfStatement) {
                break;
            }

            if (lexer.token() == Token.WHEN
                    && parent instanceof OracleExceptionStatement.Item) {
                break;
            }

            if (lexer.token() == Token.FUNCTION) {
                SQLStatement stmt = this.parseFunction();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.WHILE) {
                SQLStatement stmt = this.parseWhile();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            if (lexer.token() == Token.RETURN) {
                SQLStatement stmt = this.parseReturn();
                stmt.setParent(parent);
                statementList.add(stmt);
                continue;
            }

            throw new ParserException("TODO : " + lexer.info());
        }
    }

    public SQLStatement parseExecute() {
        acceptIdentifier("EXECUTE");

        if (lexer.token() == Token.IMMEDIATE) {
            lexer.nextToken();

            OracleExecuteImmediateStatement stmt = new OracleExecuteImmediateStatement();

            SQLExpr dyanmiacSql = this.exprParser.primary();
            stmt.setDynamicSql(dyanmiacSql);

            if (lexer.token() == Token.INTO) {
                lexer.nextToken();

                this.exprParser.exprList(stmt.getInto(), stmt);
            }

            if (lexer.token() == Token.USING) {
                lexer.nextToken();

                for (;;) {
                    SQLArgument arg = new SQLArgument();

                    if (lexer.token() == Token.IN) {
                        lexer.nextToken();
                        if (lexer.token() == Token.OUT) {
                            lexer.nextToken();
                            arg.setType(SQLParameter.ParameterType.INOUT);
                        } else {
                            arg.setType(SQLParameter.ParameterType.IN);
                        }
                    } else if (lexer.token() == Token.OUT) {
                        lexer.nextToken();
                        arg.setType(SQLParameter.ParameterType.OUT);
                    }

                    arg.setExpr(this.exprParser.primary());
                    arg.setParent(stmt);
                    stmt.getArguments().add(arg);

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
            }

            if (lexer.token() == Token.RETURNING) {
                lexer.nextToken();
                accept(Token.INTO);

                this.exprParser.exprList(stmt.getReturnInto(), stmt);
            }

            return stmt;
        }
        throw new ParserException("TODO : " + lexer.info());
    }

    public SQLStatement parseRename() {
        lexer.nextToken();
        SQLName from = this.exprParser.name();
        accept(Token.TO);
        SQLName to = this.exprParser.name();

        SQLAlterTableStatement stmt = new SQLAlterTableStatement();
        stmt.setTableSource(from);
        SQLAlterTableRename toItem = new SQLAlterTableRename(to);
        stmt.addItem(toItem);

        return stmt;
    }

    private OracleExitStatement parseExit() {
        OracleExitStatement stmt = new OracleExitStatement();

        if (lexer.token() == Token.IDENTIFIER) {
            String label = lexer.stringVal();
            stmt.setLabel(label);
            lexer.nextToken();
        }

        if (lexer.token() == Token.WHEN) {
            lexer.nextToken();
            stmt.setWhen(this.exprParser.expr());
        }
        accept(Token.SEMI);
        stmt.setAfterSemi(true);
        return stmt;
    }

    public SQLStatement parseReturn() {
        accept(Token.RETURN);
        SQLReturnStatement stmt = new SQLReturnStatement();
        if (lexer.token() != Token.SEMI) {
            SQLExpr expr = this.exprParser.expr();
            stmt.setExpr(expr);
        }

        accept(Token.SEMI);
        stmt.setAfterSemi(true);

        return stmt;
    }

    public SQLStatement parseWhile() {
        accept(Token.WHILE);

        SQLWhileStatement stmt = new SQLWhileStatement();
        stmt.setDbType(dbType);

        stmt.setCondition(this.exprParser.expr());

        accept(Token.LOOP);

        this.parseStatementList(stmt.getStatements(), -1, stmt);
        accept(Token.END);
        accept(Token.LOOP);
        accept(Token.SEMI);

        return stmt;
    }

    public SQLCreateFunctionStatement parseCreateFunction() {
        accept(Token.CREATE);
        SQLCreateFunctionStatement stmt = (SQLCreateFunctionStatement) parseFunction();
        stmt.setCreate(true);
        return stmt;
    }

    public SQLStatement parseFunction() {
        SQLCreateFunctionStatement stmt = new SQLCreateFunctionStatement();
        stmt.setDbType(dbType);

        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();
            if (lexer.token() == Token.OR) {
                lexer.nextToken();
                accept(Token.REPLACE);
                stmt.setOrReplace(true);
            }
        } else {
            stmt.setCreate(false);
        }

        accept(Token.FUNCTION);

        SQLName procedureName = this.exprParser.name();
        stmt.setName(procedureName);

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            parserParameters(stmt.getParameters(), stmt);
            accept(Token.RPAREN);
        }

        accept(Token.RETURN);
        SQLDataType returnDataType = this.exprParser.parseDataType(false);
        stmt.setReturnDataType(returnDataType);

        if (lexer.identifierEquals("AUTHID")) {
            lexer.nextToken();
            String strVal = lexer.stringVal();
            if (lexer.identifierEquals("CURRENT_USER")) {
                lexer.nextToken();
            } else {
                acceptIdentifier("DEFINER");
            }
            SQLName authid = new SQLIdentifierExpr(strVal);
            stmt.setAuthid(authid);
        }

        if (lexer.token() == Token.SEMI) {
            lexer.nextToken();
            return stmt;
        }

        if (lexer.token() == Token.IS) {
            lexer.nextToken();
        } else {
            accept(Token.AS);
        }

        if (lexer.identifierEquals("LANGUAGE")) {
            lexer.nextToken();
            if (lexer.identifierEquals("JAVA")) {
                lexer.nextToken();
                acceptIdentifier("NAME");
                String javaCallSpec = lexer.stringVal();
                accept(Token.LITERAL_CHARS);
                stmt.setJavaCallSpec(javaCallSpec);
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
            return stmt;
        }

        SQLStatement block = this.parseBlock();

        stmt.setBlock(block);

        if (lexer.identifierEquals(procedureName.getSimpleName())) {
            lexer.nextToken();
        }

        // return stmt;

        if (lexer.identifierEquals(procedureName.getSimpleName())) {
            lexer.nextToken();
        }

        return stmt;
    }

    public SQLStatement parseRaise() {
        lexer.nextToken();
        OracleRaiseStatement stmt = new OracleRaiseStatement();
        if (lexer.token() != Token.SEMI) {
            stmt.setException(this.exprParser.expr());
        }
        accept(Token.SEMI);
        return stmt;
    }

    public SQLStatement parseCase() {
        SQLCaseStatement caseStmt = new SQLCaseStatement();
        caseStmt.setDbType(dbType);
        lexer.nextToken();
        if (lexer.token() != Token.WHEN) {
            caseStmt.setValueExpr(this.exprParser.expr());
        }

        accept(Token.WHEN);
        SQLExpr testExpr = this.exprParser.expr();
        accept(Token.THEN);
        SQLStatement stmt = this.parseStatement();
        if (lexer.token() == Token.SEMI) {
            lexer.nextToken();
        }
        SQLCaseStatement.Item caseItem = new SQLCaseStatement.Item(testExpr, stmt);
        caseStmt.addItem(caseItem);

        while (lexer.token() == Token.WHEN) {
            lexer.nextToken();
            testExpr = this.exprParser.expr();
            accept(Token.THEN);
            stmt = this.parseStatement();
            if (lexer.token() == Token.SEMI) {
                lexer.nextToken();
            }
            caseItem = new SQLCaseStatement.Item(testExpr, stmt);
            caseStmt.addItem(caseItem);
        }

        if (lexer.token() == Token.ELSE) {
            lexer.nextToken();
            this.parseStatementList(caseStmt.getElseStatements(), -1, caseStmt);
        }

        accept(Token.END);
        accept(Token.CASE);
        accept(Token.SEMI);
        return caseStmt;
    }

    public SQLStatement parseIf() {
        accept(Token.IF);

        SQLIfStatement stmt = new SQLIfStatement();
        stmt.setDbType(dbType);

        stmt.setCondition(this.exprParser.expr());

        accept(Token.THEN);

        this.parseStatementList(stmt.getStatements(), -1, stmt);

        while (lexer.token() == Token.ELSIF) {
            lexer.nextToken();

            SQLIfStatement.ElseIf elseIf = new SQLIfStatement.ElseIf();

            elseIf.setCondition(this.exprParser.expr());
            elseIf.setParent(stmt);

            accept(Token.THEN);
            this.parseStatementList(elseIf.getStatements(), -1, stmt);

            stmt.getElseIfList().add(elseIf);
        }

        if (lexer.token() == Token.ELSE) {
            lexer.nextToken();



            SQLIfStatement.Else elseItem = new SQLIfStatement.Else();
            this.parseStatementList(elseItem.getStatements(), -1, elseItem);
            stmt.setElseItem(elseItem);
        }

        accept(Token.END);
        accept(Token.IF);
//        if (lexer.token() == Token.SEMI) {
//            lexer.nextToken();
//        }
        accept(Token.SEMI);
        stmt.setAfterSemi(true);

        return stmt;
    }

    public OracleForStatement parseFor() {
        OracleForStatement stmt = new OracleForStatement();

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();
        } else {
            acceptIdentifier("FORALL");
            stmt.setAll(true);
        }

        stmt.setIndex(this.exprParser.name());
        accept(Token.IN);
        stmt.setRange(this.exprParser.expr());

        if (stmt.isAll()) {
            SQLStatement itemStmt = this.parseStatement();
            itemStmt.setParent(stmt);
            stmt.getStatements().add(itemStmt);
        } else {
            accept(Token.LOOP);

            this.parseStatementList(stmt.getStatements(), -1, stmt);
            accept(Token.END);
            accept(Token.LOOP);

            if (lexer.token() != Token.SEMI) {
                SQLName endLabel = this.exprParser.name();
                stmt.setEndLabel(endLabel);
            }

            accept(Token.SEMI);
            stmt.setAfterSemi(true);
        }
        return stmt;
    }

    public SQLLoopStatement parseLoop() {
        accept(Token.LOOP);

        SQLLoopStatement stmt = new SQLLoopStatement();

        this.parseStatementList(stmt.getStatements(), -1, stmt);
        accept(Token.END);
        accept(Token.LOOP);

        if (lexer.token() == Token.IDENTIFIER) {
            String label = lexer.stringVal();
            stmt.setLabelName(label);
            lexer.nextToken();
        }

        accept(Token.SEMI);
        stmt.setAfterSemi(true);
        return stmt;
    }

    public SQLStatement parseSet() {
        accept(Token.SET);

        if (lexer.identifierEquals("TRANSACTION")) {
            lexer.nextToken();

            OracleSetTransactionStatement stmt = new OracleSetTransactionStatement();

            if (lexer.identifierEquals("READ")) {
                lexer.nextToken();

                if (lexer.identifierEquals("ONLY")) {
                    lexer.nextToken();
                    stmt.setReadOnly(true);
                } else {
                    acceptIdentifier("WRITE");
                    stmt.setWrite(true);
                }
            }

            if (lexer.identifierEquals("NAME")) {
                lexer.nextToken();

                stmt.setName(this.exprParser.expr());
            }

            return stmt;
        }

        SQLSetStatement stmt = new SQLSetStatement(getDbType());
        parseAssignItems(stmt.getItems(), stmt);

        stmt.putAttribute("parser.set", Boolean.TRUE);
        return stmt;
    }

    public SQLStatement parserAlter() {
        accept(Token.ALTER);
        if (lexer.token() == Token.SESSION) {
            lexer.nextToken();

            OracleAlterSessionStatement stmt = new OracleAlterSessionStatement();
            if (lexer.token() == Token.SET) {
                lexer.nextToken();
                parseAssignItems(stmt.getItems(), stmt);
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
            return stmt;
        } else if (lexer.token() == Token.PROCEDURE) {
            lexer.nextToken();
            OracleAlterProcedureStatement stmt = new OracleAlterProcedureStatement();
            stmt.setName(this.exprParser.name());
            if (lexer.identifierEquals("COMPILE")) {
                lexer.nextToken();
                stmt.setCompile(true);
            }

            if (lexer.identifierEquals("REUSE")) {
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

            if (lexer.identifierEquals("RENAME")) {
                lexer.nextToken();
                accept(Token.TO);
                stmt.setRenameTo(this.exprParser.name());
            }

            for (;;) {
                if (lexer.identifierEquals("rebuild")) {
                    lexer.nextToken();

                    OracleAlterIndexStatement.Rebuild rebuild = new OracleAlterIndexStatement.Rebuild();
                    stmt.setRebuild(rebuild);
                    continue;
                } else if (lexer.identifierEquals("MONITORING")) {
                    lexer.nextToken();
                    acceptIdentifier("USAGE");
                    stmt.setMonitoringUsage(Boolean.TRUE);
                    continue;
                } else if (lexer.identifierEquals("PARALLEL")) {
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
                } else if (lexer.identifierEquals("COMPILE")) {
                    lexer.nextToken();
                    stmt.setCompile(true);
                    continue;
                }
                break;
            }

            return stmt;
        } else if (lexer.identifierEquals("SYNONYM")) {
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
                } else if (lexer.identifierEquals("COMPILE")) {
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
                } else if (lexer.identifierEquals("COMPILE")) {
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

            if (lexer.identifierEquals("ADD")) {
                lexer.nextToken();

                if (lexer.identifierEquals("DATAFILE")) {
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

                        if (lexer.identifierEquals("SIZE")) {
                            lexer.nextToken();
                            file.setSize(this.exprParser.expr());
                        }

                        if (lexer.identifierEquals("AUTOEXTEND")) {
                            lexer.nextToken();
                            if (lexer.identifierEquals("OFF")) {
                                lexer.nextToken();
                                file.setAutoExtendOff(true);
                            } else if (lexer.identifierEquals("ON")) {
                                lexer.nextToken();
                                file.setAutoExtendOn(this.exprParser.expr());
                            } else {
                                throw new ParserException("TODO : " + lexer.info());
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
                    throw new ParserException("TODO : " + lexer.info());
                }
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }

            return stmt;
        }

        throw new ParserException("TODO : " + lexer.info());
    }

    private SQLStatement parseAlterTable() {
        lexer.nextToken();
        SQLAlterTableStatement stmt = new SQLAlterTableStatement(getDbType());
        stmt.setName(this.exprParser.name());

        for (;;) {
            if (lexer.identifierEquals("ADD")) {
                lexer.nextToken();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();

                    SQLAlterTableAddColumn item = parseAlterTableAddColumn();

                    stmt.addItem(item);

                    accept(Token.RPAREN);
                } else if (lexer.token() == Token.CONSTRAINT) {
                    OracleConstraint constraint = ((OracleExprParser) this.exprParser).parseConstaint();
                    SQLAlterTableAddConstraint item = new SQLAlterTableAddConstraint();
                    constraint.setParent(item);
                    item.setParent(stmt);
                    item.setConstraint(constraint);
                    stmt.addItem(item);
                } else if (lexer.token() == Token.IDENTIFIER) {
                    SQLAlterTableAddColumn item = parseAlterTableAddColumn();
                    stmt.addItem(item);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }

                continue;
            } else if (lexer.identifierEquals("MOVE")) {
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

                OracleAlterTableModify item = new OracleAlterTableModify();
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();

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

                } else {
                    SQLColumnDefinition columnDef = this.exprParser.parseColumn();
                    item.addColumn(columnDef);
                }

                stmt.addItem(item);
                continue;
            } else if (lexer.identifierEquals("SPLIT")) {
                parseAlterTableSplit(stmt);
                continue;
            } else if (lexer.token() == Token.TRUNCATE) {
                lexer.nextToken();
                if (lexer.token() == Token.PARTITION) {
                    lexer.nextToken();
                    OracleAlterTableTruncatePartition item = new OracleAlterTableTruncatePartition();
                    item.setName(this.exprParser.name());
                    stmt.addItem(item);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
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
                    stmt.addItem(item);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
            } else if (lexer.token() == Token.ENABLE) {
                lexer.nextToken();
                if (lexer.token() == Token.CONSTRAINT) {
                    lexer.nextToken();
                    SQLAlterTableDisableConstraint item = new SQLAlterTableDisableConstraint();
                    item.setConstraintName(this.exprParser.name());
                    stmt.addItem(item);
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
            }

            break;
        }

        if (lexer.token() == Token.UPDATE) {
            lexer.nextToken();

            if (lexer.identifierEquals("GLOBAL")) {
                lexer.nextToken();
                acceptIdentifier("INDEXES");
                stmt.setUpdateGlobalIndexes(true);
            } else {
                throw new ParserException("TODO : " + lexer.info());
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
            stmt.addItem(item);
        } else if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());
            stmt.addItem(item);
            accept(Token.RPAREN);
        } else if (lexer.token() == Token.COLUMN) {
            lexer.nextToken();
            SQLAlterTableDropColumnItem item = new SQLAlterTableDropColumnItem();
            this.exprParser.names(item.getColumns());
            stmt.addItem(item);
        } else if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            OracleAlterTableDropPartition item = new OracleAlterTableDropPartition();
            item.setName(this.exprParser.name());
            stmt.addItem(item);
        } else if (lexer.token() == Token.INDEX) {
            lexer.nextToken();
            SQLName indexName = this.exprParser.name();
            SQLAlterTableDropIndex item = new SQLAlterTableDropIndex();
            item.setIndexName(indexName);
            stmt.addItem(item);
        } else {
            throw new ParserException("TODO : " + lexer.info());
        }
    }

    private void parseAlterTableSplit(SQLAlterTableStatement stmt) {
        lexer.nextToken();
        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            OracleAlterTableSplitPartition item = new OracleAlterTableSplitPartition();
            item.setName(this.exprParser.name());

            if (lexer.identifierEquals("AT")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                this.exprParser.exprList(item.getAt(), item);
                accept(Token.RPAREN);
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }

            if (lexer.token() == Token.INTO) {
                lexer.nextToken();
                accept(Token.LPAREN);

                for (;;) {
                    NestedTablePartitionSpec spec = new NestedTablePartitionSpec();
                    accept(Token.PARTITION);
                    spec.setPartition(this.exprParser.name());

                    for (;;) {
                        if (lexer.token() == Token.TABLESPACE) {
                            lexer.nextToken();
                            SQLName tablespace = this.exprParser.name();
                            spec.getSegmentAttributeItems().add(new TableSpaceItem(tablespace));
                            continue;
                        } else if (lexer.identifierEquals("PCTREE")) {
                            throw new ParserException("TODO : " + lexer.info());
                        } else if (lexer.identifierEquals("PCTUSED")) {
                            throw new ParserException("TODO : " + lexer.info());
                        } else if (lexer.identifierEquals("INITRANS")) {
                            throw new ParserException("TODO : " + lexer.info());

                        } else if (lexer.identifierEquals("STORAGE")) {
                            throw new ParserException("TODO : " + lexer.info());

                        } else if (lexer.identifierEquals("LOGGING")) {
                            throw new ParserException("TODO : " + lexer.info());
                        } else if (lexer.identifierEquals("NOLOGGING")) {
                            throw new ParserException("TODO : " + lexer.info());
                        } else if (lexer.identifierEquals("FILESYSTEM_LIKE_LOGGING")) {
                            throw new ParserException("TODO : " + lexer.info());

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
            stmt.addItem(item);
        } else {
            throw new ParserException("TODO : " + lexer.info());
        }
    }

    public OracleLockTableStatement parseLock() {
        accept(Token.LOCK);
        accept(Token.TABLE);

        OracleLockTableStatement stmt = new OracleLockTableStatement();
        stmt.setTable(this.exprParser.name());

        accept(Token.IN);

        Token token = lexer.token();
        if (token == Token.SHARE) {
            lexer.nextToken();

            if (lexer.token() == Token.ROW) {
                lexer.nextToken();
                accept(Token.EXCLUSIVE);
                stmt.setLockMode(LockMode.SHARE_ROW_EXCLUSIVE);
            } else if (lexer.token() == Token.UPDATE) {
                lexer.nextToken();
                stmt.setLockMode(LockMode.SHARE_UPDATE);
            } else {
                stmt.setLockMode(LockMode.SHARE);
            }
        } else if (token == Token.EXCLUSIVE) {
            stmt.setLockMode(LockMode.EXCLUSIVE);
            lexer.nextToken();
        } else if(token == Token.ROW) {
            lexer.nextToken();
            token = lexer.token();
            if (token == Token.SHARE) {
                stmt.setLockMode(LockMode.ROW_SHARE);
                lexer.nextToken();
            } else if (token == Token.EXCLUSIVE) {
                stmt.setLockMode(LockMode.ROW_EXCLUSIVE);
                lexer.nextToken();
            } else {
                throw new ParserException(lexer.info());
            }
        } else {
            throw new ParserException(lexer.info());
        }
        accept(Token.MODE);

        if (lexer.token() == Token.NOWAIT) {
            lexer.nextToken();
            stmt.setNoWait(true);
        } else if (lexer.token() == Token.WAIT) {
            lexer.nextToken();
            stmt.setWait(exprParser.expr());
        }
        return stmt;
    }

    public SQLStatement parseBlock() {
        SQLBlockStatement block = new SQLBlockStatement();
        block.setDbType(JdbcConstants.ORACLE);

        if (lexer.token() == Token.DECLARE) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.CURSOR) {
            parserParameters(block.getParameters(), block);
            for (SQLParameter param : block.getParameters()) {
                param.setParent(block);
            }
        }

        if (lexer.token() == Token.PROCEDURE) {
            SQLCreateProcedureStatement stmt = this.parseCreateProcedure();
            for (SQLParameter param : block.getParameters()) {
                param.setParent(stmt);
                stmt.getParameters().add(param);
            }
            return stmt;
        }

        accept(Token.BEGIN);

        parseStatementList(block.getStatementList(), -1, block);

        accept(Token.END);

        Token token = lexer.token();

        if (token == Token.EOF) {
            return block;
        }

        if (token != Token.SEMI) {
            String endLabel = lexer.stringVal();
            accept(Token.IDENTIFIER);
            block.setEndLabel(endLabel);
        }
        accept(Token.SEMI);

        return block;
    }

    private void parserParameters(List<SQLParameter> parameters, SQLObject parent) {
        for (;;) {
            SQLParameter parameter = new SQLParameter();

            SQLName name;
            SQLDataType dataType = null;
            if (lexer.token() == Token.CURSOR) {
                lexer.nextToken();

                dataType = new SQLDataTypeImpl();
                dataType.setName("CURSOR");

                name = this.exprParser.name();

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    this.parserParameters(parameter.getCursorParameters(), parameter);
                    accept(Token.RPAREN);
                }

                accept(Token.IS);
                SQLSelect select = this.createSQLSelectParser().select();
                parameter.setDefaultValue(new SQLQueryExpr(select));

            } else if (lexer.token() == Token.PROCEDURE) {
                break;
            } else if (lexer.identifierEquals("TYPE")) {
                lexer.nextToken();
                name = this.exprParser.name();
                accept(Token.IS);

                if (lexer.identifierEquals("REF")) {
                    lexer.nextToken();
                    accept(Token.CURSOR);

                    dataType = new SQLDataTypeImpl("REF CURSOR");
                } else if (lexer.token() == Token.TABLE) {
                    lexer.nextToken();
                    accept(Token.OF);

                    name = this.exprParser.name();

                    if (lexer.token() == Token.PERCENT) {
                        lexer.nextToken();
                        acceptIdentifier("TYPE");
                    }

                    String typeName = "TABLE OF " + name.toString() + "%TYPE";
                    dataType = new SQLDataTypeImpl(typeName);
                } else if (lexer.identifierEquals("VARRAY")) {
                    lexer.nextToken();
                    accept(Token.LPAREN);
                    int len = this.exprParser.acceptInteger();
                    accept(Token.RPAREN);
                    accept(Token.OF);

                    if (lexer.identifierEquals("NUMBER")) {
                        lexer.nextToken();
                        String typeName = "VARRAY(" + len + ") OF NUMBER";
                        dataType = new SQLDataTypeImpl(typeName);
                    } else {
                        throw new ParserException("TODO : " + lexer.info());
                    }
                } else {
                    throw new ParserException("TODO : " + lexer.info());
                }
            } else {
                name = this.exprParser.name();

                if (lexer.token() == Token.IN) {
                    lexer.nextToken();

                    if (lexer.token() == Token.OUT) {
                        lexer.nextToken();
                        parameter.setParamType(SQLParameter.ParameterType.INOUT);
                    } else {
                        parameter.setParamType(SQLParameter.ParameterType.IN);
                    }
                } else if (lexer.token() == Token.OUT) {
                    lexer.nextToken();
                    parameter.setParamType(SQLParameter.ParameterType.OUT);
                }

                if (lexer.identifierEquals("NOCOPY")) {
                    lexer.nextToken();
                    parameter.setNoCopy(true);
                }

                if (lexer.identifierEquals("CONSTANT")) {
                    lexer.nextToken();
                    parameter.setConstant(true);
                }

                dataType = this.exprParser.parseDataType(false);

                if (lexer.token() == Token.COLONEQ) {
                    lexer.nextToken();
                    parameter.setDefaultValue(this.exprParser.expr());
                }
            }

            parameter.setName(name);
            parameter.setDataType(dataType);

            parameters.add(parameter);
            Token token = lexer.token();
            if (token == Token.COMMA || token == Token.SEMI) {
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
        } else if (lexer.token() == Token.FIRST || lexer.identifierEquals("FIRST")) {
            lexer.nextToken();
            stmt.setOption(OracleMultiInsertStatement.Option.FIRST);
        }

        while (lexer.token() == Token.INTO) {
            OracleMultiInsertStatement.InsertIntoClause clause = new OracleMultiInsertStatement.InsertIntoClause();

            boolean acceptSubQuery = stmt.getEntries().size() == 0;
            parseInsert0(clause, acceptSubQuery);

            clause.setReturning(parseReturningClause());
            clause.setErrorLogging(parseErrorLoggingClause());

            stmt.addEntry(clause);
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

                clause.addItem(item);
            }

            if (lexer.token() == Token.ELSE) {
                lexer.nextToken();

                OracleMultiInsertStatement.InsertIntoClause insertInto = new OracleMultiInsertStatement.InsertIntoClause();
                parseInsert0(insertInto, false);
                clause.setElseItem(insertInto);
            }
            stmt.addEntry(clause);
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

            this.parseStatementList(item.getStatements(), -1, item);

            stmt.addItem(item);

            if (lexer.token() == Token.SEMI) {
                lexer.nextToken();
            }

            if (lexer.token() != Token.WHEN) {
                break;
            }
        }
        return stmt;
    }

    public OracleReturningClause parseReturningClause() {
        OracleReturningClause clause = null;

        if (lexer.token() == Token.RETURNING) {
            lexer.nextToken();
            clause = new OracleReturningClause();

            for (;;) {
                SQLExpr item = exprParser.expr();
                clause.addItem(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.INTO);
            for (;;) {
                SQLExpr item = exprParser.expr();
                clause.addValue(item);
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

            if (lexer.identifierEquals("ONLY")) {
                lexer.nextToken();
                accept(Token.LPAREN);

                SQLName tableName = exprParser.name();
                deleteStatement.setTableName(tableName);

                accept(Token.RPAREN);
            } else if (lexer.token() == Token.LPAREN) {
                SQLTableSource tableSource = this.createSQLSelectParser().parseTableSource();
                deleteStatement.setTableSource(tableSource);
            } else {
                SQLName tableName = exprParser.name();
                deleteStatement.setTableName(tableName);
            }

            deleteStatement.setAlias(tableAlias());
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            deleteStatement.setWhere(this.exprParser.expr());
        }

        if (lexer.token() == Token.RETURNING) {
            OracleReturningClause clause = this.parseReturningClause();
            deleteStatement.setReturning(clause);
        }
        if (lexer.identifierEquals("RETURN") || lexer.identifierEquals("RETURNING")) {
            throw new ParserException("TODO. " + lexer.info());
        }

        if (lexer.identifierEquals("LOG")) {
            throw new ParserException("TODO. " + lexer.info());
        }

        return deleteStatement;
    }

    public SQLStatement parseCreateDbLink() {
        accept(Token.CREATE);

        OracleCreateDatabaseDbLinkStatement dbLink = new OracleCreateDatabaseDbLinkStatement();

        if (lexer.identifierEquals("SHARED")) {
            dbLink.setShared(true);
            lexer.nextToken();
        }

        if (lexer.identifierEquals("PUBLIC")) {
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
                
                if (lexer.token() == Token.IDENTIFIER) {
                    lexer.nextToken();
                } else {
                    accept(Token.LITERAL_ALIAS);
                }
            }
        }

        if (lexer.identifierEquals("AUTHENTICATED")) {
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
        } else if (lexer.identifierEquals("BITMAP")) {
            stmt.setType("BITMAP");
            lexer.nextToken();
        }

        accept(Token.INDEX);

        stmt.setName(this.exprParser.name());

        accept(Token.ON);

        if (lexer.identifierEquals("CLUSTER")) {
            lexer.nextToken();
            stmt.setCluster(true);
        }

        stmt.setTable(this.exprParser.name());

        if (lexer.token() == Token.IDENTIFIER) {
            String alias = lexer.stringVal();
            stmt.getTable().setAlias(alias);
            lexer.nextToken();
        }

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (; ; ) {
                SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
                stmt.addItem(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
        }

        for (;;) {
            this.getExprParser().parseSegmentAttributes(stmt);

            if (lexer.token() == Token.COMPUTE) {
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
            } else if (lexer.identifierEquals("ONLINE")) {
                lexer.nextToken();
                stmt.setOnline(true);
                continue;
            } else if (lexer.identifierEquals("NOPARALLEL")) {
                lexer.nextToken();
                stmt.setNoParallel(true);
                continue;
            } else if (lexer.identifierEquals("PARALLEL")) {
                lexer.nextToken();
                stmt.setParallel(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.INDEX) {
                lexer.nextToken();
                acceptIdentifier("ONLY");
                acceptIdentifier("TOPLEVEL");
                stmt.setIndexOnlyTopLevel(true);
                continue;
            } else if (lexer.identifierEquals("SORT")) {
                lexer.nextToken();
                stmt.setSort(Boolean.TRUE);
                continue;
            } else if (lexer.identifierEquals("NOSORT")) {
                lexer.nextToken();
                stmt.setSort(Boolean.FALSE);
                continue;
            } else if (lexer.identifierEquals("LOCAL")) {
                lexer.nextToken();
                stmt.setLocal(true);

                for (;;) {
                    if (lexer.token() == Token.STORE) {
                        lexer.nextToken();
                        accept(Token.IN);
                        accept(Token.LPAREN);
                        this.exprParser.names(stmt.getLocalStoreIn(), stmt);
                        accept(Token.RPAREN);
                    } else if (lexer.token() == Token.LPAREN) {
                        lexer.nextToken();
                        for (; ; ) {
                            SQLPartition partition = this.getExprParser().parsePartition();
                            partition.setParent(stmt);
                            stmt.getLocalPartitions().add(partition);
                            if (lexer.token() == Token.COMMA) {
                                lexer.nextToken();
                                continue;
                            } else if (lexer.token() == Token.RPAREN) {
                                lexer.nextToken();
                                break;
                            }
                            throw new ParserException("TODO : " + lexer.info());
                        }
                    } else {
                        break;
                    }
                }

                break;
            } else if (lexer.identifierEquals("GLOBAL")) {
                lexer.nextToken();
                stmt.setGlobal(true);

                if (lexer.token() == Token.PARTITION) {
                    lexer.nextToken();

                    accept(Token.BY);

                    if (lexer.identifierEquals("RANGE")) {
                        SQLPartitionByRange partitionByRange = this.getExprParser().partitionByRange();
                        this.getExprParser().partitionClauseRest(partitionByRange);
                        partitionByRange.setParent(stmt);
                        stmt.getGlobalPartitions().add(partitionByRange);
                        continue;
                    } else if (lexer.identifierEquals("HASH")) {
                        SQLPartitionByHash partitionByHash = this.getExprParser().partitionByHash();
                        this.getExprParser().partitionClauseRest(partitionByHash);

                        if (lexer.token() == Token.LPAREN) {
                            lexer.nextToken();
                            for (; ; ) {
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
                        partitionByHash.setParent(stmt);
                        stmt.getGlobalPartitions().add(partitionByHash);
                        continue;
                    }
                }

                break;
            } else {
                break;
            }
        }
        return stmt;
    }

    public SQLCreateSequenceStatement parseCreateSequence(boolean acceptCreate) {
        if (acceptCreate) {
            accept(Token.CREATE);
        }

        accept(Token.SEQUENCE);

        SQLCreateSequenceStatement stmt = new SQLCreateSequenceStatement();
        stmt.setDbType(JdbcConstants.ORACLE);
        stmt.setName(this.exprParser.name());

        for (;;) {
            if (lexer.token() == Token.START) {
                lexer.nextToken();
                accept(Token.WITH);
                stmt.setStartWith(this.exprParser.expr());
                continue;
            } else if (lexer.identifierEquals("INCREMENT")) {
                lexer.nextToken();
                accept(Token.BY);
                stmt.setIncrementBy(this.exprParser.expr());
                continue;
            } else if (lexer.token() == Token.CACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.TRUE);

                if (lexer.token() == Token.LITERAL_INT) {
                    stmt.setCacheValue(this.exprParser.primary());
                }
                continue;
            } else if (lexer.token() == Token.NOCACHE) {
                lexer.nextToken();
                stmt.setCache(Boolean.FALSE);
                continue;
            } else if (lexer.token() == Token.ORDER) {
                lexer.nextToken();
                stmt.setOrder(Boolean.TRUE);
                continue;
            } else if (lexer.identifierEquals("NOORDER")) {
                lexer.nextToken();
                stmt.setOrder(Boolean.FALSE);
                continue;
            } else if (lexer.identifierEquals("CYCLE")) {
                lexer.nextToken();
                stmt.setCycle(Boolean.TRUE);
                continue;
            } else if (lexer.identifierEquals("NOCYCLE")) {
                lexer.nextToken();
                stmt.setCycle(Boolean.FALSE);
                continue;
            } else if (lexer.identifierEquals("MINVALUE")) {
                lexer.nextToken();
                stmt.setMinValue(this.exprParser.expr());
                continue;
            } else if (lexer.identifierEquals("MAXVALUE")) {
                lexer.nextToken();
                stmt.setMaxValue(this.exprParser.expr());
                continue;
            } else if (lexer.identifierEquals("NOMAXVALUE")) {
                lexer.nextToken();
                stmt.setNoMaxValue(true);
                continue;
            } else if (lexer.identifierEquals("NOMINVALUE")) {
                lexer.nextToken();
                stmt.setNoMinValue(true);
                continue;
            }
            break;
        }

        return stmt;
    }

    public SQLCreateProcedureStatement parseCreateProcedure() {
        SQLCreateProcedureStatement stmt = new SQLCreateProcedureStatement();
        stmt.setDbType(dbType);

        if (lexer.token() == Token.CREATE) {
            lexer.nextToken();
            if (lexer.token() == Token.OR) {
                lexer.nextToken();
                accept(Token.REPLACE);
                stmt.setOrReplace(true);
            }
        } else {
            stmt.setCreate(false);
        }

        accept(Token.PROCEDURE);

        SQLName procedureName = this.exprParser.name();
        stmt.setName(procedureName);

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            parserParameters(stmt.getParameters(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals("AUTHID")) {
            lexer.nextToken();
            String strVal = lexer.stringVal();
            if (lexer.identifierEquals("CURRENT_USER")) {
                lexer.nextToken();
            } else {
                acceptIdentifier("DEFINER");
            }
            SQLName authid = new SQLIdentifierExpr(strVal);
            stmt.setAuthid(authid);
        }

        if (lexer.token() == Token.SEMI) {
            lexer.nextToken();
            return stmt;
        }

        if (lexer.token() == Token.IS) {
            lexer.nextToken();
        } else {
            accept(Token.AS);
        }

        if (lexer.identifierEquals("LANGUAGE")) {
            lexer.nextToken();
            if (lexer.identifierEquals("JAVA")) {
                lexer.nextToken();
                acceptIdentifier("NAME");
                String javaCallSpec = lexer.stringVal();
                accept(Token.LITERAL_CHARS);
                stmt.setJavaCallSpec(javaCallSpec);
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
            return stmt;
        }

        SQLStatement block = this.parseBlock();

        stmt.setBlock(block);

        if (lexer.identifierEquals(procedureName.getSimpleName())) {
            lexer.nextToken();
        }

        return stmt;
    }

    public SQLUpdateStatement parseUpdateStatement() {
        return new OracleUpdateParser(this.lexer).parseUpdateStatement();
    }

    public SQLStatement parseCreatePackage() {
        accept(Token.CREATE);

        boolean repalce = false;
        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            repalce = true;
        }

        acceptIdentifier("PACKAGE");

        OracleCreatePackageStatement stmt = new OracleCreatePackageStatement();
        stmt.setOrReplace(repalce);

        if (lexer.identifierEquals("BODY")) {
            lexer.nextToken();
            stmt.setBody(true);
        }

        SQLName pkgName = this.exprParser.name();
        stmt.setName(pkgName);

        if (lexer.token() == Token.IS) {
            lexer.nextToken();
        } else {
            accept(Token.AS);
        }

        // this.parseStatementList(stmt.getStatements(), -1, stmt);
        for (;;) {
            if (lexer.token() == Token.IDENTIFIER) {

                SQLDeclareStatement varDecl = new SQLDeclareStatement();
                varDecl.setDbType(dbType);
                varDecl.setParent(stmt);

                SQLDeclareItem varItem = new SQLDeclareItem();

                SQLName name = this.exprParser.name();
                varItem.setName(name);

                varItem.setDataType(this.exprParser.parseDataType(false));
                varItem.setParent(varDecl);

                varDecl.getItems().add(varItem);

                accept(Token.SEMI);
                varDecl.setAfterSemi(true);

                stmt.getStatements().add(varDecl);
            } else if (lexer.token() == Token.FUNCTION) {
                SQLStatement function = this.parseFunction();
                function.setParent(stmt);
                stmt.getStatements().add(function);
            } else if (lexer.token() == Token.PROCEDURE) {
                SQLStatement proc = this.parseCreateProcedure();
                proc.setParent(stmt);
                stmt.getStatements().add(proc);
            } else if (lexer.token() == Token.END) {
                break;
            } else {
                throw new ParserException("TODO : " + lexer.info());
            }
        }

        accept(Token.END);

        if (lexer.identifierEquals(pkgName.getSimpleName())) {
            lexer.nextToken();
        }

        accept(Token.SEMI);
        return stmt;
    }

    public SQLStatement parseCreateSynonym() {
        OracleCreateSynonymStatement stmt = new OracleCreateSynonymStatement();
        accept(Token.CREATE);

        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            accept(Token.REPLACE);
            stmt.setOrReplace(true);
        }

        if (lexer.identifierEquals("PUBLIC")) {
            lexer.nextToken();
            stmt.setPublic(true);
        }

        acceptIdentifier("SYNONYM");

        stmt.setName(this.exprParser.name());

        accept(Token.FOR);

        stmt.setObject(this.exprParser.name());
        return stmt;
    }
}
