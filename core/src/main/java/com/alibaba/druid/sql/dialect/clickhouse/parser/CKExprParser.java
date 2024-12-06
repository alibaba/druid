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
package com.alibaba.druid.sql.dialect.clickhouse.parser;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.SQLStructDataType;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.dialect.clickhouse.ast.ClickhouseColumnCodec;
import com.alibaba.druid.sql.dialect.clickhouse.ast.ClickhouseColumnTTL;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLParserFeature;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

import java.util.Arrays;
import java.util.List;

import static com.alibaba.druid.sql.parser.Token.LPAREN;
import static com.alibaba.druid.sql.parser.Token.RPAREN;

public class CKExprParser extends SQLExprParser {
    private static final String[] AGGREGATE_FUNCTIONS;
    private static final long[] AGGREGATE_FUNCTIONS_CODES;
    private static final List<String> NESTED_DATA_TYPE;

    static {
        String[] strings = {"AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM", "ROW_NUMBER",
                "ROWNUMBER"};
        AGGREGATE_FUNCTIONS_CODES = FnvHash.fnv1a_64_lower(strings, true);
        AGGREGATE_FUNCTIONS = new String[AGGREGATE_FUNCTIONS_CODES.length];
        for (String str : strings) {
            long hash = FnvHash.fnv1a_64_lower(str);
            int index = Arrays.binarySearch(AGGREGATE_FUNCTIONS_CODES, hash);
            AGGREGATE_FUNCTIONS[index] = str;
        }
        NESTED_DATA_TYPE = Arrays.asList("array", "tuple", "nullable", "lowcardinality", "variant");
    }

    public CKExprParser(String sql) {
        this(new CKLexer(sql));
        this.lexer.nextToken();
    }

    public CKExprParser(String sql, SQLParserFeature... features) {
        this(new CKLexer(sql, features));
        this.lexer.nextToken();
    }

    public CKExprParser(Lexer lexer) {
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.aggregateFunctionHashCodes = AGGREGATE_FUNCTIONS_CODES;
        this.nestedDataType = NESTED_DATA_TYPE;
    }

    protected SQLExpr parseAliasExpr(String alias) {
        String chars = alias.substring(1, alias.length() - 1);
        return new SQLCharExpr(chars);
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if (lexer.token() == Token.LBRACKET) {
            SQLArrayExpr array = new SQLArrayExpr();
            array.setExpr(expr);
            lexer.nextToken();
            this.exprList(array.getValues(), array);
            accept(Token.RBRACKET);
            return primaryRest(array);
        }

        return super.primaryRest(expr);
    }

    @Override
    protected SQLColumnDefinition parseColumnSpecific(SQLColumnDefinition column) {
        switch (lexer.token()) {
            case CODEC: {
                lexer.nextToken();
                accept(LPAREN);
                SQLExpr codecExpr = expr();
                accept(RPAREN);
                ClickhouseColumnCodec sqlColumnCodec = new ClickhouseColumnCodec();
                sqlColumnCodec.setExpr(codecExpr);
                column.addConstraint(sqlColumnCodec);
                return parseColumnRest(column);
                }
            case TTL: {
                lexer.nextToken();
                ClickhouseColumnTTL clickhouseColumnTTL = new ClickhouseColumnTTL();
                clickhouseColumnTTL.setExpr(expr());
                column.addConstraint(clickhouseColumnTTL);
                return parseColumnRest(column);
            }
            default:
                return column;
        }
    }

    @Override
    protected SQLExpr primaryDefaultRest() {
        return new SQLIdentifierExpr(lexer.stringVal());
    }

    @Override
    protected SQLDataType parseDataTypeNested() {
        lexer.nextToken();
        accept(Token.LPAREN);

        SQLStructDataType struct = new SQLStructDataType(dbType);

        for (; ; ) {
            SQLName name;
            switch (lexer.token()) {
                case GROUP:
                case ORDER:
                case FROM:
                case TO:
                    name = new SQLIdentifierExpr(lexer.stringVal());
                    lexer.nextToken();
                    break;
                default:
                    name = this.name();
                    break;
            }

            SQLDataType dataType = this.parseDataType();
            SQLStructDataType.Field field = struct.addField(name, dataType);

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
                SQLCharExpr chars = (SQLCharExpr) this.primary();
                field.setComment(chars.getText());
            }

            if (lexer.token() == Token.COMMA) {
                lexer.nextToken();
                continue;
            }
            break;
        }

        accept(Token.RPAREN);

        return struct;
    }
}
