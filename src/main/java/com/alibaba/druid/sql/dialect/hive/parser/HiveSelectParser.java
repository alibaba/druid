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
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.expr.SQLSizeExpr;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLTableSampling;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class HiveSelectParser extends SQLSelectParser {

    public HiveSelectParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public HiveSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache){
        super(exprParser, selectListCache);
    }

    public HiveSelectParser(String sql){
        this(new HiveExprParser(sql));
    }

    protected SQLExprParser createExprParser() {
        return new HiveExprParser(lexer);
    }

    protected SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
        if (lexer.identifierEquals(FnvHash.Constants.TABLESAMPLE) && tableSource instanceof SQLExprTableSource) {
            Lexer.SavePoint mark = lexer.mark();
            lexer.nextToken();
            if (lexer.token() == Token.LPAREN) {
                lexer.nextToken();

                SQLTableSampling sampling = new SQLTableSampling();

                if (lexer.identifierEquals(FnvHash.Constants.BUCKET)) {
                    lexer.nextToken();
                    SQLExpr bucket = this.exprParser.primary();
                    sampling.setBucket(bucket);

                    if (lexer.token() == Token.OUT) {
                        lexer.nextToken();
                        accept(Token.OF);
                        SQLExpr outOf = this.exprParser.primary();
                        sampling.setOutOf(outOf);
                    }

                    if (lexer.token() == Token.ON) {
                        lexer.nextToken();
                        SQLExpr on = this.exprParser.expr();
                        sampling.setOn(on);
                    }
                }

                if (lexer.token() == Token.LITERAL_INT || lexer.token() == Token.LITERAL_FLOAT) {
                    SQLExpr val = this.exprParser.primary();

                    if (lexer.identifierEquals(FnvHash.Constants.ROWS)) {
                        lexer.nextToken();
                        sampling.setRows(val);
                    } else {
                        acceptIdentifier("PERCENT");
                        sampling.setPercent(val);
                    }
                }

                if (lexer.token() == Token.IDENTIFIER) {
                    String strVal = lexer.stringVal();
                    char first = strVal.charAt(0);
                    char last = strVal.charAt(strVal.length() - 1);
                    if (last >= 'a' && last <= 'z') {
                        last -= 32; // to upper
                    }

                    boolean match = false;
                    if ((first == '.' || (first >= '0' && first <= '9'))) {
                        switch (last) {
                            case 'B':
                            case 'K':
                            case 'M':
                            case 'G':
                            case 'T':
                            case 'P':
                                match = true;
                                break;
                            default:
                                break;
                        }
                    }
                    SQLSizeExpr size = new SQLSizeExpr(strVal.substring(0, strVal.length() - 2), last);
                    sampling.setByteLength(size);
                    lexer.nextToken();
                }

                final SQLExprTableSource table = (SQLExprTableSource) tableSource;
                table.setSampling(sampling);

                accept(Token.RPAREN);
            } else {
                lexer.reset(mark);
            }
        }
        return super.parseTableSourceRest(tableSource);
    }
}
