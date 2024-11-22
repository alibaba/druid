package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.hive.parser.HiveCreateTableParser;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaCreateTableStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

public class ImpalaCreateTableParser extends HiveCreateTableParser {
    @Override
    public SQLCreateTableParser getSQLCreateTableParser() {
        return new ImpalaCreateTableParser(this.exprParser);
    }

    public ImpalaCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.impala;
    }

    public ImpalaCreateTableParser(Lexer lexer) {
        super(lexer);
        dbType = DbType.impala;
    }

    protected ImpalaCreateTableStatement newCreateStatement() {
        return new ImpalaCreateTableStatement();
    }

    @Override
    protected void parseCreateTableRest(SQLCreateTableStatement createTable) {
        ImpalaCreateTableStatement stmt = (ImpalaCreateTableStatement) createTable;

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

        if (lexer.identifierEquals(FnvHash.Constants.SORT)) {
            parseSortedBy(stmt);
        }

        // for kudu table
        SQLPartitionBy partitionClause = parsePartitionBy();
        stmt.setPartitionBy(partitionClause);
        if (lexer.nextIf(Token.COMMENT)) {
            SQLExpr comment = this.exprParser.expr();
            stmt.setComment(comment);
        }

        if (lexer.token() == Token.ROW
                || lexer.identifierEquals(FnvHash.Constants.ROW)) {
            parseRowFormat(stmt);
        }

        if (Token.LBRACKET.equals(lexer.token())) {
            stmt.setLbracketUse(true);
            lexer.nextToken();
        }
        if (lexer.identifierEquals(FnvHash.Constants.STORED)) {
            lexer.nextToken();
            accept(Token.AS);
            SQLName name = this.exprParser.name();
            stmt.setStoredAs(name);
        }

        if (Token.RBRACKET.equals(lexer.token())) {
            stmt.setRbracketUse(true);
            lexer.nextToken();
        }
        if (lexer.identifierEquals(FnvHash.Constants.LOCATION)) {
            lexer.nextToken();
            SQLExpr location = this.exprParser.primary();
            stmt.setLocation(location);
        }

        if (lexer.identifierEquals(FnvHash.Constants.UNCACHED)) {
            lexer.nextToken();
            stmt.setUnCached(true);
        }

        if (lexer.identifierEquals(FnvHash.Constants.CACHED)) {
            lexer.nextToken();
            accept(Token.IN);
            SQLExpr poolName = this.exprParser.primary();
            stmt.setCachedPool(poolName);
            if (lexer.hashLCase() == FnvHash.Constants.WITH) {
                lexer.nextToken();
                acceptIdentifier("REPLICATION");
                accept(Token.EQ);
                stmt.setCachedReplication(this.exprParser.parseIntValue());
            }
        }

        if (lexer.identifierEquals(FnvHash.Constants.TBLPROPERTIES)) {
            parseOptions(stmt);
        }

        if (lexer.token() == Token.SELECT || lexer.token() == Token.AS) {
            if (lexer.token() == Token.AS) {
                lexer.nextToken();
            }
            SQLSelect select = this.createSQLSelectParser().select();
            stmt.setSelect(select);
        }

        if (lexer.token() == Token.SELECT || lexer.token() == Token.AS) {
            if (lexer.token() == Token.AS) {
                lexer.nextToken();
            }
            SQLSelect select = this.createSQLSelectParser().select();
            stmt.setSelect(select);
        }

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            Lexer.SavePoint mark = lexer.mark();
            if (lexer.token() == Token.SELECT) {
                stmt.setLikeQuery(true);
                SQLSelect select = this.createSQLSelectParser().select();
                stmt.setSelect(select);
            } else {
                lexer.reset(mark);

                if (lexer.identifierEquals(FnvHash.Constants.MAPPING)) {
                    SQLExpr like = this.exprParser.primary();
                    stmt.setLike(new SQLExprTableSource(like));
                } else {
                    SQLName name = this.exprParser.name();
                    stmt.setLike(name);
                }
            }
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLExpr comment = this.exprParser.expr();
            stmt.setComment(comment);
        }

        if (lexer.identifierEquals(FnvHash.Constants.USING) || lexer.token() == Token.USING) {
            lexer.nextToken();
            SQLExpr using = this.exprParser.expr();
            stmt.setUsing(using);
        }

        if (lexer.identifierEquals(FnvHash.Constants.TBLPROPERTIES)) {
            parseOptions(stmt);
        }
    }

    public SQLPartitionBy parsePartitionBy() {
        if (lexer.nextIf(Token.PARTITION)) {
            accept(Token.BY);
            if (lexer.nextIfIdentifier(FnvHash.Constants.HASH)) {
                SQLPartitionBy hashPartition = new SQLPartitionByHash();
                if (lexer.nextIf(Token.LPAREN)) {
                    // e.g. partition by hash(id,name) partitions 16
                    // TODO: 'partition by hash(id) partitions 4, hash(name) partitions 4' not supported yet
                    if (lexer.token() != Token.IDENTIFIER) {
                        throw new ParserException("expect identifier. " + lexer.info());
                    }
                    for (; ; ) {
                        hashPartition.addColumn(this.exprParser.name());
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);
                    acceptIdentifier("PARTITIONS");
                    hashPartition.setPartitionsCount(acceptInteger());
                    return hashPartition;
                } else {
                    // e.g. partition by hash partitions 16
                    acceptIdentifier("PARTITIONS");
                    int numPartitions = acceptInteger();
                    hashPartition.setPartitionsCount(numPartitions);
                    return hashPartition;
                }
            } else if (lexer.nextIfIdentifier(FnvHash.Constants.RANGE)) {
                return partitionByRange();
            }
        }
        return null;
    }

    private SQLPartitionByRange partitionByRange() {
        SQLPartitionByRange rangePartition = new SQLPartitionByRange();
        accept(Token.LPAREN);
        for (; ; ) {
            rangePartition.addColumn(this.exprParser.name());
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);
        accept(Token.LPAREN);
        for (; ; ) {
            if (lexer.token() == Token.RPAREN) {
                break;
            }
            rangePartition.addPartition(this.getExprParser().parsePartition());
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);
        return rangePartition;
    }

    @Override
    public ImpalaExprParser getExprParser() {
        return (ImpalaExprParser) exprParser;
    }
}
