package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.hive.parser.HiveSelectParser;
import com.alibaba.druid.sql.dialect.hive.parser.HiveStatementParser;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class ImpalaStatementParser extends HiveStatementParser {
    {
        dbType = DbType.impala;
    }

    public ImpalaStatementParser(String sql, SQLParserFeature... features) {
        super(new ImpalaExprParser(sql, features));
    }
    public HiveSelectParser createSQLSelectParser() {
        return new ImpalaSelectParser(this.exprParser, selectListCache);
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new ImpalaCreateTableParser(this.exprParser);
    }
}
