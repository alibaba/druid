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
package com.alibaba.druid.sql.dialect.postgresql.parser;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLArrayExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.ast.expr.SQLIdentifierExpr;
import com.alibaba.druid.sql.ast.expr.SQLTimestampExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryExpr;
import com.alibaba.druid.sql.ast.expr.SQLUnaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLVariantRefExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.PGOrderBy;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGBoxExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGCidrExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGCircleExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGDateField;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGExtractExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGInetExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGIntervalExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGLineSegmentsExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGMacAddrExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGPointExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGPolygonExpr;
import com.alibaba.druid.sql.dialect.postgresql.ast.expr.PGTypeCastExpr;
import com.alibaba.druid.sql.parser.Lexer;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.JdbcConstants;

public class PGExprParser extends SQLExprParser {

    public final static String[] AGGREGATE_FUNCTIONS = { "AVG", "COUNT", "MAX", "MIN", "STDDEV", "SUM", "ROW_NUMBER" };

    public PGExprParser(String sql){
        this(new PGLexer(sql));
        this.lexer.nextToken();
        this.dbType = JdbcConstants.POSTGRESQL;
    }

    public PGExprParser(Lexer lexer){
        super(lexer);
        this.aggregateFunctions = AGGREGATE_FUNCTIONS;
        this.dbType = JdbcConstants.POSTGRESQL;
    }
    
    @Override
    public SQLDataType parseDataType() {
        if (lexer.token() == Token.TYPE) {
            lexer.nextToken();
        }
        return super.parseDataType();
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

            orderBy.addItem(parseSelectOrderByItem());

            while (lexer.token() == (Token.COMMA)) {
                lexer.nextToken();
                orderBy.addItem(parseSelectOrderByItem());
            }

            return orderBy;
        }

