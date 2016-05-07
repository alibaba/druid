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
package com.alibaba.druid.sql.dialect.teradata.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLSetQuantifier;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.dialect.teradata.ast.stmt.TeradataSelectQueryBlock;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;

public class TeradataSelectParser extends SQLSelectParser{

	public TeradataSelectParser(SQLExprParser exprParser) {
		super(exprParser);
	}
	
	public TeradataSelectParser(String sql) {
		this(new TeradataExprParser(sql));
	}
	
	public SQLSelectQuery query() {
		if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            SQLSelectQuery select = query();
            accept(Token.RPAREN);

            return queryRest(select);
        }
        
        TeradataSelectQueryBlock queryBlock = new TeradataSelectQueryBlock();
        
        if (lexer.token() == Token.SEL 
        		|| lexer.token() == Token.SELECT) {

            lexer.nextToken();

            if (lexer.token() == Token.COMMENT) {
                lexer.nextToken();
            }

            if (lexer.token() == (Token.DISTINCT)) {
                queryBlock.setDistionOption(SQLSetQuantifier.DISTINCT);
                lexer.nextToken();
            } else if (lexer.token() == (Token.ALL)) {
                queryBlock.setDistionOption(SQLSetQuantifier.ALL);
                lexer.nextToken();
            }
            parseSelectList(queryBlock);
        }
        parseFrom(queryBlock);

        parseWhere(queryBlock);

        parseGroupBy(queryBlock);
        
        parseWhere(queryBlock);
        
        parserQualify(queryBlock);
        
        parseWhere(queryBlock);
        
        queryBlock.setOrderBy(this.exprParser.parseOrderBy());

        return queryRest(queryBlock);
	}

	private void parserQualify(TeradataSelectQueryBlock queryBlock) {
		if (lexer.token() != Token.QUALIFY) {
			return;
		}
		
		lexer.nextToken();
		
		if (lexer.token() == Token.LPAREN) {
			accept(Token.LPAREN);
		}
		if (lexer.token() == Token.LITERAL_INT) {
			lexer.nextToken();
			// possibly =, >=, <=
			// ignore this for now
			lexer.nextToken();
			
			if (lexer.token() == Token.LPAREN) {
				accept(Token.LPAREN);
			}
			if (lexer.token() == Token.IDENTIFIER) {
				SQLExpr expr = new SQLIdentifierExpr(lexer.stringVal());
				lexer.nextToken();
				if (lexer.token() != Token.COMMA) {
					expr = this.exprParser.primaryRest(expr);	
				}
				if (lexer.token() == Token.RPAREN) {
					accept(Token.RPAREN);
				}
				// TODO: add qualify clause into queryBlock
				queryBlock.setQualifyClause(expr);
			} else {
				throw new ParserException("not support token:" + lexer.token());
			}
		} else if (lexer.token() == Token.IDENTIFIER) {
			SQLExpr expr = new SQLIdentifierExpr(lexer.stringVal());
			lexer.nextToken();
			if (lexer.token() != Token.COMMA) {
				expr = this.exprParser.primaryRest(expr);	
			}
			if (lexer.token() == Token.RPAREN) {
				accept(Token.RPAREN);
			}
			lexer.nextToken();
			lexer.nextToken();
			// TODO: add qualify clause into queryBlock
			queryBlock.setQualifyClause(expr);
		} else {
			throw new ParserException("not support token:" + lexer.token());
		}
		
		
	}

}
