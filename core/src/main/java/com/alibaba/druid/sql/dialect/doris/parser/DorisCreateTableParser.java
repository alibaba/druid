package com.alibaba.druid.sql.dialect.doris.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionByList;
import com.alibaba.druid.sql.ast.SQLPartitionByRange;
import com.alibaba.druid.sql.ast.SQLPartitionByValue;
import com.alibaba.druid.sql.dialect.starrocks.parser.StarRocksCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class DorisCreateTableParser
        extends StarRocksCreateTableParser {
    public DorisCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.doris;
    }

    public SQLPartitionBy parsePartitionBy() {
        if (lexer.nextIf(Token.PARTITION)) {
            accept(Token.BY);
            SQLPartitionBy partitionClause;
            boolean hasLparen = false;
            if (lexer.nextIfIdentifier(FnvHash.Constants.RANGE)) {
                partitionClause = new SQLPartitionByRange();
                accept(Token.LPAREN);
                hasLparen = true;
            } else if (lexer.nextIfIdentifier(FnvHash.Constants.LIST)) {
                partitionClause = new SQLPartitionByList();
                ((SQLPartitionByList) partitionClause).setType(SQLPartitionByList.PartitionByListType.LIST_EXPRESSION);
                accept(Token.LPAREN);
                hasLparen = true;
            } else {
                partitionClause = new SQLPartitionByValue();
            }

            for (; ;) {
                partitionClause.addColumn(this.exprParser.expr());
                if (lexer.nextIf(Token.COMMA)) {
                    continue;
                }
                break;
            }

            if (hasLparen && !lexer.nextIf(Token.RPAREN)) {
                accept(Token.RPAREN);
            }

            if (lexer.token() == Token.LPAREN) {
                accept(Token.LPAREN);
                for (; ; ) {
                    if (lexer.token() == Token.RPAREN) {
                        break;
                    }
                    partitionClause.addPartition(this.getExprParser().parsePartition());
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
            }
            return partitionClause;
        }
        return null;
    }
}
