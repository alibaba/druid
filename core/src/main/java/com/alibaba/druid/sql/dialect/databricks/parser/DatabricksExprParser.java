package com.alibaba.druid.sql.dialect.databricks.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.spark.parser.SparkExprParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class DatabricksExprParser extends SparkExprParser {
    public DatabricksExprParser(String sql, SQLParserFeature... features) {
        this(new DatabricksLexer(sql, features));
        this.lexer.nextToken();
    }

    public DatabricksExprParser(Lexer lexer) {
        super(lexer, DbType.databricks);
    }
}
