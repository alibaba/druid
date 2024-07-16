package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.hive.parser.HiveCreateTableParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class ImpalaCreateTableParser extends HiveCreateTableParser {
    @Override
    public SQLCreateTableParser getSQLCreateTableParser() {
        return new ImpalaCreateTableParser(this.exprParser);
    }
    public ImpalaCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.impala;
    }

    public ImpalaCreateTableParser(Lexer lexer) {
        super(lexer);
        dbType = DbType.impala;
    }
}
