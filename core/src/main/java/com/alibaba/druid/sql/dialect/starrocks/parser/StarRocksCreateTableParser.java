package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksAggregateKey;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksDuplicateKey;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksIndexDefinition;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateTableStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

public class StarRocksCreateTableParser extends SQLCreateTableParser {
    public StarRocksCreateTableParser(Lexer lexer) {
        super(new StarRocksExprParser(lexer));
    }

    public StarRocksCreateTableParser(String sql) {
        super(new StarRocksExprParser(sql));
    }

    public StarRocksCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    @Override
    public StarRocksExprParser getExprParser() {
        return (StarRocksExprParser) exprParser;
    }

    protected void createTableBefore(SQLCreateTableStatement createTable) {
        if (lexer.nextIfIdentifier(FnvHash.Constants.EXTERNAL)) {
            createTable.setExternal(true);
        }
        if (lexer.nextIfIdentifier(FnvHash.Constants.TEMPORARY)) {
            createTable.setTemporary(true);
        }
    }

    public void parseUniqueKey(SQLCreateTableStatement stmt) {
        SQLUnique sqlUnique;
        if (lexer.nextIfIdentifier(FnvHash.Constants.DUPLICATE)) {
            sqlUnique = new StarRocksDuplicateKey();
        } else if (lexer.nextIfIdentifier(FnvHash.Constants.AGGREGATE)) {
            sqlUnique = new StarRocksAggregateKey();
        } else if (lexer.nextIf(Token.PRIMARY)) {
            sqlUnique = new SQLPrimaryKeyImpl();
        } else if (lexer.nextIf(Token.UNIQUE)) {
            sqlUnique = new SQLUnique();
        } else {
            return;
        }
        accept(Token.KEY);
        accept(Token.LPAREN);
        this.exprParser.orderBy(sqlUnique.getColumns(), sqlUnique);
        accept(Token.RPAREN);
        stmt.setUnique(sqlUnique);
    }

    protected void parseIndex(SQLCreateTableStatement createTable) {
        if (lexer.token() == Token.INDEX) {
            StarRocksIndexDefinition index = new StarRocksIndexDefinition();
            lexer.nextToken();
            index.setIndexName(this.exprParser.name());
            accept(Token.LPAREN);
            for (; ; ) {
                index.getColumns().add(this.exprParser.name());
                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                } else {
                    lexer.nextToken();
                }
            }
            accept(Token.RPAREN);
            if (lexer.token() == Token.USING) {
                lexer.nextToken();
                if (lexer.identifierEquals(FnvHash.Constants.BITMAP)) {
                    lexer.nextToken();
                    index.setUsingBitmap(true);
                }
            }
            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                index.setComment(this.exprParser.expr());
            }
            index.setParent(createTable);
            createTable.getTableElementList().add(index);
        }
    }
    public void parseCreateTableRest(SQLCreateTableStatement stmt) {
        StarRocksCreateTableStatement srStmt = (StarRocksCreateTableStatement) stmt;

        if (lexer.nextIfIdentifier(FnvHash.Constants.ENGINE)) {
            accept(Token.EQ);
            srStmt.setEngine(
                    this.exprParser.expr()
            );
        }

        parseUniqueKey(stmt);

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLExpr comment = this.exprParser.expr();
            srStmt.setComment(comment);
        }

        stmt.setPartitionBy(parsePartitionBy());
        // Distributed by.
        if (lexer.nextIfIdentifier(FnvHash.Constants.DISTRIBUTED)) {
            accept(Token.BY);
            if (lexer.nextIfIdentifier(FnvHash.Constants.HASH)) {
                srStmt.setDistributedByType(DistributedByType.Hash);
                accept(Token.LPAREN);
                this.exprParser.orderBy(srStmt.getDistributedBy(), srStmt);
                accept(Token.RPAREN);
            } else if (lexer.nextIfIdentifier(FnvHash.Constants.RANDOM)) {
                srStmt.setDistributedByType(DistributedByType.Random);
            }
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.BUCKETS)) {
            if (lexer.token() == Token.LITERAL_INT) {
                stmt.setBuckets(lexer.integerValue().intValue());
                lexer.nextToken();
            }
        }

        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = this.exprParser.parseOrderBy();
            srStmt.setOrderBy(orderBy);
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.PROPERTIES)) {
            accept(Token.LPAREN);
            parseAssignItems(srStmt.getTableOptions(), srStmt, false);
            accept(Token.RPAREN);
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.BROKER)) {
            acceptIdentifier(FnvHash.Constants.PROPERTIES);
            accept(Token.LPAREN);
            parseAssignItems(srStmt.getBrokerProperties(), srStmt, false);
            accept(Token.RPAREN);
        }
    }

    /**
     *  PARTITION BY RANGE (col1[,col2])
     *  PARTITION BY LIST (col1[,col2])
     *  PARTITION BY (col1[,col2])
     *  PARTITION BY FUNC(param1[,param2])
     * @return
     */
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
            } else if (lexer.nextIf(Token.LPAREN)) {
                partitionClause = new SQLPartitionByValue();
                hasLparen = true;
            } else {
                partitionClause = new SQLPartitionByValue();
            }
            for (; ; ) {
                partitionClause.addColumn(this.exprParser.expr());
                if (lexer.nextIf(Token.COMMA)) {
                 continue;
                }
                break;
            }
            if (hasLparen) {
                accept(Token.RPAREN);
            }
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
            return partitionClause;
        }
        return null;
    }

    protected StarRocksCreateTableStatement newCreateStatement() {
        return new StarRocksCreateTableStatement();
    }
}
