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
package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaInsertStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaAlterTableStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaMetaStatement;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaUpdateStatements;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.JdbcConstants;

import java.util.List;

public class ImpalaStatementParser extends SQLStatementParser {



    public ImpalaStatementParser(String sql) {
        super (new ImpalaExprParser(sql));
    }

    public ImpalaStatementParser(String sql, SQLParserFeature... features) {
        super (new ImpalaExprParser(sql, features));
    }

    public ImpalaStatementParser(Lexer lexer){
        super(new ImpalaExprParser(lexer));
    }

    public ImpalaSelectParser createSQLSelectParser() {
        return new ImpalaSelectParser(this.exprParser, selectListCache);
    }

    public SQLStatement parseMeta(){
        ImpalaMetaStatement stmt = new ImpalaMetaStatement(lexer.token());
        if (lexer.token() == Token.INVALIDATE){
            accept(Token.INVALIDATE);
            accept(Token.METADATA);
            if (lexer.token() != Token.SEMI) {
                stmt.setTableSource(this.exprParser.name());
            }
        }else if (lexer.token() == Token.REFRESH){
            accept(Token.REFRESH);
            stmt.setTableSource(this.exprParser.name());
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
                    stmt.addPartition(ptExpr);
                    if (!(lexer.token() == (Token.COMMA))) {
                        break;
                    } else {
                        lexer.nextToken();
                    }
                }
                accept(Token.RPAREN);
            }
        } else{
            accept(Token.COMPUTE);
            if (lexer.token() == Token.INCREMENTAL){
                accept(Token.INCREMENTAL);
                stmt.setIncremental(true);
                accept(Token.STATS);
                stmt.setTableSource(this.exprParser.name());
                if (lexer.token() == Token.PARTITION){
                    accept(Token.PARTITION);
                    SQLExpr partition = this.exprParser.expr();
                    stmt.setComputePartition(partition);
                }
            }
            else{
                accept(Token.STATS);
                stmt.setTableSource(this.exprParser.name());
                if (lexer.token() == Token.LPAREN){
                    lexer.nextToken();
                    List<SQLExpr> columns = stmt.getColumns();
                    if (lexer.token() != Token.RPAREN) {
                        for (; ; ) {
                            String identName;
                            long hash;

                            Token token = lexer.token();
                            if (token == Token.IDENTIFIER) {
                                identName = lexer.stringVal();
                                hash = lexer.hash_lower();
                            } else if (token == Token.LITERAL_CHARS) {
                                identName = '\'' + lexer.stringVal() + '\'';
                                hash = 0;
                            } else {
                                identName = lexer.stringVal();
                                hash = 0;
                            }
                            lexer.nextTokenComma();
                            SQLExpr expr = new SQLIdentifierExpr(identName, hash);
                            while (lexer.token() == Token.DOT) {
                                lexer.nextToken();
                                String propertyName = lexer.stringVal();
                                lexer.nextToken();
                                expr = new SQLPropertyExpr(expr, propertyName);
                            }

                            expr.setParent(stmt);
                            columns.add(expr);

                            if (lexer.token() == Token.COMMA) {
                                lexer.nextTokenIdent();
                                continue;
                            }

                            break;
                        }
                    }
                    accept(Token.RPAREN);
                }

            }

        }
        return stmt;
    }

    public SQLUpdateStatement parseUpdateStatement() {
        ImpalaUpdateStatements updateStatement = createUpdateStatement();

        if (lexer.token() == Token.UPDATE) {
            lexer.nextToken();

            SQLTableSource tableSource = this.exprParser.createSelectParser().parseTableSource();
            updateStatement.setTableSource(tableSource);
        }

        parseUpdateSet(updateStatement);

        if (lexer.token() == Token.FROM){
            lexer.nextToken();
            SQLJoinTableSource join = new SQLJoinTableSource();

            SQLExprTableSource tableReference = new SQLExprTableSource();
            tableReference.setExpr(this.getExprParser().expr());
            if (lexer.token() != Token.JOIN){
                tableReference.setAlias(lexer.stringVal());
                lexer.nextToken();
            }
            join.setLeft(tableReference);
            accept(Token.JOIN);
            join.setJoinType(SQLJoinTableSource.JoinType.JOIN);

            SQLExprTableSource rightTableReference = new SQLExprTableSource();
            rightTableReference.setExpr(this.getExprParser().expr());

            if (lexer.token() != Token.ON){
                rightTableReference.setAlias(lexer.stringVal());
                lexer.nextToken();
            }
            join.setRight(rightTableReference);
            accept(Token.ON);
            SQLExpr joinOn = this.exprParser.expr();
            join.setCondition(joinOn);

            while (lexer.token() == Token.ON) {
                lexer.nextToken();

                SQLExpr joinOn2 = this.exprParser.expr();
                join.addCondition(joinOn2);
            }
            updateStatement.setJoin(join);

        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            updateStatement.setWhere(this.exprParser.expr());
        }

        return updateStatement;
    }


    public SQLStatement parseMerge() {
        accept(Token.MERGE);
        accept(Token.INTO);

        SQLReplaceStatement stmt = new SQLReplaceStatement();
        stmt.setDbType(JdbcConstants.IMPALA);

        SQLName tableName = exprParser.name();
        stmt.setTableName(tableName);

        if (lexer.token() == Token.KEY) {
            lexer.nextToken();
            accept(Token.LPAREN);
            this.exprParser.exprList(stmt.getColumns(), stmt);
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.VALUES || lexer.identifierEquals("VALUE")) {
            lexer.nextToken();

            parseValueClause(stmt.getValuesList(), 0, stmt);
        } else if (lexer.token() == Token.SELECT) {
            SQLQueryExpr queryExpr = (SQLQueryExpr) this.exprParser.expr();
            stmt.setQuery(queryExpr);
        } else if (lexer.token() == Token.LPAREN) {
            SQLSelect select = this.createSQLSelectParser().select();
            SQLQueryExpr queryExpr = new SQLQueryExpr(select);
            stmt.setQuery(queryExpr);
        }

        return stmt;
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new ImpalaCreateTableParser(this.exprParser);
    }

    public SQLStatement parseInsert() {
        ImpalaInsertStatement insert = new ImpalaInsertStatement();

        if (lexer.isKeepComments() && lexer.hasComment()) {
            insert.addBeforeComment(lexer.readAndResetComments());
        }

        SQLSelectParser selectParser = createSQLSelectParser();

        accept(Token.INSERT);

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();
        } else {
            accept(Token.OVERWRITE);
            insert.setOverwrite(true);
        }

        if (lexer.token() == Token.TABLE) {
            accept(Token.TABLE);
        }
        insert.setTableSource(this.exprParser.name());

        int columnSize = 0;

        if (lexer.token() == Token.LPAREN){
            lexer.nextToken();
            List<SQLExpr> columns = insert.getColumns();
            if (lexer.token() != Token.RPAREN) {
                for (; ; ) {
                    String identName;
                    long hash;

                    Token token = lexer.token();
                    if (token == Token.IDENTIFIER) {
                        identName = lexer.stringVal();
                        hash = lexer.hash_lower();
                    } else if (token == Token.LITERAL_CHARS) {
                        identName = '\'' + lexer.stringVal() + '\'';
                        hash = 0;
                    } else {
                        identName = lexer.stringVal();
                        hash = 0;
                    }
                    lexer.nextTokenComma();
                    SQLExpr expr = new SQLIdentifierExpr(identName, hash);
                    while (lexer.token() == Token.DOT) {
                        lexer.nextToken();
                        String propertyName = lexer.stringVal();
                        lexer.nextToken();
                        expr = new SQLPropertyExpr(expr, propertyName);
                    }

                    expr.setParent(insert);
                    columns.add(expr);
                    columnSize++;

                    if (lexer.token() == Token.COMMA) {
                        lexer.nextTokenIdent();
                        continue;
                    }

                    break;
                }
                columnSize = insert.getColumns().size();
            }
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.LINE_COMMENT) {
            lexer.nextToken();
        }

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
                insert.addPartition(ptExpr);
                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                } else {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.LINE_COMMENT) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.VALUES) {
            lexer.nextTokenLParen();
            parseValueClause(insert.getValuesList(), columnSize, insert);
        } else {
            SQLSelect query = selectParser.select();
            insert.setQuery(query);
        }

        return insert;
    }


    public SQLStatement parseAlter() {
        ImpalaAlterTableStatement stmt = null;
        accept(Token.ALTER);
        if (lexer.token() == Token.TABLE){
            lexer.nextToken();
            stmt = new ImpalaAlterTableStatement();
            stmt.setName(this.exprParser.name());
            if (lexer.token() == Token.RENAME) {
                accept(Token.RENAME);
                accept(Token.TO);
                stmt.setTableSource(new SQLExprTableSource(this.exprParser.name()));
            }else if (lexer.identifierEquals("ADD")) {
                stmt.setAlterType("ADD");
                lexer.nextToken();
                if (lexer.token() == Token.IF) {
                    accept(Token.IF);
                    accept(Token.NOT);
                    accept(Token.EXISTS);
                    stmt.setNotExists(true);
                }
                parseAlterColumnOrPartition(stmt);
            }else if (lexer.token() == Token.DROP) {
                stmt.setAlterType("DROP");
                lexer.nextToken();
                if (lexer.token() == Token.IF) {
                    accept(Token.IF);
                    accept(Token.EXISTS);
                    stmt.setExists(true);
                }
                parseAlterColumnOrPartition(stmt);
            }else{
                    throw new ParserException("sql syntax error, terminated. " + lexer.token());
            }


            }

        return stmt;
    }

    private void parseAlterColumnOrPartition(ImpalaAlterTableStatement stmt){
        if (lexer.identifierEquals("COLUMNS")) {
            lexer.nextToken();
            SQLAlterTableAddColumn column = parseAlterTableAddColumn();
            stmt.addItem(column);
        } else {
            if (lexer.token() == Token.RANGE) {
                lexer.nextToken();
            }
            lexer.nextToken();
            lexer.nextTokenLParen();
            while (lexer.token() != Token.RPAREN) {
                SQLExpr partition = this.exprParser.expr();
                stmt.getPartitions().add(partition);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
        }
    }


    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.FROM) {
            SQLStatement stmt = this.parseInsert();
            statementList.add(stmt);
            return true;
        }

        return false;
    }

    protected ImpalaUpdateStatements createUpdateStatement() {
        return new ImpalaUpdateStatements();
    }
}