        return null;
    }
    
    public SQLExpr primary() {
        if (lexer.token() == Token.ARRAY) {
            SQLArrayExpr array = new SQLArrayExpr();
            array.setExpr(new SQLIdentifierExpr(lexer.stringVal()));
            lexer.nextToken();
            accept(Token.LBRACKET);
            this.exprList(array.getValues(), array);
            accept(Token.RBRACKET);
            return primaryRest(array);
        } else if (lexer.token() == Token.POUND) {
            lexer.nextToken();
            if (lexer.token() == Token.LBRACE) {
                lexer.nextToken();
                String varName = lexer.stringVal();
                lexer.nextToken();
                accept(Token.RBRACE);
                SQLVariantRefExpr expr = new SQLVariantRefExpr("#{" + varName + "}");
                return primaryRest(expr);
            } else {
                SQLExpr value = this.primary();
                SQLUnaryExpr expr = new SQLUnaryExpr(SQLUnaryOperator.Pound, value);
                return primaryRest(expr);
            }
        }
        
        return super.primary();
    }

    @Override
    protected SQLExpr parseInterval() {
        accept(Token.INTERVAL);
        PGIntervalExpr intervalExpr=new PGIntervalExpr();
        if (lexer.token() != Token.LITERAL_CHARS) {
            return new SQLIdentifierExpr("INTERVAL");
        }
        intervalExpr.setValue(new SQLCharExpr(lexer.stringVal()));
        lexer.nextToken();
        return intervalExpr;
    }

    public SQLExpr primaryRest(SQLExpr expr) {
        if (lexer.token() == Token.COLONCOLON) {
            lexer.nextToken();
            SQLDataType dataType = this.parseDataType();
            
            PGTypeCastExpr castExpr = new PGTypeCastExpr();
            
            castExpr.setExpr(expr);
            castExpr.setDataType(dataType);

            return primaryRest(castExpr);
        }
        
        if (lexer.token() == Token.LBRACKET) {
            SQLArrayExpr array = new SQLArrayExpr();
            array.setExpr(expr);
            lexer.nextToken();
            this.exprList(array.getValues(), array);
            accept(Token.RBRACKET);
            return primaryRest(array);
        }
        
        if (expr.getClass() == SQLIdentifierExpr.class) {
            String ident = ((SQLIdentifierExpr)expr).getName();
            
            if ("TIMESTAMP".equalsIgnoreCase(ident)) {
                if (lexer.token() != Token.LITERAL_ALIAS //
                        && lexer.token() != Token.LITERAL_CHARS //
                        && lexer.token() != Token.WITH) {
                    return new SQLIdentifierExpr("TIMESTAMP");
                }

                SQLTimestampExpr timestamp = new SQLTimestampExpr();
                
                if (lexer.token() == Token.WITH) {
                    lexer.nextToken();
                    acceptIdentifier("TIME");
                    acceptIdentifier("ZONE");
                    timestamp.setWithTimeZone(true);
                }

                String literal = lexer.stringVal();
                timestamp.setLiteral(literal);
                accept(Token.LITERAL_CHARS);

                if (identifierEquals("AT")) {
                    lexer.nextToken();
                    acceptIdentifier("TIME");
                    acceptIdentifier("ZONE");

                    String timezone = lexer.stringVal();
                    timestamp.setTimeZone(timezone);
                    accept(Token.LITERAL_CHARS);
                }

                
                return primaryRest(timestamp);     
            } else if ("EXTRACT".equalsIgnoreCase(ident)) {
                accept(Token.LPAREN);
                
                PGExtractExpr extract = new PGExtractExpr();
                
                String fieldName = lexer.stringVal();
                PGDateField field = PGDateField.valueOf(fieldName.toUpperCase());
                lexer.nextToken();
                
                extract.setField(field);
                
                accept(Token.FROM);
                SQLExpr source = this.expr();
                
                extract.setSource(source);
                
                accept(Token.RPAREN);
                
                return primaryRest(extract);     
            } else if ("POINT".equalsIgnoreCase(ident)) {
                SQLExpr value = this.primary();
                PGPointExpr point = new PGPointExpr();
                point.setValue(value);
                return primaryRest(point);
            } else if ("BOX".equalsIgnoreCase(ident)) {
                SQLExpr value = this.primary();
                PGBoxExpr box = new PGBoxExpr();
                box.setValue(value);
                return primaryRest(box);
            } else if ("macaddr".equalsIgnoreCase(ident)) {
                SQLExpr value = this.primary();
                PGMacAddrExpr macaddr = new PGMacAddrExpr();
                macaddr.setValue(value);
                return primaryRest(macaddr);
            } else if ("inet".equalsIgnoreCase(ident)) {
                SQLExpr value = this.primary();
                PGInetExpr inet = new PGInetExpr();
                inet.setValue(value);
                return primaryRest(inet);
            } else if ("cidr".equalsIgnoreCase(ident)) {
                SQLExpr value = this.primary();
                PGCidrExpr cidr = new PGCidrExpr();
                cidr.setValue(value);
                return primaryRest(cidr);
            } else if ("polygon".equalsIgnoreCase(ident)) {
                SQLExpr value = this.primary();
                PGPolygonExpr polygon = new PGPolygonExpr();
                polygon.setValue(value);
                return primaryRest(polygon);
            } else if ("circle".equalsIgnoreCase(ident)) {
                SQLExpr value = this.primary();
                PGCircleExpr circle = new PGCircleExpr();
                circle.setValue(value);
                return primaryRest(circle);
            } else if ("lseg".equalsIgnoreCase(ident)) {
                SQLExpr value = this.primary();
                PGLineSegmentsExpr lseg = new PGLineSegmentsExpr();
                lseg.setValue(value);
                return primaryRest(lseg);
            } else if (ident.equalsIgnoreCase("b") && lexer.token() == Token.LITERAL_CHARS) {
                String charValue = lexer.stringVal();
                lexer.nextToken();
                expr = new SQLBinaryExpr(charValue);

                return primaryRest(expr);
            }
        }

        return super.primaryRest(expr);
    }
}
