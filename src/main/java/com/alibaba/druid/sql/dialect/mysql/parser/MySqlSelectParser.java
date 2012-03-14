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

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectGroupBy;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlSelectQueryBlock;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class MySqlSelectParser extends SQLSelectParser {

    public MySqlSelectParser(Lexer lexer){
        super(lexer);
    }

    public MySqlSelectParser(String sql) throws ParserException{
        this(new MySqlLexer(sql));
        this.lexer.nextToken();
    }

    @Override
    public SQLSelectQuery query() throws ParserException {
        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }

        MySqlSelectQueryBlock queryBlock = new MySqlSelectQueryBlock();

        if (lexer.token() == Token.SELECT) {
            lexer.nextToken();

            if (lexer.token() == (Token.DISTINCT)) {
                queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                lexer.nextToken();
            } else if (identifierEquals("DISTINCTROW")) {
                queryBlock.setDistionOption(SQLSetQuantifier.DISTINCTROW);
                lexer.nextToken();
            } else if (lexer.token() == (Token.ALL)) {
                queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                lexer.nextToken();
            }

            if (identifierEquals("HIGH_PRIORITY")) {
                queryBlock.setHignPriority(true);
                lexer.nextToken();
            }

            if (identifierEquals("STRAIGHT_JOIN")) {
                queryBlock.setStraightJoin(true);
                lexer.nextToken();
            }

            if (identifierEquals("SQL_SMALL_RESULT")) {
                queryBlock.setSmallResult(true);
                lexer.nextToken();
            }

            if (identifierEquals("SQL_BIG_RESULT")) {
                queryBlock.setBigResult(true);
                lexer.nextToken();
            }

            if (identifierEquals("SQL_BUFFER_RESULT")) {
                queryBlock.setBufferResult(true);
                lexer.nextToken();
            }

            if (identifierEquals("SQL_CACHE")) {
                queryBlock.setCache(true);
                lexer.nextToken();
            }

            if (identifierEquals("SQL_NO_CACHE")) {
                queryBlock.setCache(false);
                lexer.nextToken();
            }

            if (identifierEquals("SQL_CALC_FOUND_ROWS")) {
                queryBlock.setCalcFoundRows(true);
                lexer.nextToken();
            }

            parseSelectList(queryBlock);

            if (lexer.token() == (Token.INTO)) {
                lexer.nextToken();
                acceptIdentifier("OUTFILE");
                SQLExpr outFile = expr();
                queryBlock.setOutFile(outFile);

                if (identifierEquals("FIELDS") || identifierEquals("COLUMNS")) {
                    lexer.nextToken();

                    if (identifierEquals("TERMINATED")) {
                        lexer.nextToken();
                        accept(Token.BY);
                    }
                    queryBlock.setOutFileColumnsTerminatedBy((SQLLiteralExpr) expr());

                    if (identifierEquals("OPTIONALLY")) {
                        lexer.nextToken();
                        queryBlock.setOutFileColumnsEnclosedOptionally(true);
                    }

                    if (identifierEquals("ENCLOSED")) {
                        lexer.nextToken();
                        accept(Token.BY);
                        queryBlock.setOutFileColumnsEnclosedBy((SQLLiteralExpr) expr());
                    }

                    if (identifierEquals("ESCAPED")) {
                        lexer.nextToken();
                        accept(Token.BY);
                        queryBlock.setOutFileColumnsEscaped((SQLLiteralExpr) expr());
                    }
                }

                if (identifierEquals("LINES")) {
                    lexer.nextToken();

                    if (identifierEquals("STARTING")) {
                        lexer.nextToken();
                        accept(Token.BY);
                        queryBlock.setOutFileLinesStartingBy((SQLLiteralExpr) expr());
                    } else {
                        identifierEquals("TERMINATED");
                        lexer.nextToken();
                        accept(Token.BY);
                        queryBlock.setOutFileLinesTerminatedBy((SQLLiteralExpr) expr());
                    }
                }
            }
        }

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseGroupBy(queryBlock);

        queryBlock.setOrderBy(this.createExprParser().parseOrderBy());

        if (lexer.token() == Token.LIMIT) {
            lexer.nextToken();

            MySqlSelectQueryBlock.Limit limit = new MySqlSelectQueryBlock.Limit();

            SQLExpr temp = this.createExprParser().expr();
            if (lexer.token() == (Token.COMMA)) {
                limit.setOffset(temp);
                lexer.nextToken();
                limit.setRowCount(createExprParser().expr());
            } else if (identifierEquals("OFFSET")) {
                limit.setRowCount(temp);
                lexer.nextToken();
                limit.setOffset(createExprParser().expr());
            } else {
                limit.setRowCount(temp);
            }

            queryBlock.setLimit(limit);
        }

        if (identifierEquals("PROCEDURE")) {
            lexer.nextToken();
            throw new ParserException("TODO");
        }

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();
            throw new ParserException("TODO");
        }

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();
            accept(Token.UPDATE);

            queryBlock.setForUpdate(true);
        }

        if (lexer.token() == Token.LOCK) {
            lexer.nextToken();
            accept(Token.IN);
            acceptIdentifier("SHARE");
            acceptIdentifier("MODE");
            queryBlock.setLockInShareMode(true);
        }

        return queryRest(queryBlock);
    }

    protected void parseGroupBy(SQLSelectQueryBlock queryBlock) throws ParserException {
        if (lexer.token() == (Token.GROUP)) {
            lexer.nextToken();
            accept(Token.BY);

            SQLSelectGroupByClause groupBy = new SQLSelectGroupByClause();
            while (true) {
                groupBy.getItems().add(this.createExprParser().expr());
                if (!(lexer.token() == (Token.COMMA))) break;
                lexer.nextToken();
            }

            if (identifierEquals("WITH")) {
                lexer.nextToken();
                acceptIdentifier("ROLLUP");

                MySqlSelectGroupBy mySqlGroupBy = new MySqlSelectGroupBy();
                mySqlGroupBy.getItems().addAll(groupBy.getItems());
                mySqlGroupBy.setRollUp(true);

                groupBy = mySqlGroupBy;
            }

            if (lexer.token() == Token.HAVING) {
                lexer.nextToken();

                groupBy.setHaving(this.createExprParser().expr());
            }

            queryBlock.setGroupBy(groupBy);
        }
    }

    @Override
    protected MySqlExprParser createExprParser() {
        return new MySqlExprParser(lexer);
    }
}
