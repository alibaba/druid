package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;

/**
 * @author two brother
 * @date 2021/7/22 10:54
 */
public class DMSQLStatementParser extends SQLStatementParser {
    public DMSQLStatementParser(String sql) {
        super(new DMSQLExprParser(sql));
    }

    public DMSQLStatementParser(String sql, DbType dbType) {
        super(new DMSQLExprParser(sql, dbType));

    }

    @Override
    public SQLSelectParser createSQLSelectParser() {
        return new DMSQLSelectParser(this.exprParser, selectListCache);
    }

    @Override
    public SQLSelectParser createSQLSelectParser(SQLExprParser exprParser) {
        return new DMSQLSelectParser(exprParser);
    }

}
