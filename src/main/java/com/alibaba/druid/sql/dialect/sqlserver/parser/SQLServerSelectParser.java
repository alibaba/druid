/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.sqlserver.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLExprHint;
import com.alibaba.druid.sql.ast.statement.SQLExprTableSource;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelect;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class SQLServerSelectParser extends SQLSelectParser {

    public SQLServerSelectParser(String sql){
        super(new SQLServerExprParser(sql));
    }

    public SQLServerSelectParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public SQLSelect select() {
        SQLServerSelect select = new SQLServerSelect();

        withSubquery(select);

        select.setQuery(query());
        select.setOrderBy(parseOrderBy());

        if (select.getOrderBy() == null) {
            select.setOrderBy(parseOrderBy());
        }

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();

            if (identifierEquals("BROWSE")) {
                lexer.nextToken();
                select.setForBrowse(true);
            } else if (identifierEquals("XML")) {
                lexer.nextToken();

                for (;;) {
                    if (identifierEquals("AUTO") //
                        || identifierEquals("TYPE") //
                        || identifierEquals("XMLSCHEMA") //
                    ) {
                        select.getForXmlOptions().add(lexer.stringVal());
                        lexer.nextToken();
                    } else if (identifierEquals("ELEMENTS")) {
                        lexer.nextToken();
                        if (identifierEquals("XSINIL")) {
                            lexer.nextToken();
                            select.getForXmlOptions().add("ELEMENTS XSINIL");
                        } else {
                            select.getForXmlOptions().add("ELEMENTS");
                        }
                    } else {
                        break;
                    }
                    
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                        continue;
                    } else {
                        break;
                    }
                }
            } else {
                throw new ParserException("syntax error, not support option : " + lexer.token());
            }
        }
        
        if (identifierEquals("OFFSET")) {
            lexer.nextToken();
            SQLExpr offset = this.expr();
            
            acceptIdentifier("ROWS");
            select.setOffset(offset);
            
            if (identifierEquals("FETCH")) {
                lexer.nextToken();
                acceptIdentifier("NEXT");
                
                SQLExpr rowCount = expr();
                acceptIdentifier("ROWS");
                acceptIdentifier("ONLY");
                select.setRowCount(rowCount);
            }
        }

        return select;
    }

    public SQLSelectQuery query() {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }

        SQLServerSelectQueryBlock queryBlock = new SQLServerSelectQueryBlock();

        if (lexer.token() == Token.SELECT) {
            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            if (lexer.token() == Token.DISTINCT) {
                queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                lexer.nextToken();
            } else if (lexer.token() == Token.ALL) {
                queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                lexer.nextToken();
            }

            if (lexer.token() == Token.TOP) {
                SQLServerTop top = this.createExprParser().parseTop();
                queryBlock.setTop(top);
            }

            parseSelectList(queryBlock);
        }

        if (lexer.token() == Token.INTO) {
            lexer.nextToken();

            SQLTableSource into = this.parseTableSource();
            queryBlock.setInto((SQLExprTableSource) into);
        }

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseGroupBy(queryBlock);

        return queryRest(queryBlock);
    }

    protected SQLServerExprParser createExprParser() {
        return new SQLServerExprParser(lexer);
    }

    protected SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            accept(Token.LPAREN);

            for (;;) {
                SQLExpr expr = this.expr();
                SQLExprHint hint = new SQLExprHint(expr);
                hint.setParent(tableSource);
                tableSource.getHints().add(hint);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                } else {
                    break;
                }
            }

            accept(Token.RPAREN);
        }

        return super.parseTableSourceRest(tableSource);
    }
}
