package com.alibaba.druid.sql.dialect.athena.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoExprParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class AthenaExprParser extends PrestoExprParser {
    public AthenaExprParser(String sql, SQLParserFeature... features) {
        super(sql, features);
        this.dbType = DbType.athena;
    }

    public AthenaExprParser(Lexer lexer) {
        super(lexer);
        this.dbType = DbType.athena;
    }
}
