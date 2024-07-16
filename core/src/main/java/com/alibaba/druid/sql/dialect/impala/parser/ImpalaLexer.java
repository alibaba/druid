package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.hive.parser.HiveLexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class ImpalaLexer extends HiveLexer {
    public ImpalaLexer(String input) {
        super(input);
        dbType = DbType.impala;
    }

    public ImpalaLexer(String input, SQLParserFeature... features) {
        super(input, features);
        dbType = DbType.impala;
    }
}
