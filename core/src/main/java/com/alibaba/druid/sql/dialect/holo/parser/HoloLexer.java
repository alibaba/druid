package com.alibaba.druid.sql.dialect.holo.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGLexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class HoloLexer
        extends PGLexer {
    public HoloLexer(String input, SQLParserFeature... features) {
        super(input, features);
        dbType = DbType.hologres;
    }
}
