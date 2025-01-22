package com.alibaba.druid.sql.dialect.athena.parser;

import com.alibaba.druid.sql.dialect.presto.parser.PrestoSelectParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;

public class AthenaSelectParser extends PrestoSelectParser {
    public AthenaSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }

    @Override
    protected SQLExprParser createExprParser() {
    return new AthenaExprParser(this.lexer);
    }
}
