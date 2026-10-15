package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLJoinTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSubqueryTableSource;
import com.alibaba.druid.sql.ast.statement.SQLTableSampling;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.parser.*;

import java.util.Arrays;
import java.util.List;

public class SnowflakeSelectParser extends SQLSelectParser {
    public SnowflakeSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache) {
        super(exprParser, selectListCache);
        dbType = DbType.snowflake;
    }

    protected SQLExprParser createExprParser() {
        return new SnowflakeExprParser(lexer);
    }

    @Override
    protected void parseTop(SQLSelectQueryBlock x) {
        // Support Snowflake SELECT TOP n syntax
        if (lexer.token() == Token.TOP) {
            lexer.nextToken();
            SQLExpr topExpr = this.exprParser.primary();
            SQLLimit limit = new SQLLimit();
            limit.setRowCount(topExpr);
            x.setLimit(limit);
        }
    }

    protected void querySelectListBefore(SQLSelectQueryBlock x) {
        if (lexer.nextIf(Token.DISTINCT)) {
            x.setDistinct();
        } else if (lexer.nextIf(Token.ALL)) {
            x.setDistionOption(SQLSetQuantifier.ALL);
        }
    }

    @Override
    protected String tableAlias(boolean must) {
        Token tok = lexer.token();

        // Don't treat TABLESAMPLE or SAMPLE as table alias
        if (tok == Token.TABLESAMPLE) {
            if (must) {
                throw new ParserException("illegal alias. " + lexer.info());
            }
            return null;
        }
        if (lexer.identifierEquals("SAMPLE")) {
            if (must) {
                throw new ParserException("illegal alias. " + lexer.info());
            }
            return null;
        }

        if (tok == Token.VALUES) {
            String alias = lexer.stringVal();
            lexer.nextToken();
            return alias;
        }
        return super.tableAlias(must);
    }

    @Override
    public SQLTableSource parseTableSource(boolean forFrom) {
        // Handle LATERAL as the start of a table source
        if (lexer.token() == Token.LATERAL) {
            lexer.nextToken();
            // LATERAL FLATTEN(...) or LATERAL (SELECT ...)
            if (lexer.token() == Token.LPAREN) {
                // Could be LATERAL (SELECT ...)
                Lexer.SavePoint mark = lexer.mark();
                lexer.nextToken();
                if (lexer.token() == Token.SELECT || lexer.token() == Token.WITH) {
                    SQLSelect select = select();
                    accept(Token.RPAREN);
                    SQLSubqueryTableSource subquery = new SQLSubqueryTableSource(select);
                    subquery.putAttribute("LATERAL", true);
                    String alias = tableAlias();
                    if (alias != null) {
                        subquery.setAlias(alias);
                    }
                    return parseTableSourceRest(subquery);
                } else {
                    lexer.reset(mark);
                }
            }
            // LATERAL followed by function call like FLATTEN(...)
            SQLExpr funcExpr = this.exprParser.expr();
            SQLExprTableSource funcTableSource = new SQLExprTableSource(funcExpr);
            funcTableSource.putAttribute("LATERAL", true);
            String alias = tableAlias();
            if (alias != null) {
                funcTableSource.setAlias(alias);
            }
            return parseTableSourceRest(funcTableSource);
        }
        return super.parseTableSource(forFrom);
    }

    @Override
    public SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
        // Handle Time Travel: AT/BEFORE clause
        if (lexer.identifierEquals("AT") || lexer.identifierEquals("BEFORE")) {
            return parseTimeTravel(tableSource);
        }

        // Handle COMMA followed by LATERAL as a special case for Snowflake
        if (lexer.token() == Token.COMMA) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();
            if (lexer.token() == Token.LATERAL) {
                lexer.nextToken();
                // Parse LATERAL FLATTEN(...) or LATERAL (SELECT ...)
                SQLTableSource rightTableSource;
                if (lexer.token() == Token.LPAREN) {
                    Lexer.SavePoint mark2 = lexer.mark();
                    lexer.nextToken();
                    if (lexer.token() == Token.SELECT || lexer.token() == Token.WITH) {
                        SQLSelect select = select();
                        accept(Token.RPAREN);
                        SQLSubqueryTableSource subquery = new SQLSubqueryTableSource(select);
                        subquery.putAttribute("LATERAL", true);
                        String alias = tableAlias();
                        if (alias != null) {
                            subquery.setAlias(alias);
                        }
                        rightTableSource = subquery;
                    } else {
                        lexer.reset(mark2);
                        SQLExpr funcExpr = this.exprParser.expr();
                        SQLExprTableSource funcTs = new SQLExprTableSource(funcExpr);
                        funcTs.putAttribute("LATERAL", true);
                        String alias = tableAlias();
                        if (alias != null) {
                            funcTs.setAlias(alias);
                        }
                        rightTableSource = funcTs;
                    }
                } else {
                    // LATERAL followed by function call like FLATTEN(...)
                    SQLExpr funcExpr = this.exprParser.expr();
                    SQLExprTableSource funcTs = new SQLExprTableSource(funcExpr);
                    funcTs.putAttribute("LATERAL", true);
                    String alias = tableAlias();
                    if (alias != null) {
                        funcTs.setAlias(alias);
                    }
                    rightTableSource = funcTs;
                }

                SQLJoinTableSource join = new SQLJoinTableSource();
                join.setLeft(tableSource);
                join.setRight(rightTableSource);
                join.setJoinType(SQLJoinTableSource.JoinType.COMMA);

                return parseTableSourceRest(join);
            } else {
                lexer.reset(mark);
            }
        }

        // Continue with parent class processing (which includes parseTableSourceSample)
        return super.parseTableSourceRest(tableSource);
    }

    /**
     * Parse Snowflake Time Travel syntax: AT/BEFORE (TIMESTAMP | OFFSET | STATEMENT => value)
     */
    private SQLTableSource parseTimeTravel(SQLTableSource tableSource) {
        String timeTravelType = lexer.stringVal(); // AT or BEFORE
        lexer.nextToken();

        accept(Token.LPAREN);

        // Parse TIMESTAMP => value, OFFSET => value, or STATEMENT => value
        String paramType = lexer.stringVal();
        lexer.nextToken();
        accept(Token.EQGT);  // => operator
        SQLExpr paramValue = this.exprParser.expr();

        accept(Token.RPAREN);

        // Store as attribute on table source
        tableSource.putAttribute("TIME_TRAVEL", timeTravelType);
        tableSource.putAttribute("TIME_TRAVEL_TYPE", paramType);
        tableSource.putAttribute("TIME_TRAVEL_VALUE", paramValue);

        return parseTableSourceRest(tableSource);
    }

    @Override
    protected SQLTableSource parseLateralView(SQLTableSource tableSource) {
        // In Snowflake, LATERAL can be followed by:
        // 1. FLATTEN(...) - table function
        // 2. TABLE(FLATTEN(...)) - table function with TABLE keyword
        // 3. (SELECT ...) - lateral subquery

        if (tableSource != null && "LATERAL".equalsIgnoreCase(tableSource.getAlias())) {
            tableSource.setAlias(null);
        }

        if (lexer.token() == Token.VIEW) {
            // Hive-style LATERAL VIEW (fallback to parent)
            return super.parseLateralView(tableSource);
        }

        if (lexer.token() == Token.LPAREN) {
            // LATERAL (SELECT ...) - lateral subquery
            lexer.nextToken();
            SQLSelect select = select();
            accept(Token.RPAREN);
            SQLSubqueryTableSource subquery = new SQLSubqueryTableSource(select);
            subquery.putAttribute("LATERAL", true);
            String alias = tableAlias();
            if (alias != null) {
                subquery.setAlias(alias);
            }

            SQLJoinTableSource join = new SQLJoinTableSource();
            join.setLeft(tableSource);
            join.setRight(subquery);
            join.setJoinType(SQLJoinTableSource.JoinType.COMMA);

            return parseTableSourceRest(join);
        }

        // LATERAL FLATTEN(...) or LATERAL identifier(...)
        SQLExpr funcExpr = this.exprParser.expr();
        SQLExprTableSource funcTableSource = new SQLExprTableSource(funcExpr);
        funcTableSource.putAttribute("LATERAL", true);
        String alias = tableAlias();
        if (alias != null) {
            funcTableSource.setAlias(alias);
        }

        SQLJoinTableSource join = new SQLJoinTableSource();
        join.setLeft(tableSource);
        join.setRight(funcTableSource);
        join.setJoinType(SQLJoinTableSource.JoinType.COMMA);

        return parseTableSourceRest(join);
    }

    @Override
    protected List<String> getReturningFunctions() {
        return Arrays.asList("GENERATE_SERIES", "FLATTEN", "GENERATOR", "RESULT_SCAN", "SPLIT_TO_TABLE");
    }

    @Override
    public void parseTableSourceSample(SQLTableSource tableSource) {
        // Handle Snowflake SAMPLE / TABLESAMPLE syntax
        // Check if we're at SAMPLE or TABLESAMPLE token
        if (lexer.token() != Token.TABLESAMPLE && !lexer.identifierEquals("SAMPLE")) {
            return;
        }

        boolean hasSample = false;
        if (lexer.token() == Token.TABLESAMPLE) {
            lexer.nextToken();
            hasSample = true;
        } else if (lexer.identifierEquals("SAMPLE")) {
            lexer.nextToken();
            hasSample = true;
        }

        if (hasSample) {
            accept(Token.LPAREN);

            SQLTableSampling sampling = new SQLTableSampling();

            // Parse the sample value
            if (lexer.token() == Token.LITERAL_INT || lexer.token() == Token.LITERAL_FLOAT) {
                SQLExpr val = this.exprParser.primary();

                // Check for PERCENT keyword (identifier)
                if (lexer.identifierEquals("PERCENT")) {
                    lexer.nextToken();
                    sampling.setPercent(val);
                } else if (lexer.token() == Token.ROWS || lexer.identifierEquals("ROWS")) {
                    // ROWS can be Token or identifier
                    lexer.nextToken();
                    sampling.setRows(val);
                } else {
                    // Default is percent
                    sampling.setPercent(val);
                }
            }

            accept(Token.RPAREN);

            if (tableSource instanceof SQLExprTableSource) {
                ((SQLExprTableSource) tableSource).setSampling(sampling);
            }
        }
    }

    @Override
    protected SQLTableSource primaryTableSourceRest(SQLTableSource tableSource) {
        return tableSource;
    }
}
