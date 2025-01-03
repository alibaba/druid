package com.alibaba.druid.sql.dialect.athena.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoExprParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class AthenaExprParser extends PrestoExprParser {
    public AthenaExprParser(String sql, SQLParserFeature... features) {
        this(new AthenaLexer(sql, features));
        this.lexer.nextToken();
    }

    public AthenaExprParser(Lexer lexer) {
        super(lexer, DbType.athena);
    }
}
