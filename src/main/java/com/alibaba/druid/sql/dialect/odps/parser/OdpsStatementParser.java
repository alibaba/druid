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
package com.alibaba.druid.sql.dialect.odps.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLSetStatement;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsInsert;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsInsertStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSetLabelStatement;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsShowPartitionsStmt;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsShowStatisticStmt;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;

public class OdpsStatementParser extends SQLStatementParser {

    public OdpsStatementParser(String sql){
        super(new OdpsExprParser(sql));
    }

    public OdpsStatementParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public SQLSelectStatement parseSelect() {
        OdpsSelectParser selectParser = new OdpsSelectParser(this.exprParser);
        return new SQLSelectStatement(selectParser.select(), JdbcConstants.ODPS);
    }

    public SQLCreateTableStatement parseCreateTable() {
        SQLCreateTableParser parser = new OdpsCreateTableParser(this.exprParser);
        return parser.parseCrateTable();
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new OdpsCreateTableParser(this.exprParser);
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.FROM) {
            SQLStatement stmt = this.parseInsert();
            statementList.add(stmt);
            return true;
        }
        return false;
    }

    public SQLStatement parseInsert() {
        OdpsInsertStatement stmt = new OdpsInsertStatement();

        if (lexer.token() == Token.FROM) {
            lexer.nextToken();
            accept(Token.LPAREN);

            SQLSelectParser selectParser = createSQLSelectParser();
            SQLSelect select = selectParser.select();

            accept(Token.RPAREN);

            String alias = lexer.stringVal();
            accept(Token.IDENTIFIER);

            SQLSubqueryTableSource from = new SQLSubqueryTableSource(select, alias);

            stmt.setFrom(from);
        }

        for (;;) {
            OdpsInsert insert = parseOdpsInsert();
            stmt.getItems().add(insert);

            if (lexer.token() != Token.INSERT) {
                break;
            }
        }

        return stmt;
    }

    public OdpsInsert parseOdpsInsert() {
        OdpsInsert insert = new OdpsInsert();
        SQLSelectParser selectParser = createSQLSelectParser();

        accept(Token.INSERT);

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();
        } else {
            accept(Token.OVERWRITE);
            insert.setOverwrite(true);
        }

        accept(Token.TABLE);
        insert.setTableSource(this.exprParser.name());

        if (lexer.token() == Token.PARTITION) {
            lexer.nextToken();
            accept(Token.LPAREN);
            for (;;) {
                SQLAssignItem ptExpr = new SQLAssignItem();
                ptExpr.setTarget(this.exprParser.name());
                if (lexer.token() == Token.EQ) {
                    lexer.nextToken();
                    SQLExpr ptValue = this.exprParser.expr();
                    ptExpr.setValue(ptValue);
                }
                insert.getPartitions().add(ptExpr);
                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                } else {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        SQLSelect query = selectParser.select();
        insert.setQuery(query);

        return insert;
    }

    public SQLStatement parseShow() {
        accept(Token.SHOW);

        if (identifierEquals("PARTITIONS")) {
            lexer.nextToken();

            OdpsShowPartitionsStmt stmt = new OdpsShowPartitionsStmt();

            SQLExpr expr = this.exprParser.expr();
            stmt.setTableSource(new SQLExprTableSource(expr));

            return stmt;
        }

        if (identifierEquals("STATISTIC")) {
            lexer.nextToken();

            OdpsShowStatisticStmt stmt = new OdpsShowStatisticStmt();

            SQLExpr expr = this.exprParser.expr();
            stmt.setTableSource(new SQLExprTableSource(expr));

            return stmt;
        }
        throw new ParserException("TODO " + lexer.token() + " " + lexer.stringVal());
    }

    public SQLStatement parseSet() {
        accept(Token.SET);

        if (identifierEquals("LABEL")) {
            lexer.nextToken();
            OdpsSetLabelStatement stmt = new OdpsSetLabelStatement();
            stmt.setLabel(lexer.stringVal());
            lexer.nextToken();
            accept(Token.TO);
            if (lexer.token() == Token.USER) {
                lexer.nextToken();

                SQLName name = this.exprParser.name();
                stmt.setUser(name);
                return stmt;
            }
            accept(Token.TABLE);
            SQLExpr expr = this.exprParser.name();
            stmt.setTable(new SQLExprTableSource(expr));

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                this.exprParser.names(stmt.getColumns(), stmt);
                accept(Token.RPAREN);
            }

            return stmt;
        } else {
            SQLSetStatement stmt = new SQLSetStatement(getDbType());

            parseAssignItems(stmt.getItems(), stmt);

            return stmt;
        }
    }

}
