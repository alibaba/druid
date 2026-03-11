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
package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleSelectQueryBlock;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleSelectParser;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.Token;

public class DMSelectParser extends OracleSelectParser {
    public static final String ATTRIBUTE_DM_TOP = "dm.top";

    public DMSelectParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public DMSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }

    @Override
    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);
            select.setParenthesized(true);

            return queryRest(select, acceptUnion);
        }

        OracleSelectQueryBlock queryBlock = new OracleSelectQueryBlock();
        if (lexer.hasComment() && lexer.isKeepComments()) {
            queryBlock.addBeforeComment(lexer.readAndResetComments());
        }

        if (lexer.token() == Token.SELECT) {
            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.HINT) {
                this.exprParser.parseHints(queryBlock.getHints());
            }

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

            if (lexer.token() == Token.TOP) {
                parseTopAsLimit(queryBlock);
            }

            this.exprParser.parseHints(queryBlock.getHints());

            parseSelectList(queryBlock);
        }

        parseInto(queryBlock);

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseHierachical(queryBlock);

        parseGroupBy(queryBlock);

        parseHierachical(queryBlock);

        parseFetchClause(queryBlock);

        return queryRest(queryBlock, acceptUnion);
    }

    private void parseTopAsLimit(OracleSelectQueryBlock queryBlock) {
        lexer.nextToken(); // consume TOP

        if (lexer.token() != Token.LITERAL_INT) {
            throw new ParserException("syntax error, expect integer after TOP. " + lexer.info());
        }

        int first = lexer.integerValue().intValue();
        lexer.nextToken();

        SQLLimit limit = new SQLLimit();
        if (lexer.token() == Token.COMMA) {
            // TOP offset,count
            lexer.nextToken();
            if (lexer.token() != Token.LITERAL_INT) {
                throw new ParserException("syntax error, expect integer after comma in TOP clause. " + lexer.info());
            }
            int second = lexer.integerValue().intValue();
            lexer.nextToken();

            limit.setOffset(new SQLIntegerExpr(first));
            limit.setRowCount(new SQLIntegerExpr(second));
        } else {
            // TOP count (no offset)
            limit.setRowCount(new SQLIntegerExpr(first));
        }

        queryBlock.setLimit(limit);
        queryBlock.putAttribute(ATTRIBUTE_DM_TOP, Boolean.TRUE);
    }
}
