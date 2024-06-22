package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.statement.StarRocksCreateResourceStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

public class StarRocksStatementParser extends SQLStatementParser {
    public StarRocksStatementParser(String sql) {
        super(new StarRocksExprParser(sql));
    }

    public StarRocksStatementParser(String sql, SQLParserFeature... features) {
        super(new StarRocksExprParser(sql, features));
    }

    public StarRocksStatementParser(String sql, boolean keepComments) {
        super(new StarRocksExprParser(sql, keepComments));
    }

    public StarRocksStatementParser(String sql, boolean skipComment, boolean keepComments) {
        super(new StarRocksExprParser(sql, skipComment, keepComments));
    }

    public StarRocksStatementParser(Lexer lexer) {
        super(new StarRocksExprParser(lexer));
    }

    public SQLCreateTableParser getSQLCreateTableParser() {
        return new StarRocksCreateTableParser(this.exprParser);
    }

    @Override
    public SQLCreateTableStatement parseCreateTable() {
        return getSQLCreateTableParser().parseCreateTable();
    }

    protected SQLStatement createResource() {
        StarRocksCreateResourceStatement stmt = new StarRocksCreateResourceStatement();
        accept(Token.CREATE);
        // create external source
        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            acceptIdentifier("EXTERNAL");
            stmt.setExternal(true);
        }

        acceptIdentifier("RESOURCE");

        stmt.setName(this.exprParser.name());
        acceptIdentifier("PROPERTIES");
        accept(Token.LPAREN);

        for (; ; ) {
            if (lexer.token() == Token.RPAREN) {
                accept(Token.RPAREN);
                break;
            }

            stmt.addProperty(this.exprParser.parseAssignItem(true, stmt));
            if (lexer.token() == Token.COMMA) {
                accept(Token.COMMA);
            }
        }

        return stmt;
    }
}
