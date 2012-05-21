package com.alibaba.druid.hdriver.impl.hbql.parser;

import com.alibaba.druid.hdriver.impl.hbql.ast.HBQLShowStatement;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;


public class HBQLStatementParser extends SQLStatementParser {
    protected SQLExprParser exprParser;

    public HBQLStatementParser(String sql){
        super(new HBQLExprParser(sql));
    }

    public HBQLStatementParser(SQLExprParser exprParser){
        super(exprParser);
    }
    
    public SQLStatement parseShow() {
        acceptIdentifier("SHOW");
        acceptIdentifier("TABLES");
        return new HBQLShowStatement();
    }

}
