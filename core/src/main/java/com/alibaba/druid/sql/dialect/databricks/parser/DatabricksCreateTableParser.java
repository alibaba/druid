package com.alibaba.druid.sql.dialect.databricks.parser;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.spark.parser.SparkCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class DatabricksCreateTableParser extends SparkCreateTableParser {
    public DatabricksCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }
    public SQLSelectParser createSQLSelectParser() {
        return new DatabricksSelectParser(this.exprParser, selectListCache);
    }

    protected void createTableBefore(SQLCreateTableStatement createTable) {
        if (lexer.nextIf(Token.OR)) {
            accept(Token.REPLACE);
            createTable.config(SQLCreateTableStatement.Feature.OrReplace);
        }

        if (lexer.identifierEquals(FnvHash.Constants.TEMPORARY) || lexer.identifierEquals("temp")) {
            lexer.nextToken();
            createTable.setTemporary(true);
        }
    }
}
