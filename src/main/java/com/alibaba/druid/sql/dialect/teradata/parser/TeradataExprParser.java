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
import com.alibaba.druid.sql.ast.SQLKeep;
import com.alibaba.druid.sql.ast.SQLKeep.DenseRank;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.expr.SQLAggregateExpr;
import com.alibaba.druid.sql.ast.expr.SQLAggregateOption;
import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLMethodInvokeExpr;
import com.alibaba.druid.sql.ast.expr.SQLQueryExpr;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalytic;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataAnalyticWindowing;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataIntervalExpr;
import com.alibaba.druid.sql.dialect.teradata.ast.expr.TeradataIntervalUnit;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.SQLSelectParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;

public class TeradataExprParser extends SQLExprParser {

    public final static String[] AGGREGATE_FUNCTIONS = { "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM", "ROW_NUMBER" };

    public TeradataExprParser(String sql){
        this(new TeradataLexer(sql));
        this.lexer.nextToken();
        this.dbType = JdbcConstants.TERADATA;
    }

    public TeradataExprParser(Lexer lexer){
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.dbType = JdbcConstants.TERADATA;
    }
    
    public SQLExpr primary() {
    	SQLExpr sqlExpr = null;
    	
    	final Token tok = lexer.token();
    	
    	switch (tok){
    		case SEL: 
    			SQLQueryExpr queryExpr = new SQLQueryExpr(createSelectParser().select());
    			sqlExpr = queryExpr;
                return primaryRest(sqlExpr);
    		case CAST:
    			System.out.println("cast test!");
    			lexer.nextToken();
    			accept(Token.LPAREN);
    			SQLCastExpr cast = new SQLCastExpr();
    			cast.setExpr(expr());
    			accept(Token.AS);
    			cast.setDataType(parseDataType());
    			// TODO: deal with FORMAT
    			// currently ignore FORMAT keywords
    			if(lexer.token() == Token.FORMAT) {
    				lexer.nextToken();
    				lexer.nextToken();
    			}
    			accept(Token.RPAREN);
    			return primaryRest(cast);
    		default:
    			return super.primary();
    	}
    }
    
    public SQLSelectParser createSelectParser() {
    	return new TeradataSelectParser(this);
    }
    
    protected SQLExpr methodRest(SQLExpr expr, boolean acceptLPAREN) {
    	if (acceptLPAREN) {
    		accept(Token.LPAREN);
    	}
    	
    	if (expr instanceof SQLIdentifierExpr) {
    		String methodName = ((SQLIdentifierExpr) expr).getName();
    		SQLMethodInvokeExpr methodExpr = new SQLMethodInvokeExpr(methodName);
    		if ("trim".equalsIgnoreCase(methodName)) {
    			if (identifierEquals("LEADING")
    					|| identifierEquals("TRAILING")
    					|| identifierEquals("BOTH")) {
    				methodExpr.putAttribute("trim_option", lexer.stringVal());
    				lexer.nextToken();
    				if (lexer.token() == Token.LITERAL_CHARS) {
    				    SQLExpr trim_character = this.primary();
    					trim_character.setParent(methodExpr);
            			methodExpr.putAttribute("trim_character", trim_character);	
    				} 
    			} else {
    				SQLExpr trim_character = this.primary();
        			trim_character.setParent(methodExpr);
        			methodExpr.putAttribute("trim_character", trim_character);	
    			}
    			
    			if (lexer.token() == Token.FROM) {
    				lexer.nextToken();
    				SQLExpr trim_source = this.expr();
    				methodExpr.addParameter(trim_source);
    			}
    			
    			accept(Token.RPAREN);
    			return primaryRest(methodExpr);
    		}
    	}
    	return super.methodRest(expr, false);
    }
    
