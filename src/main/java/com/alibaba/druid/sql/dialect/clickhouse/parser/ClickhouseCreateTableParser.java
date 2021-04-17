package com.alibaba.druid.sql.dialect.clickhouse.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.clickhouse.ast.ClickhouseCreateTableStatement;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class ClickhouseCreateTableParser extends SQLCreateTableParser {
    public ClickhouseCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    protected SQLCreateTableStatement newCreateStatement() {
        return new ClickhouseCreateTableStatement();
    }

    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        ClickhouseCreateTableStatement ckStmt = (ClickhouseCreateTableStatement) stmt;
        if (lexer.identifierEquals(FnvHash.Constants.ENGINE)) {
            lexer.nextToken();
            if (lexer.token() == Token.EQ) {
                lexer.nextToken();
            }
            stmt.setEngine(
                    this.exprParser.expr()
            );
        }

        if (lexer.identifierEquals("PARTITION")) {
            lexer.nextToken();
            accept(Token.BY);
            SQLExpr expr = this.exprParser.expr();
            ckStmt.setPartitionBy(expr);
        }

        if (lexer.token() == Token.ORDER) {
            SQLOrderBy orderBy = this.exprParser.parseOrderBy();
            ckStmt.setOrderBy(orderBy);
        }

        if (lexer.identifierEquals("SAMPLE")) {
            lexer.nextToken();
            accept(Token.BY);
            SQLExpr expr = this.exprParser.expr();
            ckStmt.setSampleBy(expr);
        }

        if (lexer.identifierEquals("SETTINGS")) {
            lexer.nextToken();
            for (;;) {
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
}
