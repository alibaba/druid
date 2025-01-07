package com.alibaba.druid.sql.dialect.databricks.parser;

import com.alibaba.druid.sql.dialect.spark.parser.SparkCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;

public class DatabricksCreateTableParser extends SparkCreateTableParser {
    public DatabricksCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }
    public SQLSelectParser createSQLSelectParser() {
        return new DatabricksSelectParser(this.exprParser, selectListCache);
    }
}
