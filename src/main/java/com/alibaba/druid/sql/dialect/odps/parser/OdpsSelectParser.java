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
package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.SQLOrderingSpecification;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLListExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsLateralViewTableSource;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsSelectQueryBlock;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsValuesTableSource;
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
            this.exprParser.exprList(queryBlock.getDistributeBy(), queryBlock);

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
            queryBlock.setLimit(new SQLLimit(this.expr()));
        }

        return queryRest(queryBlock);
    }

    public SQLTableSource parseTableSource() {
        if (lexer.token() == Token.VALUES) {
            lexer.nextToken();
            OdpsValuesTableSource tableSource = new OdpsValuesTableSource();

            for (;;) {
                accept(Token.LPAREN);
                SQLListExpr listExpr = new SQLListExpr();
                this.exprParser.exprList(listExpr.getItems(), listExpr);
                accept(Token.RPAREN);

                listExpr.setParent(tableSource);

                tableSource.getValues().add(listExpr);

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }

            String alias = this.tableAlias();
            tableSource.setAlias(alias);

            accept(Token.LPAREN);
            this.exprParser.names(tableSource.getColumns(), tableSource);
            accept(Token.RPAREN);

            return tableSource;
        }

        return super.parseTableSource();
    }
    
    protected SQLTableSource parseTableSourceRest(SQLTableSource tableSource) {
        tableSource = super.parseTableSourceRest(tableSource);
        
        if ("LATERAL".equalsIgnoreCase(tableSource.getAlias()) && lexer.token() == Token.VIEW) {
            return parseLateralView(tableSource);
        }
        
        if (identifierEquals("LATERAL")) {
            lexer.nextToken();
            return parseLateralView(tableSource);
        }
        
        return tableSource;
    }

    protected SQLTableSource parseLateralView(SQLTableSource tableSource) {
        accept(Token.VIEW);
        if ("LATERAL".equalsIgnoreCase(tableSource.getAlias())) {
            tableSource.setAlias(null);
        }
        OdpsLateralViewTableSource lateralViewTabSrc = new OdpsLateralViewTableSource();
        lateralViewTabSrc.setTableSource(tableSource);
        
        SQLMethodInvokeExpr udtf = (SQLMethodInvokeExpr) this.exprParser.expr();
        lateralViewTabSrc.setMethod(udtf);
        
        String alias = as();
        lateralViewTabSrc.setAlias(alias);
        
        accept(Token.AS);
        
        this.exprParser.names(lateralViewTabSrc.getColumns());
        
        return parseTableSourceRest(lateralViewTabSrc);
    }

}
