package com.alibaba.druid.sql.dialect.clickhouse.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLWithSubqueryClause;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKSelectQueryBlock;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

public class CKSelectParser
        extends SQLSelectParser {
    public CKSelectParser(Lexer lexer) {
        super(lexer);
    }

    public CKSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }

    public SQLWithSubqueryClause parseWith() {
        SQLWithSubqueryClause withQueryClause = new SQLWithSubqueryClause();
        if (lexer.hasComment() && lexer.isKeepComments()) {
            withQueryClause.addBeforeComment(lexer.readAndResetComments());
        }

        accept(Token.WITH);

        if (lexer.token() == Token.RECURSIVE || lexer.identifierEquals(FnvHash.Constants.RECURSIVE)) {
            lexer.nextToken();
            withQueryClause.setRecursive(true);
        }

        for (; ; ) {
            SQLWithSubqueryClause.Entry entry = new SQLWithSubqueryClause.Entry();
            entry.setParent(withQueryClause);

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                switch (lexer.token()) {
                    case SELECT:
                    case LPAREN:
                    case WITH:
                    case FROM:
                        entry.setSubQuery(select());
                        break;
                    default:
                        break;
                }

                accept(Token.RPAREN);
            } else {
                entry.setExpr(expr());
            }

            accept(Token.AS);

            String alias = this.lexer.stringVal();
            lexer.nextToken();
            entry.setAlias(alias);

            withQueryClause.addEntry(entry);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return withQueryClause;
    }

    protected SQLSelectQueryBlock createSelectQueryBlock() {
        return new CKSelectQueryBlock();
    }

    public void parseWhere(SQLSelectQueryBlock queryBlock) {
        if (lexer.nextIf(Token.PREWHERE)) {
            SQLExpr preWhere = exprParser.expr();
            ((CKSelectQueryBlock) queryBlock).setPreWhere(preWhere);
        }
        super.parseWhere(queryBlock);
    }

    public SQLSelect select() {
        SQLSelect select = new SQLSelect();

        if (lexer.token() == Token.WITH) {
            SQLWithSubqueryClause with = this.parseWith();
            select.setWithSubQuery(with);
        }

        SQLSelectQuery query = query(select, true);
        select.setQuery(query);

        SQLOrderBy orderBy = this.parseOrderBy();

        if (query instanceof CKSelectQueryBlock) {
            CKSelectQueryBlock queryBlock = (CKSelectQueryBlock) query;

            if (queryBlock.getOrderBy() == null) {
                queryBlock.setOrderBy(orderBy);
                if (lexer.token() == Token.LIMIT) {
                    SQLLimit limit = this.exprParser.parseLimit();
                    queryBlock.setLimit(limit);
                }
            } else {
                select.setOrderBy(orderBy);
                if (lexer.token() == Token.LIMIT) {
                    SQLLimit limit = this.exprParser.parseLimit();
                    select.setLimit(limit);
                }
            }
            if (lexer.identifierEquals("SETTINGS")) {
                lexer.nextToken();
                for (; ; ) {
                    SQLAssignItem item = this.exprParser.parseAssignItem();
                    queryBlock.getSettings().add(item);
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }

                    break;
                }
            }

        } else {
            select.setOrderBy(orderBy);
            if (lexer.token() == Token.LIMIT) {
                SQLLimit limit = this.exprParser.parseLimit();
                select.setLimit(limit);
            }
        }

        return select;
    }

    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);
            select.setParenthesized(true);

            return queryRest(select, acceptUnion);
        }

        if (lexer.token() == Token.VALUES) {
            return valuesQuery(acceptUnion);
        }

        SQLSelectQueryBlock queryBlock = createSelectQueryBlock();
        queryBlock.setParent(parent);

        if (lexer.hasComment() && lexer.isKeepComments()) {
            queryBlock.addBeforeComment(lexer.readAndResetComments());
        }

        accept(Token.SELECT);

        querySelectListBefore(queryBlock);

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
            queryBlock.setDistionOption(SQLSetQuantifier.ALL);
            lexer.nextToken();
        } else if (lexer.token() == Token.TOP) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();
            if (lexer.token() == Token.LITERAL_INT) {
                SQLLimit limit = new SQLLimit(lexer.integerValue().intValue());
                queryBlock.setLimit(limit);
                lexer.nextToken();
            } else if (lexer.token() == Token.DOT) {
                lexer.reset(mark);
            }
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

        parseFetchClause(queryBlock);

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();
            SQLExpr expr = expr();
            if (lexer.token() != Token.COMMA) {
                queryBlock.setInto(expr);
            }
        }

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();
            accept(Token.UPDATE);

            queryBlock.setForUpdate(true);

            if (lexer.identifierEquals(FnvHash.Constants.NO_WAIT) || lexer.identifierEquals(FnvHash.Constants.NOWAIT)) {
                lexer.nextToken();
                queryBlock.setNoWait(true);
            } else if (lexer.identifierEquals(FnvHash.Constants.WAIT)) {
                lexer.nextToken();
                SQLExpr waitTime = this.exprParser.primary();
                queryBlock.setWaitTime(waitTime);
            }
        }

        return queryRest(queryBlock, acceptUnion);
    }

}