    protected SQLAggregateExpr parseAggregateExpr(String methodName) {
        methodName = methodName.toUpperCase();
        
        SQLAggregateExpr aggregateExpr;
        if (lexer.token() == Token.UNIQUE) {
            aggregateExpr = new SQLAggregateExpr(methodName, SQLAggregateOption.UNIQUE);
            lexer.nextToken();
        } else if (lexer.token() == (Token.ALL)) {
            aggregateExpr = new SQLAggregateExpr(methodName, SQLAggregateOption.ALL);
            lexer.nextToken();
        } else if (lexer.token() == (Token.DISTINCT)) {
            aggregateExpr = new SQLAggregateExpr(methodName, SQLAggregateOption.DISTINCT);
            lexer.nextToken();
        } else {
            aggregateExpr = new SQLAggregateExpr(methodName);
        }
        exprList(aggregateExpr.getArguments(), aggregateExpr);

        accept(Token.RPAREN);
              
        if (lexer.token() == Token.KEEP) {
            lexer.nextToken();
            
            SQLKeep keep = new SQLKeep();
            accept(Token.LPAREN);
            acceptIdentifier("DENSE_RANK");
            if (identifierEquals("FIRST")) {
                lexer.nextToken();
                keep.setDenseRank(DenseRank.FIRST);
            } else {
                acceptIdentifier("LAST");
                keep.setDenseRank(DenseRank.LAST);
            }
            
            SQLOrderBy orderBy = this.parseOrderBy();
            keep.setOrderBy(orderBy);
            
            aggregateExpr.setKeep(keep);
            
            accept(Token.RPAREN);
        }

        if (lexer.token() == Token.OVER) {
            TeradataAnalytic over = new TeradataAnalytic();

            lexer.nextToken();
            accept(Token.LPAREN);

            if (identifierEquals("PARTITION")) {
                lexer.nextToken();
                accept(Token.BY);

                if (lexer.token() == (Token.LPAREN)) {
                    lexer.nextToken();
                    exprList(over.getPartitionBy(), over);
                    accept(Token.RPAREN);
                } else {
                    exprList(over.getPartitionBy(), over);
                }
            }

            over.setOrderBy(parseOrderBy());
            if (over.getOrderBy() != null) {
            	TeradataAnalyticWindowing windowing = null;
                if (lexer.stringVal().equalsIgnoreCase("ROWS")) {
                    lexer.nextToken();
                    windowing = new TeradataAnalyticWindowing();
                    windowing.setType(TeradataAnalyticWindowing.Type.ROWS);
                } else if (lexer.stringVal().equalsIgnoreCase("RANGE")) {
                    lexer.nextToken();
                    windowing = new TeradataAnalyticWindowing();
                    windowing.setType(TeradataAnalyticWindowing.Type.RANGE);
                }

                if (windowing != null) {
                	if (lexer.stringVal().equalsIgnoreCase("BETWEEN")) {
                		String between = null;
                		String and = null;
                		lexer.nextToken();
                		if(lexer.token() == Token.LITERAL_INT) {
                			int val_1 = lexer.integerValue().intValue();
                			lexer.nextToken();
                			if (lexer.stringVal().equalsIgnoreCase("PRECEDING")) {
	                            lexer.nextToken();
	                            between = "BETWEEN " + String.valueOf(val_1) + " PRECEDING";
	                        } else if (lexer.stringVal().equalsIgnoreCase("FOLLOWING")) {
	                        	lexer.nextToken();
	                        	between = "BETWEEN " + String.valueOf(val_1) + " FOLLOWING";
	                        } else {
	                            throw new ParserException("syntax error");
	                        }
                		} else if (lexer.stringVal().equalsIgnoreCase("CURRENT")) {
                			lexer.nextToken();
	                        if (lexer.stringVal().equalsIgnoreCase("ROW")) {
	                            lexer.nextToken();
	                            between = "BETWEEN CURRENT ROW";
	                        }
                		} else if (lexer.stringVal().equalsIgnoreCase("UNBOUNDED")) {
                			lexer.nextToken();
	                        if (lexer.stringVal().equalsIgnoreCase("PRECEDING")) {
	                            lexer.nextToken();
	                            between = "BETWEEN UNBOUNDED PRECEDING";
	                        }
                		} else {
                			throw new ParserException("syntax error");
                		}
                		if (lexer.stringVal().equalsIgnoreCase("AND")) {
                			lexer.nextToken();
                			if(lexer.token() == Token.LITERAL_INT) {
                				System.out.println("yes, inside and");
                    			int val_2 = lexer.integerValue().intValue();
                    			lexer.nextToken();
                    			if (lexer.stringVal().equalsIgnoreCase("PRECEDING")) {
    	                            lexer.nextToken();
    	                            and = " AND " + String.valueOf(val_2) + " PRECEDING";
    	                            windowing.setExpr(new SQLIdentifierExpr(between + and));
    	                        } else if (lexer.stringVal().equalsIgnoreCase("FOLLOWING")) {
    	                        	lexer.nextToken();
    	                        	and = " AND " + String.valueOf(val_2) + " FOLLOWING";
    	                            windowing.setExpr(new SQLIdentifierExpr(between + and));
    	                        } else {
    	                            throw new ParserException("syntax error");
    	                        }
                			} else if (lexer.stringVal().equalsIgnoreCase("CURRENT")) {
                    			lexer.nextToken();
    	                        if (lexer.stringVal().equalsIgnoreCase("ROW")) {
    	                            lexer.nextToken();
    	                            and = " AND CURRENT ROW";
    	                            windowing.setExpr(new SQLIdentifierExpr(between + and));
    	                        }
                    		} else if (lexer.stringVal().equalsIgnoreCase("UNBOUNDED")) {
                    			lexer.nextToken();
    	                        if (lexer.stringVal().equalsIgnoreCase("FOLLOWING")) {
    	                            lexer.nextToken();
    	                            and  = " AND UNBOUNDED FOLLOWING";
    	                            windowing.setExpr(new SQLIdentifierExpr(between + and));
    	                        }
                    		} else {
                    			throw new ParserException("syntax error");
                    		}
                		} else {
                			throw new ParserException("syntax error");
                		}
                	} else {
	                    if (lexer.stringVal().equalsIgnoreCase("CURRENT")) {
	                        lexer.nextToken();
	                        if (lexer.stringVal().equalsIgnoreCase("ROW")) {
	                            lexer.nextToken();
	                            windowing.setExpr(new SQLIdentifierExpr("CURRENT ROW"));
	                            over.setWindowing(windowing);
	                        } else {
		                        throw new ParserException("syntax error");	                        	
	                        }
	                    }
	                    if (lexer.stringVal().equalsIgnoreCase("UNBOUNDED")) {
	                        lexer.nextToken();
	                        if (lexer.stringVal().equalsIgnoreCase("PRECEDING")) {
	                            lexer.nextToken();
	                            System.out.println("yes, inside!!");
	                            windowing.setExpr(new SQLIdentifierExpr("UNBOUNDED PRECEDING"));
	                        } else {
	                            throw new ParserException("syntax error");
	                        }
	                    }
                	}
                    over.setWindowing(windowing);
                }
            }

            accept(Token.RPAREN);

            aggregateExpr.setOver(over);
        }
        return aggregateExpr;
    }
    
    protected SQLExpr parseInterval() {
        accept(Token.INTERVAL);

        if (lexer.token() == Token.LITERAL_CHARS) {
        	SQLExpr value = expr();

            if (lexer.token() != Token.IDENTIFIER) {
                throw new ParserException("Syntax error");
            }

            String unit = lexer.stringVal();
            lexer.nextToken();

            TeradataIntervalExpr intervalExpr = new TeradataIntervalExpr();
            intervalExpr.setValue(value);
            intervalExpr.setUnit(TeradataIntervalUnit.valueOf(unit.toUpperCase()));

            return intervalExpr;
        } else {
            throw new ParserException("TODO with other interval");
        }
    }
    
}
