package com.alibaba.druid.sql.dialect.databricks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.spark.parser.SparkLexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class DatabricksLexer extends SparkLexer {
    public DatabricksLexer(String input, SQLParserFeature... features) {
        this(input, DbType.databricks, features);
    }

    public DatabricksLexer(String input, DbType dbType, SQLParserFeature... features) {
        super(input, dbType, features);
    }
}
