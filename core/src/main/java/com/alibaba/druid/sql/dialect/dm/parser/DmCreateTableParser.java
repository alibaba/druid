package com.alibaba.druid.sql.dialect.dm.parser;

import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionByHash;
import com.alibaba.druid.sql.ast.SQLPartitionByList;
import com.alibaba.druid.sql.ast.SQLPartitionByRange;
import com.alibaba.druid.sql.parser.*;

public class DmCreateTableParser extends SQLCreateTableParser {
    public DmCreateTableParser(Lexer lexer) {
        super(new DmExprParser(lexer));
    }

    public DmCreateTableParser(String sql) {
        super(new DmExprParser(sql));
    }

    public DmCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLPartitionBy parsePartitionBy() {
        lexer.nextToken();
        accept(Token.BY);

        if (lexer.identifierEquals("LIST")) {
            lexer.nextToken();
            SQLPartitionByList list = new SQLPartitionByList();

            if (lexer.token() == Token.LPAREN) {
                list.setType(SQLPartitionByList.PartitionByListType.LIST_EXPRESSION);
                lexer.nextToken();
                list.addColumn(this.exprParser.expr());
                accept(Token.RPAREN);
            } else {
                acceptIdentifier("COLUMNS");
                list.setType(SQLPartitionByList.PartitionByListType.LIST_COLUMNS);
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
        } else if (lexer.identifierEquals("HASH")) {
            SQLPartitionByHash hash = new SQLPartitionByHash();
            lexer.nextToken();

            if (lexer.token() == Token.KEY) {
                lexer.nextToken();
                hash.setKey(true);
            }

            accept(Token.LPAREN);
            this.exprParser.exprList(hash.getColumns(), hash);
            accept(Token.RPAREN);
            return hash;
        } else if (lexer.identifierEquals("RANGE")) {
            SQLPartitionByRange range = new SQLPartitionByRange();
            lexer.nextToken();

            accept(Token.LPAREN);
            this.exprParser.exprList(range.getColumns(), range);
            accept(Token.RPAREN);
            return range;
        }

        throw new ParserException("TODO " + lexer.info());
    }
}
