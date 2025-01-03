package com.alibaba.druid.sql.dialect.athena.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoLexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class AthenaLexer extends PrestoLexer {
    public AthenaLexer(String input, SQLParserFeature... features) {
        super(input, DbType.athena, features);
    }
}
