package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTableSampling;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.doris.ast.DorisExprTableSource;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksSelectParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.Token;

public class DorisSelectParser
        extends StarRocksSelectParser {
    public DorisSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        dbType = DbType.doris;
    }

    protected DorisExprParser createExprParser() {
        return new DorisExprParser(lexer);
    }

    @Override
    protected void parseBeforeSelectList(SQLSelectQueryBlock queryBlock) {
        if (lexer.token() == Token.DISTINCT) {
            queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
            lexer.nextToken();
        } else if (lexer.token() == Token.DISTINCTROW) {
            queryBlock.setDistionOption(SQLSetQuantifier.DISTINCTROW);
            lexer.nextToken();
        } else if (lexer.token() == Token.ALL) {
            lexer.nextToken();
            if (lexer.nextIf(Token.EXCEPT)) {
                queryBlock.setDistionOption(SQLSetQuantifier.ALL_EXCEPT);
            } else {
                queryBlock.setDistionOption(SQLSetQuantifier.ALL);
            }
        }
    }

    @Override
    public void parseAfterTableSourceRest(SQLTableSource tableSource) {
        if (tableSource instanceof DorisExprTableSource) {
            DorisExprTableSource dorisExprTableSource = (DorisExprTableSource) tableSource;
            if (lexer.nextIf(Token.PARTITION)) {
                accept(Token.LPAREN);
                do {
                    dorisExprTableSource.addPartition(this.exprParser.name());
                }
                while (lexer.nextIf(Token.COMMA));
                accept(Token.RPAREN);
            }
            if (lexer.nextIf(Token.TABLET)) {
                accept(Token.LPAREN);
                dorisExprTableSource.addTablet(expr());
                accept(Token.RPAREN);
            }

            if (lexer.nextIf(Token.TABLESAMPLE)) {
                accept(Token.LPAREN);
                SQLTableSampling sqlTableSampling = new SQLTableSampling();
                SQLExpr expr = expr();
                if (lexer.nextIf(Token.ROWS)) {
                    sqlTableSampling.setRows(expr);
                } else if (lexer.nextIf(Token.PERCENT)) {
                    sqlTableSampling.setPercent(expr);
                } else {
                    sqlTableSampling.setByteLength(expr);
                }
                dorisExprTableSource.setSampling(sqlTableSampling);
                accept(Token.RPAREN);
            }

            if (lexer.nextIf(Token.REPEATABLE)) {
                dorisExprTableSource.setRepeatable(expr());
            }

        }
    }
    @Override
    protected SQLExprTableSource getTableSource() {
        return new DorisExprTableSource();
    }
}
