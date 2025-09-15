package com.alibaba.druid.sql.dialect.hologres.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGLexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class HologresLexer
        extends PGLexer {
    public HologresLexer(String input, SQLParserFeature... features) {
        super(input, features);
        dbType = DbType.hologres;
    }
}
