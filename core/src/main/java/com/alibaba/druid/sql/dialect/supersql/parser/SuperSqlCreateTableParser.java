package com.alibaba.druid.sql.dialect.supersql.parser;

import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoCreateTableParser;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class SuperSqlCreateTableParser extends PrestoCreateTableParser {
    public SuperSqlCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    @Override
    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        if (lexer.nextIf(Token.PARTITIONED)) {
            accept(Token.BY);
            accept(Token.LPAREN);
            for (; ; ) {
                if (lexer.token() != Token.IDENTIFIER) {
                    throw new ParserException("expect identifier. " + lexer.info());
                }

                SQLColumnDefinition column = this.exprParser.parseColumn();
                stmt.addPartitionColumn(column);

                if (lexer.isKeepComments() && lexer.hasComment()) {
                    column.addAfterComment(lexer.readAndResetComments());
                }

                if (lexer.token() != Token.COMMA) {
                    break;
                } else {
                    lexer.nextToken();
                    if (lexer.isKeepComments() && lexer.hasComment()) {
                        column.addAfterComment(lexer.readAndResetComments());
                    }
                }
            }
            accept(Token.RPAREN);
        }
        super.parseCreateTableRest(stmt);
    }
}
