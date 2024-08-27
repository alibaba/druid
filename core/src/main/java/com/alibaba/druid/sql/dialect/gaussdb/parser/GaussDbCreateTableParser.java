package com.alibaba.druid.sql.dialect.gaussdb.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbCreateTableStatement;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbDistributeBy;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGCreateTableParser;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class GaussDbCreateTableParser extends PGCreateTableParser {
    public GaussDbCreateTableParser(String sql) {
        super(new GaussDbExprParser(sql));
    }

    protected SQLCreateTableStatement newCreateStatement() {
        return new GaussDbCreateTableStatement();
    }

    @Override
    public GaussDbExprParser getExprParser() {
        return (GaussDbExprParser) exprParser;
    }

    public GaussDbCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.gaussdb;
    }

    protected void createTableBodyItem(SQLCreateTableStatement createTable) {
        if (lexer.token() == Token.PARTIAL) {
            lexer.nextToken();
            if (lexer.nextIfIdentifier(FnvHash.Constants.CLUSTER)) {
                accept(Token.KEY);
                accept(Token.LPAREN);
                for (; ; ) {
                    SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
                    createTable.addClusteredByItem(item);
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    } else if (lexer.token() == Token.RPAREN) {
                        accept(Token.RPAREN);
                        break;
                    }
                }}
        } else {
            super.createTableBodyItem(createTable);
        }
    }

    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        GaussDbCreateTableStatement gdStmt = (GaussDbCreateTableStatement) stmt;
        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr expr = this.exprParser.expr();
            stmt.setComment(expr);
        }
        if (lexer.identifierEquals(FnvHash.Constants.AUTO_INCREMENT)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr expr = this.exprParser.expr();
            gdStmt.setAutoIncrement(expr);
        }
        if (lexer.token() == Token.DEFAULT) {
            lexer.nextToken();
        }
        if (lexer.identifierEquals(FnvHash.Constants.CHARSET)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr expr = this.exprParser.expr();
            gdStmt.setCharset(expr);
        } else if (lexer.identifierEquals(FnvHash.Constants.CHARACTER)) {
            lexer.nextToken();
            accept(Token.SET);
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr expr = this.exprParser.expr();
            gdStmt.setCharset(expr);
        }
        if (lexer.identifierEquals(FnvHash.Constants.COLLATE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            SQLExpr expr = this.exprParser.expr();
            gdStmt.setCollate(expr);
        }
        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            gdStmt.setEngine(
                    this.exprParser.expr()
            );
        }
        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            accept(Token.LPAREN);
            parseAssignItems(gdStmt.getTableOptions(), gdStmt, false);
            accept(Token.RPAREN);
        }
        GaussDbDistributeBy distributeByClause = parseDistributeBy();
        if (distributeByClause != null) {
            gdStmt.setDistributeBy(distributeByClause);
        }
        SQLPartitionBy partitionClause = parsePartitionBy();
        if (partitionClause != null) {
            gdStmt.setPartitionBy(partitionClause);
        }
    }

    public SQLPartitionBy parsePartitionBy() {
        if (lexer.nextIf(Token.PARTITION)) {
            accept(Token.BY);
            if (lexer.nextIfIdentifier(FnvHash.Constants.HASH)) {
                SQLPartitionBy hashPartition = new SQLPartitionByHash();
                if (lexer.nextIf(Token.LPAREN)) {
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
                    return hashPartition;
                }
            } else if (lexer.nextIfIdentifier(FnvHash.Constants.RANGE)) {
                return partitionByRange();
            } else if (lexer.nextIfIdentifier(FnvHash.Constants.LIST)) {
                return partitionByList();
            }
        }
        return null;
    }

    protected SQLPartitionByRange partitionByRange() {
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

    private SQLPartitionByList partitionByList() {
        SQLPartitionByList listPartition = new SQLPartitionByList();
        accept(Token.LPAREN);
        for (; ; ) {
            listPartition.addColumn(this.exprParser.name());
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);
        accept(Token.LPAREN);
        for (; ; ) {
            listPartition.addPartition(this.getExprParser().parsePartition());
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);
        return listPartition;
    }

    public GaussDbDistributeBy parseDistributeBy() {
        if (lexer.token() == Token.DISTRIBUTE) {
            lexer.nextToken();
            accept(Token.BY);
            GaussDbDistributeBy distributeBy = new GaussDbDistributeBy();
            if (lexer.identifierEquals(FnvHash.Constants.HASH)) {
                distributeBy.setType(this.exprParser.name());
                if (lexer.nextIf(Token.LPAREN)) {
                    if (lexer.token() != Token.IDENTIFIER) {
                        throw new ParserException("expect identifier. " + lexer.info());
                    }
                    for (; ; ) {
                        distributeBy.addColumn(this.exprParser.name());
                        if (lexer.token() == Token.COMMA) {
                            lexer.nextToken();
                            continue;
                        }
                        break;
                    }
                    accept(Token.RPAREN);
                    return distributeBy;
                }
            } else if (lexer.identifierEquals(FnvHash.Constants.RANGE)) {
                distributeBy.setType(this.exprParser.name());
                return distributionByContent(distributeBy);
            } else if (lexer.identifierEquals(FnvHash.Constants.LIST)) {
                distributeBy.setType(this.exprParser.name());
                return distributionByContent(distributeBy);
            }
        }
        return null;
    }

    public GaussDbDistributeBy distributionByContent(GaussDbDistributeBy distributeBy) {
        accept(Token.LPAREN);
        for (; ; ) {
            distributeBy.addColumn(this.exprParser.name());
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);
        accept(Token.LPAREN);
        for (; ; ) {
            distributeBy.addDistribution(this.getExprParser().parseDistribution());
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);
        return distributeBy;
    }
}
