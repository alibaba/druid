package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionByList;
import com.alibaba.druid.sql.parser.*;

public class PGCreateTableParser extends SQLCreateTableParser {

    public PGCreateTableParser(Lexer lexer){
        super(new PGExprParser(lexer));
    }

    public PGCreateTableParser(String sql){
        super(new PGExprParser(sql));
    }

    public PGCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLPartitionBy parsePartitionBy() {
        lexer.nextToken();
        accept(Token.BY);

        if (lexer.identifierEquals("LIST")) {
            lexer.nextToken();
            SQLPartitionByList list = new SQLPartitionByList();

            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();
                list.addColumn(this.exprParser.expr());
                accept(Token.RPAREN);
            } else {
                acceptIdentifier("COLUMNS");
                accept(Token.LPAREN);
                for (; ; ) {
                    list.addColumn(this.exprParser.name());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }

            return list;
        }
        throw new ParserException("TODO " + lexer.info());
    }
}
