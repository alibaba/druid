package com.alibaba.druid.sql.dialect.clickhouse.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLPartitionBy;
import com.alibaba.druid.sql.ast.SQLPartitionByList;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
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

        if (lexer.token() == Token.PARTITION) {
            ckStmt.setPartitionBy(parsePartitionBy());
        }

        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = this.exprParser.parseOrderBy();
            ckStmt.setOrderBy(orderBy);
        }

        if (lexer.token() == Token.PRIMARY) {
            SQLPrimaryKey sqlPrimaryKey = this.exprParser.parsePrimaryKey();
            ckStmt.setPrimaryKey(sqlPrimaryKey);
        }

        if (lexer.identifierEquals("SAMPLE")) {
            lexer.nextToken();
            accept(Token.BY);
            SQLExpr expr = this.exprParser.expr();
            ckStmt.setSampleBy(expr);
        }

        if (lexer.token() == Token.TTL) {
            lexer.nextToken();
            SQLExpr expr = this.exprParser.expr();
            ckStmt.setTtl(expr);
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
    }

    @Override
    protected void createTableAfterName(SQLCreateTableStatement createTable) {
        if (lexer.token() == Token.ON) {
            lexer.nextToken();
            acceptIdentifier("CLUSTER");
            if (lexer.token() == Token.IDENTIFIER) {
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
