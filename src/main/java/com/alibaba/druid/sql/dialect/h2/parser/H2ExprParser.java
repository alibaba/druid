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
package com.alibaba.druid.sql.dialect.h2.parser;

import com.alibaba.druid.sql.ast.expr.SQLDefaultExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;

public class H2ExprParser extends SQLExprParser {
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

    public H2ExprParser(String sql){
        this(new H2Lexer(sql));
        this.lexer.nextToken();
    }

    public H2ExprParser(String sql, SQLParserFeature... features){
        this(new H2Lexer(sql, features));
        this.lexer.nextToken();
    }

    public H2ExprParser(Lexer lexer){
        super(lexer);
        dbType = lexer.getDbType();
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
    }

    public SQLColumnDefinition parseColumnRest(SQLColumnDefinition column) {
        column = super.parseColumnRest(column);

        if (lexer.identifierEquals(FnvHash.Constants.GENERATED)) {
            lexer.nextToken();
            if (lexer.token() == Token.BY) {
                lexer.nextToken();
                accept(Token.DEFAULT);
                column.setGeneratedAlawsAs(new SQLDefaultExpr());
            } else {
                acceptIdentifier("ALWAYS");
                column.setGeneratedAlawsAs(new SQLIdentifierExpr("ALWAYS"));
            }
            accept(Token.AS);
            acceptIdentifier("IDENTITY");

            SQLColumnDefinition.Identity identity = new SQLColumnDefinition.Identity();
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                SQLIntegerExpr seed = (SQLIntegerExpr) this.primary();
                accept(Token.COMMA);
                SQLIntegerExpr increment = (SQLIntegerExpr) this.primary();
                accept(Token.RPAREN);

                identity.setSeed((Integer) seed.getNumber());
                identity.setIncrement((Integer) increment.getNumber());
            }
            column.setIdentity(identity);
        }

        return column;
    }
}
