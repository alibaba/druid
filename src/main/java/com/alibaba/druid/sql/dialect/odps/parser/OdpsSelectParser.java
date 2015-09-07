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
package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class OdpsSelectParser extends SQLSelectParser {

    public OdpsSelectParser(SQLExprParser exprParser){
        super(exprParser.getLexer());
        this.exprParser = exprParser;
    }

    @Override
    public SQLSelectQuery query() {
        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }

        OdpsSelectQueryBlock queryBlock = new OdpsSelectQueryBlock();
        
        if (lexer.hasComment() && lexer.isKeepComments()) {
            queryBlock.addBeforeComment(lexer.readAndResetComments());
        }
        
        accept(Token.SELECT);
        
        if (lexer.token() == Token.HINT) {
            this.exprParser.parseHints(queryBlock.getHints());
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
        }

        if (lexer.token() == Token.DISTINCT) {
            queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
            lexer.nextToken();
        } else if (lexer.token() == Token.UNIQUE) {
            queryBlock.setDistionOption(SQLSetQuantifier.UNIQUE);
            lexer.nextToken();
        } else if (lexer.token() == Token.ALL) {
            queryBlock.setDistionOption(SQLSetQuantifier.ALL);
            lexer.nextToken();
        }

        parseSelectList(queryBlock);

        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseGroupBy(queryBlock);

        queryBlock.setOrderBy(this.exprParser.parseOrderBy());
        
        if (lexer.token() == Token.DISTRIBUTE) {
            lexer.nextToken();
            accept(Token.BY);
            SQLExpr distributeBy = this.expr();
            queryBlock.setDistributeBy(distributeBy);
            

            if (identifierEquals("SORT")) {
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
                    
                    queryBlock.getSortBy().add(sortByItem);
                    
                    if (lexer.token() == Token.COMMA) {
                        lexer.nextToken();
                    } else {
                        break;
                    }
                }
            }
        }

        if (lexer.token() == Token.LIMIT) {
            lexer.nextToken();
            queryBlock.setLimit(this.expr());
        }

        return queryRest(queryBlock);
    }

}
