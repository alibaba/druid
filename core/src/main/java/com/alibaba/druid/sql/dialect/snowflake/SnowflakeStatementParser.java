package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class SnowflakeStatementParser extends SQLStatementParser {
    public SnowflakeStatementParser(String sql) {
        super(new SnowflakeExprParser(sql));
    }

    public SnowflakeStatementParser(String sql, SQLParserFeature... features) {
        super(new SnowflakeExprParser(sql, features));
    }

    public SnowflakeStatementParser(Lexer lexer) {
        super(new SnowflakeExprParser(lexer));
    }
}
