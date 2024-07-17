package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.hive.parser.HiveExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class ImpalaExprParser extends HiveExprParser {
    public ImpalaExprParser(String sql, SQLParserFeature... features) {
        super(new ImpalaLexer(sql, features));
        this.lexer.nextToken();
        dbType = DbType.impala;
    }
}
