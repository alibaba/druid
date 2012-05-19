package com.alibaba.druid.hbase.hbql.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;


public class HBQLStatementParser extends SQLStatementParser {
    protected SQLExprParser exprParser;

    public HBQLStatementParser(String sql){
        super(sql);

        this.exprParser = new SQLExprParser(lexer);
    }

    public HBQLStatementParser(Lexer lexer){
        super(lexer);
        this.exprParser = new SQLExprParser(lexer);
    }
}
