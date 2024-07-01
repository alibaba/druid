package com.alibaba.druid.sql.dialect.holo.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class HoloStatementParser
        extends PGSQLStatementParser {
    public HoloStatementParser(HoloExprParser parser) {
        super(parser);
        dbType = DbType.hologres;
    }

    public HoloStatementParser(String sql, SQLParserFeature... features) {
        this(new HoloExprParser(sql, features));
    }

    @Override
    public HoloSelectParser createSQLSelectParser() {
        return new HoloSelectParser(this.exprParser, selectListCache);
    }

    public HoloCreateTableParser getSQLCreateTableParser() {
        return new HoloCreateTableParser(this.exprParser);
    }
}
