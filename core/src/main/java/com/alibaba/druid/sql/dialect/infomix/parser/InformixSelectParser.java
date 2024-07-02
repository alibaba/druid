package com.alibaba.druid.sql.dialect.infomix.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.util.FnvHash;

public class InformixSelectParser
        extends SQLSelectParser {
    public InformixSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }

    protected void querySelectListBefore(SQLSelectQueryBlock queryBlock) {
        if (lexer.identifierEquals(FnvHash.Constants.SKIP)) {
            lexer.nextToken();
            SQLExpr offset = this.exprParser.primary();
            queryBlock.setOffset(offset);
        }

        if (lexer.identifierEquals(FnvHash.Constants.FIRST)) {
            lexer.nextToken();
            SQLExpr first = this.exprParser.primary();
            queryBlock.setFirst(first);
        }
    }
}
