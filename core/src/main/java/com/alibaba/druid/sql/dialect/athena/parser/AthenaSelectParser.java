package com.alibaba.druid.sql.dialect.athena.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoSelectParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;

public class AthenaSelectParser extends PrestoSelectParser {
    public AthenaSelectParser(SQLExprParser exprParser) {
        super(exprParser);
        this.dbType = DbType.athena;
    }

    public AthenaSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        this.dbType = DbType.athena;
    }

    public AthenaSelectParser(String sql) {
        super(sql);
        this.dbType = DbType.athena;
    }

    @Override
    protected SQLExprParser createExprParser() {
    return new AthenaExprParser(this.lexer);
    }
}
