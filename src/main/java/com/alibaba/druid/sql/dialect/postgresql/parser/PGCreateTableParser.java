package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionByHash;
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
        } else if (lexer.identifierEquals("HASH") || lexer.identifierEquals("UNI_HASH")) {
            SQLPartitionByHash hash = new SQLPartitionByHash();

            if (lexer.identifierEquals("UNI_HASH")) {
                hash.setUnique(true);
            }

            lexer.nextToken();

            if (lexer.token() == Token.KEY) {
                lexer.nextToken();
                hash.setKey(true);
            }

            accept(Token.LPAREN);
            this.exprParser.exprList(hash.getColumns(), hash);
            accept(Token.RPAREN);
            return hash;
        }

        throw new ParserException("TODO " + lexer.info());
    }
}
