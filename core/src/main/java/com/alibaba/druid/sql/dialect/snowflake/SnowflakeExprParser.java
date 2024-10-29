package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class SnowflakeExprParser extends SQLExprParser {
    public SnowflakeExprParser(String sql) {
        this(new SnowflakeLexer(sql));
        this.lexer.nextToken();
    }

    public SnowflakeExprParser(String sql, SQLParserFeature... features) {
        this(new SnowflakeLexer(sql, features));
        this.lexer.nextToken();
    }

    public SnowflakeExprParser(Lexer lexer) {
        super(lexer);
    }
}
