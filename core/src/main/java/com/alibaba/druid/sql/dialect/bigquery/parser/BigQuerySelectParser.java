package com.alibaba.druid.sql.dialect.bigquery.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;

public class BigQuerySelectParser extends SQLSelectParser {
    public BigQuerySelectParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.db2;
    }

    public BigQuerySelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        dbType = DbType.db2;
    }

    public BigQuerySelectParser(String sql) {
        this(new BigQueryExprParser(sql));
    }

    protected SQLExprParser createExprParser() {
        return new BigQueryExprParser(lexer);
    }

}
