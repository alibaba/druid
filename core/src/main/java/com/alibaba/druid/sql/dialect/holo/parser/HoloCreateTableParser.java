package com.alibaba.druid.sql.dialect.holo.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class HoloCreateTableParser
        extends PGCreateTableParser {
    public HoloCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.hologres;
    }
}
