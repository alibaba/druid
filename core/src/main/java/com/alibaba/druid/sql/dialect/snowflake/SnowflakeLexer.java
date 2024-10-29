package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class SnowflakeLexer extends Lexer {
    public SnowflakeLexer(String input, SQLParserFeature... features) {
        super(input);
        dbType = DbType.snowflake;
        this.skipComment = true;
        this.keepComments = true;
        this.features |= SQLParserFeature.SupportUnicodeCodePoint.mask;
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
    }
}
