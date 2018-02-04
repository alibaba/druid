/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.hive.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLPropertyExpr;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

public class HiveExprParser extends SQLExprParser {
    private final static String[] AGGREGATE_FUNCTIONS;
    private final static long[] AGGREGATE_FUNCTIONS_CODES;

    static {
        String[] strings = { "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM", "ROW_NUMBER",
                "ROWNUMBER" };

        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
    }

    public HiveExprParser(String sql){
        this(new HiveLexer(sql));
        this.lexer.nextToken();
    }

    public HiveExprParser(String sql, SQLParserFeature... features){
        this(new HiveLexer(sql, features));
        this.lexer.nextToken();
    }

    public HiveExprParser(Lexer lexer){
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if (lexer.token() == Token.COLONCOLON) {
            lexer.nextToken();
            String propertyName = lexer.stringVal();
            lexer.nextToken();
            expr = new SQLPropertyExpr(expr, propertyName);
        }
        return super.primaryRest(expr);
    }
}
