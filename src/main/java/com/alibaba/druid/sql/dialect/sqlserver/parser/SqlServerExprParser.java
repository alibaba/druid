package com.alibaba.druid.sql.dialect.sqlserver.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;

public class SqlServerExprParser extends SQLExprParser {

    public SqlServerExprParser(Lexer lexer){
        super(lexer);
    }

    public SqlServerExprParser(String sql) throws ParserException{
        super(sql);
    }

}
