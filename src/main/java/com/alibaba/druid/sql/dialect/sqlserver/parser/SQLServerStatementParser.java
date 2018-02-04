/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.sqlserver.parser;

import java.util.Collection;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLDeclareItem;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerOutput;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.ast.statement.SQLDeclareStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerExecStatement.SQLServerParameter;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerInsertStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerRollbackStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerSetTransactionIsolationLevelStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerUpdateStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerWaitForStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

public class SQLServerStatementParser extends SQLStatementParser {

    public SQLServerStatementParser(String sql){
        super(new SQLServerExprParser(sql));
    }

    public SQLServerStatementParser(String sql, SQLParserFeature... features){
        super(new SQLServerExprParser(sql, features));
    }

    public SQLSelectParser createSQLSelectParser() {
        return new SQLServerSelectParser(this.exprParser, selectListCache);
    }

    public SQLServerStatementParser(Lexer lexer){
        super(new SQLServerExprParser(lexer));
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.WITH) {
            SQLStatement stmt = parseSelect();
            statementList.add(stmt);
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXEC) || lexer.identifierEquals(FnvHash.Constants.EXECUTE)) {
            lexer.nextToken();

            SQLServerExecStatement execStmt = new SQLServerExecStatement();
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                this.parseExecParameter(execStmt.getParameters(), execStmt);
                accept(Token.RPAREN);
            } else {
                SQLName sqlNameName = this.exprParser.name();

                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                    execStmt.setReturnStatus(sqlNameName);
                    execStmt.setModuleName(this.exprParser.name());
                } else {
                    execStmt.setModuleName(sqlNameName);
                }
                
                this.parseExecParameter(execStmt.getParameters(), execStmt);
            }
            statementList.add(execStmt);
            return true;
        }
        
        if (lexer.token() == Token.DECLARE) {
            statementList.add(this.parseDeclare());
            return true;
        }
        
        if (lexer.token() == Token.IF) {
            statementList.add(this.parseIf());
            return true;
        }

        if (lexer.token() == Token.BEGIN) {
            statementList.add(this.parseBlock());
            return true;
        }
        
        if (lexer.token() == Token.COMMIT) {
            statementList.add(this.parseCommit());
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.WAITFOR)) {
            statementList.add(this.parseWaitFor());
            return true;
        }

        if (lexer.identifierEquals(FnvHash.Constants.GO)) {
            lexer.nextToken();

            SQLStatement stmt = new SQLScriptCommitStatement();
            statementList.add(stmt);
            return true;
        }
        
        return false;
    }
    /**
     * SQLServer parse Parameter statement support out type
     * @author zz [455910092@qq.com]
     */
    public void parseExecParameter(Collection<SQLServerParameter> exprCol, SQLObject parent) {
        if (lexer.token() == Token.RPAREN || lexer.token() == Token.RBRACKET) {
            return;
        }

        if (lexer.token() == Token.EOF) {
            return;
        }
        SQLServerParameter param = new SQLServerParameter();
        SQLExpr expr = this.exprParser.expr();
        expr.setParent(parent);
        param.setExpr(expr);
        if (lexer.token() == Token.OUT) {
            param.setType(true);
            accept(Token.OUT);
        }
        exprCol.add(param);
        while (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            param = new SQLServerParameter();
            expr = this.exprParser.expr();
            expr.setParent(parent);
            param.setExpr(expr);
            if (lexer.token() == Token.OUT) {
                param.setType(true);
                accept(Token.OUT);
            }
            exprCol.add(param);
        }
    }
    
    public SQLStatement parseDeclare() {
        this.accept(Token.DECLARE);

        SQLDeclareStatement declareStatement = new SQLDeclareStatement();
        
        for (;;) {
            SQLDeclareItem item = new  SQLDeclareItem();
            declareStatement.addItem(item);
            
            item.setName(this.exprParser.name());

            if (lexer.token() == Token.AS) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.TABLE) {
                lexer.nextToken();
                item.setType(SQLDeclareItem.Type.TABLE);
                
                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();

                    for (;;) {
                        if (lexer.token() == Token.IDENTIFIER //
                            || lexer.token() == Token.LITERAL_ALIAS) {
                            SQLColumnDefinition column = this.exprParser.parseColumn();
                            item.getTableElementList().add(column);
                        } else if (lexer.token() == Token.PRIMARY //
                                   || lexer.token() == Token.UNIQUE //
                                   || lexer.token() == Token.CHECK //
                                   || lexer.token() == Token.CONSTRAINT) {
                            SQLConstraint constraint = this.exprParser.parseConstaint();
                            constraint.setParent(item);
                            item.getTableElementList().add((SQLTableElement) constraint);
                        } else if (lexer.token() == Token.TABLESPACE) {
                            throw new ParserException("TODO " + lexer.info());
                        } else {
                            SQLColumnDefinition column = this.exprParser.parseColumn();
                            item.getTableElementList().add(column);
                        }

                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();

                            if (lexer.token() == Token.RPAREN) {
                                break;
                            }
                            continue;
                        }

                        break;
                    }
                    accept(Token.RPAREN);
                }
                break;
            } else if (lexer.token() == Token.CURSOR) {
                item.setType(SQLDeclareItem.Type.CURSOR);
                lexer.nextToken();
            } else {
                item.setType(SQLDeclareItem.Type.LOCAL);
                item.setDataType(this.exprParser.parseDataType());
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                    item.setValue(this.exprParser.expr());
                }
            }

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            } else {
                break;
            }
        }
        return declareStatement;
    }

    public SQLStatement parseInsert() {
        SQLServerInsertStatement insertStatement = new SQLServerInsertStatement();

        if (lexer.token() == Token.INSERT) {
            accept(Token.INSERT);
        }

        parseInsert0(insertStatement);
        return insertStatement;
    }

    protected void parseInsert0(SQLInsertInto insert, boolean acceptSubQuery) {
        SQLServerInsertStatement insertStatement = (SQLServerInsertStatement) insert;
        
        SQLServerTop top = this.getExprParser().parseTop();
        if (top != null) {
            insertStatement.setTop(top);
        }

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();
        }
        
        SQLName tableName = this.exprParser.name();
        insertStatement.setTableName(tableName);

        if (lexer.token() == Token.LITERAL_ALIAS) {
            insertStatement.setAlias(tableAlias());
        }

        parseInsert0_hinits(insertStatement);

        if (lexer.token() == Token.IDENTIFIER && !lexer.stringVal().equalsIgnoreCase("OUTPUT")) {
            insertStatement.setAlias(lexer.stringVal());
            lexer.nextToken();
        }

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            this.exprParser.exprList(insertStatement.getColumns(), insertStatement);
            accept(Token.RPAREN);
        }
        
        SQLServerOutput output = this.getExprParser().parserOutput();
        if (output != null) {
            insertStatement.setOutput(output);
        }

        if (lexer.token() == Token.VALUES) {
            lexer.nextToken();

            for (;;) {
                accept(Token.LPAREN);
                SQLInsertStatement.ValuesClause values = new SQLInsertStatement.ValuesClause();
                this.exprParser.exprList(values.getValues(), values);
                insertStatement.addValueCause(values);
                accept(Token.RPAREN);

                if (!parseCompleteValues && insertStatement.getValuesList().size() >= parseValuesSize) {
                    lexer.skipToEOF();
                    break;
                }
                
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }
        } else if (acceptSubQuery && (lexer.token() == Token.SELECT || lexer.token() == Token.LPAREN)) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            insertStatement.setQuery(queryExpr.getSubQuery());
        } else if (lexer.token() == Token.DEFAULT) {
            lexer.nextToken();
            accept(Token.VALUES);
            insertStatement.setDefaultValues(true);
        }
    }

    protected SQLServerUpdateStatement createUpdateStatement() {
        return new SQLServerUpdateStatement();
    }

    public SQLUpdateStatement parseUpdateStatement() {
        SQLServerUpdateStatement udpateStatement = createUpdateStatement();

        accept(Token.UPDATE);

        SQLServerTop top = this.getExprParser().parseTop();
        if (top != null) {
            udpateStatement.setTop(top);
        }

        SQLTableSource tableSource = this.exprParser.createSelectParser().parseTableSource();
        udpateStatement.setTableSource(tableSource);

        parseUpdateSet(udpateStatement);
        
        SQLServerOutput output = this.getExprParser().parserOutput();
        if (output != null) {
            udpateStatement.setOutput(output);
        }

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            SQLTableSource from = this.exprParser.createSelectParser().parseTableSource();
            udpateStatement.setFrom(from);
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            udpateStatement.setWhere(this.exprParser.expr());
        }

        return udpateStatement;
    }
    
    public SQLServerExprParser getExprParser() {
        return (SQLServerExprParser) exprParser;
    }
    
    public SQLStatement parseSet() {
        accept(Token.SET);

        if (lexer.identifierEquals(FnvHash.Constants.TRANSACTION)) {
            lexer.nextToken();
            acceptIdentifier("ISOLATION");
            acceptIdentifier("LEVEL");

            SQLServerSetTransactionIsolationLevelStatement stmt = new SQLServerSetTransactionIsolationLevelStatement();

            if (lexer.identifierEquals("READ")) {
                lexer.nextToken();

                if (lexer.identifierEquals("UNCOMMITTED")) {
                    stmt.setLevel("READ UNCOMMITTED");
                    lexer.nextToken();
                } else if (lexer.identifierEquals("COMMITTED")) {
                    stmt.setLevel("READ COMMITTED");
                    lexer.nextToken();
                } else {
                    throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal() + ", " + lexer.info());
                }
            } else if (lexer.identifierEquals("SERIALIZABLE")) {
                stmt.setLevel("SERIALIZABLE");
                lexer.nextToken();
            } else if (lexer.identifierEquals("SNAPSHOT")) {
                stmt.setLevel("SNAPSHOT");
                lexer.nextToken();
            } else if (lexer.identifierEquals("REPEATABLE")) {
                lexer.nextToken();
                if (lexer.identifierEquals("READ")) {
                    stmt.setLevel("REPEATABLE READ");
                    lexer.nextToken();
                } else {
                    throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal() + ", " + lexer.info());
                }
            } else {
                throw new ParserException("UNKOWN TRANSACTION LEVEL : " + lexer.stringVal() + ", " + lexer.info());
            }

            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.STATISTICS)) {
            lexer.nextToken();

            SQLSetStatement stmt = new SQLSetStatement();

            if (lexer.identifierEquals("IO") || lexer.identifierEquals("XML") || lexer.identifierEquals("PROFILE")
                || lexer.identifierEquals("TIME")) {

                SQLExpr target = new SQLIdentifierExpr("STATISTICS " + lexer.stringVal().toUpperCase());

                lexer.nextToken();
                if (lexer.token() == Token.ON) {
                    stmt.set(target, new SQLIdentifierExpr("ON"));
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.OFF)) {
                    stmt.set(target, new SQLIdentifierExpr("OFF"));
                    lexer.nextToken();
                }
            }
            return stmt;
        }

        if (lexer.identifierEquals(FnvHash.Constants.IDENTITY_INSERT)) {
            SQLSetStatement stmt = new SQLSetStatement();
            stmt.setOption(SQLSetStatement.Option.IDENTITY_INSERT);

            lexer.nextToken();
            SQLName table = this.exprParser.name();

            if (lexer.token() == Token.ON) {
                SQLExpr value = new SQLIdentifierExpr("ON");
                stmt.set(table, value);
                lexer.nextToken();
            } else if (lexer.identifierEquals(FnvHash.Constants.OFF)) {
                SQLExpr value = new SQLIdentifierExpr("OFF");
                stmt.set(table, value);
                lexer.nextToken();
            }
            return stmt;
        }

        if (lexer.token() == Token.VARIANT) {
            SQLSetStatement stmt = new SQLSetStatement(getDbType());
            parseAssignItems(stmt.getItems(), stmt);
            return stmt;
        } else {
            SQLSetStatement stmt = new SQLSetStatement();
            SQLExpr target = this.exprParser.expr();

            if (lexer.token() == Token.ON) {
                stmt.set(target, new SQLIdentifierExpr("ON"));
                lexer.nextToken();
            } else if (lexer.identifierEquals("OFF")) {
                stmt.set(target, new SQLIdentifierExpr("OFF"));
                lexer.nextToken();
            } else {
                stmt.set(target, this.exprParser.expr());
            }
            return stmt;
        }
    }
    
    public SQLIfStatement parseIf() {
        accept(Token.IF);

        SQLIfStatement stmt = new SQLIfStatement();

        stmt.setCondition(this.exprParser.expr());

        this.parseStatementList(stmt.getStatements(), 1, stmt);
        
        if(lexer.token() == Token.SEMI) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.ELSE) {
            lexer.nextToken();

            SQLIfStatement.Else elseItem = new SQLIfStatement.Else();
            this.parseStatementList(elseItem.getStatements(), 1, elseItem);
            stmt.setElseItem(elseItem);
        }

        return stmt;
    }

    public SQLStatement parseBlock() {
        accept(Token.BEGIN);

        if (lexer.identifierEquals("TRANSACTION") || lexer.identifierEquals("TRAN")) {
            lexer.nextToken();

            SQLStartTransactionStatement startTrans = new SQLStartTransactionStatement();
            startTrans.setDbType(dbType);

            if (lexer.token() == Token.IDENTIFIER) {
                SQLName name = this.exprParser.name();
                startTrans.setName(name);
            }
            return startTrans;
        }

        SQLBlockStatement block = new SQLBlockStatement();
        parseStatementList(block.getStatementList());

        accept(Token.END);

        return block;
    }
    
    public SQLStatement parseCommit() {
        acceptIdentifier("COMMIT");

        SQLCommitStatement stmt = new SQLCommitStatement();

        if (lexer.identifierEquals("WORK")) {
            lexer.nextToken();
            stmt.setWork(true);
        }

        if (lexer.identifierEquals("TRAN") || lexer.identifierEquals("TRANSACTION")) {
            lexer.nextToken();
            if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.VARIANT) {
                stmt.setTransactionName(this.exprParser.expr());
            }

            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                accept(Token.LPAREN);
                acceptIdentifier("DELAYED_DURABILITY");
                accept(Token.EQ);
                stmt.setDelayedDurability(this.exprParser.expr());
                accept(Token.RPAREN);
            }

        }

        return stmt;
    }
    
    public SQLServerRollbackStatement parseRollback() {
        acceptIdentifier("ROLLBACK");

        SQLServerRollbackStatement stmt = new SQLServerRollbackStatement();

        if (lexer.identifierEquals("WORK")) {
            lexer.nextToken();
            stmt.setWork(true);
        }

        if (lexer.identifierEquals("TRAN") || lexer.identifierEquals("TRANSACTION")) {
            lexer.nextToken();
            if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.VARIANT) {
                stmt.setName(this.exprParser.expr());
            }


        }

        return stmt;
    }
    
    public SQLServerWaitForStatement parseWaitFor() {
        acceptIdentifier("WAITFOR");

        SQLServerWaitForStatement stmt = new SQLServerWaitForStatement();

        if (lexer.identifierEquals("DELAY")) {
            lexer.nextToken();
            stmt.setDelay(this.exprParser.expr());
        }

        if (lexer.identifierEquals("TIME")) {
            lexer.nextToken();
            stmt.setTime(this.exprParser.expr());
        }

        if (lexer.token() == Token.COMMA) {
            lexer.nextToken();
            if (lexer.identifierEquals("TIMEOUT")) {
                lexer.nextToken();
                stmt.setTimeout(this.exprParser.expr());
            }
        }

        return stmt;
    }
}
