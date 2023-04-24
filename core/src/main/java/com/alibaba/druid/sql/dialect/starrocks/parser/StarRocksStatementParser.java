package com.alibaba.druid.sql.dialect.starrocks.parser;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.expr.StarRocksCharExpr;
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

    @Override
    public SQLStatement parseCreate() {
        Lexer.SavePoint savePoint = lexer.markOut();
        lexer.nextToken();

        // create external source
        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            acceptIdentifier("EXTERNAL");
        }

        if (lexer.identifierEquals(FnvHash.Constants.RESOURCE)) {
            lexer.reset(savePoint);
            return parseCreateResourceStatement();
        }

        lexer.reset(savePoint);
        return super.parseCreate();
    }

    private StarRocksCreateResourceStatement parseCreateResourceStatement() {
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

        while (true) {
            if (Token.RPAREN == lexer.token()) {
                accept(Token.RPAREN);
                break;
            } else if (Token.COMMA == lexer.token()) {
                accept(Token.COMMA);
            }

            String keyText = SQLUtils.forcedNormalize(lexer.stringVal(), getDbType());
            lexer.nextToken();
            accept(Token.EQ);
            SQLCharExpr value = new StarRocksCharExpr(SQLUtils.forcedNormalize(lexer.stringVal(), getDbType()));
            stmt.addProperty(keyText, value);
            lexer.nextToken();
        }

        return stmt;
    }
}
