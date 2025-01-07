package com.alibaba.druid.sql.dialect.spark.parser;

import com.alibaba.druid.sql.dialect.hive.parser.HiveSelectParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;

public class SparkSelectParser extends HiveSelectParser {
    public SparkSelectParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SparkSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }
}
