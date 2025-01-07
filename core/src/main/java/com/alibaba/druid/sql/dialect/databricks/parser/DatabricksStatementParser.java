package com.alibaba.druid.sql.dialect.databricks.parser;

import com.alibaba.druid.sql.dialect.spark.parser.SparkStatementParser;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class DatabricksStatementParser extends SparkStatementParser {
    public DatabricksStatementParser(String sql, SQLParserFeature... features) {
        super(new DatabricksExprParser(sql, features));
    }

    @Override
    public DatabricksSelectParser createSQLSelectParser() {
        return new DatabricksSelectParser(this.exprParser, selectListCache);
    }

    @Override
    public SQLCreateTableParser getSQLCreateTableParser() {
        return new DatabricksCreateTableParser(this.exprParser);
    }

}
