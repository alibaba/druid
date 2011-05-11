package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleInsertParser extends SQLStatementParser {
    public OracleInsertParser(String sql) throws ParserException {
        super(sql);
    }

    public OracleInsertParser(Lexer lexer) {
        super(lexer);
    }

    public SQLInsertStatement parseInsert() throws ParserException {
        throw new ParserException("TODO");
    }

    public OracleInsertStatement parseOracleInsert() throws ParserException {
        accept(Token.INSERT);

        OracleInsertStatement insertStatement = new OracleInsertStatement();

        parseHints(insertStatement);

        if (lexer.token() == Token.INTO) {
            OracleInsertStatement.SigleTableInert sigleTableInsert = new OracleInsertStatement.SigleTableInert();
            sigleTableInsert.setInto(parseInto());

            if (lexer.token() == Token.VALUES) {
                OracleInsertStatement.IntoValues values = new OracleInsertStatement.IntoValues();

                lexer.nextToken();
                accept(Token.LPAREN);
                this.exprParser.exprList(values.getValues());
                accept(Token.RPAREN);

                sigleTableInsert.setSource(values);
            }

            if (identifierEquals("LOG")) {
                throw new ParserException("TODO");
            }

            insertStatement.setInsert(sigleTableInsert);
        } else {
            if (lexer.token() == Token.ALL) {
                throw new ParserException("TODO");
            }
            throw new ParserException("syntax error");
        }

        return insertStatement;
    }

    private OracleInsertStatement.Into parseInto() throws ParserException {
        accept(Token.INTO);

        OracleInsertStatement.Into into = new OracleInsertStatement.Into();
        into.setTarget(this.exprParser.expr());
        into.setAlias(as());

        if (lexer.token() == (Token.LPAREN)) {
            lexer.nextToken();
            this.exprParser.exprList(into.getColumns());
            accept(Token.RPAREN);
        }

        return into;
    }

    private void parseHints(OracleInsertStatement parseInsert) throws ParserException {
        if (lexer.token() == Token.HINT) throw new ParserException("TODO");
    }
}
