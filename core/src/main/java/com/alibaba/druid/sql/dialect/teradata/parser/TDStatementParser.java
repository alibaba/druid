package com.alibaba.druid.sql.dialect.teradata.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class TDStatementParser extends SQLStatementParser {
    public TDStatementParser(TDExprParser parser) {
        super(parser);
        dbType = DbType.teradata;
    }
    public TDStatementParser(String sql, SQLParserFeature... features) {
    this(new TDExprParser(sql, features));
    }

    @Override
    public TDSelectParser createSQLSelectParser() {
        return new TDSelectParser(this.exprParser, selectListCache);
    }

    @Override
    public TDCreateTableParser getSQLCreateTableParser() {
        return new TDCreateTableParser(this.exprParser);
    }
}
