package com.alibaba.druid.sql.dialect.snowflake;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class SnowflakeExprParser extends SQLExprParser {
    private static final String[] AGGREGATE_FUNCTIONS = {
            "ANY_VALUE",
            "APPROX_COUNT_DISTINCT",
            "APPROX_PERCENTILE",
            "APPROX_TOP_K",
            "ARRAY_AGG",
            "ARRAY_UNIQUE_AGG",
            "AVG",
            "BITAND_AGG",
            "BITOR_AGG",
            "BITXOR_AGG",
            "BOOLAND_AGG",
            "BOOLOR_AGG",
            "BOOLXOR_AGG",
            "COUNT",
            "COUNT_IF",
            "COVAR_POP",
            "COVAR_SAMP",
            "GROUPING",
            "GROUPING_ID",
            "HASH_AGG",
            "KURTOSIS",
            "LISTAGG",
            "MAX",
            "MAX_BY",
            "MEDIAN",
            "MIN",
            "MIN_BY",
            "MODE",
            "PERCENTILE_CONT",
            "PERCENTILE_DISC",
            "RANK",
            "DENSE_RANK",
            "ROW_NUMBER",
            "NTILE",
            "SKEW",
            "STDDEV",
            "STDDEV_POP",
            "STDDEV_SAMP",
            "SUM",
            "VAR_POP",
            "VAR_SAMP",
            "VARIANCE"
    };

    private static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(AGGREGATE_FUNCTIONS, true);
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
        super(lexer, DbType.snowflake);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    @Override
    public SQLSelectParser createSelectParser() {
        return new SnowflakeSelectParser(this, null);
    }

    @Override
    public SQLLimit parseLimit() {
        SQLLimit limit = new SQLLimit();

        accept(Token.LIMIT);

        SQLExpr expr = this.expr();
        limit.setRowCount(expr);

        if (lexer.nextIf(Token.OFFSET)) {
            SQLExpr offset = this.expr();
            limit.setOffset(offset);
        }

        return limit;
    }
}
