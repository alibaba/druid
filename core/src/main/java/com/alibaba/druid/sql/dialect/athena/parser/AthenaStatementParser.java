package com.alibaba.druid.sql.dialect.athena.parser;

import com.alibaba.druid.sql.dialect.presto.parser.PrestoStatementParser;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class AthenaStatementParser extends PrestoStatementParser {
    public AthenaStatementParser(String sql, SQLParserFeature... features) {
        super(new AthenaExprParser(sql, features));
    }

    @Override
    public AthenaSelectParser createSQLSelectParser() {
        return new AthenaSelectParser(this.exprParser, selectListCache);
    }

    @Override
    public SQLCreateTableParser getSQLCreateTableParser() {
        return new AthenaCreateTableParser(this.exprParser);
    }

}
