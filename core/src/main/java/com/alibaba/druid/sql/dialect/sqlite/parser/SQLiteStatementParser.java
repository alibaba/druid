package com.alibaba.druid.sql.dialect.sqlite.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateIndexStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLReplaceStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.sqlite.ast.*;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

import java.util.List;

public class SQLiteStatementParser extends SQLStatementParser {
    public SQLiteStatementParser(String sql) {
        super(new SQLiteExprParser(sql));
    }

    public SQLiteStatementParser(String sql, SQLParserFeature... features) {
        super(new SQLiteExprParser(sql, features));
    }

    public SQLiteStatementParser(Lexer lexer) {
        super(new SQLiteExprParser(lexer));
    }

    public SQLiteSelectParser createSQLSelectParser() {
        return new SQLiteSelectParser(this.exprParser, selectListCache);
    }

    @Override
    public SQLCreateIndexStatement parseCreateIndex() {
        accept(Token.CREATE);
        SQLCreateIndexStatement stmt = new SQLCreateIndexStatement(getDbType());

        if (lexer.token() == Token.UNIQUE) {
            stmt.setType("UNIQUE");
            lexer.nextToken();
        }

        accept(Token.INDEX);

        if (lexer.token() == Token.IF) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        stmt.setName(this.exprParser.name());
        accept(Token.ON);
        stmt.setTable(this.exprParser.name());

        accept(Token.LPAREN);
        for (; ; ) {
            SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
            item.setParent(stmt);
            stmt.addItem(item);
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }
        accept(Token.RPAREN);

        return stmt;
    }

    @Override
    public SQLStatement parseInsert() {
        if (lexer.token() == Token.INSERT) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.OR) {
            lexer.nextToken();
            String action = lexer.stringVal().toUpperCase();
            lexer.nextToken();

            if ("REPLACE".equals(action)) {
                SQLReplaceStatement stmt = new SQLReplaceStatement();
                stmt.setDbType(getDbType());
                accept(Token.INTO);
                stmt.setTableName(exprParser.name());

                if (lexer.token() == Token.LPAREN) {
                    lexer.nextToken();
                    this.exprParser.exprList(stmt.getColumns(), stmt);
                    accept(Token.RPAREN);
                }

                if (lexer.token() == Token.VALUES || lexer.identifierEquals("VALUE")) {
                    lexer.nextToken();
                    parseValueClause(stmt.getValuesList(), null, 0, stmt);
                } else if (lexer.token() == Token.SELECT || lexer.token() == Token.LPAREN) {
                    SQLSelect select = this.createSQLSelectParser().select();
                    SQLQueryExpr queryExpr = new SQLQueryExpr(select);
                    stmt.setQuery(queryExpr);
                }

                return stmt;
            }

            // INSERT OR IGNORE/ABORT/ROLLBACK/FAIL -> parse as regular INSERT
            SQLInsertStatement stmt = new SQLInsertStatement();
            stmt.setDbType(getDbType());
            parseInsert0(stmt);
            return stmt;
        }

        SQLInsertStatement stmt = new SQLInsertStatement();
        stmt.setDbType(getDbType());
        parseInsert0(stmt);
        return stmt;
    }

    @Override
    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        String identifier = null;
        if (lexer.token() == Token.IDENTIFIER) {
            identifier = lexer.stringVal();
        }

        if (identifier == null) {
            return false;
        }

        String upperIdent = identifier.toUpperCase();
        switch (upperIdent) {
            case "PRAGMA":
                statementList.add(parsePragma());
                return true;
            case "ATTACH":
                statementList.add(parseAttach());
                return true;
            case "DETACH":
                statementList.add(parseDetach());
                return true;
            case "VACUUM":
                statementList.add(parseVacuum());
                return true;
            case "REINDEX":
                statementList.add(parseReindex());
                return true;
            default:
                return false;
        }
    }

    private SQLitePragmaStatement parsePragma() {
        lexer.nextToken(); // skip PRAGMA

        SQLitePragmaStatement stmt = new SQLitePragmaStatement();
        stmt.setName(exprParser.name());

        if (lexer.token() == Token.EQ) {
            lexer.nextToken();
            stmt.setValue(exprParser.expr());
        } else if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();
            stmt.setValue(exprParser.expr());
            accept(Token.RPAREN);
        }

        return stmt;
    }

    private SQLiteAttachStatement parseAttach() {
        lexer.nextToken(); // skip ATTACH

        SQLiteAttachStatement stmt = new SQLiteAttachStatement();

        if (lexer.token() == Token.DATABASE) {
            lexer.nextToken();
        }

        stmt.setDatabase(exprParser.expr());
        accept(Token.AS);
        stmt.setSchemaName(exprParser.name());

        return stmt;
    }

    private SQLiteDetachStatement parseDetach() {
        lexer.nextToken(); // skip DETACH

        SQLiteDetachStatement stmt = new SQLiteDetachStatement();

        if (lexer.token() == Token.DATABASE) {
            lexer.nextToken();
        }

        stmt.setSchemaName(exprParser.name());

        return stmt;
    }

    private SQLiteVacuumStatement parseVacuum() {
        lexer.nextToken(); // skip VACUUM

        SQLiteVacuumStatement stmt = new SQLiteVacuumStatement();

        if (lexer.token() != Token.SEMI && lexer.token() != Token.EOF) {
            stmt.setSchemaName(exprParser.name());
        }

        return stmt;
    }

    private SQLiteReindexStatement parseReindex() {
        lexer.nextToken(); // skip REINDEX

        SQLiteReindexStatement stmt = new SQLiteReindexStatement();

        if (lexer.token() != Token.SEMI && lexer.token() != Token.EOF) {
            stmt.setName(exprParser.name());
        }

        return stmt;
    }
}
