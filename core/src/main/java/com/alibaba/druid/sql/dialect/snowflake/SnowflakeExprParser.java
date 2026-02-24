package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.*;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

import static com.alibaba.druid.util.FnvHash.fnv1a_64_lower;

public class SnowflakeExprParser extends SQLExprParser {
    private static final String[] AGGREGATE_FUNCTIONS;
    private static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {
                "ANY_VALUE",
                "APPROX_COUNT_DISTINCT",
                "APPROX_PERCENTILE",
                "APPROX_TOP_K",
                "ARRAY_AGG",
                "AVG",
                "BITAND_AGG",
                "BITOR_AGG",
                "BITXOR_AGG",
                "BOOLAND_AGG",
                "BOOLOR_AGG",
                "CORR",
                "COUNT",
                "COUNT_IF",
                "COVAR_POP",
                "COVAR_SAMP",
                "FIRST_VALUE",
                "GROUPING",
                "HASH_AGG",
                "HLL",
                "HLL_ACCUMULATE",
                "HLL_COMBINE",
                "HLL_ESTIMATE",
                "HLL_EXPORT",
                "HLL_IMPORT",
                "KURTOSIS",
                "LAST_VALUE",
                "LAG",
                "LEAD",
                "LISTAGG",
                "MAX",
                "MEDIAN",
                "MIN",
                "MIN_BY",
                "MAX_BY",
                "MODE",
                "OBJECT_AGG",
                "PERCENTILE_CONT",
                "PERCENTILE_DISC",
                "REGR_AVGX",
                "REGR_AVGY",
                "REGR_COUNT",
                "REGR_INTERCEPT",
                "REGR_R2",
                "REGR_SLOPE",
                "REGR_SXX",
                "REGR_SXY",
                "REGR_SYY",
                "REGR_VALX",
                "REGR_VALY",
                "SKEW",
                "STDDEV",
                "STDDEV_POP",
                "STDDEV_SAMP",
                "SUM",
                "VAR_POP",
                "VAR_SAMP",
                "VARIANCE",
                "VARIANCE_POP",
                "VARIANCE_SAMP"
        };
        AGGREGATE_FUNCTIONS_CODES = fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    {
        dbType = DbType.snowflake;
    }

    public SnowflakeExprParser(String sql) {
        this(new SnowflakeLexer(sql));
        this.lexer.nextToken();
    }

    public SnowflakeExprParser(String sql, SQLParserFeature... features) {
        this(new SnowflakeLexer(sql, features));
        this.lexer.nextToken();
    }

