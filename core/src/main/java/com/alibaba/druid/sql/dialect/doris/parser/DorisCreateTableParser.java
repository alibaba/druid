package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class DorisCreateTableParser
        extends StarRocksCreateTableParser {
    public DorisCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.doris;
    }
}
