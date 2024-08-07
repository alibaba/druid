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
package com.alibaba.druid.sql.dialect.db2.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock;
import com.alibaba.druid.sql.dialect.db2.ast.stmt.DB2SelectQueryBlock.Isolation;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

public class DB2SelectParser extends SQLSelectParser {
    public DB2SelectParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.db2;
    }

    public DB2SelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        dbType = DbType.db2;
    }

    public DB2SelectParser(String sql) {
        this(new DB2ExprParser(sql));
    }

    protected SQLExprParser createExprParser() {
        return new DB2ExprParser(lexer);
    }

    @Override
    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select, acceptUnion);
        }

        accept(Token.SELECT);

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
        }

        DB2SelectQueryBlock queryBlock = new DB2SelectQueryBlock();

        if (lexer.token() == Token.DISTINCT) {
            queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
            lexer.nextToken();
        } else if (lexer.token() == Token.UNIQUE) {
            queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
            lexer.nextToken();
        } else if (lexer.token() == Token.ALL) {
            queryBlock.setDistionOption(SQLSetQuantifier.ALL);
            lexer.nextToken();
        }

        parseSelectList(queryBlock);

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();

            SQLExpr expr = expr();
            if (lexer.token() != Token.COMMA) {
                queryBlock.setInto(expr);
            }
        }

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseHierachical(queryBlock);

        parseGroupBy(queryBlock);

        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = parseOrderBy();
            queryBlock.setOrderBy(orderBy);
        }

        for (; ; ) {
            if (lexer.token() == Token.FETCH) {
                lexer.nextToken();
                accept(Token.FIRST);
                SQLExpr first = this.exprParser.primary();
                queryBlock.setFirst(first);
                if (lexer.identifierEquals("ROW") || lexer.identifierEquals("ROWS")) {
                    lexer.nextToken();
                }
                accept(Token.ONLY);
                continue;
            }

            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                if (lexer.identifierEquals("RR")) {
                    queryBlock.setIsolation(Isolation.RR);
                    parseLockRequest(queryBlock);
                } else if (lexer.identifierEquals("RS")) {
                    queryBlock.setIsolation(Isolation.RS);
                    parseLockRequest(queryBlock);
                } else if (lexer.identifierEquals("CS")) {
                    queryBlock.setIsolation(Isolation.CS);
                } else if (lexer.identifierEquals("UR")) {
                    queryBlock.setIsolation(Isolation.UR);
                } else {
                    throw new ParserException("TODO. " + lexer.info());
                }
                lexer.nextToken();
                continue;
            }

            if (lexer.token() == Token.FOR) {
                lexer.nextToken();

                if (lexer.token() == Token.UPDATE) {
                    queryBlock.setForUpdate(true);
                    lexer.nextToken();
                } else {
                    acceptIdentifier("READ");
                    accept(Token.ONLY);
                    queryBlock.setForReadOnly(true);
                }
                continue;
            }

            if (lexer.token() == Token.OPTIMIZE) {
                lexer.nextToken();
                accept(Token.FOR);

                queryBlock.setOptimizeFor(this.expr());
                if (lexer.identifierEquals("ROW")) {
                    lexer.nextToken();
                } else {
                    acceptIdentifier("ROWS");
                }
            }

            break;
        }

        return queryRest(queryBlock, acceptUnion);
    }

    private void parseLockRequest(DB2SelectQueryBlock queryBlock) {
        lexer.nextToken();
        accept(Token.USE);
        accept(Token.AND);
        if (!lexer.identifierEquals("KEEP")) {
            throw new ParserException("TODO. " + lexer.info());
        }
        lexer.nextToken();
        DB2SelectQueryBlock.LockRequest lockRequest = null;
        switch (lexer.token()) {
            case SHARE: {
                lockRequest = DB2SelectQueryBlock.LockRequest.SHARE;
                break;
            }
            case UPDATE: {
                lockRequest = DB2SelectQueryBlock.LockRequest.UPDATE;
                break;
            }
            case EXCLUSIVE: {
                lockRequest = DB2SelectQueryBlock.LockRequest.EXCLUSIVE;
                break;
            }
            default:
                throw new ParserException("TODO. " + lexer.info());
        }
        lexer.nextToken();
        if (lexer.identifierEquals("LOCKS")) {
            queryBlock.setLockRequest(lockRequest);
        } else {
            throw new ParserException("TODO. " + lexer.info());
        }
    }

    @Override
    protected void parseOrderByWith(SQLSelectGroupByClause groupBy, SQLSelectQueryBlock queryBlock) {
        Lexer.SavePoint mark = lexer.mark();
        lexer.nextToken();

        if (lexer.identifierEquals(FnvHash.Constants.CUBE)) {
            lexer.nextToken();
            groupBy.setWithCube(true);
        } else if (lexer.identifierEquals(FnvHash.Constants.ROLLUP)) {
            lexer.nextToken();
            groupBy.setWithRollUp(true);
        } else if (lexer.identifierEquals(FnvHash.Constants.RS)) {
            lexer.nextToken();
            ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.RS);
        } else if (lexer.identifierEquals(FnvHash.Constants.RR)) {
            lexer.nextToken();
            ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.RR);
        } else if (lexer.identifierEquals(FnvHash.Constants.CS)) {
            lexer.nextToken();
            ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.CS);
        } else if (lexer.identifierEquals(FnvHash.Constants.UR)) {
            lexer.nextToken();
            ((DB2SelectQueryBlock) queryBlock).setIsolation(DB2SelectQueryBlock.Isolation.UR);
        } else {
            lexer.reset(mark);
        }
    }
}
