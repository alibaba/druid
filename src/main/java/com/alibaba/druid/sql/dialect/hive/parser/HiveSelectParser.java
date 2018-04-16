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
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectListCache;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

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

    protected void parseSortBy(SQLSelectQueryBlock queryBlock) {
        if (lexer.token() == Token.SORT) {
            lexer.nextToken();
            accept(Token.BY);
            for (;;) {
                SQLExpr expr = this.expr();

                SQLSelectOrderByItem sortByItem = new SQLSelectOrderByItem(expr);

                if (lexer.token() == Token.ASC) {
                    sortByItem.setType(SQLOrderingSpecification.ASC);
                    lexer.nextToken();
                } else if (lexer.token() == Token.DESC) {
                    sortByItem.setType(SQLOrderingSpecification.DESC);
                    lexer.nextToken();
                }

                queryBlock.addSortBy(sortByItem);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                } else {
                    break;
                }
            }
        }
    }

}
