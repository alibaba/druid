package com.alibaba.druid.sql.dialect.clickhouse.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectGroupByClause;
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

    @Override
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
            SQLExpr sqlExpr = exprParser.expr();

            if (sqlExpr instanceof SQLIdentifierExpr) {
                String alias = ((SQLIdentifierExpr) sqlExpr).getName();
                accept(Token.AS);
                accept(Token.LPAREN);
                entry.setSubQuery(select());
                entry.setPrefixAlias(true);
                entry.setAlias(alias);
                accept(Token.RPAREN);
            } else {
                entry.setExpr(sqlExpr);
                accept(Token.AS);
                String alias = this.lexer.stringVal();
                lexer.nextToken();
                entry.setPrefixAlias(false);
                entry.setAlias(alias);
            }

            withQueryClause.addEntry(entry);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        return withQueryClause;
    }

    @Override
    protected SQLSelectQueryBlock createSelectQueryBlock() {
        return new CKSelectQueryBlock();
    }

    @Override
    public void parseWhere(SQLSelectQueryBlock queryBlock) {
        if (lexer.nextIf(Token.PREWHERE)) {
            SQLExpr preWhere = exprParser.expr();
            ((CKSelectQueryBlock) queryBlock).setPreWhere(preWhere);
        }
        super.parseWhere(queryBlock);
    }

    @Override
    public void parseFrom(SQLSelectQueryBlock queryBlock) {
        super.parseFrom(queryBlock);
        if (lexer.token() == Token.FINAL) {
            lexer.nextToken();
            ((CKSelectQueryBlock) queryBlock).setFinal(true);
        } else {
            ((CKSelectQueryBlock) queryBlock).setFinal(false);
        }
    }

    @Override
    protected void afterParseFetchClause(SQLSelectQueryBlock queryBlock) {
        if (queryBlock instanceof CKSelectQueryBlock) {
            CKSelectQueryBlock ckSelectQueryBlock = (CKSelectQueryBlock) queryBlock;
            if (lexer.token() == Token.SETTINGS) {
                lexer.nextToken();
                for (; ; ) {
                    SQLAssignItem item = this.exprParser.parseAssignItem();
                    ckSelectQueryBlock.getSettings().add(item);
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
            }

            if (lexer.token() == Token.FORMAT) {
                lexer.nextToken();
                ckSelectQueryBlock.setFormat(expr());
            }
        }
    }

    @Override
    protected void afterParseLimitClause(SQLSelectQueryBlock queryBlock) {
        if (queryBlock instanceof CKSelectQueryBlock) {
            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                acceptIdentifier("TIES");
                ((CKSelectQueryBlock) queryBlock).setWithTies(true);
            }
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
        } else if (lexer.identifierEquals("TOTALS")) {
            lexer.nextToken();
            ((CKSelectQueryBlock) queryBlock).setWithTotals(true);
        } else {
            lexer.reset(mark);
        }
    }

    @Override
    protected void parseAfterOrderBy(SQLSelectQueryBlock queryBlock) {
        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            acceptIdentifier("FILL");
            ((CKSelectQueryBlock) queryBlock).setWithFill(true);
        }
    }
}
