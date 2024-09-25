package com.alibaba.druid.sql.dialect.redshift.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGCreateTableParser;
import com.alibaba.druid.sql.dialect.redshift.stmt.RedshiftCreateTableStatement;
import com.alibaba.druid.sql.dialect.redshift.stmt.RedshiftSortKey;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

import java.util.List;
public class RedshiftCreateTableParser
        extends PGCreateTableParser {
    public RedshiftCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
        dbType = DbType.redshift;
    }

    public SQLCreateTableStatement parseCreateTable() {
        List<String> comments = null;
        if (lexer.isKeepComments() && lexer.hasComment()) {
            comments = lexer.readAndResetComments();
        }

        RedshiftCreateTableStatement createTable = new RedshiftCreateTableStatement();
        if (comments != null) {
            createTable.addBeforeComment(comments);
        }

        createTable.setDbType(dbType);

        if (lexer.hasComment() && lexer.isKeepComments()) {
            createTable.addBeforeComment(lexer.readAndResetComments());
        }

        accept(Token.CREATE);
        createTableBefore(createTable);
        accept(Token.TABLE);
        createTableBeforeName(createTable);
        createTable.setName(
                this.exprParser.name());
        createTableAfterName(createTable);
        createTableBody(createTable);
        createTableQuery(createTable);
        parseCreateTableRest(createTable);

        return createTable;
    }

    @Override
    public void parseCreateTableRest(SQLCreateTableStatement createTable) {
        if (createTable instanceof RedshiftCreateTableStatement) {
            RedshiftCreateTableStatement redshiftCreateTableStatement = (RedshiftCreateTableStatement) createTable;
            if (lexer.nextIf(Token.BACKUP)) {
                redshiftCreateTableStatement.setBackup(this.exprParser.expr());
            }
            if (lexer.nextIf(Token.DISTSTYLE)) {
                if (lexer.token() == Token.EVEN || lexer.token() == Token.KEY || lexer.token() == Token.AUTO || lexer.token() == Token.ALL) {
                    redshiftCreateTableStatement.setDistStyle(new SQLIdentifierExpr(lexer.token().name));
                    lexer.nextToken();
                } else {
                    throw new IllegalArgumentException();
                }
            }

            if (lexer.nextIf(Token.DISTKEY)) {
                accept(Token.LPAREN);
                redshiftCreateTableStatement.setDistKey(this.exprParser.expr());
                accept(Token.RPAREN);
            }

            if (lexer.token() == Token.COMPOUND || lexer.token() == Token.INTERLEAVED) {
                RedshiftSortKey sortKey = new RedshiftSortKey();
                sortKey.setCompound(lexer.token() == Token.COMPOUND);
                sortKey.setInterleaved(lexer.token() == Token.INTERLEAVED);
                lexer.nextToken();
                accept(Token.SORTKEY);
                accept(Token.LPAREN);
                for (;;) {
                    sortKey.addColumn(this.exprParser.expr());
                    if (!lexer.nextIf(Token.COMMA)) {
                        break;
                    }
                }
                accept(Token.RPAREN);
                redshiftCreateTableStatement.setSortKey(sortKey);
            } else if (lexer.nextIf(Token.SORTKEY)) {
                accept(Token.AUTO);
                RedshiftSortKey sortKey = new RedshiftSortKey();
                sortKey.setAuto(true);
                redshiftCreateTableStatement.setSortKey(sortKey);
            }

            if (lexer.nextIf(Token.ENCODE)) {
                accept(Token.AUTO);
                redshiftCreateTableStatement.setEncodeAuto(true);
            }
        } else {
            super.parseCreateTableRest(createTable);
        }
    }

    @Override
    protected void createTableBefore(SQLCreateTableStatement createTable) {
        if (lexer.nextIfIdentifier("TEMPORARY") || lexer.nextIfIdentifier("TEMP")) {
            createTable.config(SQLCreateTableStatement.Feature.Temporary);
        }
        super.createTableBefore(createTable);
    }
}
