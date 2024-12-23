package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLInsertInto;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksStatementParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;

public class DorisStatementParser
        extends StarRocksStatementParser {
    public DorisStatementParser(SQLExprParser parser) {
        super(parser);
        dbType = DbType.doris;
    }

    public DorisStatementParser(String sql, SQLParserFeature... features) {
        this(new DorisExprParser(sql, features));
    }

    @Override
    public DorisSelectParser createSQLSelectParser() {
        return new DorisSelectParser(this.exprParser, selectListCache);
    }

    public DorisCreateTableParser getSQLCreateTableParser() {
        return new DorisCreateTableParser(this.exprParser);
    }

    @Override
    protected void parseInsertOverwrite(SQLInsertInto insertStatement) {
        insertStatement.setOverwrite(true);
        accept(Token.TABLE);
    }
}
