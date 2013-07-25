package com.alibaba.druid.sql.dialect.db2.parser;

import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLStatementParser;


public class DB2StatementParser extends SQLStatementParser {
    public DB2StatementParser(String sql) {
        super (new DB2ExprParser(sql));
    }

    public DB2StatementParser(Lexer lexer){
        super(new DB2ExprParser(lexer));
    }
    
    public DB2SelectParser createSQLSelectParser() {
        return new DB2SelectParser(this.exprParser);
    }
}
