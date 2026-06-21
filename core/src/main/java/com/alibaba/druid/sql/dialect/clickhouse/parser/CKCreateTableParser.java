package com.alibaba.druid.sql.dialect.clickhouse.parser;

import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionByList;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKeyImpl;
import com.alibaba.druid.sql.dialect.clickhouse.ast.CKCreateTableStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class CKCreateTableParser extends SQLCreateTableParser {
    public CKCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    protected SQLCreateTableStatement newCreateStatement() {
        return new CKCreateTableStatement();
    }

    @Override
    public SQLPartitionBy parsePartitionBy() {
        lexer.nextToken();
        accept(Token.BY);
        SQLPartitionByList sqlPartitionBy = new SQLPartitionByList();
        sqlPartitionBy.setType(SQLPartitionByList.PartitionByListType.LIST_EXPRESSION);
        boolean hasParen = false;
        if (lexer.nextIf(Token.LPAREN)) {
            hasParen = true;
        }
        for (; ; ) {
            sqlPartitionBy.addColumn(this.exprParser.expr());
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        if (hasParen) {
            accept(Token.RPAREN);
        }

        return sqlPartitionBy;
    }

    // ClickHouse PRIMARY KEY accepts a bare column list (PRIMARY KEY a, b) as well as PRIMARY KEY (a, b)
    private SQLPrimaryKey parseCKPrimaryKey() {
        accept(Token.PRIMARY);
        accept(Token.KEY);
        SQLPrimaryKeyImpl pk = new SQLPrimaryKeyImpl();
        boolean paren = lexer.nextIf(Token.LPAREN);
        this.exprParser.orderBy(pk.getColumns(), pk);
        if (paren) {
            accept(Token.RPAREN);
        }
        return pk;
    }

    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        CKCreateTableStatement ckStmt = (CKCreateTableStatement) stmt;
        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            ckStmt.setEngine(
                    this.exprParser.expr()
            );
        }

        // ClickHouse allows PARTITION BY / ORDER BY / PRIMARY KEY / SAMPLE BY / TTL in any order
        // (e.g. PRIMARY KEY before ORDER BY); loop until no more engine clauses match (issue #4950)
        boolean partitionSeen = false;
        boolean orderSeen = false;
        boolean primarySeen = false;
        boolean sampleSeen = false;
        boolean ttlSeen = false;
        for (; ; ) {
            if (lexer.token() == Token.PARTITION && !partitionSeen) {
                ckStmt.setPartitionBy(parsePartitionBy());
                partitionSeen = true;
            } else if (lexer.token() == Token.ORDER && !orderSeen) {
                ckStmt.setOrderBy(this.exprParser.parseOrderBy());
                orderSeen = true;
            } else if (lexer.token() == Token.PRIMARY && !primarySeen) {
                ckStmt.setPrimaryKey(parseCKPrimaryKey());
                primarySeen = true;
            } else if (lexer.identifierEquals("SAMPLE") && !sampleSeen) {
                lexer.nextToken();
                accept(Token.BY);
                ckStmt.setSampleBy(this.exprParser.expr());
                sampleSeen = true;
            } else if (lexer.token() == Token.TTL && !ttlSeen) {
                lexer.nextToken();
                ckStmt.setTtl(this.exprParser.expr());
                ttlSeen = true;
            } else {
                // a repeated engine clause (e.g. two ORDER BY) would otherwise leak to the caller
                // and produce a misleading "expect EOF" error; report it clearly instead
                if (lexer.token() == Token.PARTITION
                        || lexer.token() == Token.ORDER
                        || lexer.token() == Token.PRIMARY
                        || lexer.token() == Token.TTL
                        || lexer.identifierEquals("SAMPLE")) {
                    throw new ParserException("duplicate ClickHouse engine clause. " + lexer.info());
                }
                break;
            }
        }

        if (lexer.token() == Token.SETTINGS) {
            lexer.nextToken();
            for (; ; ) {
                SQLAssignItem item = this.exprParser.parseAssignItem();
                item.setParent(ckStmt);
                ckStmt.getSettings().add(item);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }

                break;
            }
        }

        if (lexer.nextIf(Token.COMMENT)) {
            ckStmt.setComment(this.exprParser.expr());
        }
    }

    @Override
    protected void createTableAfterName(SQLCreateTableStatement createTable) {
        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            acceptIdentifier("CLUSTER");
            // 支持 IDENTIFIER 或 DEFAULT 关键字作为 cluster 名称
            if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.DEFAULT) {
                String clusterName = lexer.stringVal();
                CKCreateTableStatement ckStmt = (CKCreateTableStatement) createTable;
                ckStmt.setOnClusterName(clusterName);
                lexer.nextToken();
            } else {
                setErrorEndPos(lexer.pos());
                throw new ParserException("syntax error, expect IDENTIFIER, actual " + lexer.token() + ", " + lexer.info());
            }
        }
    }
}
