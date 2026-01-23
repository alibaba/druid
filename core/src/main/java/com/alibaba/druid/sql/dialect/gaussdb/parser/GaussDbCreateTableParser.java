package com.alibaba.druid.sql.dialect.gaussdb.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.gaussdb.ast.GaussDbDistributeBy;
import com.alibaba.druid.sql.dialect.gaussdb.ast.stmt.GaussDbCreateTableStatement;
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
        if (lexer.identifierEquals(FnvHash.Constants.SERVER)) {
            acceptIdentifier(FnvHash.Constants.SERVER);
            gdStmt.setServer(exprParser.expr());
            acceptIdentifier(FnvHash.Constants.OPTIONS);
            accept(Token.LPAREN);
            parseAssignItems(stmt.getTableOptions(), stmt, false);
            accept(Token.RPAREN);
            if (lexer.nextIfIdentifier("write")) {
                accept(Token.ONLY);
                gdStmt.setForeignTableMode(GaussDbCreateTableStatement.ForeignTableMode.WRITE_ONLY);
            } else if (lexer.identifierEquals("read")) {
                if (lexer.nextIf(Token.ONLY)) {
                    gdStmt.setForeignTableMode(GaussDbCreateTableStatement.ForeignTableMode.READ_ONLY);
                } else if (lexer.identifierEquals("write")) {
                    gdStmt.setForeignTableMode(GaussDbCreateTableStatement.ForeignTableMode.READ_WRITE);
                }
            }
        }
        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            accept(Token.LPAREN);
            parseAssignItems(gdStmt.getTableOptions(), gdStmt, false);
            accept(Token.RPAREN);
        }
        if (lexer.nextIf(Token.ON)) {
            if (lexer.nextIfIdentifier(FnvHash.Constants.COMMIT)) {
                if (lexer.identifierEquals("PRESERVE") || lexer.token() == Token.DELETE) {
                    gdStmt.setOnCommitExpr(exprParser.name());
                    accept(Token.ROWS);
                }
            }
        }
        if (lexer.identifierEquals("COMPRESS") || lexer.identifierEquals("NOCOMPRESS")) {
            gdStmt.setCompressType(exprParser.name());
        }
        GaussDbDistributeBy distributeByClause = parseDistributeBy();
        if (distributeByClause != null) {
            gdStmt.setDistributeBy(distributeByClause);
        }

        if (lexer.nextIf(Token.TO)) {
            if (lexer.nextIf(Token.GROUP)) {
                SQLExpr group = this.exprParser.expr();
                gdStmt.setToGroup(group);
            }
            if (lexer.nextIfIdentifier(FnvHash.Constants.NODE)) {
                SQLExpr node = this.exprParser.expr();
                gdStmt.setToNode(node);
            }
        }

        SQLPartitionBy partitionClause = parsePartitionBy();
        if (partitionClause != null) {
            gdStmt.setPartitionBy(partitionClause);
        }

        parseRowMovement(gdStmt);

        if (lexer.nextIf(Token.COMMENT)) {
            lexer.nextIf(Token.EQ);
            SQLExpr comment = this.exprParser.expr();
            gdStmt.setComment(comment);
        }
    }
    public void parseRowMovement(GaussDbCreateTableStatement stmt) {
        if (lexer.token() == Token.ENABLE || lexer.token() == Token.DISABLE) {
            stmt.setRowMovementType(exprParser.name());
            accept(Token.ROW);
            acceptIdentifier("MOVEMENT");
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
            if (lexer.token() == Token.PARTITION) {
                listPartition.addPartition(this.getExprParser().parsePartition());
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
            }
            break;
        }
        accept(Token.RPAREN);
        return listPartition;
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
        } else if (lexer.nextIf(Token.FOREIGN)) {
            createTable.config(SQLCreateTableStatement.Feature.External);
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
            } else if (lexer.identifierEquals(FnvHash.Constants.ROUNDROBIN)) {
                distributeBy.setType(this.exprParser.name());
                return distributeBy;
            } else if (lexer.identifierEquals(FnvHash.Constants.REPLICATION)) {
                distributeBy.setType(this.exprParser.name());
                return distributeBy;
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
