package com.alibaba.druid.sql.dialect.sqlserver.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class SQLServerExprParser extends SQLExprParser {

    public SQLServerExprParser(Lexer lexer){
        super(lexer);
    }

    public SQLServerExprParser(String sql) throws ParserException{
        super(new SQLServerLexer(sql));
        this.lexer.nextToken();
    }

}
