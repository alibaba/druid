package com.alibaba.druid.sql.dialect.holo.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGExprParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class HoloExprParser
        extends PGExprParser {
    public HoloExprParser(String sql, SQLParserFeature... features) {
        super(sql, features);
        dbType = DbType.hologres;
    }

    public HoloExprParser(Lexer lexer) {
        super(lexer);
        dbType = DbType.hologres;
    }
}
