package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlLexer;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;

/**
 * @author two brother
 * @date 2021/7/22 11:38
 */
public class DMSQLExprParser extends SQLExprParser {

    public DMSQLExprParser(String sql) {
        super(new DMLexer(sql), DbType.dm);
        this.lexer.nextToken();
    }
    public DMSQLExprParser(String sql, DbType dbType, SQLParserFeature... features) {
        this(new DMLexer(sql, features), dbType);
        for (SQLParserFeature feature : features) {
            config(feature, true);
        }
        this.lexer.nextToken();

    }
    public DMSQLExprParser(Lexer lexer, DbType dbType) {
        super(lexer, dbType);
    }
}
