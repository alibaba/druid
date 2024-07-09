package com.alibaba.druid.sql.dialect.clickhouse.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
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

    @Override
    protected void afterParseFetchClause(SQLSelectQueryBlock queryBlock) {
        if (queryBlock instanceof CKSelectQueryBlock) {
            CKSelectQueryBlock ckSelectQueryBlock = (CKSelectQueryBlock) queryBlock;
            if (lexer.identifierEquals("SETTINGS")) {
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
        }
    }

}
