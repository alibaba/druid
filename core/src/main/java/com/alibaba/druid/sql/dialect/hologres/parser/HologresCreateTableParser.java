package com.alibaba.druid.sql.dialect.hologres.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionOf;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGCreateTableParser;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class HologresCreateTableParser
        extends PGCreateTableParser {
    public HologresCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.hologres;
    }

    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        boolean isLogical = lexer.nextIfIdentifier("LOGICAL");
        // For partition of/by for PG
        for (int i = 0; i < 2; i++) {
            if (lexer.token() == Token.PARTITION) {
                Lexer.SavePoint mark = lexer.mark();
                lexer.nextToken();
                if (Token.OF.equals(lexer.token())) {
                    lexer.reset(mark);
                    SQLPartitionOf partitionOf = parsePartitionOf();
                    stmt.setPartitionOf(partitionOf);
                } else if (Token.BY.equals(lexer.token())) {
                    lexer.reset(mark);
                    SQLPartitionBy partitionClause = parsePartitionBy();
                    partitionClause.setLogical(isLogical);
                    stmt.setPartitionBy(partitionClause);
                }
            }
        }

        if (lexer.nextIf(Token.WITH)) {
            accept(Token.LPAREN);
            parseAssignItems(stmt.getTableOptions(), stmt, false);
            accept(Token.RPAREN);
        }

        if (lexer.nextIf(Token.TABLESPACE)) {
            stmt.setTablespace(
                    this.exprParser.name()
            );
        }
    }
}
