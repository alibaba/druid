package com.alibaba.druid.sql.dialect.supersql.parser;

import com.alibaba.druid.sql.dialect.presto.parser.PrestoStatementParser;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class SuperSqlStatementParser extends PrestoStatementParser {
    public SuperSqlStatementParser(String sql, SQLParserFeature... features) {
        super(new SuperSqlExprParser(sql, features));
    }

    @Override
    public SuperSqlSelectParser createSQLSelectParser() {
        return new SuperSqlSelectParser(this.exprParser, selectListCache);
    }

    @Override
    public SQLCreateTableParser getSQLCreateTableParser() {
        return new SuperSqlCreateTableParser(this.exprParser);
    }

}
