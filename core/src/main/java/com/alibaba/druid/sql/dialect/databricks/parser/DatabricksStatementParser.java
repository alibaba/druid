package com.alibaba.druid.sql.dialect.databricks.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLRefreshTableStatement;
import com.alibaba.druid.sql.dialect.spark.parser.SparkStatementParser;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

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

    public SQLStatement parseRefresh() {
        acceptIdentifier("REFRESH");
        accept(Token.TABLE);
        SQLRefreshTableStatement stmt = new SQLRefreshTableStatement();
        stmt.setDbType(dbType);
        stmt.setName(this.exprParser.name());
        return stmt;
    }
}
