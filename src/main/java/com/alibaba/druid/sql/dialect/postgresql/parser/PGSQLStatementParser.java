package com.alibaba.druid.sql.dialect.postgresql.parser;

import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGDeleteStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGTruncateStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class PGSQLStatementParser extends SQLStatementParser {

    public PGSQLStatementParser(String sql) throws ParserException{
        this(new PGLexer(sql));
        this.lexer.nextToken();
    }

    public PGSQLStatementParser(Lexer lexer){
        super(lexer);
    }

    public PGSelectParser createSQLSelectParser() {
        return new PGSelectParser(this.lexer);
    }

    public SQLDeleteStatement parseDeleteStatement() throws ParserException {
        lexer.nextToken();
        PGDeleteStatement deleteStatement = new PGDeleteStatement();

        if (lexer.token() == (Token.FROM)) {
            lexer.nextToken();
        }
        if (lexer.token() == (Token.ONLY)) {
            lexer.nextToken();
            deleteStatement.setOnly(true);
        }

        SQLName tableName = exprParser.name();

        deleteStatement.setTableName(tableName);

        if (lexer.token() == Token.USING) {
            lexer.nextToken();
            for (;;) {
                SQLName name = this.createExprParser().name();
                deleteStatement.getUsing().add(name);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
        }

        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            SQLExpr where = this.exprParser.expr();
            deleteStatement.setWhere(where);
        }

        return deleteStatement;
    }

    public SQLStatement parseTruncate() {
        accept(Token.TRUNCATE);

        PGTruncateStatement stmt = new PGTruncateStatement();

        if (lexer.token() == Token.TABLE) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.ONLY) {
            lexer.nextToken();
            stmt.setOnly(true);
        }

        for (;;) {
            SQLName name = this.exprParser.name();
            stmt.getTableNames().add(name);

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        if (lexer.token() == Token.RESTART) {
            lexer.nextToken();
            accept(Token.IDENTITY);
            stmt.setRestartIdentity(Boolean.TRUE);
        } else if (lexer.token() == Token.SHARE) {
            lexer.nextToken();
            accept(Token.IDENTITY);
            stmt.setRestartIdentity(Boolean.FALSE);
        }

        if (lexer.token() == Token.CASCADE) {
            lexer.nextToken();
            stmt.setCascade(Boolean.TRUE);
        } else if (lexer.token() == Token.RESTRICT) {
            lexer.nextToken();
            stmt.setCascade(Boolean.FALSE);
        }

        return stmt;
    }

    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.token() == Token.WITH) {
            SQLStatement stmt = parseSelect();
            statementList.add(stmt);
            return true;
        }

        return false;
    }
}
