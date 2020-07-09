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
package com.alibaba.druid.sql.dialect.impala.parser;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.impala.ast.ImpalaKuduPartition;
import com.alibaba.druid.sql.dialect.impala.stmt.ImpalaCreateTableStatement;
import com.alibaba.druid.sql.parser.*;
import com.alibaba.druid.util.FnvHash;
import org.apache.ibatis.jdbc.SQL;

import java.util.ArrayList;
import java.util.List;

public class ImpalaCreateTableParser extends SQLCreateTableParser {

    public ImpalaCreateTableParser(SQLExprParser exprParser) {
        super(exprParser);
    }

    public ImpalaCreateTableParser(Lexer lexer) {
        super(new ImpalaExprParser(lexer));
    }

    public SQLCreateTableStatement parseCreateTable(boolean acceptCreate) {
        ImpalaCreateTableStatement stmt = newCreateStatement();

        if (acceptCreate) {
            if (lexer.hasComment() && lexer.isKeepComments()) {
                stmt.addBeforeComment(lexer.readAndResetComments());
            }

            accept(Token.CREATE);
        }

        if (lexer.identifierEquals("EXTERNAL")) {
            lexer.nextToken();
            stmt.setType(SQLCreateTableStatement.Type.EXTERNAL);
        }

        accept(Token.TABLE);

        if (lexer.token() == Token.IF || lexer.identifierEquals("IF")) {
            lexer.nextToken();
            accept(Token.NOT);
            accept(Token.EXISTS);

            stmt.setIfNotExiists(true);
        }

        stmt.setName(this.exprParser.name());

        if (lexer.token() == Token.LPAREN) {
            lexer.nextToken();

            for (; ; ) {
                Token token = lexer.token();
                if (token == Token.IDENTIFIER //
                        || token == Token.LITERAL_ALIAS) {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    stmt.getTableElementList().add(column);
                } else if (token == Token.PRIMARY //
                        || token == Token.UNIQUE //
                        || token == Token.CHECK //
                        || token == Token.CONSTRAINT
                        || token == Token.FOREIGN) {
                    SQLConstraint constraint = this.exprParser.parseConstaint();
                    constraint.setParent(stmt);
                    stmt.getTableElementList().add((SQLTableElement) constraint);
                } else {
                    SQLColumnDefinition column = this.exprParser.parseColumn();
                    stmt.getTableElementList().add(column);
                }

                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();

                    if (lexer.token() == Token.RPAREN) { // compatible for sql server
                        break;
                    }
                    continue;
                }

                break;
            }

            accept(Token.RPAREN);

            if (lexer.identifierEquals("INHERITS")) {
                lexer.nextToken();
                accept(Token.LPAREN);
                SQLName inherits = this.exprParser.name();
                stmt.setInherits(new SQLExprTableSource(inherits));
                accept(Token.RPAREN);
            }
        }

        if (lexer.token() == Token.COMMENT) {
            lexer.nextToken();
            SQLExpr comment = this.exprParser.expr();
            stmt.setComment(comment);
        }

        if (lexer.token() == Token.PARTITIONED) {
            lexer.nextToken();
            accept(Token.BY);
            if (lexer.token() == Token.LPAREN) {
                accept(Token.LPAREN);

                for (; ; ) {
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
        }
        if (lexer.token()==Token.PARTITION){
            lexer.nextToken();
            accept(Token.BY);
            for(;;) {
                if (lexer.token() == Token.HASH) {
                    ImpalaKuduPartition kuduPartition = new ImpalaKuduPartition(lexer.token());
                    lexer.nextToken();
                    if (lexer.token() == Token.LPAREN) {
                        accept(Token.LPAREN);
                        while (lexer.token() != Token.RPAREN) {
                            SQLColumnDefinition column = this.exprParser.parseColumn();
                            kuduPartition.getPartitionColumns().add(column);
                        }
                        accept(Token.RPAREN);
                    }
                    if (lexer.identifierEquals("PARTITIONS")) {
                        lexer.nextToken();
                        kuduPartition.setNumber(lexer.integerValue().intValue());
                        stmt.getKuduPartitions().add(kuduPartition);
                        accept(Token.LITERAL_INT);
                    } else {
                        throw new ParserException("expect partitions, but get " + lexer.stringVal() + ". " + lexer.info());
                    }
                } else if (lexer.token() == Token.RANGE) {
                    ImpalaKuduPartition kuduPartition = new ImpalaKuduPartition(lexer.token());
                    accept(Token.RANGE);
                    accept(Token.LPAREN);
                    while (lexer.token() != Token.RPAREN) {
                        SQLColumnDefinition column = this.exprParser.parseColumn();
                        kuduPartition.getPartitionColumns().add(column);
                    }
                    accept(Token.RPAREN);
                    kuduPartition.getPartitionAssign().addAll(generateStringList());
                    stmt.getKuduPartitions().add(kuduPartition);
                } else{
                    throw new ParserException("error partition type. " + lexer.info());
                }
                if (lexer.token() != Token.COMMA){
                    break;
                }
                accept(Token.COMMA);
            }
        }




        if (lexer.token() == Token.SORT) {
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


        if (lexer.identifierEquals(FnvHash.Constants.STORED)) {
            lexer.nextToken();
            accept(Token.AS);
            SQLName name = this.exprParser.name();
            stmt.setStoredAs(name);
        }

        if (lexer.identifierEquals(FnvHash.Constants.LOCATION)) {
            lexer.nextToken();
            SQLName name = this.exprParser.name();
            stmt.setLocation(name);
        }

        if (lexer.token() == Token.AS) {
            lexer.nextToken();
            SQLSelect select = this.createSQLSelectParser().select();
            stmt.setSelect(select);
        }

        if (lexer.identifierEquals(FnvHash.Constants.TBLPROPERTIES)) {
            lexer.nextToken();
            accept(Token.LPAREN);

            for (;;) {
                String name = lexer.stringVal();
                lexer.nextToken();
                accept(Token.EQ);
                SQLExpr value = this.exprParser.primary();
                stmt.getTableOptions().put(name, value);
                if (lexer.token() == Token.COMMA) {
                    lexer.nextToken();
                    continue;
                }
                break;
            }

            accept(Token.RPAREN);
        }

        return stmt;
    }

    protected ImpalaCreateTableStatement newCreateStatement() {
        return new ImpalaCreateTableStatement();
    }

    private List<String> generateStringList(){
        List<String> result = new ArrayList<String>();
        accept(Token.LPAREN);
        StringBuilder rangeAssgin = new StringBuilder();
        for (;;) {
            if (lexer.token() == Token.RPAREN){
                result.add(rangeAssgin.toString());
                break;
            }
            if (lexer.token() == Token.COMMA) {
                result.add(rangeAssgin.toString());
                rangeAssgin = new StringBuilder();
                accept(Token.COMMA);
                continue;
            }
            if (lexer.token() == Token.IDENTIFIER || lexer.token() == Token.LITERAL_CHARS){
                rangeAssgin.append(lexer.stringVal());
            }else if (lexer.token() == Token.LITERAL_INT){
                rangeAssgin.append(lexer.integerValue());
            }else{
                rangeAssgin.append(lexer.token().name == null? lexer.token().name():
                    lexer.token().name);
            }
            rangeAssgin.append(" ");
            lexer.nextToken();
        }
        accept(Token.RPAREN);
        return result;
    }
}
