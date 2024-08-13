package com.alibaba.druid.sql.dialect.hologres.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class HologresCreateTableParser
        extends PGCreateTableParser {
    public HologresCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.hologres;
    }
}
