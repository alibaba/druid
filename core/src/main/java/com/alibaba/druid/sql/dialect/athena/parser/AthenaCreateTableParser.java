package com.alibaba.druid.sql.dialect.athena.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLExternalRecordFormat;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.athena.ast.stmt.AthenaCreateTableStatement;
import com.alibaba.druid.sql.dialect.hive.ast.HiveInputOutputFormat;
import com.alibaba.druid.sql.dialect.presto.parser.PrestoCreateTableParser;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class AthenaCreateTableParser extends PrestoCreateTableParser {
    public AthenaCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    @Override
    protected void createTableBefore(SQLCreateTableStatement stmt) {
        acceptIdentifier(FnvHash.Constants.EXTERNAL);
        stmt.setExternal(true);
    }
    @Override
    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLExpr comment = this.exprParser.expr();
            stmt.setComment(comment);
        }

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

        if (lexer.nextIfIdentifier(FnvHash.Constants.CLUSTERED)) {
            accept(Token.BY);
            accept(Token.LPAREN);
            for (; ; ) {
                SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
                stmt.addClusteredByItem(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.ROW
                || lexer.identifierEquals(FnvHash.Constants.ROW)) {
            parseRowFormat(stmt);
        }

        if (lexer.identifierEquals(FnvHash.Constants.STORED)) {
            lexer.nextToken();

            accept(Token.AS);

            if (lexer.identifierEquals(FnvHash.Constants.INPUTFORMAT)) {
                HiveInputOutputFormat format = new HiveInputOutputFormat();
                lexer.nextToken();
                format.setInput(this.exprParser.primary());

                if (lexer.identifierEquals(FnvHash.Constants.OUTPUTFORMAT)) {
                    lexer.nextToken();
                    format.setOutput(this.exprParser.primary());
                }
                stmt.setStoredAs(format);
            } else {
                SQLName name = this.exprParser.name();
                stmt.setStoredAs(name);
            }
        }
        parseCreateTableWithSerderPropertie(stmt);
        if (lexer.identifierEquals(FnvHash.Constants.LOCATION)) {
            lexer.nextToken();
            SQLExpr location = this.exprParser.primary();
            stmt.setLocation(location);
        }

        if (lexer.identifierEquals(FnvHash.Constants.TBLPROPERTIES)) {
            lexer.nextToken();
            accept(Token.LPAREN);
            parseAssignItems(stmt.getTableOptions(), stmt, false);
            accept(Token.RPAREN);
        }
    }

    protected void parseRowFormat(SQLCreateTableStatement stmt) {
        SQLExternalRecordFormat format = this.getExprParser().parseRowFormat();
        stmt.setRowFormat(format);
    }

    protected void parseCreateTableWithSerderPropertie(SQLCreateTableStatement stmt) {
        if (stmt instanceof AthenaCreateTableStatement) {
            AthenaCreateTableStatement athenaStmt = (AthenaCreateTableStatement) stmt;
            if (lexer.token() == Token.WITH) {
                lexer.nextToken();
                acceptIdentifier("SERDEPROPERTIES");

                accept(Token.LPAREN);

                for (; ; ) {
                    String key = lexer.stringVal();
                    lexer.nextToken();
                    accept(Token.EQ);
                    SQLExpr value = this.exprParser.primary();
                    athenaStmt.getSerdeProperties().put(key, value);
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    }
                    break;
                }

                accept(Token.RPAREN);
            }
        }
    }

    @Override
    protected AthenaCreateTableStatement newCreateStatement() {
        return new AthenaCreateTableStatement();
    }
}
