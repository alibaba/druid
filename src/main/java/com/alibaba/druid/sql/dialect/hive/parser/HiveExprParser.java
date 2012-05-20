package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;


public class HiveExprParser extends SQLExprParser {
    public HiveExprParser(String sql) throws ParserException{
        super(new HiveLexer(sql));
        this.lexer.nextToken();
    }

    public HiveExprParser(Lexer lexer){
        super(lexer);
    }
}
