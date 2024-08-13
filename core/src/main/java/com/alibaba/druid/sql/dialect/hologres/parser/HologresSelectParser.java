package com.alibaba.druid.sql.dialect.hologres.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSelectParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;

public class HologresSelectParser
        extends PGSelectParser {
    public HologresSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        dbType = DbType.hologres;
    }
}
