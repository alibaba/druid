package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleCreateTableStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;

public class OracleCreateTableParser extends SQLCreateTableParser {

    public OracleCreateTableParser(Lexer lexer){
        super(lexer);
        this.exprParser = new OracleExprParser(lexer);
    }

    public OracleCreateTableParser(String sql){
        super(new OracleLexer(sql));
        this.lexer.nextToken();
        this.exprParser = new OracleExprParser(lexer);
    }

    protected OracleCreateTableStatement newCreateStatement() {
        return new OracleCreateTableStatement();
    }
    
    public OracleCreateTableStatement parseCrateTable(boolean acceptCreate) {
        OracleCreateTableStatement stmt = (OracleCreateTableStatement) super.parseCrateTable(acceptCreate);
        
        if (identifierEquals("TABLESPACE")) {
            lexer.nextToken();
            stmt.setTablespace(this.exprParser.name());
        }
        
        return stmt;
    }
}
