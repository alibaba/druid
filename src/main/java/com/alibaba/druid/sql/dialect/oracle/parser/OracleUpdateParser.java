package com.alibaba.druid.sql.dialect.oracle.parser;

import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListClause;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateSetListSingleColumnItem;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;

public class OracleUpdateParser extends SQLStatementParser {
    public OracleUpdateParser(String sql) throws ParserException {
        super(sql);
    }

    public OracleUpdateParser(Lexer lexer) {
        super(lexer);
    }

    public OracleUpdateStatement parseUpdate() throws ParserException {
        accept(Token.UPDATE);

        OracleUpdateStatement update = new OracleUpdateStatement();
        parseHints(update);

        if (identifierEquals("ONLY")) {
            update.setOnly(true);
        }

        update.setTable(this.exprParser.expr());

        if ((update.getAlias() == null) || (update.getAlias().length() == 0)) {
            update.setAlias(as());
        }

        parseSet(update);

        parseWhere(update);

        parseReturn(update);

        parseErrorLoging(update);

        return update;
    }

    private void parseErrorLoging(OracleUpdateStatement update) throws ParserException {
        if (identifierEquals("LOG")) throw new ParserException("TODO");
    }

    private void parseReturn(OracleUpdateStatement update) throws ParserException {
        if (identifierEquals("RETURN") || identifierEquals("RETURNING")) {
            throw new ParserException("TODO");
        }
    }

    private void parseHints(OracleUpdateStatement update) throws ParserException {
        if (lexer.token() == Token.HINT) {
            throw new ParserException("TODO");
        }
    }

    private void parseWhere(OracleUpdateStatement update) throws ParserException {
        if (lexer.token() == (Token.WHERE)) {
            lexer.nextToken();
            update.setWhere(this.exprParser.expr());
        }
    }

    private void parseSet(OracleUpdateStatement update) throws ParserException {
        accept(Token.SET);

        if (identifierEquals("VALUE")) {
            throw new ParserException("TODO");
        }

        OracleUpdateSetListClause setListClause = new OracleUpdateSetListClause();
        while (true) {
            if (lexer.token() == (Token.LPAREN)) {
                throw new ParserException("TODO");
            }

            OracleUpdateSetListSingleColumnItem item = new OracleUpdateSetListSingleColumnItem();
            item.setColumn(this.exprParser.primary());
            accept(Token.EQ);
            item.setValue(this.exprParser.expr());
            setListClause.getItems().add(item);

            if (!(lexer.token() == (Token.COMMA))) break;
            lexer.nextToken();
        }

        update.setSetClause(setListClause);
    }
}
