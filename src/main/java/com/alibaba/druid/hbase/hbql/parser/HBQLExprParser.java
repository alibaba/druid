package com.alibaba.druid.hbase.hbql.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;


public class HBQLExprParser extends SQLExprParser {
    public HBQLExprParser(String sql) throws ParserException{
        super(sql);
    }

    public HBQLExprParser(Lexer lexer){
        super(lexer);
    }
}
