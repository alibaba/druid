/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGAggregateExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGOrderBy;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGAnalytic;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;

public class PGExprParser extends SQLExprParser{
    public PGExprParser(String sql) throws ParserException{
        super(new PGLexer(sql));
        this.lexer.nextToken();
    }

    public PGExprParser(Lexer lexer){
        super(lexer);
    }
    
    protected SQLAggregateExpr parseAggregateExpr(String methodName) throws ParserException {
        methodName = methodName.toUpperCase();
        
        PGAggregateExpr aggregateExpr;
        if (lexer.token() == Token.ALL) {
            aggregateExpr = new PGAggregateExpr(methodName, SQLAggregateExpr.Option.ALL);
            lexer.nextToken();
        } else if (lexer.token() == Token.DISTINCT) {
            aggregateExpr = new PGAggregateExpr(methodName, SQLAggregateExpr.Option.DISTINCT);
            lexer.nextToken();
        } else {
            aggregateExpr = new PGAggregateExpr(methodName);
        }

        exprList(aggregateExpr.getArguments());

        accept(Token.RPAREN);
        
        if (lexer.token() == Token.OVER) {
        	lexer.nextToken();
        	PGAnalytic over = new PGAnalytic();
        	accept(Token.LPAREN);
        	
            if (identifierEquals("PARTITION")) {
                lexer.nextToken();
                accept(Token.BY);

                if (lexer.token() == (Token.LPAREN)) {
                    lexer.nextToken();
                    exprList(over.getPartitionBy());
                    accept(Token.RPAREN);
                } else {
                    exprList(over.getPartitionBy());
                }
            }


            over.setOrderBy(parseOrderBy());
            
            if (over.getOrderBy() != null) {
            	//TODO window
            }
        	
        	accept(Token.RPAREN);
        	aggregateExpr.setOver(over);

        }
        
        return aggregateExpr;
    }
    
    
    @Override
    public PGOrderBy parseOrderBy() {
        if (lexer.token() == (Token.ORDER)) {
        	PGOrderBy orderBy = new PGOrderBy();
            lexer.nextToken();

            if (identifierEquals("SIBLINGS")) {
                lexer.nextToken();
                orderBy.setSibings(true);
            }

            accept(Token.BY);

            orderBy.getItems().add(parseSelectOrderByItem());

            while (lexer.token() == (Token.COMMA)) {
                lexer.nextToken();
                orderBy.getItems().add(parseSelectOrderByItem());
            }

            return orderBy;
        }

        return null;
    }
}
