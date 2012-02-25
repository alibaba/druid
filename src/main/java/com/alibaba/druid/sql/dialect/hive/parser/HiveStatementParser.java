package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLStatementParser;


public class HiveStatementParser extends SQLStatementParser {

    public HiveStatementParser(Lexer lexer){
        super(lexer);
    }

    public HiveStatementParser(String sql){
        super(new HiveLexer(sql));
        this.lexer.nextToken();
    }

}
