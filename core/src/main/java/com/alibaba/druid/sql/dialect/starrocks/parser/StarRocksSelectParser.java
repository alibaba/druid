package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;

public class StarRocksSelectParser extends SQLSelectParser {
    public StarRocksSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        dbType = DbType.starrocks;
    }

    protected StarRocksExprParser createExprParser() {
        return new StarRocksExprParser(lexer);
    }
}
