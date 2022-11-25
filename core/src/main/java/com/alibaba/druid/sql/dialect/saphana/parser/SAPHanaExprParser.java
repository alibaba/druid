/*
 * Copyright 1999-2017 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.alibaba.druid.sql.dialect.saphana.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.saphana.ast.statement.SAPHanaCreateTableStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

/**
 * @author nukiyoam
 */
public class SAPHanaExprParser extends SQLExprParser {
    public static final String[] AGGREGATE_FUNCTIONS;

    public static final long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        // @formatter:off
        String[] strings = {
                "AUTO_CORR",
                "AVG",
                "CORR",
                "CORR_SPEARMAN",
                "COUNT",
                "CROSS_CORR",
                "DFT",
                "FIRST_VALUE",
                "LAST_VALUE",
                "MAX",
                "MIN",
                "MEDIAN",
                "NTH_VALUE",
                "STDDEV",
                "STDDEV_POP",
                "STDDEV_SAMP",
                "STRING_AGG",
                "SUM",
                "VAR",
                "VAR_POP",
                "VAR_SAMP",
        };
        // @formatter:on

        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public SAPHanaExprParser(Lexer lexer) {
        super(lexer, DbType.sap_hana);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public SAPHanaExprParser(String sql) {
        this(new SAPHanaLexer(sql));
        this.lexer.nextToken();
    }

    public SAPHanaExprParser(String sql, SQLParserFeature... features) {
        super(new SAPHanaLexer(sql, features), DbType.sap_hana);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
        this.lexer.nextToken();

    }

    public SAPHanaExprParser(String sql, boolean keepComments) {
        this(new SAPHanaLexer(sql, true, keepComments));
        this.lexer.nextToken();
    }

    public SAPHanaExprParser(String sql, boolean skipComment, boolean keepComments) {
        this(new SAPHanaLexer(sql, skipComment, keepComments));
        this.lexer.nextToken();
    }

    @Override
    public SQLSelectParser createSelectParser() {
        return new SAPHanaSelectParser(this);
    }

    @Override
    protected SQLCreateTableStatement newCreateStatement() {
        return new SAPHanaCreateTableStatement();
    }

    @Override
    public SQLExpr primary() {
        final Token tok = lexer.token();
        SQLExpr sqlExpr = null;
        switch (tok) {
            case CURRENT_SCHEMA:
                lexer.nextToken();
                sqlExpr = new SQLIdentifierExpr(lexer.stringVal());
                break;
            default:
                // ignore
        }

        super.primaryRest(sqlExpr);

        if (sqlExpr == null) {
            sqlExpr = super.primary();
        }
        return sqlExpr;
    }
}
