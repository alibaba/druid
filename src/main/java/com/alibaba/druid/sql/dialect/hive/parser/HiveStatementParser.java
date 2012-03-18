package com.alibaba.druid.sql.dialect.hive.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.hive.ast.stmt.HiveShowTablesStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class HiveStatementParser extends SQLStatementParser {

    public HiveStatementParser(Lexer lexer){
        super(lexer);
    }

    public HiveStatementParser(String sql){
        super(new HiveLexer(sql));
        this.lexer.nextToken();
    }

    public HiveCreateTableParser getSQLCreateTableParser() {
        return new HiveCreateTableParser(lexer);
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.SHOW) {
            lexer.nextToken();

            if (lexer.token() == Token.TABLES) {
                lexer.nextToken();

                HiveShowTablesStatement stmt = new HiveShowTablesStatement();

                if (lexer.token() == Token.LITERAL_CHARS) {
                    stmt.setPattern((SQLCharExpr) exprParser.primary());
                }

                statementList.add(stmt);
                return true;
            }

            throw new ParserException("TODO " + lexer.token());
        }
        return false;
    }
}