    public SnowflakeExprParser(Lexer lexer) {
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    static final long TRY_CAST = fnv1a_64_lower("TRY_CAST");
    static final long FLATTEN = fnv1a_64_lower("FLATTEN");
    static final long GENERATOR = fnv1a_64_lower("GENERATOR");
    static final long RESULT_SCAN = fnv1a_64_lower("RESULT_SCAN");

    protected SQLExpr methodRest(SQLExpr expr, boolean acceptLPAREN) {
        if (expr instanceof SQLIdentifierExpr) {
            SQLIdentifierExpr identifierExpr = (SQLIdentifierExpr) expr;
            long hashCode64 = identifierExpr.hashCode64();

            if (hashCode64 == TRY_CAST && acceptLPAREN) {
                SQLCastExpr castExpr = new SQLCastExpr();
                lexer.nextToken();
                castExpr.setExpr(expr());
                castExpr.setTry(true);
                accept(Token.AS);
                castExpr.setDataType(parseDataType());
                accept(Token.RPAREN);
                return castExpr;
            }
        }
        return super.methodRest(expr, acceptLPAREN);
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        // Support Snowflake :: cast operator (e.g. column::VARCHAR)
        while (lexer.nextIf(Token.COLONCOLON)) {
            SQLDataType dataType = this.parseDataType();
            expr = new SQLCastExpr(expr, dataType);
        }

        // Support Snowflake semi-structured data access: expr:field
        if (lexer.token() == Token.COLON && expr instanceof SQLIdentifierExpr) {
            // variant path access e.g. src:key
            lexer.nextToken();
            String fieldName = lexer.stringVal();
            lexer.nextToken();
            SQLExpr pathExpr = new SQLPropertyExpr(expr, fieldName);

            // chain dot access: src:key.subkey
            while (lexer.nextIf(Token.DOT)) {
                String subField = lexer.stringVal();
                lexer.nextToken();
                pathExpr = new SQLPropertyExpr(pathExpr, subField);
            }

            // allow :: cast after path access: src:key::VARCHAR
            while (lexer.nextIf(Token.COLONCOLON)) {
                SQLDataType dataType = this.parseDataType();
                pathExpr = new SQLCastExpr(pathExpr, dataType);
            }

            return primaryRest(pathExpr);
        }

        return super.primaryRest(expr);
    }

    @Override
    public SQLExpr primary() {
        // Handle Snowflake JSON object construction: {'key': 'value', ...}
        if (lexer.token() == Token.LBRACE) {
            return parseJSONObject();
        }

        return super.primary();
    }

    /**
     * Parse Snowflake JSON object construction: {'key': 'value', ...}
     */
    private SQLExpr parseJSONObject() {
        accept(Token.LBRACE);

        // Check if it's a special timestamp/date literal like {ts '...'}
        if (lexer.token() == Token.IDENTIFIER) {
            String ident = lexer.stringVal();
            if ("ts".equalsIgnoreCase(ident) || "d".equalsIgnoreCase(ident)
                    || "t".equalsIgnoreCase(ident) || "fn".equalsIgnoreCase(ident)) {
                // Let parent handle it
                lexer.reset(lexer.mark()); // Reset to before LBRACE
                return super.primary();
            }
        }

        // Parse JSON object as a method call representation: OBJECT_CONSTRUCT(key, value, ...)
        SQLMethodInvokeExpr objectConstruct = new SQLMethodInvokeExpr("OBJECT_CONSTRUCT", null);

        for (;;) {
            if (lexer.token() == Token.RBRACE) {
                break;
            }

            // Parse key (can be string literal or identifier)
            SQLExpr key;
            if (lexer.token() == Token.LITERAL_CHARS || lexer.token() == Token.LITERAL_ALIAS) {
                key = this.primary();
            } else if (lexer.token() == Token.IDENTIFIER) {
                key = new SQLIdentifierExpr(lexer.stringVal());
                lexer.nextToken();
            } else {
                key = this.primary();
            }

            objectConstruct.addParameter(key);

            // Expect colon separator
            if (lexer.token() == Token.COLON) {
                lexer.nextToken();
            }

            // Parse value
            SQLExpr value = this.expr();
            objectConstruct.addParameter(value);

            // Check for comma or end
            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }

            break;
        }

        accept(Token.RBRACE);

        return objectConstruct;
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        // Support Snowflake AUTOINCREMENT / IDENTITY
        if (lexer.identifierEquals("AUTOINCREMENT")) {
            lexer.nextToken();
            column.setAutoIncrement(true);
            if (lexer.nextIf(Token.LPAREN)) {
                this.expr();
                accept(Token.COMMA);
                this.expr();
                accept(Token.RPAREN);
            }
        } else if (lexer.identifierEquals("IDENTITY")) {
            lexer.nextToken();
            column.setAutoIncrement(true);
            if (lexer.nextIf(Token.LPAREN)) {
                this.expr();
                accept(Token.COMMA);
                this.expr();
                accept(Token.RPAREN);
            }
        }

        // Support Snowflake COLLATE
        if (lexer.identifierEquals(FnvHash.Constants.COLLATE)) {
            lexer.nextToken();
            column.setCollateExpr(primary());
        }

        // Support MASKING POLICY
        if (lexer.identifierEquals("MASKING")) {
            lexer.nextToken();
            acceptIdentifier("POLICY");
            this.name(); // policy name
            if (lexer.nextIf(Token.USING)) {
                accept(Token.LPAREN);
                this.exprList(new java.util.ArrayList<>(), column);
                accept(Token.RPAREN);
            }
        }

        // Support TAG
        if (lexer.nextIf(Token.WITH)) {
            if (lexer.identifierEquals("TAG")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                for (;;) {
                    this.name();
                    accept(Token.EQ);
                    this.primary();
                    if (!lexer.nextIf(Token.COMMA)) {
                        break;
                    }
                }
                accept(Token.RPAREN);
            }
        }

        return super.parseColumnRest(column);
    }

    public SQLSelectParser createSelectParser() {
        return new SnowflakeSelectParser(this, null);
    }

    @Override
    public SQLLimit parseLimit() {
        if (lexer.token() != Token.LIMIT) {
            return null;
        }

        SQLLimit limit = new SQLLimit();
        lexer.nextTokenValue();

        SQLExpr temp;
        if (lexer.token() == Token.LITERAL_INT) {
            temp = new SQLIntegerExpr(lexer.integerValue());
            lexer.nextTokenComma();
            if (lexer.token() != Token.COMMA && lexer.token() != Token.EOF && lexer.token() != Token.IDENTIFIER) {
                temp = this.primaryRest(temp);
                temp = this.exprRest(temp);
            }
        } else {
            temp = this.expr();
        }

        if (lexer.token() == Token.COMMA) {
            limit.setOffset(temp);
            lexer.nextTokenValue();

            SQLExpr rowCount;
            if (lexer.token() == Token.LITERAL_INT) {
                rowCount = new SQLIntegerExpr(lexer.integerValue());
                lexer.nextToken();
                if (lexer.token() != Token.EOF && lexer.token() != Token.IDENTIFIER) {
                    rowCount = this.primaryRest(rowCount);
                    rowCount = this.exprRest(rowCount);
                }
            } else {
                rowCount = this.expr();
            }

            limit.setRowCount(rowCount);
        } else if (lexer.token() == Token.OFFSET || lexer.identifierEquals(FnvHash.Constants.OFFSET)) {
            // Handle both Token.OFFSET and identifier OFFSET
            limit.setRowCount(temp);
            lexer.nextToken();
            limit.setOffset(this.expr());
            limit.setOffsetClause(true);
        } else {
            limit.setRowCount(temp);
        }

        return limit;
    }

    protected SQLCastExpr parseCastFormat(SQLCastExpr cast) {
        if (lexer.nextIfIdentifier("FORMAT")) {
            cast.setFormat(this.expr());
        }
        return cast;
    }
}
