package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveShowTablesStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class HiveStatementParser extends SQLStatementParser {

    public HiveStatementParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public HiveStatementParser(String sql){
        super(new SQLExprParser(sql));
    }

    public HiveCreateTableParser getSQLCreateTableParser() {
        return new HiveCreateTableParser(this.exprParser);
    }

    public SQLStatement parseShow() {
        acceptIdentifier("SHOW");

        if (identifierEquals("TABLES")) {
            lexer.nextToken();

            HiveShowTablesStatement stmt = new HiveShowTablesStatement();

            if (lexer.token() == Token.LITERAL_CHARS) {
                stmt.setPattern((SQLCharExpr) exprParser.primary());
            }

            return stmt;
        }

        throw new ParserException("TODO " + lexer.info());
    }
}
