package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLCreateViewStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.List;

public class SnowflakeStatementParser extends SQLStatementParser {
    public SnowflakeStatementParser(String sql) {
        super(new SnowflakeExprParser(sql));
    }

    public SnowflakeStatementParser(String sql, SQLParserFeature... features) {
        super(new SnowflakeExprParser(sql, features));
    }

    public SnowflakeStatementParser(Lexer lexer) {
        super(new SnowflakeExprParser(lexer));
    }

    @Override
    public SnowflakeSelectParser createSQLSelectParser() {
        return new SnowflakeSelectParser(this.exprParser, null);
    }

    @Override
    public SQLSelectStatement parseSelect() {
        SnowflakeSelectParser selectParser = createSQLSelectParser();
        SQLSelect select = selectParser.select();
        return new SQLSelectStatement(select, DbType.snowflake);
    }

    @Override
    public boolean parseStatementListDialect(List<SQLStatement> statementList) {
        if (lexer.nextIf(Token.UNDROP)) {
            lexer.nextIf(Token.TABLE);
            lexer.nextIf(Token.SCHEMA);
            lexer.nextIf(Token.DATABASE);
            return true;
        }

        if (lexer.token() == Token.CREATE) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();
            if (lexer.token() == Token.STAGE
                    || lexer.token() == Token.WAREHOUSE
                    || lexer.token() == Token.FILE
                    || lexer.token() == Token.PIPE
                    || lexer.token() == Token.STREAM
                    || lexer.token() == Token.TASK) {
                lexer.reset(mark);
                lexer.nextToken();
                lexer.nextToken();
                return true;
            }
            lexer.reset(mark);
        }

        if (lexer.nextIf(Token.USE)) {
            return true;
        }

        if (lexer.nextIf(Token.COPY)) {
            return true;
        }

        return false;
    }

    @Override
    public SQLCreateTableStatement parseCreateTable() {
        SQLCreateTableStatement stmt = new SQLCreateTableStatement(dbType);

        accept(Token.CREATE);

        if (lexer.nextIf(Token.OR)) {
            accept(Token.REPLACE);
            stmt.setReplace(true);
        }

        if (lexer.nextIf(Token.TEMPORARY)) {
            stmt.setTemporary(true);
        }

        accept(Token.TABLE);

        if (lexer.nextIf(Token.IF)) {
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        stmt.setName(this.exprParser.name());

        // Handle CLONE clause for Snowflake
        if (lexer.nextIfIdentifier(FnvHash.Constants.CLONE)) {
            stmt.setLike(this.exprParser.name());
        }

        if (lexer.nextIf(Token.LPAREN)) {
            for (;;) {
                if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.LITERAL_ALIAS) {
                    stmt.addColumn(this.exprParser.parseColumn());
                    if (lexer.nextIfComma()) {
                        continue;
                    }
                }
                break;
            }
            accept(Token.RPAREN);
        }

        return stmt;
    }

    @Override
    public SQLCreateViewStatement parseCreateView() {
        SQLCreateViewStatement stmt = new SQLCreateViewStatement(dbType);

        accept(Token.CREATE);

        if (lexer.nextIf(Token.OR)) {
            accept(Token.REPLACE);
            stmt.setOrReplace(true);
        }

        accept(Token.VIEW);

        if (lexer.nextIf(Token.IF)) {
            accept(Token.NOT);
            accept(Token.EXISTS);
            stmt.setIfNotExists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.nextIf(Token.LPAREN)) {
            for (;;) {
                if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.LITERAL_ALIAS) {
                    stmt.addColumn(this.exprParser.parseColumn());
                    if (lexer.nextIfComma()) {
                        continue;
                    }
                }
                break;
            }
            accept(Token.RPAREN);
        }

        if (lexer.nextIf(Token.AS)) {
            stmt.setSubQuery(new SQLSelect(createSQLSelectParser().query()));
        }

        return stmt;
    }
}
