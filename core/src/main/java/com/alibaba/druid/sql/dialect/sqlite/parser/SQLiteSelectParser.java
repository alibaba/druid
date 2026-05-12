package com.alibaba.druid.sql.dialect.sqlite.parser;

import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;

public class SQLiteSelectParser extends SQLSelectParser {
    public SQLiteSelectParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLiteSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
    }

    public SQLiteSelectParser(String sql) {
        this(new SQLiteExprParser(sql));
    }

    protected SQLExprParser createExprParser() {
        return new SQLiteExprParser(lexer);
    }
}
