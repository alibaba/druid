package com.alibaba.druid.sql.dialect.supersql.parser;

import com.alibaba.druid.sql.dialect.presto.parser.PrestoSelectParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;

public class SuperSqlSelectParser extends PrestoSelectParser {
    public SuperSqlSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }

    @Override
    protected SQLExprParser createExprParser() {
    return new SuperSqlExprParser(this.lexer);
    }
}
