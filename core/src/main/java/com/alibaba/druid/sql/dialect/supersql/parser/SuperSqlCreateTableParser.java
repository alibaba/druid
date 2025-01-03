package com.alibaba.druid.sql.dialect.supersql.parser;

import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.util.FnvHash;

public class SuperSqlCreateTableParser extends PrestoCreateTableParser {
    public SuperSqlCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    @Override
    protected void createTableBefore(SQLCreateTableStatement stmt) {
        acceptIdentifier(FnvHash.Constants.EXTERNAL);
        stmt.setExternal(true);
    }
}
