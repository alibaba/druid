package com.alibaba.druid.hbase.hbql.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;


public class HBQLStatementParser extends SQLStatementParser {
    protected SQLExprParser exprParser;

    public HBQLStatementParser(String sql){
        super(new SQLExprParser(sql));
    }

    public HBQLStatementParser(Lexer lexer){
        super(new SQLExprParser(lexer));
    }
}
