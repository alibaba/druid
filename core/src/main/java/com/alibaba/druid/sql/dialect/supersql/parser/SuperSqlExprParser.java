package com.alibaba.druid.sql.dialect.supersql.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoExprParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class SuperSqlExprParser extends PrestoExprParser {
    public SuperSqlExprParser(String sql, SQLParserFeature... features) {
        this(new SuperSqlLexer(sql, features));
        this.lexer.nextToken();
    }

    public SuperSqlExprParser(Lexer lexer) {
        super(lexer, DbType.supersql);
    }
}
