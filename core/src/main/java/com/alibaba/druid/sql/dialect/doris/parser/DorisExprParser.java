package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksExprParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class DorisExprParser
        extends StarRocksExprParser {
    public DorisExprParser(String sql, SQLParserFeature... features) {
        super(new DorisLexer(sql, features));
        lexer.nextToken();
        dbType = DbType.doris;
    }

    public DorisExprParser(Lexer lexer) {
        super(lexer);
        dbType = DbType.doris;
    }
}
