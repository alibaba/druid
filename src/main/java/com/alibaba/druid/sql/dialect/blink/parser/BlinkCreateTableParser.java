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
package com.alibaba.druid.sql.dialect.blink.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.blink.ast.BlinkCreateTableStatement;
import com.alibaba.druid.sql.parser.ParserException;
import com.alibaba.druid.sql.parser.SQLCreateTableParser;
import com.alibaba.druid.sql.parser.SQLExprParser;
import com.alibaba.druid.sql.parser.Token;
import com.alibaba.druid.util.FnvHash;

public class BlinkCreateTableParser extends SQLCreateTableParser {

    public BlinkCreateTableParser(String sql){
        super(new BlinkExprParser(sql));
    }

    public BlinkCreateTableParser(SQLExprParser exprParser){
        super(exprParser);
    }

    public SQLCreateTableStatement parseCreateTable(boolean acceptCreate) {
        BlinkCreateTableStatement stmt = new BlinkCreateTableStatement();
        
        if (acceptCreate) {
            accept(Token.CREATE);
        }

        if (lexer.identifierEquals(FnvHash.Constants.EXTERNAL)) {
            lexer.nextToken();
            stmt.setExternal(true);
        }
        
        accept(Token.TABLE);

        if (lexer.token() == Token.IF || lexer.identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            stmt.setIfNotExiists(true);
        }

        stmt.setName(this.exprParser.name());

        accept(Token.LPAREN);

        if (lexer.isKeepComments() && lexer.hasComment()) {
            stmt.addBodyBeforeComment(lexer.readAndResetComments());
        }

        for_:
        for (;;) {
            SQLColumnDefinition column = null;
            switch (lexer.token()) {
                case IDENTIFIER:
                case KEY:
                    column = this.exprParser.parseColumn();
                    column.setParent(stmt);
                    stmt.getTableElementList().add(column);
                    break;
                case PRIMARY:
                    SQLTableConstraint constraint = this.parseConstraint();
                    constraint.setParent(stmt);
                    stmt.getTableElementList().add(constraint);
                    break;
                case PERIOD:
                    lexer.nextToken();
                    accept(Token.FOR);
                    SQLExpr periodFor = this.exprParser.primary();
                    stmt.setPeriodFor(periodFor);
                    break for_;
                default:
                    throw new ParserException("expect identifier. " + lexer.info());
            }

//        } else if (lexer.token() == Token.CONSTRAINT //
//                || lexer.token() == Token.PRIMARY //
//                || lexer.token() == Token.UNIQUE) {
//            SQLTableConstraint constraint = this.parseConstraint();
//            constraint.setParent(stmt);
//            stmt.getTableElementList().add(constraint);
//        }



            if (lexer.isKeepComments() && lexer.hasComment() && column != null) {
                column.addAfterComment(lexer.readAndResetComments());
            }

            if (!(lexer.token() == (Token.COMMA))) {
                break;
            } else {
                lexer.nextToken();

                if (lexer.isKeepComments() && lexer.hasComment() && column != null) {
                    column.addAfterComment(lexer.readAndResetComments());
                }
            }
        }
        accept(Token.RPAREN);

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            stmt.setComment(this.exprParser.primary());
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

        if (lexer.token() == Token.WITH) {
            lexer.nextToken();
            accept(Token.LPAREN);
            parseAssignItems(stmt.getTableOptions(), stmt, true);
            accept(Token.RPAREN);
        }

        return stmt;
    }
}
