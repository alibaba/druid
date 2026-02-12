package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class SnowflakeCreateTableParser extends SQLCreateTableParser {
    public SnowflakeCreateTableParser(String sql) {
        super(new SnowflakeExprParser(sql));
    }

    public SnowflakeCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public SQLSelectParser createSQLSelectParser() {
        return new SnowflakeSelectParser(this.exprParser, selectListCache);
    }

    protected void createTableBefore(SQLCreateTableStatement createTable) {
        if (lexer.nextIf(Token.OR)) {
            accept(Token.REPLACE);
            createTable.config(SQLCreateTableStatement.Feature.OrReplace);
        }

        // Support LOCAL/GLOBAL keywords (compatibility)
        if (lexer.identifierEquals("LOCAL") || lexer.identifierEquals("GLOBAL")) {
            lexer.nextToken();
        }

        // Support TRANSIENT keyword
        if (lexer.identifierEquals("TRANSIENT")) {
            lexer.nextToken();
            createTable.putAttribute("TRANSIENT", true);
        }

        // Support VOLATILE keyword
        if (lexer.identifierEquals("VOLATILE")) {
            lexer.nextToken();
            createTable.putAttribute("VOLATILE", true);
        }

        // Support TEMPORARY/TEMP keywords (as Token or identifier)
        if (lexer.token() == Token.TEMPORARY) {
            lexer.nextToken();
            createTable.setTemporary(true);
        } else if (lexer.identifierEquals("TEMPORARY") || lexer.identifierEquals("TEMP")) {
            lexer.nextToken();
            createTable.setTemporary(true);
        }

        if (lexer.nextIfIdentifier(FnvHash.Constants.EXTERNAL)) {
            createTable.setExternal(true);
        }
    }

    protected void parseCreateTableRest(SQLCreateTableStatement stmt) {
        for (;;) {
            if (lexer.nextIf(Token.COMMENT)) {
                lexer.nextIf(Token.EQ);
                SQLExpr comment = this.exprParser.expr();
                stmt.setComment(comment);
                continue;
            }

            if (lexer.nextIfIdentifier("CLUSTER")) {
                accept(Token.BY);
                accept(Token.LPAREN);
                for (;;) {
                    SQLSelectOrderByItem item = exprParser.parseSelectOrderByItem();
                    item.setParent(stmt);
                    stmt.getClusteredBy().add(item);
                    if (lexer.nextIf(Token.COMMA)) {
                        continue;
                    }
                    break;
                }
                accept(Token.RPAREN);
                continue;
            }

            if (lexer.nextIfIdentifier("CLONE")) {
                stmt.setLike(exprParser.name());
                continue;
            }

            if (lexer.nextIf(Token.LIKE)) {
                stmt.setLike(exprParser.name());
                continue;
            }

            if (lexer.nextIf(Token.AS)) {
                stmt.setSelect(
                        this.createSQLSelectParser().select()
                );
                continue;
            }

            // DATA_RETENTION_TIME_IN_DAYS = n
            if (lexer.identifierEquals("DATA_RETENTION_TIME_IN_DAYS")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("DATA_RETENTION_TIME_IN_DAYS", this.exprParser.expr());
                continue;
            }

            // MAX_DATA_EXTENSION_TIME_IN_DAYS = n
            if (lexer.identifierEquals("MAX_DATA_EXTENSION_TIME_IN_DAYS")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("MAX_DATA_EXTENSION_TIME_IN_DAYS", this.exprParser.expr());
                continue;
            }

            // CHANGE_TRACKING = TRUE/FALSE
            if (lexer.identifierEquals("CHANGE_TRACKING")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("CHANGE_TRACKING", this.exprParser.expr());
                continue;
            }

            // ENABLE_SCHEMA_EVOLUTION = TRUE/FALSE
            if (lexer.identifierEquals("ENABLE_SCHEMA_EVOLUTION")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("ENABLE_SCHEMA_EVOLUTION", this.exprParser.expr());
                continue;
            }

            // DEFAULT_DDL_COLLATION = 'spec'
            if (lexer.identifierEquals("DEFAULT_DDL_COLLATION")) {
                lexer.nextToken();
                accept(Token.EQ);
                stmt.putAttribute("DEFAULT_DDL_COLLATION", this.exprParser.expr());
                continue;
            }

            // COPY GRANTS
            if (lexer.identifierEquals("COPY")) {
                lexer.nextToken();
                if (lexer.token() == Token.GRANT) {
                    lexer.nextToken();
                    stmt.putAttribute("COPY_GRANTS", true);
                    continue;
                }
            }

            // TAG
            if (lexer.nextIf(Token.WITH)) {
                if (lexer.identifierEquals("TAG")) {
                    parseTag(stmt);
                    continue;
                }
            }
            if (lexer.identifierEquals("TAG")) {
                parseTag(stmt);
                continue;
            }

            if (lexer.nextIfIdentifier(FnvHash.Constants.LIFECYCLE)) {
                lexer.nextIf(Token.EQ);
                stmt.setLifeCycle(this.exprParser.primary());
                continue;
            }

            break;
        }
    }

    private void parseTag(SQLCreateTableStatement stmt) {
        lexer.nextToken(); // skip TAG
        accept(Token.LPAREN);
        for (;;) {
            SQLExpr tagName = exprParser.name();
            accept(Token.EQ);
            SQLExpr tagValue = this.exprParser.primary();
            stmt.putAttribute("TAG_" + tagName, tagValue);
            if (!lexer.nextIf(Token.COMMA)) {
                break;
            }
        }
        accept(Token.RPAREN);
    }

    @Override
    protected SQLSelect createTableQueryRest() {
        return new SnowflakeSelectParser(this.exprParser, selectListCache).select();
    }
}
