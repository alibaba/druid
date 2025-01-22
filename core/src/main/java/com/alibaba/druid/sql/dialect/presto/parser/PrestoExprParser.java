/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.presto.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.presto.ast.PrestoColumnWith;
import com.alibaba.druid.sql.dialect.presto.ast.PrestoDateTimeExpr;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

import static com.alibaba.druid.util.FnvHash.fnv1a_64_lower;

/**
 * Created by wenshao on 16/9/13.
 */
public class PrestoExprParser extends SQLExprParser {
    private static final String[] AGGREGATE_FUNCTIONS;
    private static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = {
                "ANY_VALUE", "ARBITRARY", "ARRAY_AGG", "AVG", "BOOL_AND", "BOOL_OR", "CHECKSUM", "COUNT", "COUNT_IF",
                "EVERY", "GEOMETRIC_MEAN", "MAX_BY", "MIN_BY", "MAX", "MIN", "REDUCE_AGG", "SET_AGG", "SET_UNION",
                "SUM", "BITWISE_AND_AGG", "BITWISE_OR_AGG", "BITWISE_XOR_AGG", "HISTOGRAM", "MAP_AGG", "MAP_UNION",
                "MAP_UNION_SUM", "MULTIMAP_AGG", "APPROX_DISTINCT", "APPROX_PERCENTILE", "NUMERIC_HISTOGRAM", "CORR",
                "COVAR_POP", "COVAR_SAMP", "ENTROPY", "KURTOSIS", "REGR_INTERCEPT", "REGR_SLOPE", "REGR_AVGX",
                "REGR_AVGY", "REGR_COUNT", "REGR_R2", "REGR_SXX", "REGR_SXY", "REGR_SYY", "SKEWNESS", "STDDEV",
                "STDDEV_POP", "STDDEV_SAMP", "VARIANCE", "VAR_POP", "VAR_SAMP", "CLASSIFICATION_MISS_RATE",
                "CLASSIFICATION_FALL_OUT", "CLASSIFICATION_PRECISION", "CLASSIFICATION_RECALL",
                "CLASSIFICATION_THRESHOLDS", "DIFFERENTIAL_ENTROPY", "APPROX_MOST_FREQUENT", "RESERVOIR_SAMPLE",
                "NOISY_COUNT_GAUSSIAN", "NOISY_COUNT_IF_GAUSSIAN", "NOISY_SUM_GAUSSIAN", "NOISY_AVG_GAUSSIAN",
                "NOISY_APPROX_SET_SFM", "NOISY_APPROX_SET_SFM_FROM_INDEX_AND_ZEROS", "NOISY_APPROX_DISTINCT_SFM",
                "NOISY_EMPTY_APPROX_SET_SFM", "CARDINALITY", "MERGE", "MERGE_SFM", "CUME_DIST", "DENSE_RANK", "NTILE",
                "PERCENT_RANK", "RANK", "ROW_NUMBER", "FIRST_VALUE", "LAST_VALUE", "NTH_VALUE", "LEAD", "LAG"
        };
        AGGREGATE_FUNCTIONS_CODES = fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public PrestoExprParser(String sql, SQLParserFeature... features) {
        this(new PrestoLexer(sql, features));
        this.lexer.nextToken();
    }

    public PrestoExprParser(Lexer lexer, DbType dbType) {
        super(lexer, dbType);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public PrestoExprParser(Lexer lexer) {
        this(lexer, DbType.presto);
    }

    @Override
    protected SQLColumnDefinition parseColumnSpecific(SQLColumnDefinition column) {
        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            PrestoColumnWith prestoColumnWith = new PrestoColumnWith();
            accept(Token.LPAREN);
            parseAssignItems(prestoColumnWith.getProperties(), prestoColumnWith, false);
            accept(Token.RPAREN);
            column.addConstraint(prestoColumnWith);
            return parseColumnRest(column);
        }
        return column;
    }

    @Override
    public SQLExpr primaryRest(SQLExpr expr) {
        Lexer.SavePoint savePoint = lexer.markOut();
        if (lexer.identifierEquals(FnvHash.Constants.AT)) {
            lexer.nextToken();
            if (lexer.nextIfIdentifier(FnvHash.Constants.TIME)) {
                acceptIdentifier("ZONE");
                SQLExpr timeZone = primary();
                expr = new PrestoDateTimeExpr(expr, timeZone);
            } else {
                lexer.reset(savePoint);
            }
        }
        return super.primaryRest(expr);
    }
}
