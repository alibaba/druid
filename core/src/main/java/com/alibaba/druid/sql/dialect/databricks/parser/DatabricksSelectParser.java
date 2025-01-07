package com.alibaba.druid.sql.dialect.databricks.parser;

import com.alibaba.druid.sql.dialect.spark.parser.SparkSelectParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;

public class DatabricksSelectParser extends SparkSelectParser {
    public DatabricksSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }

    @Override
    protected SQLExprParser createExprParser() {
    return new DatabricksExprParser(this.lexer);
    }
}
