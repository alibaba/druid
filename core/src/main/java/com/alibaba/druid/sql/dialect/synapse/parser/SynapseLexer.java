package com.alibaba.druid.sql.dialect.synapse.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerLexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class SynapseLexer extends SQLServerLexer {
    public SynapseLexer(String input, SQLParserFeature... features) {
        super(input, features);
        this.dbType = DbType.synapse;
    }
}
