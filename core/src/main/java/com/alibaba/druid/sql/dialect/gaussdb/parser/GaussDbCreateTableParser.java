package com.alibaba.druid.sql.dialect.gaussdb.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbDistributeBy;
import com.alibaba.druid.sql.dialect.gaussdb.ast.stmt.GaussDbCreateTableStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGCreateTableParser;
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
        if (lexer.nextIf(Token.COMMENT)) {
            lexer.nextIf(Token.EQ);
            SQLExpr comment = this.exprParser.expr();
            gdStmt.setComment(comment);
        }
    }

    protected void createTableBefore(SQLCreateTableStatement createTable) {
        parseTableType(createTable);
        parseTableType(createTable);
    }

    private void parseTableType(SQLCreateTableStatement createTable) {
        if (lexer.nextIfIdentifier("UNLOGGED")) {
            createTable.config(SQLCreateTableStatement.Feature.Unlogged);
        } else if (lexer.nextIfIdentifier(FnvHash.Constants.GLOBAL)) {
            createTable.config(SQLCreateTableStatement.Feature.Global);
        } else if (lexer.nextIfIdentifier(FnvHash.Constants.TEMPORARY) || lexer.nextIfIdentifier("TEMP")) {
            createTable.config(SQLCreateTableStatement.Feature.Temporary);
        } else if (lexer.nextIf(Token.LOCAL)) {
            createTable.config(SQLCreateTableStatement.Feature.Local);
        }
    }

    public GaussDbDistributeBy parseDistributeBy() {
        if (lexer.token() == Token.DISTRIBUTE) {
            lexer.nextToken();
            accept(Token.BY);
            GaussDbDistributeBy distributeBy = new GaussDbDistributeBy();
            if (lexer.identifierEquals(FnvHash.Constants.HASH)) {
                distributeBy.setType(this.exprParser.name());
                if (lexer.nextIf(Token.LPAREN)) {
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
