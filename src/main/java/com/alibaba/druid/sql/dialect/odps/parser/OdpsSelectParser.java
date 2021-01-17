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
package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

public class OdpsSelectParser extends SQLSelectParser {
    public OdpsSelectParser(SQLExprParser exprParser){
        super(exprParser.getLexer());
        this.exprParser = exprParser;
    }

    public OdpsSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache){
        super(exprParser.getLexer());
        this.exprParser = exprParser;
        this.selectListCache = selectListCache;
    }

    @Override
    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select, acceptUnion);
        }

        OdpsSelectQueryBlock queryBlock = new OdpsSelectQueryBlock();
        
        if (lexer.hasComment() && lexer.isKeepComments()) {
            queryBlock.addBeforeComment(lexer.readAndResetComments());
        }

        if (lexer.token() == Token.FROM) {
            parseFrom(queryBlock);
            parseWhere(queryBlock);
            parseGroupBy(queryBlock);

            if (lexer.token() == Token.SELECT) {
                lexer.nextToken();

                if (lexer.token() == Token.HINT) {
                    this.exprParser.parseHints(queryBlock.getHints());
                }

                if (lexer.token() == Token.COMMENT) {
                    lexer.nextToken();
                }

                if (lexer.token() == Token.DISTINCT) {
                    queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                    lexer.nextToken();
                } else if (lexer.token() == Token.UNIQUE) {
                    queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
                    lexer.nextToken();
                } else if (lexer.token() == Token.ALL) {
                    String str = lexer.stringVal();
                    lexer.nextToken();
                    if (lexer.token() == Token.DOT) {

                    }
                    queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                }

                parseSelectList(queryBlock);
            }

            if (queryBlock.getWhere() == null && lexer.token() == Token.WHERE) {
                parseWhere(queryBlock);
            }
        } else {
            accept(Token.SELECT);

            if (lexer.token() == Token.HINT) {
                this.exprParser.parseHints(queryBlock.getHints());
            }

            if (lexer.token() == Token.COMMENT) {
                String tokenStr = lexer.stringVal();
                lexer.nextToken();
                if (lexer.token() == Token.COMMA) {
                    queryBlock.addSelectItem(new SQLIdentifierExpr(tokenStr));
                    lexer.nextToken();
                }
            }

            if (queryBlock.getSelectList().isEmpty()) {
                if (lexer.token() == Token.DISTINCT) {
                    queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                    lexer.nextToken();
                } else if (lexer.token() == Token.UNIQUE) {
                    queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
                    lexer.nextToken();
                } else if (lexer.token() == Token.ALL) {
                    Lexer.SavePoint mark = lexer.mark();
                    lexer.nextToken();
                    if (lexer.token() == Token.DOT) {
                        lexer.reset(mark);
                    } else {
                        queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                    }
                }
            }

            parseSelectList(queryBlock);

            parseFrom(queryBlock);
            if (queryBlock.getFrom() == null && lexer.token() == Token.LATERAL) {
                lexer.nextToken();
                SQLTableSource tableSource = this.parseLateralView(null);
                queryBlock.setFrom(tableSource);
            }

            parseWhere(queryBlock);
            parseGroupBy(queryBlock);
        }

        if (lexer.identifierEquals(FnvHash.Constants.WINDOW)) {
            parseWindow(queryBlock);
        }

        parseGroupBy(queryBlock);

        queryBlock.setOrderBy(this.exprParser.parseOrderBy());
        
        if (lexer.token() == Token.DISTRIBUTE) {
            lexer.nextToken();
            accept(Token.BY);

            for (;;) {
                SQLSelectOrderByItem distributeByItem = this.exprParser.parseSelectOrderByItem();
                queryBlock.addDistributeBy(distributeByItem);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.SORT)) {
            lexer.nextToken();
            accept(Token.BY);

            for (;;) {
                SQLSelectOrderByItem sortByItem = this.exprParser.parseSelectOrderByItem();
                queryBlock.addSortBy(sortByItem);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.CLUSTER)) {
            lexer.nextToken();
            accept(Token.BY);

            for (;;) {
                SQLSelectOrderByItem clusterByItem = this.exprParser.parseSelectOrderByItem();
                queryBlock.addClusterBy(clusterByItem);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }
        }

        if (lexer.token() == Token.LIMIT) {
            SQLLimit limit = exprParser.parseLimit();
            queryBlock.setLimit(limit);
        }

        return queryRest(queryBlock, acceptUnion);
    }

    public SQLTableSource parseTableSource() {
        if (lexer.token() == Token.NULL) {
            String str = lexer.stringVal();
            lexer.nextToken();
            return new SQLExprTableSource(new SQLIdentifierExpr(str));
        }
        SQLTableSource tableSource = super.parseTableSource();

        if (lexer.token() == Token.TABLE && tableSource.getAlias() == null) {
            tableSource.setAlias(
                    lexer.stringVal()
            );
            lexer.nextToken();

            tableSource = parseTableSourceRest(tableSource);
        }

        return tableSource;
    }

    protected SQLTableSource primaryTableSourceRest(SQLTableSource tableSource) {
        if (lexer.identifierEquals(FnvHash.Constants.LATERAL) || lexer.token() == Token.LATERAL) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();
            if (lexer.token() == Token.VIEW) {
                tableSource = parseLateralView(tableSource);
            } else {
                lexer.reset(mark);
            }
        }
        return tableSource;
    }
}
