package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.dialect.dm.ast.stmt.DmSelectQueryBlock;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

public class DmSelectParser extends SQLSelectParser {
    public DmSelectParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public DmSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }

    public DmSelectParser(String sql) {
        this(new DmExprParser(sql));
    }

    protected DmExprParser createExprParser() {
        return new DmExprParser(lexer);
    }

    @Override
    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (lexer.token() == Token.VALUES) {
            return valuesQuery(acceptUnion);
        }

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            if (select instanceof SQLSelectQueryBlock) {
                ((SQLSelectQueryBlock) select).setParenthesized(true);
            }
            accept(Token.RPAREN);
            select.setParenthesized(true);

            return queryRest(select, acceptUnion);
        }

        DmSelectQueryBlock queryBlock = new DmSelectQueryBlock();

        if (lexer.hasComment() && lexer.isKeepComments()) {
            queryBlock.addBeforeComment(lexer.readAndResetComments());
        }

        if (lexer.token() == Token.SELECT) {
            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.DISTINCT) {
                queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                lexer.nextToken();
            } else if (lexer.token() == Token.ALL) {
                queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                lexer.nextToken();
            } else if (lexer.token() == Token.UNIQUE) {
                queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
                lexer.nextToken();
            }

            // DM supports TOP N [PERCENT] [WITH TIES]
            if (lexer.token() == Token.TOP) {
                SQLTop top = this.createExprParser().parseTop();
                queryBlock.setTop(top);
            }

            parseSelectList(queryBlock);
        }

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseHierachical(queryBlock);

        parseGroupBy(queryBlock);

        if (lexer.token() == Token.WINDOW) {
            this.parseWindow(queryBlock);
        }

        queryBlock.setOrderBy(this.createExprParser().parseOrderBy());

        // DM LIMIT syntax:
        // LIMIT <count>
        // LIMIT <offset>, <count>
        // LIMIT <count> OFFSET <offset>
        // OFFSET <offset> LIMIT <count>
        // OFFSET <offset> ROW[S]
        for (;;) {
            if (lexer.token() == Token.LIMIT) {
                SQLLimit limit = getOrInitLimit(queryBlock);

                lexer.nextToken();
                if (lexer.token() == Token.ALL) {
                    limit.setRowCount(new SQLIdentifierExpr("ALL"));
                    lexer.nextToken();
                } else {
                    SQLExpr first = expr();
                    // LIMIT offset, count
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        SQLExpr second = expr();
                        limit.setOffset(first);
                        limit.setRowCount(second);
                    } else {
                        limit.setRowCount(first);
                    }
                }

                if (lexer.token() == Token.OFFSET) {
                    lexer.nextToken();
                    SQLExpr offset = expr();
                    limit.setOffset(offset);
                    limit.setOffsetClause(true);
                }

                queryBlock.setLimit(limit);
            } else if (lexer.token() == Token.OFFSET) {
                SQLLimit limit = getOrInitLimit(queryBlock);
                lexer.nextToken();
                SQLExpr offset = expr();
                limit.setOffset(offset);
                limit.setOffsetClause(true);

                if (lexer.token() == Token.ROW || lexer.token() == Token.ROWS) {
                    lexer.nextToken();
                }

                queryBlock.setLimit(limit);
            } else {
                break;
            }
        }

        // FETCH FIRST|NEXT <count> [PERCENT] ROW[S] ONLY|WITH TIES
        if (lexer.token() == Token.FETCH) {
            lexer.nextToken();
            DmSelectQueryBlock.FetchClause fetch = new DmSelectQueryBlock.FetchClause();

            if (lexer.token() == Token.FIRST) {
                fetch.setOption(DmSelectQueryBlock.FetchClause.Option.FIRST);
                lexer.nextToken();
            } else if (lexer.token() == Token.NEXT) {
                fetch.setOption(DmSelectQueryBlock.FetchClause.Option.NEXT);
                lexer.nextToken();
            } else {
                throw new ParserException("expect 'FIRST' or 'NEXT'. " + lexer.info());
            }

            SQLExpr count = expr();
            fetch.setCount(count);

            if (lexer.token() == Token.ROW || lexer.token() == Token.ROWS) {
                lexer.nextToken();
            } else {
                throw new ParserException("expect 'ROW' or 'ROWS'. " + lexer.info());
            }

            if (lexer.token() == Token.ONLY) {
                lexer.nextToken();
            } else {
                throw new ParserException("expect 'ONLY'. " + lexer.info());
            }

            queryBlock.setFetch(fetch);
        }

        // FOR UPDATE [OF <col_list>] [NOWAIT | WAIT N | SKIP LOCKED]
        // FOR READ ONLY (not commonly used, skip for now)
        if (lexer.token() == Token.FOR) {
            lexer.nextToken();

            DmSelectQueryBlock.ForClause forClause = new DmSelectQueryBlock.ForClause();

            if (lexer.token() == Token.UPDATE) {
                forClause.setOption(DmSelectQueryBlock.ForClause.Option.UPDATE);
                lexer.nextToken();
            } else if (lexer.token() == Token.SHARE) {
                forClause.setOption(DmSelectQueryBlock.ForClause.Option.SHARE);
                lexer.nextToken();
            } else {
                throw new ParserException("expect 'UPDATE' or 'SHARE'. " + lexer.info());
            }

            if (lexer.token() == Token.OF) {
                lexer.nextToken();
                for (;;) {
                    SQLExpr expr = this.createExprParser().expr();
                    expr.setParent(forClause);
                    forClause.getOf().add(expr);
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    } else {
                        break;
                    }
                }
            }

            if (lexer.token() == Token.NOWAIT) {
                lexer.nextToken();
                forClause.setNoWait(true);
            } else if (lexer.token() == Token.WAIT) {
                lexer.nextToken();
                SQLExpr waitTimeout = expr();
                forClause.setWaitTimeout(waitTimeout);
            } else if (lexer.identifierEquals(FnvHash.Constants.SKIP)) {
                lexer.nextToken();
                acceptIdentifier("LOCKED");
                forClause.setSkipLocked(true);
            }

            queryBlock.setForClause(forClause);
        }

        return queryRest(queryBlock, acceptUnion);
    }

    private SQLLimit getOrInitLimit(SQLSelectQueryBlock queryBlock) {
        SQLLimit limit = queryBlock.getLimit();
        if (limit == null) {
            limit = new SQLLimit();
            queryBlock.setLimit(limit);
        }
        return limit;
    }
}
