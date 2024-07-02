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
package com.alibaba.druid.sql.dialect.presto.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

/**
 * presto的 选择解析器
 * <p>
 * author zhangcanlong
 * date 2022/01/11
 */
public class PrestoSelectParser extends SQLSelectParser {
    public PrestoSelectParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public PrestoSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }

    public PrestoSelectParser(String sql) {
        this(new PrestoExprParser(sql));
    }

    protected SQLExprParser createExprParser() {
        return new PrestoExprParser(this.lexer);
    }

    @Override
    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (this.lexer.token() == Token.LPAREN) {
            this.lexer.nextToken();

            SQLSelectQuery select = this.query();
            this.accept(Token.RPAREN);

            return this.queryRest(select, acceptUnion);
        }

        if (this.lexer.token() == Token.VALUES) {
            return this.valuesQuery(acceptUnion);
        }

        SQLSelectQueryBlock queryBlock = new SQLSelectQueryBlock(this.dbType);

        if (this.lexer.hasComment() && this.lexer.isKeepComments()) {
            queryBlock.addBeforeComment(this.lexer.readAndResetComments());
        }

        this.accept(Token.SELECT);

        if (this.lexer.token() == Token.HINT) {
            this.exprParser.parseHints(queryBlock.getHints());
        }

        if (this.lexer.token() == Token.COMMENT) {
            this.lexer.nextToken();
        }

        if (this.lexer.token() == Token.DISTINCT) {
            queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
            this.lexer.nextToken();
        } else if (this.lexer.token() == Token.UNIQUE) {
            queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
            this.lexer.nextToken();
        } else if (this.lexer.token() == Token.ALL) {
            queryBlock.setDistionOption(SQLSetQuantifier.ALL);
            this.lexer.nextToken();
        }

        this.parseSelectList(queryBlock);

        if (this.lexer.token() == Token.INTO) {
            this.lexer.nextToken();

            SQLExpr expr = this.expr();
            if (this.lexer.token() != Token.COMMA) {
                queryBlock.setInto(expr);
            }
        }

        this.parseFrom(queryBlock);

        this.parseWhere(queryBlock);

        this.parseGroupBy(queryBlock);

        if (this.lexer.identifierEquals(FnvHash.Constants.WINDOW)) {
            this.parseWindow(queryBlock);
        }

        this.parseSortBy(queryBlock);

        this.parseFetchClause(queryBlock);

        if (this.lexer.token() == Token.FOR) {
            this.lexer.nextToken();
            this.accept(Token.UPDATE);

            queryBlock.setForUpdate(true);

            if (this.lexer.identifierEquals(FnvHash.Constants.NO_WAIT) || this.lexer.identifierEquals(FnvHash.Constants.NOWAIT)) {
                this.lexer.nextToken();
                queryBlock.setNoWait(true);
            } else if (this.lexer.identifierEquals(FnvHash.Constants.WAIT)) {
                this.lexer.nextToken();
                SQLExpr waitTime = this.exprParser.primary();
                queryBlock.setWaitTime(waitTime);
            }
        }

        return this.queryRest(queryBlock, acceptUnion);
    }

    @Override
    public void parseFetchClause(SQLSelectQueryBlock queryBlock) {
        // 如果是presto，则先解析 offset 再解析limit
        if (this.lexer.identifierEquals(FnvHash.Constants.OFFSET) || this.lexer.token() == Token.OFFSET) {
            this.lexer.nextToken();
            SQLExpr offset = this.exprParser.expr();
            queryBlock.setOffset(offset);
            if (this.lexer.identifierEquals(FnvHash.Constants.ROW) || this.lexer.identifierEquals(FnvHash.Constants.ROWS)) {
                this.lexer.nextToken();
            }
        }

        if (this.lexer.token() == Token.LIMIT) {
            SQLLimit limit = queryBlock.getLimit();
            // 原始的limit
            SQLLimit originLimit = this.exprParser.parseLimit();
            if (limit == null) {
                limit = originLimit;
            }
            limit.setRowCount(originLimit.getRowCount());
            queryBlock.setLimit(limit);
            return;
        }

        if (this.lexer.token() == Token.FETCH) {
            this.lexer.nextToken();
            if (this.lexer.token() == Token.FIRST || this.lexer.token() == Token.NEXT || this.lexer.identifierEquals(FnvHash.Constants.NEXT)) {
                this.lexer.nextToken();
            } else {
                this.acceptIdentifier("FIRST");
            }
            SQLExpr first = this.exprParser.primary();
            queryBlock.setFirst(first);
            if (this.lexer.identifierEquals(FnvHash.Constants.ROW) || this.lexer.identifierEquals(FnvHash.Constants.ROWS)) {
                this.lexer.nextToken();
            }

            if (this.lexer.token() == Token.ONLY) {
                this.lexer.nextToken();
            } else {
                this.acceptIdentifier("ONLY");
            }
        }
    }
}
