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
package com.alibaba.druid.sql.dialect.sqlserver.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerSelectQueryBlock;
import com.alibaba.druid.sql.dialect.sqlserver.ast.SQLServerTop;
import com.alibaba.druid.sql.parser.*;

public class SQLServerSelectParser extends SQLSelectParser {

    public SQLServerSelectParser(String sql){
        super(new SQLServerExprParser(sql));
    }

    public SQLServerSelectParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public SQLServerSelectParser(SQLExprParser exprParser, SQLSelectListCache selectListCache){
        super(exprParser, selectListCache);
    }

    public SQLSelect select() {
        SQLSelect select = new SQLSelect();

        if (lexer.token() == Token.WITH) {
            SQLWithSubqueryClause with = this.parseWith();
            select.setWithSubQuery(with);
        }

        select.setQuery(query());
        select.setOrderBy(parseOrderBy());

        if (select.getOrderBy() == null) {
            select.setOrderBy(parseOrderBy());
        }

        if (lexer.token() == Token.FOR) {
            lexer.nextToken();

            if (lexer.identifierEquals("BROWSE")) {
                lexer.nextToken();
                select.setForBrowse(true);
            } else if (lexer.identifierEquals("XML")) {
                lexer.nextToken();

                for (;;) {
                    if (lexer.identifierEquals("AUTO") //
                        || lexer.identifierEquals("TYPE") //
                        || lexer.identifierEquals("XMLSCHEMA") //
                    ) {
                        select.getForXmlOptions().add(lexer.stringVal());
                        lexer.nextToken();
                    } else if (lexer.identifierEquals("ELEMENTS")) {
                        lexer.nextToken();
                        if (lexer.identifierEquals("XSINIL")) {
                            lexer.nextToken();
                            select.getForXmlOptions().add("ELEMENTS XSINIL");
                        } else {
                            select.getForXmlOptions().add("ELEMENTS");
                        }
                    } else if (lexer.identifierEquals("PATH")) {
                        SQLExpr xmlPath = this.exprParser.expr();
                        select.setXmlPath(xmlPath);
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
                throw new ParserException("syntax error, not support option : " + lexer.token() + ", " + lexer.info());
            }
        }
        
        if (lexer.identifierEquals("OFFSET")) {
            lexer.nextToken();
            SQLExpr offset = this.expr();
            
            acceptIdentifier("ROWS");
            select.setOffset(offset);
            
            if (lexer.token() == Token.FETCH) {
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

    public SQLSelectQuery query(SQLObject parent, boolean acceptUnion) {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select, acceptUnion);
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

        queryBlock.setOrderBy(this.exprParser.parseOrderBy());

        parseFetchClause(queryBlock);

        return queryRest(queryBlock, acceptUnion);
    }

    protected SQLServerExprParser createExprParser() {
        return new SQLServerExprParser(lexer);
    }

    public SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
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
