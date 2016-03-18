package com.alibaba.druid.sql.dialect.teradata.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLStatementParser;

public class TeradataStatementParser extends SQLStatementParser {

    public TeradataStatementParser(String sql){
        super(new TeradataExprParser(sql));
    }

    public TeradataStatementParser(Lexer lexer){
        super(new TeradataExprParser(lexer));
    }

}
