package com.alibaba.druid.sql.dialect.hologres.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class HologresStatementParser
        extends PGSQLStatementParser {
    public HologresStatementParser(HologresExprParser parser) {
        super(parser);
        dbType = DbType.hologres;
    }

    public HologresStatementParser(String sql, SQLParserFeature... features) {
        this(new HologresExprParser(sql, features));
    }

    @Override
    public HologresSelectParser createSQLSelectParser() {
        return new HologresSelectParser(this.exprParser, selectListCache);
    }

    public HologresCreateTableParser getSQLCreateTableParser() {
        return new HologresCreateTableParser(this.exprParser);
    }
}
