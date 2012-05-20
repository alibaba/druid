package com.alibaba.druid.hbase.hbql.parser;

import com.alibaba.druid.hbase.hbql.ast.HBQLShowStatement;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;


public class HBQLStatementParser extends SQLStatementParser {
    protected SQLExprParser exprParser;

    public HBQLStatementParser(String sql){
        super(new SQLExprParser(sql));
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
