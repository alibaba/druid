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
package com.alibaba.druid.sql.dialect.odps.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.SQLColumnDefinition;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.odps.ast.OdpsCreateTableStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class OdpsCreateTableParser extends SQLCreateTableParser {

    public OdpsCreateTableParser(String sql){
        super(new OdpsExprParser(sql));
    }

    public OdpsCreateTableParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public SQLCreateTableStatement parseCreateTable(boolean acceptCreate) {
        OdpsCreateTableStatement stmt = new OdpsCreateTableStatement();
        
        if (acceptCreate) {
            accept(Token.CREATE);
        }
        
        accept(Token.TABLE);

        if (lexer.token() == Token.IF || lexer.identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            stmt.setIfNotExiists(true);
        }

        stmt.setName(this.exprParser.name());
        
        if (lexer.identifierEquals("LIFECYCLE")) {
            lexer.nextToken();
            stmt.setLifecycle(this.exprParser.expr());
        }

        if (lexer.token() == Token.LIKE) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setLike(name);
        } else if (lexer.token() == Token.AS) {
            lexer.nextToken();
            
            OdpsSelectParser selectParser = new OdpsSelectParser(this.exprParser);
            SQLSelect select = selectParser.select();
            
            stmt.setSelect(select);
        } else {
            accept(Token.LPAREN);
            
            if (lexer.isKeepComments() && lexer.hasComment()) {
                stmt.addBodyBeforeComment(lexer.readAndResetComments());
            }
            
            for (;;) {
                if (lexer.token() != Token.IDENTIFIER) {
                    throw new ParserException("expect identifier. " + lexer.info());
                }
                
                SQLColumnDefinition column = this.exprParser.parseColumn();
                stmt.getTableElementList().add(column);
                
                if (lexer.isKeepComments() && lexer.hasComment()) {
                    column.addAfterComment(lexer.readAndResetComments());
                }
                
                if (!(lexer.token() == (Token.COMMA))) {
                    break;
                } else {
                    lexer.nextToken();
                    
                    if (lexer.isKeepComments() && lexer.hasComment()) {
                        column.addAfterComment(lexer.readAndResetComments());
                    }
                }
            }
            accept(Token.RPAREN);
        }
        
        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            stmt.setComment(this.exprParser.primary());
        }
        
        if (lexer.token() == Token.PARTITIONED) {
            lexer.nextToken();
            accept(Token.BY);
            accept(Token.LPAREN);
            
            for (;;) {
                if (lexer.token() != Token.IDENTIFIER) {
                    throw new ParserException("expect identifier. " + lexer.info());
                }
                
                SQLColumnDefinition column = this.exprParser.parseColumn();
                stmt.addPartitionColumn(column);
                
                if (lexer.isKeepComments() && lexer.hasComment()) {
                    column.addAfterComment(lexer.readAndResetComments());
                }
                
                if (lexer.token() != Token.COMMA) {
                    break;
                } else {
                    lexer.nextToken();
                    if (lexer.isKeepComments() && lexer.hasComment()) {
                        column.addAfterComment(lexer.readAndResetComments());
                    }
                }
            }
            
            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals(FnvHash.Constants.CLUSTERED)) {
            lexer.nextToken();
            accept(Token.BY);
            accept(Token.LPAREN);
            for (; ; ) {
                SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
                stmt.addClusteredByItem(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
        }

        if (lexer.identifierEquals(FnvHash.Constants.SORTED)) {
            lexer.nextToken();
            accept(Token.BY);
            accept(Token.LPAREN);
            for (; ; ) {
                SQLSelectOrderByItem item = this.exprParser.parseSelectOrderByItem();
                stmt.addSortedByItem(item);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }
            accept(Token.RPAREN);
        }

        if (stmt.getClusteredBy().size() > 0 || stmt.getSortedBy().size() > 0) {
            accept(Token.INTO);
            if (lexer.token() == Token.LITERAL_INT) {
                stmt.setBuckets(lexer.integerValue().intValue());
                lexer.nextToken();
            } else {
                throw new ParserException("into buckets must be integer. " + lexer.info());
            }
            acceptIdentifier("BUCKETS");
        }
        
        if (lexer.identifierEquals(FnvHash.Constants.LIFECYCLE)) {
            lexer.nextToken();
            stmt.setLifecycle(this.exprParser.expr());
        }

        while (lexer.identifierEquals(FnvHash.Constants.STORED)) {
            lexer.nextToken();
            if (lexer.token() == Token.AS) {
                lexer.nextToken();
                SQLName storedAs = this.exprParser.name();
                stmt.setStoredAs(storedAs);
            } else {
                accept(Token.BY);
                SQLExpr storedBy = this.exprParser.expr();
                stmt.setStoredBy(storedBy);
            }
        }
        
        return stmt;
    }
}
