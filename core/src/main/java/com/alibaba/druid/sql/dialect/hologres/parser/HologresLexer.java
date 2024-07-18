package com.alibaba.druid.sql.dialect.hologres.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGLexer;
import com.alibaba.druid.sql.parser.Keywords;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class HologresLexer
        extends PGLexer {
    @Override
    protected Keywords loadKeywords() {
        return super.loadKeywords();
    }

    public HologresLexer(String input, SQLParserFeature... features) {
        super(input, features);
        dbType = DbType.hologres;
    }
}
