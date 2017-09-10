package com.alibaba.druid.sql.dialect.h2.parser;

import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;

public class H2SelectParser extends SQLSelectParser {

    public H2SelectParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public H2SelectParser(String sql){
        this(new H2ExprParser(sql));
    }

    protected SQLExprParser createExprParser() {
        return new H2ExprParser(lexer);
    }
}
