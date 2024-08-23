package com.alibaba.druid.sql.dialect.gaussdb.parser;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

public class GaussDbStatementParser extends PGSQLStatementParser {
    public GaussDbStatementParser(String sql) {
        super(new GaussDbExprParser(sql));
    }

    public GaussDbStatementParser(String sql, SQLParserFeature... features) {
        super(new GaussDbExprParser(sql, features));
    }

    public GaussDbCreateTableParser getSQLCreateTableParser() {
        return new GaussDbCreateTableParser(this.exprParser);
    }

    @Override
    public SQLCreateTableStatement parseCreateTable() {
        return getSQLCreateTableParser().parseCreateTable();
    }
}
