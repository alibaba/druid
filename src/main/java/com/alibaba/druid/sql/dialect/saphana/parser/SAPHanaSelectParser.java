/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.alibaba.druid.sql.dialect.saphana.parser;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLUnionQuery;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaSelectQueryBlock;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

/**
 * @author nukiyoam
 */
public class SAPHanaSelectParser extends SQLSelectParser {
    public SAPHanaSelectParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SAPHanaSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }

    public SAPHanaSelectParser(String sql) {
        this(new SAPHanaExprParser(sql));
    }

    @Override
    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            select.setParenthesized(true);
            accept(Token.RPAREN);

            return queryRest(select, acceptUnion);
        }

        SAPHanaSelectQueryBlock queryBlock = new SAPHanaSelectQueryBlock();
        queryBlock.setParent(parent);

        if (lexer.token() == Token.SELECT) {
            if (selectListCache != null) {
                selectListCache.match(lexer, queryBlock);
            }
        }

        if (lexer.token() == Token.SELECT) {
            lexer.nextTokenValue();

            while (true) {
                Token token = lexer.token();
                if (token == (Token.DISTINCT)) {
                    queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.DISTINCTROW)) {
                    queryBlock.setDistionOption(SQLSetQuantifier.DISTINCTROW);
                    lexer.nextToken();
                } else if (token == (Token.ALL)) {
                    queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                    lexer.nextToken();
                } else if (token == (Token.UNIQUE)) {
                    queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
                    lexer.nextToken();
                } else if (lexer.identifierEquals(FnvHash.Constants.TOP)) {
                    Lexer.SavePoint mark = lexer.mark();

                    lexer.nextToken();
                    if (lexer.token() == Token.LITERAL_INT) {
                        SQLLimit limit = new SQLLimit(lexer.integerValue().intValue());
                        queryBlock.setLimit(limit);
                        lexer.nextToken();
                    } else if (lexer.token() == Token.DOT) {
                        lexer.reset(mark);
                        break;
                    }
                } else {
                    break;
                }
            }

            parseSelectList(queryBlock);

            parseInto(queryBlock);
        }

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseHierachical(queryBlock);

        if (lexer.token() == Token.GROUP || lexer.token() == Token.HAVING) {
            parseGroupBy(queryBlock);
        }

        if (lexer.identifierEquals(FnvHash.Constants.WINDOW)) {
            parseWindow(queryBlock);
        }

        if (lexer.token() == Token.ORDER) {
            queryBlock.setOrderBy(this.exprParser.parseOrderBy());
        }

        if (lexer.token() == Token.LIMIT) {
            queryBlock.setLimit(this.exprParser.parseLimit());
        }

        if (lexer.token() == Token.INTO) {
            parseInto(queryBlock);
        }

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();

            if (lexer.token() == Token.UPDATE) {
                lexer.nextToken();
                queryBlock.setForUpdate(true);

                if (lexer.identifierEquals(FnvHash.Constants.NO_WAIT)
                        || lexer.identifierEquals(FnvHash.Constants.NOWAIT)) {
                    lexer.nextToken();
                    queryBlock.setNoWait(true);
                } else if (lexer.identifierEquals(FnvHash.Constants.WAIT)) {
                    lexer.nextToken();
                    SQLExpr waitTime = this.exprParser.primary();
                    queryBlock.setWaitTime(waitTime);
                }

                if (lexer.identifierEquals(FnvHash.Constants.SKIP)) {
                    lexer.nextToken();
                    acceptIdentifier("LOCKED");
                    queryBlock.setSkipLocked(true);
                }
            } else {
                acceptIdentifier("SHARE");
                queryBlock.setForShare(true);
            }
        }

        return queryRest(queryBlock, acceptUnion);
    }

    protected void parseInto(SQLSelectQueryBlock queryBlock) {
        if (lexer.token() != Token.INTO) {
            return;
        }

        lexer.nextToken();

        SQLExpr intoExpr = this.exprParser.name();
        if (lexer.token() == Token.COMMA) {
            SQLListExpr list = new SQLListExpr();
            list.addItem(intoExpr);

            while (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                SQLName name = this.exprParser.name();
                list.addItem(name);
            }

            intoExpr = list;
        }
        queryBlock.setInto(intoExpr);
    }

    public SQLUnionQuery unionRest(SQLUnionQuery union) {
        if (lexer.token() == Token.LIMIT) {
            union.setLimit(this.exprParser.parseLimit());
        }
        return super.unionRest(union);
    }

}
