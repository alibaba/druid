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

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleErrorLoggingClause;
import com.alibaba.druid.sql.dialect.oracle.ast.clause.OracleReturningClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleMergeStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OraclePLSQLCommitStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleStatementParser extends SQLStatementParser {

    public OracleStatementParser(String sql){
        super(new OracleLexer(sql));
        this.lexer.nextToken();
    }

    public OracleStatementParser(Lexer lexer){
        super(lexer);
    }

    protected OracleExprParser createExprParser() {
        return new OracleExprParser(lexer);
    }

    @Override
    public void parseStatementList(List<SQLStatement> statementList) throws ParserException {
        for (;;) {
            if (lexer.token() == Token.EOF) {
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

                // if ((this.tokenList.lookup(1).equals(Token.ViewToken)) ||
                // (this.tokenList.lookup(3).equals(Token.ViewToken))
                // || (this.tokenList.lookup(4).equals(Token.ViewToken)) ||
                // (this.tokenList.lookup(5).equals(Token.ViewToken))) {
                // statementList.add(parseCreateView());
                // }
                //
                // if ((this.tokenList.lookup(1).equals(Token.TableToken)) ||
                // (this.tokenList.lookup(3).equals(Token.TableToken))) {
                // statementList.add(parseOracleCreateTable());
                // }

                throw new ParserException("TODO");
            }

            if (lexer.token() == Token.INSERT) {
                statementList.add(parseInsert());
                continue;
            }

            if (lexer.token() == (Token.DELETE)) {
                statementList.add(new OracleDeleteParser(this.lexer).parseDelete());
                continue;
            }

            if (lexer.token() == (Token.SLASH)) {
                lexer.nextToken();
                statementList.add(new OraclePLSQLCommitStatement());
                continue;
            }

            if (lexer.token() == Token.ALTER) {
                throw new ParserException("TODO");
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

            throw new ParserException("TODO : " + lexer.token() + " " + lexer.stringVal());
        }
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
            accept(Token.LPAREN);
            exprParser.exprList(insertClause.getColumns());
            accept(Token.RPAREN);
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
        accept(Token.INTO);

        OracleInsertStatement stmt = new OracleInsertStatement();

        parseInsert0(stmt);

        stmt.setReturning(parseReturningClause());
        stmt.setErrorLogging(parseErrorLoggingClause());

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

    private OracleReturningClause parseReturningClause() {
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
}
