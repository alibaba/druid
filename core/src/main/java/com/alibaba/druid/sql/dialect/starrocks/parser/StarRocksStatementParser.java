package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.dialect.clickhouse.parser.ClickhouseCreateTableParser;
import com.alibaba.druid.sql.parser.*;

import java.util.List;

public class StarRocksStatementParser extends SQLStatementParser {


    public StarRocksStatementParser(String sql) {
        super(new StarRocksExprParser(sql));
    }

    public StarRocksStatementParser(String sql, SQLParserFeature... features) {
        super(new StarRocksExprParser(sql, features));
    }

    public StarRocksStatementParser(String sql, boolean keepComments) {
        super(new StarRocksExprParser(sql, keepComments));
    }

    public StarRocksStatementParser(String sql, boolean skipComment, boolean keepComments) {
        super(new StarRocksExprParser(sql, skipComment, keepComments));
    }

    public StarRocksStatementParser(Lexer lexer) {
        super(new StarRocksExprParser(lexer));
    }


    public SQLCreateTableParser getSQLCreateTableParser() {
        return new StarRocksCreateTableParser(this.exprParser);
    }

}
